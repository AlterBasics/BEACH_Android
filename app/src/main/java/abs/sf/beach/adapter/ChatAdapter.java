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

import java.util.Map;

import abs.ixi.client.util.DateUtils;
import abs.sf.beach.activity.PollResponseActivity;
import abs.sf.beach.android.R;
import abs.sf.client.android.db.DbManager;
import abs.sf.client.android.messaging.ChatLine;
import abs.sf.client.android.messaging.PollContent;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private static final int POLL=1;
    private static final int TEXT=2;

    private Context context;
    private Map<String, ChatLine> chatLine;
    private String[] mKeys;
    private boolean isGroup;

    public ChatAdapter(Context context, Map<String, ChatLine> chatLine, boolean isGroup) {
        this.context = context;
        this.chatLine = chatLine;
        this.mKeys = chatLine.keySet().toArray(new String[chatLine.size()]);
        this.isGroup = isGroup;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public ViewHolder(View view){
            super(view);
        }
    }

    public class PollViewHolder extends ViewHolder {

        public LinearLayout toLayout, fromLayout;
        public TextView toQuestion, toMsgTime, fromQuestion, fromMsgTime, toName, toExpiryTime, fromExpiryTime;
        public ImageView ivStatus;
        public Button btnToRespond, btnFromRespond;

        public PollViewHolder(View itemView, Context c) {
            super(itemView);

            toLayout = (LinearLayout) itemView.findViewById(R.id.toLayout);
            fromLayout = (LinearLayout) itemView.findViewById(R.id.fromLayout);
            toQuestion = (TextView) itemView.findViewById(R.id.toQuestion);
            toMsgTime = (TextView) itemView.findViewById(R.id.toMsgTime);
            fromQuestion = (TextView) itemView.findViewById(R.id.fromQuestion);
            fromMsgTime = (TextView) itemView.findViewById(R.id.fromMsgTime);
            toName = (TextView) itemView.findViewById(R.id.toName);
            toExpiryTime = (TextView) itemView.findViewById(R.id.toExpiryTime);
            fromExpiryTime = (TextView) itemView.findViewById(R.id.fromExpiryTime);
            ivStatus = (ImageView) itemView.findViewById(R.id.ivStatus);
            btnToRespond = (Button) itemView.findViewById(R.id.btnToRespond);
            btnFromRespond = (Button) itemView.findViewById(R.id.btnFromRespond);
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
        if(viewType == POLL){
            return new PollViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.adapter_poll, parent, false), context);
        }
        return new TextViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_chat, parent, false), context);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        ChatLine chatLine = this.chatLine.get(mKeys[position]);

        switch (chatLine.getContentType()){
            case POLL:
                PollViewHolder pollViewHolder = (PollViewHolder) holder;
                setPollViews(chatLine, pollViewHolder);
                break;
            default:
                TextViewHolder textViewHolder = (TextViewHolder) holder;
                setTextViews(chatLine, textViewHolder);
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        ChatLine chatLine = this.chatLine.get(mKeys[position]);
        if(chatLine.getContentType().equals(ChatLine.ContentType.POLL)){
            return POLL;
        }
        return TEXT;
    }


    @Override
    public int getItemCount() {
        return chatLine.size();
    }

    private void setPollViews(final ChatLine chatLine, PollViewHolder holder){
        final PollContent content = DbManager.getInstance().getPollDetails(chatLine.getContentId());
        holder.btnToRespond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openResponseActivity(chatLine.getContentId());
            }
        });
        holder.btnFromRespond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openResponseActivity(chatLine.getContentId());
            }
        });
        switch (chatLine.getDirection()) {
            case SEND:
                holder.toLayout.setVisibility(View.GONE);
                holder.fromLayout.setVisibility(View.VISIBLE);
                holder.fromQuestion.setText(content.getQuestion());
                holder.fromMsgTime.setText(chatLine.getDisplayTime());
                holder.fromExpiryTime.setText("Poll expires on" + DateUtils.getDisplayTime(content.getExpiryTime()));
                if(chatLine.getDeliveryStatus()==0){
                    holder.ivStatus.setImageResource(R.mipmap.tick_unsent);
                }else if(chatLine.getDeliveryStatus()==1){
                    holder.ivStatus.setImageResource(R.mipmap.tick_sent);
                }else if(chatLine.getDeliveryStatus()==2){
                    holder.ivStatus.setImageResource(R.mipmap.tick_delivered);
                }else{
                    holder.ivStatus.setImageResource(R.mipmap.tick_read);
                }
                if(content.getStatus().name().equalsIgnoreCase(PollContent.PollStatus.RESPONDED.name())){
                    holder.btnFromRespond.setText("You responded");
                }
                break;

            case RECEIVE:
                holder.fromLayout.setVisibility(View.GONE);
                holder.toLayout.setVisibility(View.VISIBLE);
                holder.toQuestion.setText(content.getQuestion());
                holder.toMsgTime.setText(chatLine.getDisplayTime());
                holder.toExpiryTime.setText("Poll expires on" + DateUtils.getDisplayTime(content.getExpiryTime()));
                if (isGroup) {
                    holder.toName.setVisibility(View.VISIBLE);
                    holder.toName.setText(chatLine.getPeerName());
                } else {
                    holder.toName.setVisibility(View.GONE);
                }
                if(content.getStatus().name().equalsIgnoreCase(PollContent.PollStatus.RESPONDED.name())){
                    holder.btnToRespond.setText("You responded");
                }
                break;
        }
    }

    private void setTextViews(ChatLine chatLine, TextViewHolder holder){
        switch (chatLine.getDirection()) {
            case SEND:
                holder.toLayout.setVisibility(View.GONE);
                holder.fromLayout.setVisibility(View.VISIBLE);
                holder.fromMsg.setText(chatLine.getText());
                holder.fromMsgTime.setText(chatLine.getDisplayTime());
                if(chatLine.getDeliveryStatus()==0){
                    holder.ivStatus.setImageResource(R.mipmap.tick_unsent);
                }else if(chatLine.getDeliveryStatus()==1){
                    holder.ivStatus.setImageResource(R.mipmap.tick_sent);
                }else if(chatLine.getDeliveryStatus()==2){
                    holder.ivStatus.setImageResource(R.mipmap.tick_delivered);
                }else{
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

    private void openResponseActivity(long pollId){
        Intent intent = new Intent(context, PollResponseActivity.class);
        intent.putExtra("pollId", pollId);
        context.startActivity(intent);
    }
}
