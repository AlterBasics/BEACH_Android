package abs.sf.beach.adapter;

import android.content.Context;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import abs.sf.beach.android.R;
import abs.sf.client.android.messaging.ChatLine;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private Context context;
    private List<ChatLine> msgList;
    private boolean isGroup;

    public ChatAdapter(Context context, List<ChatLine> msgList, boolean isGroup) {
        this.context = context;
        this.msgList = msgList;
        this.isGroup = isGroup;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_chat, parent, false), context);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        ChatLine chatLine = msgList.get(position);

        switch (chatLine.getDirection()) {
            case SEND:
                holder.toLayout.setVisibility(View.GONE);
                holder.fromLayout.setVisibility(View.VISIBLE);
                holder.fromMsg.setText(chatLine.getText());
                holder.fromMsgTime.setText(chatLine.getDisplayTime());
                if(chatLine.getDeliveryStatus()==1){
                    holder.ivStatus.setImageResource(R.mipmap.tick_delivered);
                }else{
                    holder.ivStatus.setImageResource(R.mipmap.tick_undelivered);
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

    @Override
    public int getItemCount() {
        return msgList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout toLayout, fromLayout;
        public TextView toMsg, toMsgTime, fromMsg, fromMsgTime, toName;
        public ImageView ivStatus;

        public ViewHolder(View itemView, Context c) {
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
}
