package abs.sf.beach.core;

import abs.ixi.client.xmpp.JID;

public interface PushNotificationListener {
    /**
     * Triggers when FCM Notification Received
     */
    public void onNotificationReceived(JID from, String message);
}
