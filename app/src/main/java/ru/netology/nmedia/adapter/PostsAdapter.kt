package ru.netology.nmedia.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.dto.Post

interface OnInteractionListener {

    fun onLike(post: Post) {}

    fun onShare(post: Post) {}

    fun onEdit(post: Post) {}

    fun onRemove(post: Post) {}
}

class PostsAdapter(
    private val onInteractionListener: OnInteractionListener
) : ListAdapter<Post, PostViewHolder>(PostDiffCallback) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PostViewHolder {

        val binding = CardPostBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return PostViewHolder(
            binding = binding,
            onInteractionListener = onInteractionListener
        )
    }

    override fun onBindViewHolder(
        holder: PostViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }
}

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onInteractionListener: OnInteractionListener
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(post: Post) {
        with(binding) {

            author.text = post.author
            published.text = post.published
            content.text = post.content

            likesCount.text = formatCount(post.likes)
            sharesCount.text = formatCount(post.shares)
            viewsCount.text = formatCount(post.views)

            likeIcon.setImageResource(
                if (post.likedByMe) {
                    R.drawable.baseline_favorite_24
                } else {
                    R.drawable.baseline_favorite_border_24
                }
            )

            likeIcon.setOnClickListener {
                onInteractionListener.onLike(post)
            }

            shareIcon.setOnClickListener {
                onInteractionListener.onShare(post)
            }

            menu.setOnClickListener { view ->

                PopupMenu(view.context, view).apply {

                    inflate(R.menu.options_post)

                    setOnMenuItemClickListener { item ->

                        when (item.itemId) {

                            R.id.edit -> {
                                onInteractionListener.onEdit(post)
                                true
                            }

                            R.id.remove -> {
                                onInteractionListener.onRemove(post)
                                true
                            }

                            else -> false
                        }
                    }
                }.show()
            }
        }
    }
}

object PostDiffCallback : DiffUtil.ItemCallback<Post>() {

    override fun areItemsTheSame(
        oldItem: Post,
        newItem: Post
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: Post,
        newItem: Post
    ): Boolean {
        return oldItem == newItem
    }
}

private fun formatCount(count: Int): String {
    return when {

        count < 1_000 -> {
            count.toString()
        }

        count < 10_000 -> {
            val thousands = count / 1_000
            val hundreds = count % 1_000 / 100

            if (hundreds == 0) {
                "${thousands}K"
            } else {
                "${thousands}.${hundreds}K"
            }
        }

        count < 1_000_000 -> {
            "${count / 1_000}K"
        }

        else -> {
            val millions = count / 1_000_000
            val hundredThousands =
                count % 1_000_000 / 100_000

            if (hundredThousands == 0) {
                "${millions}M"
            } else {
                "${millions}.${hundredThousands}M"
            }
        }
    }
}