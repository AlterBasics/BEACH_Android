package abs.sf.beach.notification;

import java.util.List;

import abs.ixi.client.core.PacketCollector;
import abs.ixi.client.core.Platform;
import abs.ixi.client.util.StringUtils;
import abs.ixi.client.xmpp.packet.Message;
import abs.ixi.client.xmpp.packet.Packet;
import abs.sf.beach.activity.ChatActivity;
import abs.sf.beach.utils.NotificationUtils;
import abs.sf.client.android.managers.AndroidChatManager;
import abs.sf.client.android.messaging.ChatLine;
import abs.sf.client.android.messaging.ChatLineReceiver;

public class NotificationGenerator implements ChatLineReceiver{
    private ChatActivity chatActivity;

    private static NotificationGenerator instance;

    private  NotificationGenerator() {
        AndroidChatManager chatManager = (AndroidChatManager) Platform.getInstance().getChatManager();
        chatManager.addChatLineReceiver(this);
    }

    public synchronized static NotificationGenerator getInstance() {
        if (instance == null) {
            instance = new NotificationGenerator();
        }

        return instance;
    }

    public void setChatActivity(ChatActivity chatActivity) {
        this.chatActivity = chatActivity;
    }

    public void removeChatActivity() {
        this.chatActivity = null;
    }

    @Override
    public void handleChatLine(final ChatLine chatLine) {
        if(chatActivity == null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    NotificationUtils.show(chatLine.getText(), chatLine, ChatActivity.this);
                }
            });

        } else if(!StringUtils.safeEquals(chatActivity.getJID().getBareJID(), chatLine.getPeerBareJid())){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    NotificationUtils.show(chatLine.getText(), chatLine, ChatActivity.this);
                }
            });
        }
    }

}
