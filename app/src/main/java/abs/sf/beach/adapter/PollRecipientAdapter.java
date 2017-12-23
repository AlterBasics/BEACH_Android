package abs.sf.beach.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import abs.ixi.client.core.Platform;
import abs.ixi.client.xmpp.packet.Roster;
import abs.sf.beach.activity.PollRecipientActivity;
import abs.sf.beach.android.R;
import abs.sf.beach.utils.AndroidUtils;
import abs.sf.client.android.managers.AndroidChatManager;
import abs.sf.client.android.managers.AndroidUserManager;

import static abs.sf.beach.android.R.id.cbSelect;
import static android.R.id.list;


public class PollRecipientAdapter extends RecyclerView.Adapter<PollRecipientAdapter.ViewHolder> {
    private Context context;
    private List<Roster.RosterItem> contacts, contactsOriginal;
    private HashMap<String, Boolean> selectedRecipient;
    public PollRecipientAdapter(Context context, List<Roster.RosterItem> contacts) {
        this.context = context;
        this.contacts = contacts;
        this.contactsOriginal = contacts;
        selectedRecipient = new HashMap<>();
    }

    @Override
    public PollRecipientAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_poll_recipient, parent, false);

        return new PollRecipientAdapter.ViewHolder(itemView, viewType, context);
    }

    @Override
    public void onBindViewHolder(final PollRecipientAdapter.ViewHolder holder, final int position) {
        final Roster.RosterItem contactModel = contacts.get(position);

        holder.tvContactName.setText(contactModel.getName());

        holder.ivContactImage.setBackground(context.getResources().getDrawable(R.mipmap.user_placeholder));

        if(selectedRecipient.containsKey(contactModel.getJid().getBareJID()) && selectedRecipient.get(contactModel.getJid().getBareJID())){
            holder.cbSelect.setChecked(true);
        }else{
            holder.cbSelect.setChecked(false);
        }
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivContactImage;
        TextView tvContactName;
        CheckBox cbSelect;
        RelativeLayout rlContactRow;

        ViewHolder(View itemView, int viewType, Context c) {
            super(itemView);

            rlContactRow = (RelativeLayout) itemView.findViewById(R.id.rlContactRow);
            ivContactImage = (ImageView) itemView.findViewById(R.id.ivContactImage);
            tvContactName = (TextView) itemView.findViewById(R.id.tvContactName);
            cbSelect = (CheckBox) itemView.findViewById(R.id.cbSelect);

            rlContactRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean val = setCheckedBox(getAdapterPosition());
                    cbSelect.setChecked(val);
                }
            });
            cbSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean val = setCheckedBox(getAdapterPosition());
                    cbSelect.setChecked(val);
                }
            });
        }
    }

    private boolean setCheckedBox(int pos){
        final Roster.RosterItem contactModel = contacts.get(pos);
        if(selectedRecipient.containsKey(contactModel.getJid().getBareJID())){
            if(!selectedRecipient.get(contactModel.getJid().getBareJID())){
                selectedRecipient.put(contactModel.getJid().getBareJID(),true);
                return true;
            }else{
                selectedRecipient.remove(contactModel.getJid().getBareJID());
                return false;
            }
        }else {
            selectedRecipient.put(contactModel.getJid().getBareJID(),true);
            return true;
        }
    }

    public void filterData(CharSequence s) {
        if (s.length() == 0) {
            this.contacts = contactsOriginal;
        }
        List<Roster.RosterItem> p = new ArrayList<>();
        for (Roster.RosterItem data : contactsOriginal) {
            if (data.getName().toLowerCase().contains(s)) {
                p.add(data);
            }
        }
        this.contacts = p;
        notifyDataSetChanged();
    }

    public void sendData(long pollId){
        if(selectedRecipient.size()<1){
            AndroidUtils.showToast(context, "Please select at least one recipient");
            return;
        }
        List<String> list = new ArrayList<>();
        for(String s : selectedRecipient.keySet()){
            list.add(s);
        }
        AndroidChatManager chatManager = (AndroidChatManager) Platform.getInstance().getChatManager();

        chatManager.sendPollMessage(pollId, list);
        ((PollRecipientActivity)(context)).finish();
        //1956741773
    }
}