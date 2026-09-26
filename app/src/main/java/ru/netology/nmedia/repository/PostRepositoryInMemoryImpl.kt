package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.dto.Post

class PostRepositoryInMemoryImpl : PostRepository {

    private var posts = listOf(
        Post(
            id = 3,
            author = "Нетология. Университет интернет-профессий",
            content = "Освоение новой профессии — это не только новые возможности, но и настоящий вызов самому себе. Нужно искать время для занятий, менять привычный распорядок и не бояться ошибок.",
            published = "23 сентября в 10:12",
            likes = 999,
            likedByMe = false,
            shares = 15,
            views = 1_200
        ),
        Post(
            id = 2,
            author = "Нетология. Университет интернет-профессий",
            content = "Таймбоксинг — способ навести порядок в календаре. На каждое дело заранее выделяется определённый отрезок времени.",
            published = "22 сентября в 10:14",
            likes = 80,
            likedByMe = false,
            shares = 10,
            views = 850
        ),
        Post(
            id = 1,
            author = "Нетология. Университет интернет-профессий",
            content = "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов.",
            published = "21 мая в 18:36",
            likes = 10,
            likedByMe = false,
            shares = 5,
            views = 5
        )
    )

    private val data = MutableLiveData(posts)

    override fun getAll(): LiveData<List<Post>> = data

    override fun likeById(id: Long) {
        posts = posts.map { post ->
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

        data.value = posts
    }

    override fun shareById(id: Long) {
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
}