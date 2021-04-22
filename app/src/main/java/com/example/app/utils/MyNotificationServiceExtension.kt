package aioria.com.br.kotlinbaseapp.utils

import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.NavDeepLinkBuilder
import com.example.app.R
import com.github.ajalt.timberkt.d
import com.onesignal.OSNotificationReceivedEvent
import com.onesignal.OneSignal


class MyNotificationServiceExtension() : OneSignal.OSRemoteNotificationReceivedHandler {



    override fun remoteNotificationReceived(
        context: Context?,
        notificationReceivedEvent: OSNotificationReceivedEvent?
    ) {
        d{"RECEIVED custom"}
        var screen: String?=null
        var programID: String?=null
        var lessonID: String?=null
        val notification = notificationReceivedEvent!!.notification
        val data = notification.additionalData
        if (data != null) {
            screen = data.optString("screen", null);
            programID = data.optString("product_id", null);
            lessonID = data.optString("lessonID", null);
        }

        val mutableNotification = notification.mutableCopy()
        mutableNotification.setExtender { builder: NotificationCompat.Builder ->
            builder.setColor(
                ContextCompat.getColor(context!!, R.color.colorPrimary)
            )

            builder.setPriority(NotificationCompat.PRIORITY_HIGH)
            builder.setCategory(NotificationCompat.CATEGORY_PROMO)
//            if(screen.isNullOrEmpty() == false){
//                builder.setContentIntent(buildPendingIntentFromNavigation(context, screen, programID, lessonID))
//            }else{
//                builder
//            }
        }

        notificationReceivedEvent.complete(mutableNotification);
    }

}