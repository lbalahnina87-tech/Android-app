package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity

class PostRepositoryRoomImpl(
    private val dao: PostDao
) : PostRepository {

    private val data = MediatorLiveData<List<Post>>().apply {
        addSource(dao.getAll()) { entities ->
            value = entities.map { entity ->
                entity.toDto()
            }
        }
    }

    override fun getAll(): LiveData<List<Post>> = data

    override fun save(post: Post) {
        dao.save(PostEntity.fromDto(post))
    }

    override fun likeById(id: Long) {
        dao.likeById(id)
    }

    override fun shareById(id: Long) {
        dao.shareById(id)
    }

    override fun removeById(id: Long) {
        dao.removeById(id)
    }
}