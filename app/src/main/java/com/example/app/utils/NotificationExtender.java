package com.example.app.utils;

import com.onesignal.NotificationExtenderService;
import com.onesignal.OSNotificationReceivedResult;

public class NotificationExtender extends NotificationExtenderService {
    @Override
    protected boolean onNotificationProcessing(OSNotificationReceivedResult notification) {
        return true;
    }
}
