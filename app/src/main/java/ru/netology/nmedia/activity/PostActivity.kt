package ru.netology.nmedia.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.ActivityPostBinding
import ru.netology.nmedia.util.AndroidUtils

class PostActivity : AppCompatActivity() {

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        val binding =
            ActivityPostBinding.inflate(layoutInflater)

        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(
            binding.main
        ) { view, insets ->

            val systemBars =
                insets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
                )

            view.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )

            insets
        }

        val postId = intent.getLongExtra(
            PostEditorResultContract.EXTRA_POST_ID,
            0L
        )

        val oldContent = intent.getStringExtra(
            PostEditorResultContract.EXTRA_POST_CONTENT
        ).orEmpty()

        val oldVideo = intent.getStringExtra(
            PostEditorResultContract.EXTRA_POST_VIDEO
        ).orEmpty()

        binding.screenTitle.setText(
            if (postId == 0L) {
                R.string.new_post
            } else {
                R.string.edit_post
            }
        )

        binding.postContent.setText(oldContent)
        binding.postVideo.setText(oldVideo)

        binding.postContent.setSelection(
            binding.postContent.text?.length ?: 0
        )

        AndroidUtils.showKeyboard(
            binding.postContent
        )

        binding.save.setOnClickListener {

            val content =
                binding.postContent.text
                    ?.toString()
                    ?.trim()
                    .orEmpty()

            val video =
                binding.postVideo.text
                    ?.toString()
                    ?.trim()
                    ?.ifBlank { null }

            if (content.isBlank()) {
                Toast.makeText(
                    this,
                    R.string.error_empty_content,
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            val resultIntent = Intent().apply {
                putExtra(
                    PostEditorResultContract.EXTRA_POST_ID,
                    postId
                )

                putExtra(
                    PostEditorResultContract.EXTRA_POST_CONTENT,
                    content
                )

                putExtra(
                    PostEditorResultContract.EXTRA_POST_VIDEO,
                    video
                )
            }

            setResult(
                Activity.RESULT_OK,
                resultIntent
            )

            finish()
        }
    }
}