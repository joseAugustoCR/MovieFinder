package aioria.com.br.kotlinbaseapp.utils

import android.content.Context
import android.util.Log
import com.example.app.utils.NotificationHelper
import com.github.ajalt.timberkt.d
import com.onesignal.OSNotification
import com.onesignal.OSNotificationAction
import com.onesignal.OneSignal

class MyNotificationReceivedHandler(private val mContext: Context) : OneSignal.NotificationReceivedHandler {
    override fun notificationReceived(notification: OSNotification) {
        val data = notification.payload.additionalData
        val customKey: String
        var screen: String?=null
        var campaignid: String?=null
        val `object`: String
        val result: String

        val title = notification.payload.title
        val msg = notification.payload.body
        val url = notification.payload.launchURL

        if (data != null) {
            screen = data.optString("screen", null);
            campaignid = data.optString("campaignid", null);
        }

        d{"NOTIFICATION_RECEIVED title=$title msg=$msg screen=$screen campaignid=$campaignid"}

        NotificationHelper(mContext).sendLocalNotification(
            title =  title,
            message =  msg,
            screen = screen,
            campaignid = campaignid,
            url = url
        )

    }
}