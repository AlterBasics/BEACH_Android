package abs.sf.beach.activity;

import android.os.Bundle;

import java.util.List;

import abs.ixi.client.core.Platform;
import abs.ixi.client.xmpp.JID;
import abs.ixi.client.xmpp.packet.ChatRoom;
import abs.ixi.client.xmpp.packet.Roster;
import abs.sf.beach.utils.CommonConstants;
import abs.sf.client.android.managers.AndroidUserManager;

public class AddParticipantActivity extends StringflowActivity {
    private List<Roster.RosterItem> allRosterItems;
    private JID roomJID;
    private ChatRoom chatRoom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onResume() {
        super.onResume();
        this.allRosterItems = getUserRosterItems();
        this.roomJID = (JID) getIntent().getSerializableExtra(CommonConstants.JID);

        AndroidUserManager userManager = (AndroidUserManager) Platform.getInstance().getUserManager();
        this.chatRoom = userManager.getChatRoomDetails(this.roomJID);

        setAdapter();
    }

    private List<Roster.RosterItem> getUserRosterItems() {
        AndroidUserManager userManager = (AndroidUserManager) Platform.getInstance().getUserManager();
        return userManager.getRosterItemList();
    }
}
