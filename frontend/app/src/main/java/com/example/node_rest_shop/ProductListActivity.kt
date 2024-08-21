package com.example.node_rest_shop

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.TimeoutError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class ProductListActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var productAdapter: ProductAdapter
    private lateinit var token: String

    companion object {
        private const val TAG = "ProductListActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate llamado")
        setContentView(R.layout.activity_product_list)

        val sharedPref = getSharedPreferences("MyApp", Context.MODE_PRIVATE)
        token = sharedPref.getString("JWT_TOKEN", "") ?: ""
        Log.d(TAG, "Token obtenido: $token")

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        productAdapter = ProductAdapter()
        recyclerView.adapter = productAdapter

        Log.d(TAG, "RecyclerView y Adapter configurados")

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val product = productAdapter.getProductAt(position)
                deleteProduct(product.id, position)
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)

        Log.d(TAG, "ItemTouchHelper configurado")

        fetchProducts()
    }

    private fun fetchProducts() {
        val url = "http://192.168.100.81:3000/products"
        Log.d(TAG, "Iniciando fetchProducts con URL: $url")

        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                Log.d(TAG, "Respuesta recibida: $response")
                val products = parseProducts(response)
                Log.d(TAG, "Productos parseados: ${products.size}")
                productAdapter.updateProducts(products)
                Log.d(TAG, "Adapter actualizado con ${products.size} productos")
            },
            { error ->
                Log.e(TAG, "Error al obtener productos: ${error.message}", error)
                Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer $token"
                Log.d(TAG, "Headers de la solicitud: $headers")
                return headers
            }
        }

        Volley.newRequestQueue(this).add(jsonObjectRequest)
    }

    private fun deleteProduct(productId: String, position: Int) {
        val url = "http://192.168.100.81:3000/products/$productId"
        Log.d(TAG, "Iniciando deleteProduct para producto ID: $productId")

        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.DELETE, url, null,
            { response ->
                Log.d(TAG, "Producto eliminado exitosamente: $response")
                productAdapter.removeProduct(position)
                Toast.makeText(this, "Producto eliminado", Toast.LENGTH_SHORT).show()
            },
            { error ->
                Log.e(TAG, "Error al eliminar producto: ${error.message}", error)
                Toast.makeText(this, "Error al eliminar: ${error.message}", Toast.LENGTH_SHORT).show()
                productAdapter.notifyItemChanged(position)
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer $token"
                Log.d(TAG, "Headers de la solicitud de eliminación: $headers")
                return headers
            }
        }

        Volley.newRequestQueue(this).add(jsonObjectRequest)
    }

    private fun parseProducts(response: JSONObject): List<Product> {
        val productsArray = response.getJSONArray("products")
        val products = mutableListOf<Product>()
        for (i in 0 until productsArray.length()) {
            val productJson = productsArray.getJSONObject(i)
            val product = Product(
                productJson.getString("name"),
                productJson.getDouble("price"),
                productJson.getString("productImage"),
                productJson.getString("id")
            )
            Log.d(TAG, "Producto parseado: $product")
            products.add(product)
        }
        return products
    }
    private fun fetchProductsWithRetry(retryCount: Int = 3) {
        if (retryCount == 0) {
            Toast.makeText(this, "No se pudieron obtener los productos después de varios intentos", Toast.LENGTH_LONG).show()
            return
        }

        val url = "http://192.168.100.81:3000/products"
        Log.d(TAG, "Intentando obtener productos, intento ${4 - retryCount}")

        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                Log.d(TAG, "Respuesta recibida: $response")
                val products = parseProducts(response)
                productAdapter.updateProducts(products)
            },
            { error ->
                Log.e(TAG, "Error al obtener productos: ${error.message}")
                if (error is TimeoutError) {
                    fetchProductsWithRetry(retryCount - 1)
                } else {
                    Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer $token"
                return headers
            }
        }

        val policy = DefaultRetryPolicy(
            30000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        jsonObjectRequest.retryPolicy = policy

        Volley.newRequestQueue(this).add(jsonObjectRequest)
    }
}
