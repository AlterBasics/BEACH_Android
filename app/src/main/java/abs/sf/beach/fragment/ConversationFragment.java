package abs.sf.beach.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import abs.ixi.client.core.PacketCollector;
import abs.ixi.client.core.Platform;
import abs.ixi.client.core.Session;
import abs.ixi.client.util.CollectionUtils;
import abs.ixi.client.util.StringUtils;
import abs.ixi.client.util.TaskExecutor;
import abs.ixi.client.xmpp.JID;
import abs.ixi.client.xmpp.packet.Message;
import abs.ixi.client.xmpp.packet.MessageContent;
import abs.ixi.client.xmpp.packet.Packet;
import abs.sf.beach.adapter.ConversationAdapter;
import abs.sf.beach.android.R;
import abs.sf.beach.utils.NotificationUtils;
import abs.sf.client.android.db.DbManager;
import abs.sf.client.android.messaging.ChatLine;
import abs.sf.client.android.messaging.Conversation;


/**
 * this fragment is for Conversations
 */
public class ConversationFragment extends Fragment implements PacketCollector {
    private RecyclerView recyclerViewConversation;
    private List<Conversation> conversations;
    private ConversationAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        System.out.println("Conersation fragment on Create");
        View view = inflater.inflate(R.layout.fragment_conversation, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("Conersation fragment on resume");
        Platform.getInstance().getChatManager().addPacketCollector(Message.class, this);
        this.conversations = DbManager.getInstance().fetchConversations();
        setConversationAdapter();
    }

    @Override
    public void onStop() {
        super.onStop();
        System.out.println("Conersation fragment on stop");
    }

    @Override
    public void onPause() {
        super.onPause();
        System.out.println("Conersation fragment on pause");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Platform.getInstance().getChatManager().removePacketCollector(Message.class, this);
        System.out.println("Conersation fragment on destroy");
    }

    private void initView(View view) {
        recyclerViewConversation = (RecyclerView) view.findViewById(R.id.recyclerViewConversation);
    }

    private void setConversationAdapter() {
        if (!CollectionUtils.isNullOrEmpty(this.conversations)) {
            if(recyclerViewConversation!=null){
                recyclerViewConversation.setLayoutManager(new LinearLayoutManager(getActivity()));

                Collections.sort(this.conversations, new Comparator<Conversation>() {
                    @Override
                    public int compare(Conversation conversation, Conversation t1) {
                        return conversation.getLastUpdateTime() >= t1.getLastUpdateTime() ? -1 : 1;
                    }
                });

                adapter = new ConversationAdapter(getActivity(), conversations);
                recyclerViewConversation.setAdapter(adapter);
            }
        }
    }

    @Override
    public void collect(Packet packet) {
        System.out.println("Conersation fragment entering collect for packet : " + packet);

        if (packet instanceof Message) {
            Message msg = (Message) packet;

            final ChatLine chatLine = new ChatLine(msg.getId(), msg.getFrom().getBareJID(), ChatLine.Direction.RECEIVE, ChatLine.ContentType.TEXT);
            chatLine.setDeliveryStatus(-1);
            chatLine.setPeerResource(msg.getFrom().getResource());
            chatLine.setPeerName(msg.getFrom().getNode());

            if (msg.getType().val().equalsIgnoreCase("groupchat")) {
                String memberNickName = msg.getFrom().getResource();
                JID memberJID = new JID(memberNickName, Platform.getInstance().getSession().get(Session.KEY_DOMAIN).toString());

                String userName = DbManager.getInstance().getUserName(memberJID.getBareJID());

                if (StringUtils.isNullOrEmpty(userName)) {
                    chatLine.setPeerName(memberNickName);

                } else {
                    chatLine.setPeerName(userName);
                }
            }

            if (msg.getContent().isContentType(MessageContent.MessageContentType.BODY)) {
                chatLine.setContentType(ChatLine.ContentType.TEXT);
                chatLine.setText(msg.getContent().toString());
            }

            System.out.println("Conersation fragment generating notification for packet : " + packet);
            NotificationUtils.show(chatLine.getText(), chatLine, getActivity());

            this.conversations = DbManager.getInstance().fetchConversations();

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyDataSetChanged();
                }
            });


            System.out.println("Conersation fragment get out from collect for packet : " + packet);
        }
    }

    @Override
    public <T extends Packet> void collect(List<T> list) {
        for (T packet : list) {
            this.collect(packet);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser){
            this.conversations = DbManager.getInstance().fetchConversations();
            setConversationAdapter();
        }
    }
}
