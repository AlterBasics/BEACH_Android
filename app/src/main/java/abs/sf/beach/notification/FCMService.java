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
        if (remoteMessage == null) {
            return;
        }

        if(remoteMessage.getData() == null) {
            return;
        }

        Map<String, String> dataMap = remoteMessage.getData();

        String sfNotificationCode = dataMap.get(SFNotifiactionCode.SF_NOTIFICATION_CODE);

        if (!StringUtils.isNullOrEmpty(sfNotificationCode)) {
            int code = Integer.parseInt(sfNotificationCode);

            if (code == SFNotifiactionCode.NEW_MESSAGE.val()) {
                String fromJID = dataMap.get("from_jid");
                String message = dataMap.get("message");
                String messageId = dataMap.get("message_id");

                try {
                    JID jid = new JID(fromJID);

                    ChatLine chatLine = new ChatLine(messageId, jid.getBareJID(), ChatLine.Direction.RECEIVE, ChatLine.ContentType.TEXT);
                    chatLine.setDeliveryStatus(-1);
                    chatLine.setText(message);
                    chatLine.setPeerResource(jid.getResource());
                    chatLine.setPeerName(jid.getNode());

                    DbManager.getInstance().addToChatStore(chatLine);

                    if(DbManager.getInstance().conversationExists(jid.getBareJID())) {
                        DbManager.getInstance().updateConversation(chatLine);

                    } else {
                        Conversation conv2 = new Conversation(chatLine);
                        conv2.setUnreadChatLines(1);
                        DbManager.getInstance().addConversation(conv2);
                    }


                    NotificationUtils.show(chatLine.getText(), chatLine, this);


                } catch (InvalidJabberId invalidJabberId) {
                    //Swallow Exception
                }
            }

        }
    }

}