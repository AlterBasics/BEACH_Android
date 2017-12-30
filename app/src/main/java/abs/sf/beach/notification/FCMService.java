package abs.sf.beach.notification;

import com.google.firebase.messaging.RemoteMessage;

import abs.sf.client.android.notification.fcm.SFFcmService;


public class FCMService extends SFFcmService{
    private static final String TAG = FCMService.class.getSimpleName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        //handle non SF notifications
    }

}