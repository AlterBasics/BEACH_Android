package abs.sf.beach.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import abs.ixi.client.xmpp.JID;
import abs.ixi.client.xmpp.packet.Roster;
import abs.sf.beach.android.R;

public class GroupAddParticipantAdapter extends RecyclerView.Adapter<GroupAddParticipantAdapter.ParticipantViewHolder> {
    private List<Roster.RosterItem> allRosterItems, itemsOriginal;
    private Context context;

    private  int selectedPos = RecyclerView.NO_POSITION;
    private  List<JID> selectedGroupMembers;

    public GroupAddParticipantAdapter(List<Roster.RosterItem> allRosterItems, List<JID> selectedGroupMembers, Context context){
        this.allRosterItems = allRosterItems;
        this.itemsOriginal = allRosterItems;
        this.context = context;
        this.selectedGroupMembers = selectedGroupMembers;
    }

    public class ParticipantViewHolder extends RecyclerView.ViewHolder{
        public ImageView ivContactImage;
        public TextView tvContactName;
        public ImageView tick;
        public RelativeLayout contactRow;

        public ParticipantViewHolder(View itemView) {
            super(itemView);
            ivContactImage = (ImageView) itemView.findViewById(R.id.ivContactImage);
            tvContactName = (TextView) itemView.findViewById(R.id.tvContactName);
            contactRow = (RelativeLayout)itemView.findViewById(R.id.rlSelectUSer);
            tick = (ImageView)itemView.findViewById(R.id.ivTick);
            tick.setVisibility(View.INVISIBLE);

            contactRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Roster.RosterItem item = allRosterItems.get(getAdapterPosition());

                    if (selectedGroupMembers.contains(item.getJid())){
                        selectedGroupMembers.remove(item.getJid());
                        // disable that tick
                        tick.setVisibility(View.INVISIBLE);
                    }
                    else {
                        selectedGroupMembers.add(item.getJid());
                        // enable that tick
                        tick.setVisibility(View.VISIBLE);
                    }
                }
            });

        }
    }

    @Override
    public  GroupAddParticipantAdapter.ParticipantViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_group_add_participant,parent,false);
        return  new ParticipantViewHolder(view);

    }

    @Override
    public void onBindViewHolder(GroupAddParticipantAdapter.ParticipantViewHolder holder, int position) {
        holder.tvContactName.setText(allRosterItems.get(position).getName());
        holder.itemView.setSelected(selectedPos == position);

    }

    @Override
    public int getItemCount() {
        return allRosterItems.size();
    }

    public void filterData(CharSequence s){
        if(s.length()==0){
            this.allRosterItems = itemsOriginal;
        }
        List<Roster.RosterItem> ri = new ArrayList<>();
        for(Roster.RosterItem data : itemsOriginal){
            if(data.getName().toLowerCase().contains(s)){
                ri.add(data);
            }
        }
        this.allRosterItems = ri;
        notifyDataSetChanged();
    }

}
