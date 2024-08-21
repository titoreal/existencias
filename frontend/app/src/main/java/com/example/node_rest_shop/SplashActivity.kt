package com.example.node_rest_shop

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.example.node_rest_shop.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ocultar la barra de acción si está presente
        supportActionBar?.hide()

        // Animación de escalado para el logo
        binding.ivLogo.alpha = 0f
        binding.ivLogo.scaleX = 0.5f
        binding.ivLogo.scaleY = 0.5f
        binding.ivLogo.animate().setDuration(1500)
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .withEndAction {
                // Animar el texto deslizándose hacia arriba
                val slideUp = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left)
                binding.tvAppName.startAnimation(slideUp)
                binding.tvAppName.animate().setDuration(1000).alpha(1f).withEndAction {
                    // Iniciar la actividad principal
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    finish()
                }
            }

        // Efecto de resplandor en el logo
        val pulse = AnimationUtils.loadAnimation(this, R.anim.pulse)
        binding.ivLogo.startAnimation(pulse)
    }
}
