

package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Post

class PostRepositorySQLiteImpl(
    private val dao: PostDao
) : PostRepository {

    private var posts: List<Post> =
        dao.getAll()

    private val data =
        MutableLiveData(posts)

    override fun getAll(): LiveData<List<Post>> {
        return data
    }

    override fun save(post: Post) {

        val oldId = post.id
        val savedPost = dao.save(post)

        posts = if (oldId == 0L) {

            listOf(savedPost) + posts

        } else {

            posts.map { currentPost ->

                if (currentPost.id == oldId) {
                    savedPost
                } else {
                    currentPost
                }
            }
        }

        data.value = posts
    }

    override fun likeById(id: Long) {

        dao.likeById(id)

        posts = posts.map { post ->

            if (post.id != id) {
                post
            } else {

                post.copy(
                    likedByMe = !post.likedByMe,

                    likes = if (post.likedByMe) {
                        post.likes - 1
                    } else {
                        post.likes + 1
                    }
                )
            }
        }

        data.value = posts
    }

    override fun shareById(id: Long) {

        dao.shareById(id)

        posts = posts.map { post ->

            if (post.id != id) {
                post
            } else {
                post.copy(
                    shares = post.shares + 1
                )
            }
        }

        data.value = posts
    }

    override fun removeById(id: Long) {

        dao.removeById(id)

        posts = posts.filter { post ->
            post.id != id
        }

        data.value = posts
    }
}