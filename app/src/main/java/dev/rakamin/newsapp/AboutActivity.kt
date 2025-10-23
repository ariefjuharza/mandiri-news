package dev.rakamin.newsapp

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import dev.rakamin.newsapp.databinding.ActivityAboutBinding

class AboutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        openLinkedin()
    }

    private fun openLinkedin() {
        binding.ivLinkedin.setOnClickListener {
            val linkedinUri = "https://www.linkedin.com/in/ariefjuharza/".toUri()
            val redirectToLinkedin = Intent(Intent.ACTION_VIEW, linkedinUri)
            try {
                startActivity(redirectToLinkedin)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(this, getString(R.string.gagal_buka_linkedin, e.message), Toast.LENGTH_SHORT).show()
            }
        }
    }
}