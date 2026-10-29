

package ru.netology.nmedia.dao

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import ru.netology.nmedia.dto.Post

class PostDaoImpl(
    private val db: SQLiteDatabase
) : PostDao {

    companion object {

        val DDL = """
            CREATE TABLE ${PostColumns.TABLE} (
                ${PostColumns.COLUMN_ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${PostColumns.COLUMN_AUTHOR} TEXT NOT NULL,
                ${PostColumns.COLUMN_CONTENT} TEXT NOT NULL,
                ${PostColumns.COLUMN_PUBLISHED} TEXT NOT NULL,
                ${PostColumns.COLUMN_LIKED_BY_ME} INTEGER NOT NULL DEFAULT 0,
                ${PostColumns.COLUMN_LIKES} INTEGER NOT NULL DEFAULT 0,
                ${PostColumns.COLUMN_SHARES} INTEGER NOT NULL DEFAULT 0,
                ${PostColumns.COLUMN_VIEWS} INTEGER NOT NULL DEFAULT 0,
                ${PostColumns.COLUMN_VIDEO} TEXT
            );
        """.trimIndent()
    }

    object PostColumns {

        const val TABLE = "posts"

        const val COLUMN_ID = "id"
        const val COLUMN_AUTHOR = "author"
        const val COLUMN_CONTENT = "content"
        const val COLUMN_PUBLISHED = "published"
        const val COLUMN_LIKED_BY_ME = "likedByMe"
        const val COLUMN_LIKES = "likes"
        const val COLUMN_SHARES = "shares"
        const val COLUMN_VIEWS = "views"
        const val COLUMN_VIDEO = "video"

        val ALL_COLUMNS = arrayOf(
            COLUMN_ID,
            COLUMN_AUTHOR,
            COLUMN_CONTENT,
            COLUMN_PUBLISHED,
            COLUMN_LIKED_BY_ME,
            COLUMN_LIKES,
            COLUMN_SHARES,
            COLUMN_VIEWS,
            COLUMN_VIDEO
        )
    }

    override fun getAll(): List<Post> {

        val posts = mutableListOf<Post>()

        db.query(
            PostColumns.TABLE,
            PostColumns.ALL_COLUMNS,
            null,
            null,
            null,
            null,
            "${PostColumns.COLUMN_ID} DESC"
        ).use { cursor ->

            while (cursor.moveToNext()) {
                posts.add(map(cursor))
            }
        }

        return posts
    }

    override fun save(post: Post): Post {

        val values = ContentValues().apply {
            put(PostColumns.COLUMN_CONTENT, post.content)

            if (post.video == null) {
                putNull(PostColumns.COLUMN_VIDEO)
            } else {
                put(PostColumns.COLUMN_VIDEO, post.video)
            }
        }

        val id = if (post.id == 0L) {

            values.apply {
                put(PostColumns.COLUMN_AUTHOR, "Me")
                put(PostColumns.COLUMN_PUBLISHED, "now")
                put(PostColumns.COLUMN_LIKED_BY_ME, 0)
                put(PostColumns.COLUMN_LIKES, 0)
                put(PostColumns.COLUMN_SHARES, 0)
                put(PostColumns.COLUMN_VIEWS, 0)
            }

            db.insert(
                PostColumns.TABLE,
                null,
                values
            ).also { newId ->
                check(newId != -1L) {
                    "Не удалось сохранить публикацию"
                }
            }

        } else {

            db.update(
                PostColumns.TABLE,
                values,
                "${PostColumns.COLUMN_ID} = ?",
                arrayOf(post.id.toString())
            )

            post.id
        }

        db.query(
            PostColumns.TABLE,
            PostColumns.ALL_COLUMNS,
            "${PostColumns.COLUMN_ID} = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        ).use { cursor ->

            check(cursor.moveToFirst()) {
                "Сохранённая публикация не найдена"
            }

            return map(cursor)
        }
    }

    override fun likeById(id: Long) {

        db.execSQL(
            """
                UPDATE ${PostColumns.TABLE}
                SET
                    ${PostColumns.COLUMN_LIKES} =
                        ${PostColumns.COLUMN_LIKES} +
                        CASE
                            WHEN ${PostColumns.COLUMN_LIKED_BY_ME} = 1
                            THEN -1
                            ELSE 1
                        END,
                    ${PostColumns.COLUMN_LIKED_BY_ME} =
                        CASE
                            WHEN ${PostColumns.COLUMN_LIKED_BY_ME} = 1
                            THEN 0
                            ELSE 1
                        END
                WHERE ${PostColumns.COLUMN_ID} = ?;
            """.trimIndent(),
            arrayOf(id)
        )
    }

    override fun shareById(id: Long) {

        db.execSQL(
            """
                UPDATE ${PostColumns.TABLE}
                SET ${PostColumns.COLUMN_SHARES} =
                    ${PostColumns.COLUMN_SHARES} + 1
                WHERE ${PostColumns.COLUMN_ID} = ?;
            """.trimIndent(),
            arrayOf(id)
        )
    }

    override fun removeById(id: Long) {

        db.delete(
            PostColumns.TABLE,
            "${PostColumns.COLUMN_ID} = ?",
            arrayOf(id.toString())
        )
    }

    private fun map(cursor: Cursor): Post {

        return with(cursor) {

            Post(
                id = getLong(
                    getColumnIndexOrThrow(
                        PostColumns.COLUMN_ID
                    )
                ),

                author = getString(
                    getColumnIndexOrThrow(
                        PostColumns.COLUMN_AUTHOR
                    )
                ),

                content = getString(
                    getColumnIndexOrThrow(
                        PostColumns.COLUMN_CONTENT
                    )
                ),

                published = getString(
                    getColumnIndexOrThrow(
                        PostColumns.COLUMN_PUBLISHED
                    )
                ),

                likedByMe = getInt(
                    getColumnIndexOrThrow(
                        PostColumns.COLUMN_LIKED_BY_ME
                    )
                ) != 0,

                likes = getInt(
                    getColumnIndexOrThrow(
                        PostColumns.COLUMN_LIKES
                    )
                ),

                shares = getInt(
                    getColumnIndexOrThrow(
                        PostColumns.COLUMN_SHARES
                    )
                ),

                views = getInt(
                    getColumnIndexOrThrow(
                        PostColumns.COLUMN_VIEWS
                    )
                ),

                video = getString(
                    getColumnIndexOrThrow(
                        PostColumns.COLUMN_VIDEO
                    )
                )
            )
        }
    }
}