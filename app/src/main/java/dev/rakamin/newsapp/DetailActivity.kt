package dev.rakamin.newsapp

import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import dev.rakamin.newsapp.databinding.ActivityDetailBinding
import dev.rakamin.newsapp.model.Articles

class DetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_NEWS = "extra_news"
    }

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val news = if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra(EXTRA_NEWS, Articles::class.java)
        } else {
            @Suppress("DEPRECATION") intent.getParcelableExtra(EXTRA_NEWS)
        }

        val url = news?.url
        if (url != null) {
            setupWebView(url)
            setupOnBackPressed()
        } else {
            finish()
        }

    }

    private fun setupWebView(url: String) {
        binding.webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                binding.progressBar.visibility = View.VISIBLE
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                binding.progressBar.visibility = View.GONE
            }
        }
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.loadUrl(url)
    }

    private fun setupOnBackPressed() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.webView.canGoBack()) {
                    binding.webView.goBack()
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)
    }

}