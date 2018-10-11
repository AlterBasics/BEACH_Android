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
    private List<Roster.RosterItem> items, itemsOriginal;
    private Context context;
    private String groupName;
    private String grouptype;


    private  int selectedPos = RecyclerView.NO_POSITION;
    private  List<JID> selectedContacts;

    public GroupAddParticipantAdapter(List<Roster.RosterItem> items, String groupName, String grouptype,Context context){
        this.items = items;
        this.itemsOriginal = items;
        this.groupName = groupName;
        this.grouptype = grouptype;
        this.context = context;
        this.selectedContacts = new ArrayList<>();
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
            contactRow = (RelativeLayout)itemView.findViewById(R.id.rlContactRow);
            tick = (ImageView)itemView.findViewById(R.id.ivTick);


            contactRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Roster.RosterItem item = items.get(getAdapterPosition());
                    if (selectedContacts.contains(item.getJid())){
                        selectedContacts.remove(item.getJid());
                        // disable that tick
                        tick.setVisibility(View.INVISIBLE);
                    }
                    else {
                        selectedContacts.add(item.getJid());
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
        holder.tvContactName.setText(items.get(position).getName());
        holder.itemView.setSelected(selectedPos == position);

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private void showSlectedcontact( final Roster.RosterItem item){

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
