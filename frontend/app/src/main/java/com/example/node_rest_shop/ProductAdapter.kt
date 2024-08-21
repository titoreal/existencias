package com.example.node_rest_shop

import android.graphics.drawable.Drawable
import com.bumptech.glide.request.target.Target
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import com.bumptech.glide.Glide
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener


class ProductAdapter(private var products: MutableList<Product> = mutableListOf()) :
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.productName)
        val priceTextView: TextView = view.findViewById(R.id.productPrice)
        val imageView: ImageView = view.findViewById(R.id.productImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.product_item, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        holder.nameTextView.text = product.name
        holder.priceTextView.text = "$ ${product.price}"

        Log.d("ProductAdapter", "Loading image from URL: ${product.imageUrl}")

        Glide.with(holder.imageView.context)
            .load(product.imageUrl)
            .error(R.drawable.error_image)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                    Log.e("ProductAdapter", "Error loading image: ${e?.message}", e)
                    return false
                }

                override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    Log.d("ProductAdapter", "Image loaded successfully")
                    return false
                }
            })
            .into(holder.imageView)
    }
    override fun getItemCount() = products.size

    fun getProductAt(position: Int): Product {
        return products[position]
    }

    fun removeProduct(position: Int) {
        Log.d("ProductAdapter", "Removing product at position: $position")
        if (position in products.indices) {
            products.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, products.size)
            Log.d("ProductAdapter", "Product removed, new size: ${products.size}")
        } else {
            Log.e("ProductAdapter", "Attempted to remove product at invalid position: $position")
        }
    }

    fun updateProducts(newProducts: List<Product>) {
        products.clear()
        products.addAll(newProducts)
        notifyDataSetChanged()
    }
}