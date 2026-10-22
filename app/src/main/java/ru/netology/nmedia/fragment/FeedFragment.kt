package ru.netology.nmedia.fragment

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewmodel.PostViewModel

class FeedFragment : Fragment() {

    private val viewModel: PostViewModel
            by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding =
            FragmentFeedBinding.inflate(
                inflater,
                container,
                false
            )

        val adapter = PostsAdapter(
            object : OnInteractionListener {

                override fun onOpen(post: Post) {
                    val arguments = Bundle().apply {
                        putLong(
                            PostDetailsFragment.ARG_POST_ID,
                            post.id
                        )
                    }

                    findNavController().navigate(
                        R.id.action_feedFragment_to_postDetailsFragment,
                        arguments
                    )
                }

                override fun onLike(post: Post) {
                    viewModel.likeById(post.id)
                }

                override fun onShare(post: Post) {
                    sharePost(post)
                }

                override fun onEdit(post: Post) {
                    openEditor(post)
                }

                override fun onRemove(post: Post) {
                    viewModel.removeById(post.id)
                }

                override fun onVideo(post: Post) {
                    openVideo(post)
                }
            }
        )

        binding.list.adapter = adapter

        viewModel.data.observe(
            viewLifecycleOwner
        ) { posts ->
            adapter.submitList(posts)
        }

        binding.fab.setOnClickListener {
            findNavController().navigate(
                R.id.action_feedFragment_to_postEditorFragment
            )
        }

        return binding.root
    }

    private fun openEditor(post: Post) {
        val arguments = Bundle().apply {
            putLong(
                PostEditorFragment.ARG_POST_ID,
                post.id
            )

            putString(
                PostEditorFragment.ARG_POST_CONTENT,
                post.content
            )

            putString(
                PostEditorFragment.ARG_POST_VIDEO,
                post.video
            )
        }

        findNavController().navigate(
            R.id.action_feedFragment_to_postEditorFragment,
            arguments
        )
    }

    private fun sharePost(post: Post) {
        viewModel.shareById(post.id)

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
            putExtra(
                Intent.EXTRA_TEXT,
                textToShare
            )
            type = "text/plain"
        }

        startActivity(
            Intent.createChooser(
                intent,
                getString(
                    R.string.chooser_share_post
                )
            )
        )
    }

    private fun openVideo(post: Post) {
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
                requireContext(),
                R.string.no_app_for_video,
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}