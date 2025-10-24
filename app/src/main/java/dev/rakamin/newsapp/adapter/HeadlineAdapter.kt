package dev.rakamin.newsapp.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dev.rakamin.newsapp.DetailActivity
import dev.rakamin.newsapp.R
import dev.rakamin.newsapp.databinding.ItemHeadlineCardBinding
import dev.rakamin.newsapp.model.Articles
import dev.rakamin.newsapp.utils.Utils

class HeadlineAdapter(private val headlines: List<Articles>) : RecyclerView.Adapter<HeadlineAdapter.HeadlineViewHolder>() {
    inner class HeadlineViewHolder(val binding: ItemHeadlineCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(headline: Articles) {
            binding.tvNewsTitle.text = headline.title
            binding.tvPublishedAt.text = Utils.formatNewsDate(headline.publishedAt)
            binding.tvNewsAuthor.text = headline.author ?: "Sumber Tidak Diketahui"

            Glide.with(itemView.context)
                .load(headline.urlToImage)
                .placeholder(R.drawable.livin_grey)
                .error(R.drawable.livin_grey)
                .into(binding.ivNews)

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, DetailActivity::class.java)
                intent.putExtra(DetailActivity.EXTRA_NEWS, headline)
                itemView.context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeadlineViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemHeadlineCardBinding.inflate(inflater, parent, false)
        return HeadlineViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HeadlineViewHolder, position: Int) {
        holder.bind(headlines[position])
    }

    override fun getItemCount() = headlines.size
}
