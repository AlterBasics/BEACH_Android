package abs.sf.beach.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import abs.ixi.client.xmpp.InvalidJabberId;
import abs.ixi.client.xmpp.JID;
import abs.sf.beach.activity.ChatActivity;
import abs.sf.beach.android.R;
import abs.sf.client.android.messaging.Conversation;


public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ViewHolder> {

    private Context context;
    private List<Conversation> conversations;


    public ConversationAdapter(Context context, List<Conversation> conversations) {
        this.context = context;
        this.conversations = conversations;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_conversation, parent, false);
        return new ViewHolder(itemView, context);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        Conversation conversation = conversations.get(position);

        holder.userName.setText(conversation.getPeerName());

        if(conversation.isTyping()){
            holder.chatMsg.setText(context.getString(R.string.is_typing));
        }else {
            holder.chatMsg.setText(conversation.getLastChatLine());
        }

        if (conversation.getDisplayTime() != null) {
            holder.chatMsgTime.setText(conversation.getDisplayTime());
        } else {
            holder.chatMsgTime.setVisibility(View.INVISIBLE);
        }

        if (conversation.getUnreadChatLines() == 0) {
            holder.chatMsgCount.setVisibility(View.INVISIBLE);
        } else {
            holder.chatMsgCount.setVisibility(View.VISIBLE);
            holder.chatMsgCount.setText(String.valueOf(conversation.getUnreadChatLines()));
        }

        if (conversation.isOnline()) {
            holder.userPresence.setBackground(context.getResources().getDrawable(R.drawable.circle_green));

        } else {
            holder.userPresence.setBackground(context.getResources().getDrawable(R.drawable.circle_grey));
        }

        if (conversation.isGroup()) {
            holder.userImage.setBackground(context.getResources().getDrawable(R.mipmap.group));
            holder.userPresence.setVisibility(View.INVISIBLE);
        } else {
            holder.userImage.setBackground(context.getResources().getDrawable(R.mipmap.user_placeholder));
            holder.userPresence.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return conversations.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public RelativeLayout imageRelLayout, rlContactRow;
        public ImageView userImage;
        public TextView userPresence, userName, chatMsgTime, chatMsg, chatMsgCount;


        public ViewHolder(View itemView, Context c) {
            super(itemView);
            itemView.setClickable(true);
            imageRelLayout = (RelativeLayout) itemView.findViewById(R.id.imageRelLayout);
            rlContactRow = (RelativeLayout) itemView.findViewById(R.id.rlContactRow);
            userImage = (ImageView) itemView.findViewById(R.id.ivContactImage);
            userPresence = (TextView) itemView.findViewById(R.id.userPresence);
            userName = (TextView) itemView.findViewById(R.id.userName);
            chatMsgTime = (TextView) itemView.findViewById(R.id.chatMsgTime);
            chatMsg = (TextView) itemView.findViewById(R.id.chatMsg);
            chatMsgCount = (TextView) itemView.findViewById(R.id.chatMsgCount);

            rlContactRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ChatActivity.class);
                    try {
                        intent.putExtra("jid", new JID(conversations.get(getAdapterPosition()).getPeerJid()));
                        intent.putExtra("name", conversations.get(getAdapterPosition()).getPeerName());
                        intent.putExtra("from", "Conversation");
                        context.startActivity(intent);
                    } catch (InvalidJabberId invalidJabberId) {
                        invalidJabberId.printStackTrace();
                    }
                }
            });
        }
    }
}
