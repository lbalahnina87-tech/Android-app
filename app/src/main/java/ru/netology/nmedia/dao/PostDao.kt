package ru.netology.nmedia.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import ru.netology.nmedia.entity.PostEntity

@Dao
interface PostDao {

    @Query("SELECT * FROM posts ORDER BY id DESC")
    fun getAll(): LiveData<List<PostEntity>>

    @Insert
    fun insert(post: PostEntity)

    @Query(
        """
        UPDATE posts
        SET content = :content,
            video = :video
        WHERE id = :id
        """
    )
    fun updateContentById(
        id: Long,
        content: String,
        video: String?
    )

    fun save(post: PostEntity) {
        if (post.id == 0L) {
            insert(
                post.copy(
                    author = "Me",
                    published = "now",
                    likes = 0,
                    likedByMe = false,
                    shares = 0,
                    views = 0
                )
            )
        } else {
            updateContentById(
                id = post.id,
                content = post.content,
                video = post.video
            )
        }
    }

    @Query(
        """
        UPDATE posts
        SET likes = likes +
            CASE WHEN likedByMe THEN -1 ELSE 1 END,
            likedByMe =
            CASE WHEN likedByMe THEN 0 ELSE 1 END
        WHERE id = :id
        """
    )
    fun likeById(id: Long)

    @Query(
        """
        UPDATE posts
        SET shares = shares + 1
        WHERE id = :id
        """
    )
    fun shareById(id: Long)

    @Query("DELETE FROM posts WHERE id = :id")
    fun removeById(id: Long)
}