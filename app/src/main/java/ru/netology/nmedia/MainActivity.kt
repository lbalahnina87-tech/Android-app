package ru.netology.nmedia

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ru.netology.nmedia.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
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

        val post = Post(
            id = 1,
            author = getString(R.string.author),
            published = getString(R.string.published),
            content = getString(R.string.post_content),
            likedByMe = false,
            likes = 10,
            shares = 5,
            views = 5
        )

        fun renderPost() {
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

        renderPost()

        binding.likeIcon.setOnClickListener {
            post.likedByMe = !post.likedByMe

            if (post.likedByMe) {
                post.likes++
            } else {
                post.likes--
            }

            renderPost()
        }

        binding.shareIcon.setOnClickListener {
            post.shares++
            renderPost()
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