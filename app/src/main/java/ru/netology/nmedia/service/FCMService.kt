package ru.netology.nmedia.service

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import ru.netology.nmedia.R
import kotlin.random.Random

class FCMService : FirebaseMessagingService() {

    private val gson = Gson()

    override fun onCreate() {
        super.onCreate()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.channel_remote_name),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description =
                    getString(R.string.channel_remote_description)
            }

            val manager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager

            manager.createNotificationChannel(channel)
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        val actionName = message.data[ACTION_KEY]
        val json = message.data[CONTENT_KEY] ?: return

        val action = Action.entries.firstOrNull { action ->
            action.name == actionName
        }

        if (action == null) {
            Log.w(TAG, "Unknown push action: $actionName")
            return
        }

        when (action) {
            Action.LIKE -> {
                val like = runCatching {
                    gson.fromJson(json, Like::class.java)
                }.getOrElse { error ->
                    Log.e(TAG, "Invalid LIKE content", error)
                    return
                }

                handleLike(like)
            }

            Action.NEW_POST -> {
                val post = runCatching {
                    gson.fromJson(json, NewPost::class.java)
                }.getOrElse { error ->
                    Log.e(TAG, "Invalid NEW_POST content", error)
                    return
                }

                handleNewPost(post)
            }
        }
    }

    override fun onNewToken(token: String) {
        Log.d(TAG, "New FCM token: $token")
    }

    private fun handleLike(like: Like) {
        val notification = NotificationCompat.Builder(
            this,
            CHANNEL_ID
        )
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(
                getString(
                    R.string.notification_user_liked,
                    like.userName,
                    like.postAuthor
                )
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        showNotification(notification)
    }

    private fun handleNewPost(post: NewPost) {
        val notification = NotificationCompat.Builder(
            this,
            CHANNEL_ID
        )
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(
                getString(
                    R.string.notification_new_post,
                    post.author
                )
            )
            .setContentText(post.content)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(post.content)
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        showNotification(notification)
    }

    private fun showNotification(notification: Notification) {
        val permissionGranted =
            Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                    checkSelfPermission(
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED

        if (permissionGranted) {
            NotificationManagerCompat.from(this).notify(
                Random.nextInt(100_000),
                notification
            )
        }
    }

    companion object {
        private const val TAG = "FCMService"
        private const val CHANNEL_ID = "remote"
        private const val ACTION_KEY = "action"
        private const val CONTENT_KEY = "content"
    }
}

enum class Action {
    LIKE,
    NEW_POST
}

data class Like(
    val userId: Long,
    val userName: String,
    val postId: Long,
    val postAuthor: String
)

data class NewPost(
    val postId: Long,
    val author: String,
    val content: String
)