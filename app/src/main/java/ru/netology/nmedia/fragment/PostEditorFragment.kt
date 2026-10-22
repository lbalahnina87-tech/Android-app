package ru.netology.nmedia.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentPostEditorBinding
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.viewmodel.PostViewModel

class PostEditorFragment : Fragment() {

    companion object {
        const val ARG_POST_ID =
            "postId"

        const val ARG_POST_CONTENT =
            "postContent"

        const val ARG_POST_VIDEO =
            "postVideo"
    }

    private val viewModel: PostViewModel
            by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding =
            FragmentPostEditorBinding.inflate(
                inflater,
                container,
                false
            )

        val postId =
            arguments?.getLong(
                ARG_POST_ID
            ) ?: 0L

        val oldContent =
            arguments?.getString(
                ARG_POST_CONTENT
            ).orEmpty()

        val oldVideo =
            arguments?.getString(
                ARG_POST_VIDEO
            ).orEmpty()

        binding.screenTitle.setText(
            if (postId == 0L) {
                R.string.new_post
            } else {
                R.string.edit_post
            }
        )

        binding.postContent.setText(
            oldContent
        )

        binding.postVideo.setText(
            oldVideo
        )

        binding.postContent.setSelection(
            binding.postContent.text
                ?.length ?: 0
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
                    requireContext(),
                    R.string.error_empty_content,
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            viewModel.save(
                id = postId,
                content = content,
                video = video
            )

            AndroidUtils.hideKeyboard(
                binding.root
            )

            findNavController().navigateUp()
        }

        return binding.root
    }
}