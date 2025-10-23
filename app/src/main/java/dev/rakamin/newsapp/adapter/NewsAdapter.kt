package dev.rakamin.newsapp.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dev.rakamin.newsapp.DetailActivity
import dev.rakamin.newsapp.R
import dev.rakamin.newsapp.databinding.ItemRowNewsBinding
import dev.rakamin.newsapp.model.Articles
import dev.rakamin.newsapp.utils.Utils

class NewsAdapter(private val listNews: List<Articles>) : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    inner class NewsViewHolder(val binding: ItemRowNewsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(news: Articles) {
            binding.tvNewsTitle.text = news.title
            binding.tvNewsAuthor.text = news.author ?: "Sumber Tidak Diketahui"
            binding.tvPublishedAt.text = Utils.formatNewsDate(news.publishedAt)

            Glide.with(itemView.context)
                .load(news.urlToImage)
                .placeholder(R.drawable.livin_grey)
                .error(R.drawable.livin_grey)
                .into(binding.ivNews)

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, DetailActivity::class.java)
                intent.putExtra(DetailActivity.EXTRA_NEWS, news)
                itemView.context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemRowNewsBinding.inflate(inflater, parent, false)
        return NewsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.bind(listNews[position])
    }

    override fun getItemCount() = listNews.size

    fun setArticles(newArticles: List<Articles>) {
        (listNews as MutableList).clear()
        listNews.addAll(newArticles)
        notifyDataSetChanged()
    }

    fun addArticles(newArticles: List<Articles>) {
        val oldSize = listNews.size
        (listNews as MutableList).addAll(newArticles)
        notifyItemRangeInserted(oldSize, newArticles.size)
    }

}