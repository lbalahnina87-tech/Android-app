package ru.netology.nmedia.activity

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewmodel.PostViewModel

class MainActivity : AppCompatActivity() {

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        val binding =
            ActivityMainBinding.inflate(layoutInflater)

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

        val viewModel: PostViewModel by viewModels()

        val postEditorLauncher =
            registerForActivityResult(
                PostEditorResultContract
            ) { result ->

                result
                    ?: return@registerForActivityResult

                viewModel.save(
                    id = result.id,
                    content = result.content,
                    video = result.video
                )
            }

        val adapter = PostsAdapter(
            object : OnInteractionListener {

                override fun onLike(post: Post) {
                    viewModel.likeById(post.id)
                }

                override fun onShare(post: Post) {
                    val textToShare = buildString {
                        append(post.content)

                        post.video
                            ?.takeIf { it.isNotBlank() }
                            ?.let { videoUrl ->
                                append("\n\n")
                                append(videoUrl)
                            }
                    }

                    val intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, textToShare)
                        type = "text/plain"
                    }

                    val shareIntent = Intent.createChooser(
                        intent,
                        getString(R.string.chooser_share_post)
                    )

                    startActivity(shareIntent)
                }

                override fun onEdit(post: Post) {
                    postEditorLauncher.launch(
                        PostEditorInput(
                            id = post.id,
                            content = post.content,
                            video = post.video
                        )
                    )
                }

                override fun onRemove(post: Post) {
                    viewModel.removeById(post.id)
                }



                override fun onVideo(post: Post) {
                    val videoUrl =
                        post.video ?: return

                    val intent = Intent(
                        Intent.ACTION_VIEW,
                        videoUrl.toUri()
                    )

                    try {
                        startActivity(intent)
                    } catch (
                        error: ActivityNotFoundException
                    ) {
                        Toast.makeText(
                            this@MainActivity,
                            R.string.no_app_for_video,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        )

        binding.list.adapter = adapter

        viewModel.data.observe(this) { posts ->
            adapter.submitList(posts)
        }

        binding.fab.setOnClickListener {
            postEditorLauncher.launch(
                PostEditorInput()
            )
        }
    }
}