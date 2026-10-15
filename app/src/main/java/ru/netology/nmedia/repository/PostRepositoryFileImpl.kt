package ru.netology.nmedia.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.netology.nmedia.dto.Post

class PostRepositoryFileImpl(
    private val context: Context
) : PostRepository {

    private var posts: List<Post> = readPosts()

    private var nextId: Long =
        (posts.maxOfOrNull { it.id } ?: 0L) + 1L

    private val data =
        MutableLiveData(posts)

    override fun getAll(): LiveData<List<Post>> {
        return data
    }

    override fun likeById(id: Long) {
        val updatedPosts = posts.map { post ->

            if (post.id != id) {
                post
            } else {
                val newLikedByMe = !post.likedByMe

                post.copy(
                    likedByMe = newLikedByMe,
                    likes = if (newLikedByMe) {
                        post.likes + 1
                    } else {
                        post.likes - 1
                    }
                )
            }
        }

        updatePosts(updatedPosts)
    }

    override fun shareById(id: Long) {
        val updatedPosts = posts.map { post ->

            if (post.id != id) {
                post
            } else {
                post.copy(
                    shares = post.shares + 1
                )
            }
        }

        updatePosts(updatedPosts)
    }

    override fun save(post: Post) {
        if (post.id == 0L) {
            val newPost = post.copy(
                id = nextId++,
                author = "Me",
                published = "now",
                likedByMe = false,
                likes = 0,
                shares = 0,
                views = 0
            )

            updatePosts(
                listOf(newPost) + posts
            )

            return
        }

        val updatedPosts = posts.map { currentPost ->

            if (currentPost.id != post.id) {
                currentPost
            } else {
                currentPost.copy(
                    content = post.content,
                    video = post.video
                )
            }
        }

        updatePosts(updatedPosts)
    }

    override fun removeById(id: Long) {
        val updatedPosts = posts.filter { post ->
            post.id != id
        }

        updatePosts(updatedPosts)
    }

    private fun updatePosts(
        updatedPosts: List<Post>
    ) {
        posts = updatedPosts
        data.value = posts
        sync()
    }

    private fun sync() {
        context.openFileOutput(
            FILENAME,
            Context.MODE_PRIVATE
        ).bufferedWriter().use { writer ->

            val json = gson.toJson(posts)
            writer.write(json)
        }
    }

    private fun readPosts(): List<Post> {
        val file =
            context.filesDir.resolve(FILENAME)

        if (!file.exists()) {
            return initialPosts()
        }

        return context.openFileInput(FILENAME)
            .bufferedReader()
            .use { reader ->
                gson.fromJson<List<Post>>(
                    reader,
                    postsType
                ) ?: emptyList()
            }
    }

    private fun initialPosts(): List<Post> {
        return listOf(
            Post(
                id = 3L,
                author = "Нетология. Университет интернет-профессий",
                content = "Освоение новой профессии — это не только новые возможности, но и настоящий вызов самому себе. Нужно искать время для занятий, менять привычный распорядок и не бояться ошибок.",
                published = "23 сентября в 10:12",
                likes = 999,
                likedByMe = false,
                shares = 15,
                views = 1_200,
                video = "https://rutube.ru/video/6550a91e7e523f9503bed47e4c46d0cb"
            ),
            Post(
                id = 2L,
                author = "Нетология. Университет интернет-профессий",
                content = "Таймбоксинг — способ навести порядок в календаре. На каждое дело заранее выделяется определённый отрезок времени.",
                published = "22 сентября в 10:14",
                likes = 80,
                likedByMe = false,
                shares = 10,
                views = 850
            ),
            Post(
                id = 1L,
                author = "Нетология. Университет интернет-профессий",
                content = "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению.",
                published = "21 мая в 18:36",
                likes = 10,
                likedByMe = false,
                shares = 5,
                views = 5
            )
        )
    }

    companion object {
        private const val FILENAME =
            "posts.json"

        private val gson = Gson()

        private val postsType =
            TypeToken.getParameterized(
                List::class.java,
                Post::class.java
            ).type
    }
}