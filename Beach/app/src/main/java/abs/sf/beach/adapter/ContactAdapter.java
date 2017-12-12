package abs.sf.beach.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import abs.ixi.client.xmpp.packet.Roster;
import abs.sf.beach.activity.ChatActivity;
import abs.sf.beach.android.R;
import abs.sf.client.android.db.DbManager;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {
    private Context context;
    private List<Roster.RosterItem> contacts, contactsOriginal;

    public ContactAdapter(Context context, List<Roster.RosterItem> contacts) {
        this.context = context;
        this.contacts = contacts;
        this.contactsOriginal = contacts;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_contact, parent, false);

        return new ViewHolder(itemView, viewType, context);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Roster.RosterItem contactModel = contacts.get(position);

        holder.tvContactName.setText(contactModel.getName());

        holder.ivContactImage.setBackground(context.getResources().getDrawable(R.mipmap.user_placeholder));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("jid", contacts.get(position).getJid());
                intent.putExtra("name", contacts.get(position).getName());
                intent.putExtra("from", "Contact");
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView ivContactImage;
        public TextView tvContactName;


        public ViewHolder(View itemView, int viewType, Context c) {
            super(itemView);

            itemView.setClickable(true);
            ivContactImage = (ImageView) itemView.findViewById(R.id.ivContactImage);
            tvContactName = (TextView) itemView.findViewById(R.id.tvContactName);
        }
    }

    public void filterData(CharSequence s){
        if(s.length()==0){
            this.contacts = contactsOriginal;
        }
        List<Roster.RosterItem> p = new ArrayList<>();
        for(Roster.RosterItem data : contactsOriginal){
            if(data.getName().toLowerCase().contains(s)){
                p.add(data);
            }
        }
        this.contacts = p;
        notifyDataSetChanged();
    }
}
