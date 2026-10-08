package ru.netology.nmedia.activity


import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract

data class PostEditorInput(
    val id: Long = 0L,
    val content: String = "",
    val video: String? = null
)

data class PostEditorResult(
    val id: Long,
    val content: String,
    val video: String?
)

object PostEditorResultContract :
    ActivityResultContract<PostEditorInput, PostEditorResult?>() {

    const val EXTRA_POST_ID =
        "ru.netology.nmedia.extra.POST_ID"

    const val EXTRA_POST_CONTENT =
        "ru.netology.nmedia.extra.POST_CONTENT"

    const val EXTRA_POST_VIDEO =
        "ru.netology.nmedia.extra.POST_VIDEO"

    override fun createIntent(
        context: Context,
        input: PostEditorInput
    ): Intent {
        return Intent(
            context,
            PostActivity::class.java
        ).apply {
            putExtra(EXTRA_POST_ID, input.id)
            putExtra(EXTRA_POST_CONTENT, input.content)
            putExtra(EXTRA_POST_VIDEO, input.video)
        }
    }

    override fun parseResult(
        resultCode: Int,
        intent: Intent?
    ): PostEditorResult? {

        if (resultCode != Activity.RESULT_OK) {
            return null
        }

        val content =
            intent?.getStringExtra(EXTRA_POST_CONTENT)
                ?: return null

        return PostEditorResult(
            id = intent.getLongExtra(
                EXTRA_POST_ID,
                0L
            ),
            content = content,
            video = intent.getStringExtra(
                EXTRA_POST_VIDEO
            )
        )
    }
}