package abs.sf.beach.notification;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import abs.ixi.client.xmpp.packet.IQPushRegistration;
import abs.sf.beach.utils.ApplicationProps;
import abs.sf.beach.utils.SharedPrefs;
import abs.sf.client.android.utils.SharedPrefProxy;

public class FCMIdService extends FirebaseInstanceIdService {
    private static final String TAG = FCMIdService.class.getSimpleName();
 
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        // Storing fcm token
        storeFcmToken(refreshedToken);
    }

    private void storeFcmToken(String token) {
        SharedPrefProxy.getInstance().savePushNotifiactionDetatils(IQPushRegistration.PushNotificationService.FCM, token);
    }
}