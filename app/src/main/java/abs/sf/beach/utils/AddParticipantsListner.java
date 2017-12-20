package abs.sf.beach.utils;

import abs.ixi.client.xmpp.packet.Roster;

public interface AddParticipantsListner {
    void add(String action, Roster.RosterItem item);
    void remove(int pos);
}
