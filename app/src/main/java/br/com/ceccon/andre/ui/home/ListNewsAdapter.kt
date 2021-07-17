package br.com.ceccon.andre.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import br.com.ceccon.andre.R
import br.com.ceccon.andre.data.model.Article
import br.com.ceccon.andre.databinding.NewsItemBinding
import br.com.ceccon.andre.utils.getText
import br.com.ceccon.andre.utils.toDate
import coil.load
import coil.transform.RoundedCornersTransformation

class ListNewsAdapter(
    val onClickArticle: (Article) -> Unit
) : ListAdapter<Article, ListNewsAdapter.NewsViewHolder>(diffCalback) {

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): NewsViewHolder = NewsViewHolder.create(parent = parent)

    override fun onBindViewHolder(
        holder: NewsViewHolder, position: Int
    ) {
        holder.bind(article = getItem(position))
        holder.itemView.setOnClickListener { onClickArticle.invoke(getItem(position)) }
    }

    companion object {
        private val diffCalback = object : DiffUtil.ItemCallback<Article>() {
            override fun areItemsTheSame(
                oldItem: Article, newItem: Article
            ): Boolean = oldItem.uId == newItem.uId

            override fun areContentsTheSame(
                oldItem: Article, newItem: Article
            ): Boolean = oldItem == newItem
        }
    }

    class NewsViewHolder(
        private val itemBinding: NewsItemBinding
    ) : RecyclerView.ViewHolder(itemBinding.root) {

        private var urlImagem: String = String()

        fun bind(article: Article) {
            itemBinding.run {
                tvTitle.text = article.title.getText()
                tvAuthor.text = article.author.getText()
                tvDescription.text = article.description.getText()
                tvPublishedAt.text = article.publishedAt?.toDate().getText()

                urlImagem = article.urlToImage.toString()

                if (urlImagem.isNotEmpty() && urlImagem.isNotBlank()) {
                    itemBinding.ivWine.load(urlImagem) {
                        crossfade(true)
                        error(R.drawable.newspaper)
                        placeholder(R.drawable.newspaper)
                        transformations(RoundedCornersTransformation())
                    }
                }
            }
        }

        companion object {
            fun create(
                parent: ViewGroup
            ): NewsViewHolder {
                val itemBinding = NewsItemBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )

                return NewsViewHolder(itemBinding = itemBinding)
            }
        }
    }
}