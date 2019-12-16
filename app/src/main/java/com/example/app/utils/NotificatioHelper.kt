package com.example.app.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat
import com.example.app.R


class NotificationHelper(val context: Context) {

    companion object {
        private const val CHANNEL_ID_LETTER = "channel_navigation"
        private const val CHANNEL_NAME_LETTER = "navigation"
        private const val EXTRA_LETTER = "navigation"
        private const val NOTIFICATION_ID_LETTER = 1
    }

//  private val gson by lazy { Gson() }

    fun sendLocalNotification(title: String?, message:String?="",  screen:String?="", campaignid:String?="", url: String?="") {
        var pendingIntent =buildPendingIntentFromNavigation(screen, campaignid)
        if(url.isNullOrEmpty() == false){
            pendingIntent = buildBrowserPendingIntent(url)
        }
        val notification = buildNotification(title, message, pendingIntent)

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_ID_LETTER,
                    CHANNEL_NAME_LETTER,
                    NotificationManager.IMPORTANCE_HIGH
                )
            )
        }
        notificationManager.notify(NOTIFICATION_ID_LETTER, notification)
    }

    private fun buildNotification(
        title: String?,
        message: String?="",
        pendingIntent: PendingIntent?=null
    ): Notification? {
        return NotificationCompat.Builder(context, CHANNEL_ID_LETTER)
            .let {
                if(title == null){
                    it.setContentTitle("Vakinha")
                }else{
                    it.setContentTitle(title)

                }
            }
            .let {
                if(message.isNullOrEmpty() == false){
                    it.setContentText(message)
                    it.setStyle(
                        NotificationCompat.BigTextStyle()
                            .bigText(message)
                    )
                }else{
                    it
                }
            }
            .setSmallIcon(android.R.drawable.sym_def_app_icon)
            .setAutoCancel(true)
            .let {
                if(pendingIntent != null){
                    it.setContentIntent(pendingIntent)
                }else{
                    it
                }
            }

            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .build()
    }

    private fun buildPendingIntentFromNavigation(screen: String?, campaignid: String?, url:String=""): PendingIntent? {
//        return NavDeepLinkBuilder(context)
//            .let {
//                //without screen specified
//                if(screen.isNullOrEmpty()){
//                    it.setGraph(R.navigation.nav_graph)
//                    it.setDestination(R.id.mainFragment)
//
//                    //to details
//                }else if(screen.equals("campaigns") && campaignid.isNullOrEmpty() == false){
//                    val id = campaignid.toInt()
//                    it.setGraph(R.navigation.nav_graph_main)
//                    it.setDestination(R.id.campaignDetailsFragment)
//                    val campaign = Campaign(id = id)
//                    it.setArguments(bundleOf("campaign" to campaign))
//
//                    //to my campaigns
//                }else if(screen.equals("mycampaigns") ){
//                    it.setGraph(R.navigation.nav_graph_main)
//                    it.setDestination(R.id.myCampaignsFragment)
//
//                }else{
//                    it.setGraph(R.navigation.nav_graph)
//                    it.setDestination(R.id.mainFragment)
//                }
//            }
//            .createPendingIntent()
        return null
    }


    fun buildBrowserPendingIntent(url: String) : PendingIntent{
        val notificationIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        val contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0)
        return contentIntent
    }

}