package abs.sf.beach.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import abs.ixi.client.core.Platform;
import abs.ixi.client.util.CollectionUtils;
import abs.ixi.client.util.StringUtils;
import abs.ixi.client.xmpp.packet.Roster;
import abs.ixi.client.xmpp.packet.UserSearchData;
import abs.sf.beach.activity.ChatActivity;
import abs.sf.beach.android.R;
import abs.sf.client.android.managers.AndroidUserManager;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {
    private Context context;
    private List<UserSearchData.Item> searchedUsers;
    private List<UserSearchData.Item> searchUsersOriginal;
    private List<Roster.RosterItem> userRosterItems;

    public SearchAdapter(Context context, List<UserSearchData.Item> search) {
        this.context = context;
        this.searchedUsers = search;
        this.searchUsersOriginal = search;
        AndroidUserManager userManager = (AndroidUserManager) Platform.getInstance().getUserManager();
        this.userRosterItems = userManager.getRosterItemList();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_search, parent, false);
        return new ViewHolder(itemView, viewType, context);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        UserSearchData.Item searchModel = searchedUsers.get(position);
        holder.ivSearchContactImage.setBackground(context.getResources().getDrawable(R.mipmap.user_placeholder));
        holder.tvSearchContactName.setText((CharSequence) searchModel);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ChatActivity.class);
                //intent.putExtra("jid", search.get(position).getJid());
                // intent.putExtra("name", search.get(position).getName());
                //intent.putExtra("from", "Search");
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return searchedUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivSearchContactImage;
        public TextView tvSearchContactName;

        public ViewHolder(View itemView, int viewType, Context context) {
            super(itemView);
            itemView.setClickable(true);
            ivSearchContactImage = (ImageView) itemView.findViewById(R.id.ivSearchContactImage);
            tvSearchContactName = (TextView) itemView.findViewById(R.id.tvSearchName);
        }

    }

    private String getUserName(UserSearchData.Item item) {
        String userName = null;

        if (!StringUtils.isNullOrEmpty(item.getFirstName())) {
            userName = item.getFirstName();

            if (!StringUtils.isNullOrEmpty(item.getLastName())) {
                userName = userName + " " + item.getLastName();
            }
        }

        if (StringUtils.isNullOrEmpty(userName) && !StringUtils.isNullOrEmpty(item.getNickName())) {
            userName = item.getNickName();
        }

        if (StringUtils.isNullOrEmpty(userName)) {
            userName = item.getUserJID().getNode();
        }

        return userName;
    }

    private boolean isAlreadyInUserContact(UserSearchData.Item item) {
        if (!CollectionUtils.isNullOrEmpty(this.userRosterItems)) {
            for (Roster.RosterItem rosterItem : this.userRosterItems) {
                if (item.getUserJID().equals(rosterItem.getJid())) {
                    return true;
                }
            }
        }

        return false;
    }
}
