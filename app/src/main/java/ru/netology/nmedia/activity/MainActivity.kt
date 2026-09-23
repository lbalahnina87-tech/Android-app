package ru.netology.nmedia.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.viewmodel.PostViewModel

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars =
                insets.getInsets(WindowInsetsCompat.Type.systemBars())

            view.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )

            insets
        }

        val viewModel: PostViewModel by viewModels()

        viewModel.data.observe(this) { post ->
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
            }
        }

        binding.likeIcon.setOnClickListener {
            viewModel.like()
        }

        binding.shareIcon.setOnClickListener {
            viewModel.share()
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
}