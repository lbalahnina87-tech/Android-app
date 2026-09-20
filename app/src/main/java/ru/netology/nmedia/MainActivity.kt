package ru.netology.nmedia

import android.content.res.ColorStateList
import android.graphics.Color
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
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            view.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )

            insets
        }

        var liked = false
        var likes = 10
        var shares = 10
        val views = 5

        fun updateStatistics() {
            binding.likesCount.text = formatCount(likes)
            binding.sharesCount.text = formatCount(shares)
            binding.viewsCount.text = formatCount(views)
        }

        updateStatistics()

        binding.likeIcon.setOnClickListener {
            liked = !liked

            if (liked) {
                likes++
                binding.likeIcon.setImageResource(
                    R.drawable.baseline_favorite_24
                )
                binding.likeIcon.imageTintList =
                    ColorStateList.valueOf(Color.parseColor("#E91E63"))
            } else {
                likes--
                binding.likeIcon.setImageResource(
                    R.drawable.baseline_favorite_border_24
                )
                binding.likeIcon.imageTintList =
                    ColorStateList.valueOf(Color.parseColor("#757575"))
            }

            updateStatistics()
        }

        binding.shareIcon.setOnClickListener {
            shares++
            updateStatistics()
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
                val hundredThousands = count % 1_000_000 / 100_000

                if (hundredThousands == 0) {
                    "${millions}M"
                } else {
                    "${millions}.${hundredThousands}M"
                }
            }
        }
    }
}