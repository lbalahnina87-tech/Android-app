package ru.netology.nmedia.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.dto.Post

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val author: String,
    val content: String,
    val published: String,
    val likes: Int = 0,
    val likedByMe: Boolean = false,
    val shares: Int = 0,
    val views: Int = 0,
    val video: String? = null
) {
    fun toDto() = Post(
        id = id,
        author = author,
        content = content,
        published = published,
        likes = likes,
        likedByMe = likedByMe,
        shares = shares,
        views = views,
        video = video
    )

    companion object {
        fun fromDto(dto: Post) = PostEntity(
            id = dto.id,
            author = dto.author,
            content = dto.content,
            published = dto.published,
            likes = dto.likes,
            likedByMe = dto.likedByMe,
            shares = dto.shares,
            views = dto.views,
            video = dto.video
        )
    }
}