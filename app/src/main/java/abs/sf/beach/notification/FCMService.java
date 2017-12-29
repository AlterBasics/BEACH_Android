package abs.sf.beach.notification;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import abs.ixi.client.util.DateUtils;
import abs.ixi.client.util.StringUtils;
import abs.ixi.client.xmpp.InvalidJabberId;
import abs.ixi.client.xmpp.JID;
import abs.sf.beach.utils.NotificationUtils;
import abs.sf.client.android.db.DbManager;
import abs.sf.client.android.messaging.ChatLine;
import abs.sf.client.android.messaging.Conversation;
import abs.sf.client.android.utils.SFNotifiactionCode;

public class FCMService extends FirebaseMessagingService {
    private static final String TAG = FCMService.class.getSimpleName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

    }

}