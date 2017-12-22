package abs.sf.beach.notification;

import java.util.List;

import abs.ixi.client.core.PacketCollector;
import abs.ixi.client.core.Platform;
import abs.ixi.client.util.StringUtils;
import abs.ixi.client.xmpp.packet.Message;
import abs.ixi.client.xmpp.packet.Packet;
import abs.sf.beach.activity.ChatActivity;

public class NotificationGenerator implements PacketCollector{
    private ChatActivity chatActivity;

    private static NotificationGenerator instance;

    private  NotificationGenerator() {
        Platform.getInstance().getChatManager().addPacketCollector(Message.class, this);
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
    public void collect(Packet packet) {
        Message message = (Message) packet;

        if(chatActivity == null) {
            //generate notification
        } else if(!StringUtils.safeEquals(chatActivity.getJID().getBareJID(), message.getFrom().getBareJID())){
            //generate notification
        }
    }

    @Override
    public <T extends Packet> void collect(List<T> list) {

    }
}
