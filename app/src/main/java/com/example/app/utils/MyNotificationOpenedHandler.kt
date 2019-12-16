package aioria.com.br.kotlinbaseapp.utils

import android.content.Context
import android.content.Intent
import androidx.navigation.NavDeepLinkBuilder
import com.example.app.R
import com.example.app.ui.MainActivity
import com.github.ajalt.timberkt.d
import com.onesignal.OSNotificationAction
import com.onesignal.OSNotificationOpenResult
import com.onesignal.OneSignal

class MyNotificationOpenedHandler(// This fires when a notification is opened by tapping on it.
        private val mContext: Context) : OneSignal.NotificationOpenedHandler {

    override fun notificationOpened(result: OSNotificationOpenResult) {
        val actionType = result.action.type
        val data = result.notification.payload.additionalData
        val screen: String
        val id: String

        d{"Notification handler"}


        val intent = Intent(mContext, MainActivity::class.java)
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT  or Intent.FLAG_ACTIVITY_NEW_TASK);

                if (data != null) {
                    screen = data.optString("screen", null);
                    if (screen != null) {
                        intent.putExtra("type", screen);
                    }
                    if (actionType == OSNotificationAction.ActionType.ActionTaken){
                        d{"Click - screen $screen"}
                    }
                }

//                if (!AioriaSharedPreferences.getInstance().getStringValue(AioriaSharedPreferences.ACTIVE_USER).isEmpty()) {
//                    mContext.startActivity(intent);
//                }

        val pendingIntent = NavDeepLinkBuilder(mContext)
//            .setGraph(R.navigation.nav_graph_main)
            .setDestination(R.id.timelineFragment)
            .createPendingIntent()

                            mContext.startActivity(intent);

//        val newIntent = Intent(Intent.ACTION_VIEW)
//        newIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT  or Intent.FLAG_ACTIVITY_NEW_TASK);
//
//        newIntent.setData(Uri.parse("vakinha://details/1"))
//        mContext.startActivity(newIntent)





    }
}