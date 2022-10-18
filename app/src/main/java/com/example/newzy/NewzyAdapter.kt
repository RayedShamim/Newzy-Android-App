package com.example.newzy

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.newzy.databinding.NewsListItemBinding
import com.example.newzy.network.Articles

class NewzyAdapter(val onItemClicked: (Articles) -> Unit): ListAdapter<Articles,NewzyAdapter.NewzyViewHolder>(DiffCallback) {

    class NewzyViewHolder(val binding: NewsListItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(articles: Articles) {
            binding.title.text = articles.title
            binding.description.text = articles.description

            val thumbnail = binding.imageView
            val imageUrl = articles.urlToImage

            thumbnail.load(imageUrl) {
                error(R.drawable.ic_baseline_warning_64)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewzyViewHolder {
        val viewHolder = NewzyViewHolder(
            NewsListItemBinding
                .inflate(LayoutInflater.from(parent.context),parent,false)
        )

        viewHolder.itemView.setOnClickListener {
            val position = viewHolder.adapterPosition
            onItemClicked(getItem(position))
        }

        return viewHolder
    }

    override fun onBindViewHolder(holder: NewzyViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        val DiffCallback = object : DiffUtil.ItemCallback<Articles>() {
            override fun areItemsTheSame(oldItem: Articles, newItem: Articles): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Articles, newItem: Articles): Boolean {
                return oldItem.title == newItem.title
            }

        }
    }

}