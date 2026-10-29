

package ru.netology.nmedia.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dao.PostDaoImpl

class AppDb private constructor(
    db: SQLiteDatabase
) {

    val postDao: PostDao = PostDaoImpl(db)

    companion object {

        @Volatile
        private var instance: AppDb? = null

        fun getInstance(context: Context): AppDb {

            return instance ?: synchronized(this) {

                instance ?: AppDb(
                    buildDatabase(
                        context.applicationContext
                    )
                ).also {
                    instance = it
                }
            }
        }

        private fun buildDatabase(
            context: Context
        ): SQLiteDatabase {

            return DbHelper(
                context = context,
                dbVersion = 1,
                dbName = "app.db",
                ddls = arrayOf(PostDaoImpl.DDL)
            ).writableDatabase
        }
    }
}

class DbHelper(
    context: Context,
    dbVersion: Int,
    dbName: String,
    private val ddls: Array<String>
) : SQLiteOpenHelper(
    context,
    dbName,
    null,
    dbVersion
) {

    override fun onCreate(db: SQLiteDatabase) {

        ddls.forEach { ddl ->
            db.execSQL(ddl)
        }
    }

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {
        // Сейчас используется первая версия базы.
        // Миграции понадобятся при изменении её структуры.
    }
}