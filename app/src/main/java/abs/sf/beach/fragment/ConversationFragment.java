package abs.sf.beach.fragment;

import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;


import abs.ixi.client.core.Platform;
import abs.ixi.client.util.CollectionUtils;
import abs.sf.beach.adapter.ConversationAdapter;
import abs.sf.beach.android.R;
import abs.sf.client.android.db.DbManager;
import abs.sf.client.android.managers.AndroidChatManager;
import abs.sf.client.android.messaging.ChatLine;
import abs.sf.client.android.messaging.ChatLineReceiver;
import abs.sf.client.android.messaging.Conversation;


/**
 * this fragment is for Conversations
 */
public class ConversationFragment extends Fragment implements ChatLineReceiver {
    private RecyclerView recyclerViewConversation;
    private List<Conversation> conversations;
    private ConversationAdapter adapter;

    private AndroidChatManager chatManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        System.out.println("Conersation fragment on Create");
        View view = inflater.inflate(R.layout.fragment_conversation, container, false);
        initView(view);

        chatManager = (AndroidChatManager) Platform.getInstance().getChatManager();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("Conersation fragment on resume");
        this.conversations = DbManager.getInstance().fetchConversations();
        setConversationAdapter();

        this.chatManager.addChatLineReceiver(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        System.out.println("Conersation fragment on stop");
    }

    @Override
    public void onPause() {
        super.onPause();
        this.chatManager.removeChatLineReceiver(this);
        System.out.println("Conersation fragment on pause");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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
    public void handleChatLine(final ChatLine chatLine) {
        this.conversations = DbManager.getInstance().fetchConversations();
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setConversationAdapter();
            }
        });
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
