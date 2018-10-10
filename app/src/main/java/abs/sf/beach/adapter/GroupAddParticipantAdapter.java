package abs.sf.beach.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import abs.ixi.client.xmpp.JID;
import abs.ixi.client.xmpp.packet.Roster;
import abs.sf.beach.android.R;

public class GroupAddParticipantAdapter extends RecyclerView.Adapter<GroupAddParticipantAdapter.ParticipantViewHolder> {
    private List<Roster.RosterItem> items, itemsOriginal;
    private Context context;
    private JID roomJID;
    private String groupName;

    public GroupAddParticipantAdapter(List<Roster.RosterItem> items, JID roomJID, String groupName, Context context){
        this.items = items;
        this.itemsOriginal = items;
        this.roomJID = roomJID;
        this.groupName = groupName;
        this.context = context;
    }

    public class ParticipantViewHolder extends RecyclerView.ViewHolder{
        public ImageView ivContactImage;
        public TextView tvContactName;

        public ParticipantViewHolder(View itemView) {
            super(itemView);
            ivContactImage = (ImageView) itemView.findViewById(R.id.ivContactImage);
            tvContactName = (TextView) itemView.findViewById(R.id.tvContactName);

        }
    }

    @Override
    public  GroupAddParticipantAdapter.ParticipantViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_group_add_participant,parent,false);
        return  new ParticipantViewHolder(view);

    }

    @Override
    public void onBindViewHolder(GroupAddParticipantAdapter.ParticipantViewHolder holder, int position) {
        holder.tvContactName.setText(items.get(position).getName());

    }

    @Override
    public int getItemCount() {
        return items.size();
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
