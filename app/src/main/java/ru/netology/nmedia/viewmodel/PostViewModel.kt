package ru.netology.nmedia.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryInMemoryImpl

private val emptyPost = Post(
    id = 0L,
    author = "",
    content = "",
    published = ""
)

class PostViewModel : ViewModel() {

    private val repository: PostRepository =
        PostRepositoryInMemoryImpl()

    val data = repository.getAll()

    val edited = MutableLiveData(emptyPost)

    fun save(content: String) {
        edited.value?.let { post ->
            val text = content.trim()

            if (post.content != text) {
                repository.save(
                    post.copy(content = text)
                )
            }
        }

        edited.value = emptyPost
    }

    fun edit(post: Post) {
        edited.value = post
    }

    fun cancelEdit() {
        edited.value = emptyPost
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