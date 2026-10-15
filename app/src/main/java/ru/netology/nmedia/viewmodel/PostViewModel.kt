package ru.netology.nmedia.viewmodel

import androidx.lifecycle.ViewModel
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryInMemoryImpl

class PostViewModel : ViewModel() {

    private val repository: PostRepository =
        PostRepositoryInMemoryImpl()

    val data = repository.getAll()

    fun save(
        id: Long,
        content: String,
        video: String?
    ) {
        repository.save(
            Post(
                id = id,
                author = "",
                content = content.trim(),
                published = "",
                video = video
                    ?.trim()
                    ?.ifBlank { null }
            )
        )
    }

    fun likeById(id: Long) {
        repository.likeById(id)
    }

    fun shareById(id: Long) {
        repository.shareById(id)
    }

    fun removeById(id: Long) {
        repository.removeById(id)
    }
}