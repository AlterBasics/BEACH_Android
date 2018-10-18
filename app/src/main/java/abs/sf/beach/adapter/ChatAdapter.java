package abs.sf.beach.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import abs.ixi.client.util.DateUtils;
import abs.sf.beach.android.R;
import abs.sf.client.android.db.DbManager;
import abs.sf.client.android.messaging.ChatLine;


public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private static final int POLL=1;
    private static final int TEXT=2;
    private static final int IMAGE=3;

    private Context context;
    private List<ChatLine> chatLines;
    private boolean isGroup;

    public ChatAdapter(Context context, List<ChatLine> chatLines, boolean isGroup) {
        this.context = context;
        this.chatLines = chatLines;
        this.isGroup = isGroup;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public ViewHolder(View view){
            super(view);
        }
    }

    public class PollViewHolder extends ViewHolder {

        public LinearLayout toLayout, fromLayout;
        public TextView toMsgTime, fromMsgTime, toName;
        public ImageView ivStatus;

        public PollViewHolder(View itemView, Context c) {
            super(itemView);

            toLayout = (LinearLayout) itemView.findViewById(R.id.toLayout);
            fromLayout = (LinearLayout) itemView.findViewById(R.id.fromLayout);
            toMsgTime = (TextView) itemView.findViewById(R.id.toMsgTime);
            fromMsgTime = (TextView) itemView.findViewById(R.id.fromMsgTime);
            toName = (TextView) itemView.findViewById(R.id.toName);
            ivStatus = (ImageView) itemView.findViewById(R.id.ivStatus);
        }
    }

    public class TextViewHolder extends ViewHolder {

        public LinearLayout toLayout, fromLayout;
        public TextView toMsg, toMsgTime, fromMsg, fromMsgTime, toName;
        public ImageView ivStatus;

        public TextViewHolder(View itemView, Context c) {
            super(itemView);

            toLayout = (LinearLayout) itemView.findViewById(R.id.toLayout);
            fromLayout = (LinearLayout) itemView.findViewById(R.id.fromLayout);
            toMsg = (TextView) itemView.findViewById(R.id.toMsg);
            toMsgTime = (TextView) itemView.findViewById(R.id.toMsgTime);
            fromMsg = (TextView) itemView.findViewById(R.id.fromMsg);
            fromMsgTime = (TextView) itemView.findViewById(R.id.fromMsgTime);
            toName = (TextView) itemView.findViewById(R.id.toName);
            ivStatus = (ImageView) itemView.findViewById(R.id.ivStatus);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TextViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_chat, parent, false), context);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        ChatLine chatLine = this.chatLines.get(position);

        switch (chatLine.getContentType()){
            default:
                TextViewHolder textViewHolder = (TextViewHolder) holder;
                setTextViews(chatLine, textViewHolder);
                break;
        }
    }

//    @Override
//    public int getItemViewType(int position) {
//        ChatLine chatLine = this.chatLines.get(position);
//        //if(chatLine.getContentType().equals(ChatLine.ContentType.POLL)){
//            return POLL;
//        }
//        return TEXT;
//    }


    @Override
    public int getItemCount() {
        return chatLines.size();
    }



    private void setTextViews(ChatLine chatLine, TextViewHolder holder){
        switch (chatLine.getDirection()) {
            case SEND:
                holder.toLayout.setVisibility(View.GONE);
                holder.fromLayout.setVisibility(View.VISIBLE);
                holder.fromMsg.setText(chatLine.getText());
                holder.fromMsgTime.setText(chatLine.getDisplayTime());

                if(chatLine.getMessageStatus() == ChatLine.MessageStatus.NOT_DELIVERED_TO_SERVER){
                    holder.ivStatus.setImageResource(R.mipmap.tick_unsent);

                }else if(chatLine.getMessageStatus() == ChatLine.MessageStatus.DELIVERED_TO_SERVER){
                    holder.ivStatus.setImageResource(R.mipmap.tick_sent);

                }else if(chatLine.getMessageStatus() == ChatLine.MessageStatus.DELIVERED_TO_RECEIVER){
                    holder.ivStatus.setImageResource(R.mipmap.tick_delivered);

                }else if(chatLine.getMessageStatus() == ChatLine.MessageStatus.RECEIVER_IS_ACKNOWLEDGED){
                    holder.ivStatus.setImageResource(R.mipmap.tick_delivered);

                } else if(chatLine.getMessageStatus() == ChatLine.MessageStatus.RECEIVER_HAS_VIEWED){
                    holder.ivStatus.setImageResource(R.mipmap.tick_read);
                }
                break;

            case RECEIVE:
                holder.fromLayout.setVisibility(View.GONE);
                holder.toLayout.setVisibility(View.VISIBLE);
                holder.toMsg.setText(chatLine.getText());
                holder.toMsgTime.setText(chatLine.getDisplayTime());
                if (isGroup) {
                    holder.toName.setVisibility(View.VISIBLE);
                    holder.toName.setText(chatLine.getPeerName());
                } else {
                    holder.toName.setVisibility(View.GONE);
                }
                break;
        }
    }
}
