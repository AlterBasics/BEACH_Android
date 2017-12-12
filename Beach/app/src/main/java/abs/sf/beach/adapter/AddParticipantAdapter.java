package abs.sf.beach.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import abs.ixi.client.util.UUIDGenerator;
import abs.ixi.client.xmpp.JID;
import abs.ixi.client.xmpp.packet.Roster;
import abs.sf.beach.android.R;
import abs.sf.beach.utils.AddParticipantsListner;
import abs.sf.client.android.managers.AndroidUserManager;

public class AddParticipantAdapter extends RecyclerView.Adapter<AddParticipantAdapter.ParticipantViewHolder>{
    private List<Roster.RosterItem> items, itemsOriginal;
    private AddParticipantsListner listener;
    private Context context;
    private JID roomJID;
    private String groupName;

    public AddParticipantAdapter(List<Roster.RosterItem> items, AddParticipantsListner listener, JID roomJID, String groupName, Context context){
        this.items = items;
        this.itemsOriginal = items;
        this.listener = listener;
        this.roomJID = roomJID;
        this.groupName = groupName;
        this.context = context;
    }

    public class ParticipantViewHolder extends RecyclerView.ViewHolder{
        public ImageView ivContactImage, ivAddParticipants;
        public TextView tvContactName;

        public ParticipantViewHolder(View itemView) {
            super(itemView);
            ivAddParticipants = (ImageView) itemView.findViewById(R.id.ivAddParticipants);
            ivContactImage = (ImageView) itemView.findViewById(R.id.ivContactImage);
            tvContactName = (TextView) itemView.findViewById(R.id.tvContactName);

            ivAddParticipants.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showAddAlert(items.get(getAdapterPosition()));
                }
            });
        }
    }

    @Override
    public AddParticipantAdapter.ParticipantViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_add_participants,parent,false);
        return new ParticipantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AddParticipantAdapter.ParticipantViewHolder holder, int position) {
        holder.tvContactName.setText(items.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private void showAddAlert(final Roster.RosterItem item){
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle("Add Participants");
        dialog.setMessage("Add "+ item.getName() +" to "+groupName+"?");
        dialog.setCancelable(true);
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AndroidUserManager.getInstance().addChatRoomMemberRequest(UUIDGenerator.secureId(), roomJID, item.getJid());
                listener.add("requestAddParticipant", item);
                dialog.dismiss();
            }
        });
        dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void filterData(CharSequence s){
        if(s.length()==0){
            this.items = itemsOriginal;
        }
        List<Roster.RosterItem> ri = new ArrayList<>();
        for(Roster.RosterItem data : itemsOriginal){
            if(data.getName().toLowerCase().contains(s)){
                ri.add(data);
            }
        }
        this.items = ri;
        notifyDataSetChanged();
    }
}
