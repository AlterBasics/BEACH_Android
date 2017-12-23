package abs.sf.beach.notification;

import android.os.AsyncTask;

import abs.ixi.client.core.Platform;
import abs.ixi.client.util.StringUtils;
import abs.sf.beach.activity.ChatActivity;
import abs.sf.beach.core.BasicApplication;
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

    public synchronized  void setChatActivity(ChatActivity chatActivity) {
        this.chatActivity = chatActivity;
    }

    public  synchronized void removeChatActivity() {
        this.chatActivity = null;
    }

    @Override
    public void handleChatLine(final ChatLine chatLine) {
        if(chatActivity == null) {

            new NotificationCaller(chatLine).execute();

        } else if(!StringUtils.safeEquals(chatActivity.getJID().getBareJID(), chatLine.getPeerBareJid())){

            new NotificationCaller(chatLine).execute();

        }
    }

    private class NotificationCaller extends AsyncTask<Void, Void, Void>{
        private ChatLine chatLine;
        public NotificationCaller(ChatLine chatLine){
            this.chatLine = chatLine;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            NotificationUtils.show(chatLine.getText(), chatLine, BasicApplication.getContext());
        }

        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }
    }

}
