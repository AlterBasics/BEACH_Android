package abs.sf.beach.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;



import abs.ixi.client.core.Platform;
import abs.ixi.client.util.CollectionUtils;
import abs.ixi.client.util.StringUtils;
import abs.ixi.client.xmpp.packet.Roster;
import abs.ixi.client.xmpp.packet.UserSearchData;
import abs.sf.beach.activity.ChatActivity;
import abs.sf.beach.activity.ProfileActivity;
import abs.sf.beach.android.R;
import abs.sf.beach.utils.OnRefreshViewListener;
import abs.sf.client.android.managers.AndroidUserManager;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {
    private Context context;
    private List<UserSearchData.Item> searchedUsers;
    private List<UserSearchData.Item> searchUsersOriginal;
    private List<Roster.RosterItem> userRosterItems;
    private OnRefreshViewListener refreshViewListener;

    public SearchAdapter(Context context, List<UserSearchData.Item> search,OnRefreshViewListener refreshViewListener) {
        this.context = context;
        this.searchedUsers = search;
        this.searchUsersOriginal = search;
        AndroidUserManager userManager = (AndroidUserManager) Platform.getInstance().getUserManager();
        this.userRosterItems = userManager.getRosterItemList();
        this.refreshViewListener = refreshViewListener;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_search, parent, false);
        return new ViewHolder(itemView, viewType, context);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final UserSearchData.Item searchModel = searchedUsers.get(position);
        holder.ivSearchContactImage.setBackground(context.getResources().getDrawable(R.mipmap.user_placeholder));
        holder.tvSearchContactName.setText(getUserName(searchModel));
        if (isAlreadyInUserContact(searchModel)){
            //toDO

           holder.tvSearchEmail.setText("No Result");
        }
        else {
            holder.tvSearchEmail.setVisibility(View.VISIBLE);
            holder.tvSearchEmail.setText(searchModel.getEmail());
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater myLayout = LayoutInflater.from(context);
                final View dialogView = myLayout.inflate(R.layout.dialog_search, null);
                final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                final AlertDialog dialog1 = dialog.create();
                dialog1.setView(dialogView);
                dialog1.show();
                final TextView tv4 = (TextView) dialogView.findViewById(R.id.tvMessage);
                final TextView tv5 = (TextView) dialogView.findViewById(R.id.tvView);
                TextView tv1 = (TextView) dialogView.findViewById(R.id.tvremove);
                TextView tv2 = (TextView) dialogView.findViewById(R.id.tvAddtoContact);


                tv4.setText("Message" + " " + getUserName(searchModel) );
                tv4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, ChatActivity.class);
                        intent.putExtra("jid", searchModel.getUserJID());
                        intent.putExtra("name", searchModel);
                        intent.putExtra("from", "Search");
                        context.startActivity(intent);
                        refreshViewListener.refreshView();
                        dialog1.dismiss();
                    }
                });

                tv5.setText("View" + " " + getUserName(searchModel) );
                tv5.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context,ProfileActivity.class);
                        intent.putExtra("jid", searchModel.getUserJID());
                        intent.putExtra("name", searchModel);
                        intent.putExtra("from", "Search");
                        context.startActivity(intent);
                        dialog1.dismiss();
                        refreshViewListener.refreshView();
                    }
                });

                if (isAlreadyInUserContact(searchModel)){
                    tv1.setText("Remove" + " " + getUserName(searchModel));
                    tv1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            final UserSearchData.Item  item = searchedUsers.get(position);
                            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                            dialog.setMessage("Are yos sure wanna remove this member from contacts");
                            dialog.setCancelable(true);

                            dialog.setPositiveButton(" YES", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    AndroidUserManager userManager = (AndroidUserManager) Platform.getInstance().getUserManager();

                                    boolean removed = userManager.removeRosterMember(item.getUserJID());

                                    if (removed) {
                                        Toast.makeText(context, "Successfully Removed ", Toast.LENGTH_SHORT).show();

                                    } else {
                                        Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                                    }

                                    refreshViewListener.refreshView();

                                    dialog1.dismiss();
                                }
                            });

                            dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog1.dismiss();
                                }
                            });

                            dialog.show();
                        }
                    });
                }



                if (!isAlreadyInUserContact(searchModel)){
                    tv2.setVisibility(View.VISIBLE);
                    tv2.setText("Add to contacts" + " " + getUserName(searchModel));
                    tv2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            final UserSearchData.Item  item = searchedUsers.get(position);

                            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                            dialog.setMessage("Are yos sure wanna add this member in contacts");
                            dialog.setCancelable(true);

                            dialog.setPositiveButton(" YES", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    AndroidUserManager userManager = (AndroidUserManager) Platform.getInstance().getUserManager();

                                    boolean removed = userManager.addRosterMember(item.getUserJID(),getUserName(searchModel));

                                    if (removed) {
                                        Toast.makeText(context, "Successfully Added ", Toast.LENGTH_SHORT).show();

                                    } else {
                                        Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                                    }

                                      refreshViewListener.refreshView();

                                    dialog1.dismiss();
                                }
                            });

                            dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog1.dismiss();
                                }
                            });

                            dialog.show();
                        }
                    });
                }


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
        public TextView tvSearchEmail;

        public ViewHolder(View itemView, int viewType, Context context) {
            super(itemView);
            itemView.setClickable(true);
            ivSearchContactImage = (ImageView) itemView.findViewById(R.id.ivSearchContactImage);
            tvSearchContactName = (TextView) itemView.findViewById(R.id.tvSearchName);
            tvSearchEmail = (TextView) itemView.findViewById(R.id.tvSerachEmail);
            tvSearchEmail.setVisibility(View.INVISIBLE);
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
