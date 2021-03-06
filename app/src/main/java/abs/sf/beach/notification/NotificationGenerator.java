package abs.sf.beach.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import abs.ixi.client.Platform;
import abs.ixi.client.util.StringUtils;
import abs.sf.beach.activity.ChatActivity;
import abs.sf.beach.core.BasicApplication;
import abs.sf.beach.utils.NotificationUtils;
import abs.sf.client.android.managers.AndroidChatManager;
import abs.sf.client.android.messaging.ChatLine;
import abs.sf.client.android.utils.SFConstants;

public class NotificationGenerator extends BroadcastReceiver {
    private static ChatActivity chatActivity;

    @Override
    public void onReceive(Context context, Intent intent) {

        ChatLine chatLine = (ChatLine) intent.getSerializableExtra(SFConstants.CHATLINE_OBJECT);

        if (chatActivity == null) {
            new NotificationCaller(chatLine).execute();
            sendCMAcknowledgeReceipt(chatLine);

        } else {
            synchronized (chatActivity) {
                if (!StringUtils.safeEquals(chatActivity.getJID().getBareJID(), chatLine.getPeerBareJid())) {
                    new NotificationCaller(chatLine).execute();
                    sendCMAcknowledgeReceipt(chatLine);
                }
            }
        }
    }

    public void sendCMAcknowledgeReceipt(ChatLine chatLine) {
        AndroidChatManager chatManager = (AndroidChatManager) Platform.getInstance().getChatManager();

        if(chatManager != null) {
            chatManager.sendMessageAcknowledgementReceipt(chatLine);
        }
    }

    /**
     * SET currently open {@link ChatActivity}
     *
     * @param cActivity
     */
    public static synchronized void setChatActivity(ChatActivity cActivity) {
        chatActivity = cActivity;
    }

    public static synchronized void removeChatActivity() {
        chatActivity = null;
    }

    private class NotificationCaller extends AsyncTask<Void, Void, Void> {
        private ChatLine chatLine;

        public NotificationCaller(ChatLine chatLine) {
            this.chatLine = chatLine;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            NotificationUtils.show(chatLine, BasicApplication.getContext());
        }

        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }
    }

}
