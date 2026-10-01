package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.viewmodel.PostViewModel

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        val binding =
            ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(
            binding.root
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

        val viewModel: PostViewModel by viewModels()

        val adapter = PostsAdapter(
            object : OnInteractionListener {

                override fun onLike(post: Post) {
                    viewModel.likeById(post.id)
                }

                override fun onShare(post: Post) {
                    viewModel.shareById(post.id)
                }

                override fun onEdit(post: Post) {
                    viewModel.edit(post)
                }

                override fun onRemove(post: Post) {
                    viewModel.removeById(post.id)
                }
            }
        )

        binding.list.adapter = adapter

        viewModel.data.observe(this) { posts ->
            adapter.submitList(posts)
        }

        viewModel.edited.observe(this) { post ->
            val isEditing = post.id != 0L

            binding.editingGroup.visibility =
                if (isEditing) {
                    View.VISIBLE
                } else {
                    View.GONE
                }

            if (isEditing) {
                binding.editingContent.text = post.content
                binding.content.setText(post.content)

                binding.content.setSelection(
                    binding.content.text.length
                )

                AndroidUtils.showKeyboard(
                    binding.content
                )
            }
        }

        binding.cancelEdit.setOnClickListener {
            viewModel.cancelEdit()

            binding.content.setText("")
            binding.content.clearFocus()

            AndroidUtils.hideKeyboard(
                binding.content
            )
        }

        binding.save.setOnClickListener {
            val content =
                binding.content.text.toString()

            if (content.isBlank()) {
                Toast.makeText(
                    this,
                    getString(
                        R.string.error_empty_content
                    ),
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            viewModel.save(content)

            binding.content.setText("")
            binding.content.clearFocus()

            AndroidUtils.hideKeyboard(
                binding.content
            )
        }
    }
}