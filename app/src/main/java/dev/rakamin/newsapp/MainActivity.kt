package dev.rakamin.newsapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import dev.rakamin.newsapp.adapter.HeadlineAdapter
import dev.rakamin.newsapp.adapter.NewsAdapter
import dev.rakamin.newsapp.api.ApiClient
import dev.rakamin.newsapp.databinding.ActivityMainBinding
import dev.rakamin.newsapp.model.Articles
import dev.rakamin.newsapp.model.NewsResponse
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "MainActivity"
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewPager: ViewPager2
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var runnable: Runnable
    private lateinit var newsRecyclerView: RecyclerView

    private var currentPage = 1
    private var isFetching = false
    private var totalResults = 0
    private lateinit var newsAdapter: NewsAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        viewPager = binding.vpHeadlineNews
        newsRecyclerView = binding.rvListNews
        linearLayoutManager = LinearLayoutManager(this)
        newsRecyclerView.layoutManager = linearLayoutManager

        setupRecyclerView()
        setupScrollListener()

        fetchAllNewsData()
        fetchHeadlineData()

        binding.ivAccount.setOnClickListener {
            val aboutIntent = Intent(this@MainActivity, AboutActivity::class.java)
            startActivity(aboutIntent)
        }

    }

    private fun fetchAllNewsData() {
        if (isFetching) return
        isFetching = true

        lifecycleScope.launch {
            val call = ApiClient.instance.getAllNews(query = "business", page = currentPage)
            call.enqueue(object : Callback<NewsResponse> {
                override fun onResponse(call: Call<NewsResponse>, response: Response<NewsResponse>) {
                    if (response.isSuccessful) {
                        val articles = response.body()?.articles
                        totalResults = response.body()?.totalResults ?: 0

                        if (!articles.isNullOrEmpty()) {
                            if (currentPage == 1) {
                                newsAdapter.setArticles(articles)
                            } else {
                                newsAdapter.addArticles(articles)
                            }
                        } else {
                            Log.d(TAG, "All news Response successful but article list is empty.")
                        }
                    } else {
                        Log.e(TAG, "All news response not successful: ${response.code()}")
                    }
                    isFetching = false
                }

                override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
                    Log.e(TAG, "All news API Call Failed: ${t.message}", t)
                    isFetching = false
                }
            })
        }
    }

    private fun setupRecyclerView() {
        newsAdapter = NewsAdapter(mutableListOf())
        newsRecyclerView.adapter = newsAdapter
    }

    private fun setupScrollListener() {
        newsRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val visibleItemCount = linearLayoutManager.childCount
                val totalItemCount = linearLayoutManager.itemCount
                val firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition()

                if (!isFetching && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0 && totalItemCount < totalResults) {
                    currentPage++
                    fetchAllNewsData()
                }
            }
        })
    }

    private fun fetchHeadlineData() {
        lifecycleScope.launch {
            val call = ApiClient.instance.getTopHeadlines()
            call.enqueue(object : Callback<NewsResponse> {
                override fun onResponse(call: Call<NewsResponse>, response: Response<NewsResponse>) {
                    if (response.isSuccessful) {
                        val allArticles = response.body()?.articles
                        if (!allArticles.isNullOrEmpty()) {
                            val limitedArticles = allArticles.take(5)
                            setupViewPager(limitedArticles)
                        } else {
                            Log.d(TAG, "Headline Response successful but article list is empty.")
                        }
                    } else {
                        Log.e(TAG, "Headline Response not successful: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
                    Log.e(TAG, "Headline API Call Failed: ${t.message}", t)
                }
            })
        }
    }

    private fun setupViewPager(articles: List<Articles>) {
        val adapter = HeadlineAdapter(articles)
        viewPager.adapter = adapter

        val tabLayout = binding.tabLayout
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
        }.attach()

        setupAutoSwipe()

    }

    private fun setupAutoSwipe() {
        val slideInterval = 5000L

        runnable = Runnable {
            var currentItem = viewPager.currentItem
            val adapter = viewPager.adapter
            if (adapter != null && adapter.itemCount > 0) {
                currentItem++
                if (currentItem >= adapter.itemCount) {
                    currentItem = 0
                }
                viewPager.setCurrentItem(currentItem, true)
            }
        }

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                handler.removeCallbacks(runnable)
                handler.postDelayed(runnable, slideInterval)
            }
        })
    }

    override fun onPause() {
        super.onPause()
        if (::runnable.isInitialized) {
            handler.removeCallbacks(runnable)
        }
    }

    override fun onResume() {
        super.onResume()
        if (::runnable.isInitialized) {
            handler.postDelayed(runnable, 3000L)
        }
    }
}