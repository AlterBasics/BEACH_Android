package abs.sf.beach.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import abs.ixi.client.core.Platform;
import abs.ixi.client.core.Session;
import abs.ixi.client.util.StringUtils;
import abs.ixi.client.xmpp.JID;
import abs.ixi.client.xmpp.packet.ChatRoom;
import abs.sf.beach.activity.ChatActivity;
import abs.sf.beach.activity.GroupDetailsActivity;
import abs.sf.beach.android.R;
import abs.sf.beach.utils.AndroidUtils;
import abs.sf.beach.utils.OnRefreshViewListener;
import abs.sf.client.android.managers.AndroidUserManager;

public class GroupDetailsAdapter extends RecyclerView.Adapter<GroupDetailsAdapter.ViewHolder> {
    private Context context;
    private OnRefreshViewListener refreshViewListener;
    private List<ChatRoom.ChatRoomMember> memberList;
    private JID roomJID,myJid;
    private String groupName;
    private ChatRoom.ChatRoomMember loggedInMember;

    public GroupDetailsAdapter(Context context, List<ChatRoom.ChatRoomMember> memberList, JID roomJID, String groupName, ChatRoom.ChatRoomMember loggedInMember) {
        this.context = context;
        this.refreshViewListener = (OnRefreshViewListener) context;
        this.memberList = memberList;
        this.roomJID = roomJID;
        this.groupName = groupName;
        this.loggedInMember = loggedInMember;
        this.myJid = (JID)Platform.getInstance().getSession().get(Session.KEY_USER_JID);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public RelativeLayout rlContactRow;
        public ImageView ivContactImage;
        public TextView tvContactName, tvAffiliation;


        public ViewHolder(View itemView, Context c) {
            super(itemView);

            rlContactRow = (RelativeLayout) itemView.findViewById(R.id.rlContactRow);
            ivContactImage = (ImageView) itemView.findViewById(R.id.ivContactImage);
            tvContactName = (TextView) itemView.findViewById(R.id.tvContactName);
            tvAffiliation = (TextView) itemView.findViewById(R.id.tvAffiliation);

            rlContactRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (loggedInMember != null) {
                        if (loggedInMember.getAffiliation() == ChatRoom.Affiliation.ADMIN || loggedInMember.getAffiliation() == ChatRoom.Affiliation.OWNER) {
                            ChatRoom.ChatRoomMember selectMember = memberList.get(getAdapterPosition());

                            if (!loggedInMember.equals(selectMember)) {
                                showOnClickDialog(selectMember,myJid,getAdapterPosition());
                            }

                        }
                    }
                }
            });
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_group_details, parent, false), context);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        ChatRoom.ChatRoomMember member = memberList.get(position);

        String userName = getMemberName(member);

        holder.tvContactName.setText(userName);

        holder.tvAffiliation.setText(member.getAffiliation().val());
    }

    private String getMemberName(ChatRoom.ChatRoomMember member) {
        String userName;

        if (StringUtils.safeEquals(member.getUserJID().getBareJID(), Platform.getInstance().getUserJID().getBareJID())) {
            userName = "you";

        } else {
            AndroidUserManager userManager = (AndroidUserManager) Platform.getInstance().getUserManager();
            userName = userManager.getRosterItemName(member.getUserJID());
        }

        if (StringUtils.isNullOrEmpty(userName)) {
            userName = member.getUserJID().getNode() + "~" + member.getNickName();
        }

        return userName;
    }

    @Override
    public int getItemCount() {
        return memberList.size();
    }

    private void showOnClickDialog(final ChatRoom.ChatRoomMember selectedMember, final JID jid,final int pos) {
        final String selectMemberName = getMemberName(selectedMember);
        LayoutInflater myLayout = LayoutInflater.from(context);
        final View dialogView = myLayout.inflate(R.layout.dialog_layout, null);
        final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        final AlertDialog dialog1 = dialog.create();
        dialog1.setView(dialogView);
        dialog1.show();
        final TextView tv4 = (TextView) dialogView.findViewById(R.id.tvMessage);
        final TextView tv5 = (TextView) dialogView.findViewById(R.id.tvView);
        final TextView tv1 = (TextView) dialogView.findViewById(R.id.tvRemove);
        TextView tv2 = (TextView) dialogView.findViewById(R.id.tvOwner);
        TextView tv3 = (TextView) dialogView.findViewById(R.id.tvAdmin);

        if (selectedMember.getAffiliation() == ChatRoom.Affiliation.MEMBER) {
            //TODO: these actions should be on dialog
            tv4.append("Message" + " " + selectMemberName);
            tv4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ChatActivity.class);
                    intent.putExtra("jid", memberList.get(pos).getUserJID());
                    intent.putExtra("name", memberList.get(pos).getUserJID());
                    intent.putExtra("from", "Contact");
                    context.startActivity(intent);
                }
            });
            tv5.append("View" + " " + selectMemberName);
            tv5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                }
            });

            tv1.append("Remove" + " " + selectMemberName );
            tv1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                    dialog.setMessage("Are you sure wanna remove this member from group?");
                    dialog.setCancelable(true);

                    dialog.setPositiveButton(" YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            AndroidUserManager userManager = (AndroidUserManager) Platform.getInstance().getUserManager();
                            boolean removed  = userManager.removeChatRoomMember(roomJID,jid);
                            if (removed){
                                memberList.remove(pos);
                                notifyDataSetChanged();
                                Toast.makeText(context, "Successfully Removed ", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                            }

                            //goBack(true, false);
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

            tv2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                    dialog.setMessage("Are you sure wanna make this member owner of group?");
                    dialog.setCancelable(true);

                    dialog.setPositiveButton(" YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            AndroidUserManager userManager = (AndroidUserManager) Platform.getInstance().getUserManager();
                            boolean success  = userManager.addChatRoomOwner(roomJID,jid);
                            if (success){
                                memberList.get(pos);
                                notifyDataSetChanged();
                                Toast.makeText(context, "Successfully Added ", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                            }

                            //goBack(true, false);
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
            tv3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                    dialog.setMessage("Are you sure wanna make this member admin of group?");
                    dialog.setCancelable(true);

                    dialog.setPositiveButton(" YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            AndroidUserManager userManager = (AndroidUserManager) Platform.getInstance().getUserManager();
                            boolean admin  = userManager.addChatRoomAdmin(roomJID,jid);
                            if (admin){
                                memberList.get(pos);
                                notifyDataSetChanged();
                                Toast.makeText(context, "Successfully Added ", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                            }

                            //goBack(true, false);
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
            //1)  message selectMemberName
            //2) view selectMemberName
            //3) remove selectMemberName
            //4) make group admin
            //5) make group owner

        } else if (selectedMember.getAffiliation() == ChatRoom.Affiliation.ADMIN) {
            //TODO: these actions should be on dialog
            tv4.append("Message" + " " + selectMemberName );
            tv4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ChatActivity.class);
                    intent.putExtra("jid", memberList.get(pos).getUserJID());
                    intent.putExtra("name", memberList.get(pos).getUserJID());
                    intent.putExtra("from", "Contact");
                    context.startActivity(intent);
                }
            });
            tv5.append("View" + " " + selectMemberName);
            tv5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                }
            });

            tv1.append("Remove" + " " +  selectMemberName );
            tv1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                    dialog.setMessage("Are you sure wanna remove this member from group?");
                    dialog.setCancelable(true);

                    dialog.setPositiveButton(" YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            AndroidUserManager userManager = (AndroidUserManager) Platform.getInstance().getUserManager();
                            boolean removed  = userManager.removeChatRoomMember(roomJID,jid);
                            if (removed){
                                memberList.remove(pos);
                                notifyDataSetChanged();
                                Toast.makeText(context, "Successfully Removed ", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                            }

                            //goBack(true, false);
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

            tv2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                    dialog.setMessage("Are you sure wanna make this member owner of group?");
                    dialog.setCancelable(true);

                    dialog.setPositiveButton(" YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            AndroidUserManager userManager = (AndroidUserManager) Platform.getInstance().getUserManager();
                            boolean success  = userManager.addChatRoomOwner(roomJID,jid);
                            if (success){
                                memberList.get(pos);
                                notifyDataSetChanged();
                                Toast.makeText(context, "Successfully Added ", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                            }

                            //goBack(true, false);
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
            tv3.append(selectMemberName + " " + "Dissmis as Admin" );
            tv3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                    dialog.setMessage("Are you sure wanna dismiss this member as admin of group?");
                    dialog.setCancelable(true);

                    dialog.setPositiveButton(" YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            AndroidUserManager userManager = (AndroidUserManager) Platform.getInstance().getUserManager();
                            boolean admin  = userManager.addChatRoomAdmin(roomJID,jid);
                            if (admin){
                                Toast.makeText(context, "Successfully Dismiss ", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                            }

                            //goBack(true, false);
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
            //1)  message selectMemberName
            //2) view selectMemberName
            //3) remove selectMemberName
            //4) make group owner
            //5) dismiss as admin

        } else if (selectedMember.getAffiliation() == ChatRoom.Affiliation.OWNER) {
            //TODO: these actions should be on dialog
            tv4.append("Message" + " " +  selectMemberName );
            tv4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ChatActivity.class);
                    intent.putExtra("jid", memberList.get(pos).getUserJID());
                    intent.putExtra("name", memberList.get(pos).getUserJID());
                    intent.putExtra("from", "Contact");
                    context.startActivity(intent);
                }
            });
            tv5.append("View" + " "  + selectMemberName);
            tv5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                }
            });

            tv1.append("Remove" +" " +  selectMemberName );
            /*tv1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                    dialog.setMessage("Are you sure wanna remove this member from group?");
                    dialog.setCancelable(true);

                    dialog.setPositiveButton(" YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            AndroidUserManager userManager = (AndroidUserManager) Platform.getInstance().getUserManager();
                            boolean removed  = userManager.removeChatRoomMember(roomJID,myJid);
                            if (removed){
                                memberList.remove(pos);
                                notifyDataSetChanged();
                                Toast.makeText(context, "Successfully Removed ", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                            }

                            //goBack(true, false);
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
            }); */

            tv2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                    dialog.setMessage("Are you sure wanna Dismiss this member as a owner from group?");
                    dialog.setCancelable(true);

                    dialog.setPositiveButton(" YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            AndroidUserManager userManager = (AndroidUserManager) Platform.getInstance().getUserManager();
                            boolean success  = userManager.addChatRoomOwner(roomJID,jid);
                            if (success){
                                Toast.makeText(context, "Successfully Dismiss ", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                            }

                            //goBack(true, false);
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
            tv3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                    dialog.setMessage("Are you sure wanna make this member admin of group?");
                    dialog.setCancelable(true);

                    dialog.setPositiveButton(" YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            AndroidUserManager userManager = (AndroidUserManager) Platform.getInstance().getUserManager();
                            boolean admin  = userManager.addChatRoomAdmin(roomJID,jid);
                            if (admin){
                                memberList.get(pos);
                                notifyDataSetChanged();
                                Toast.makeText(context, "Successfully Added ", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                            }

                            //goBack(true, false);
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
            //1)  message selectMemberName
            //2) view selectMemberName
            //3) remove selectMemberName
            //4) make group admin
            //5) dismiss as owner
        }
    }

    private void showRemoveParticipantAlert(final JID jid, final int pos) {
        LayoutInflater myLayout = LayoutInflater.from(context);
        final View dialogView = myLayout.inflate(R.layout.dialog_layout, null);
        final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        final AlertDialog dialog1 = dialog.create();
        dialog1.setView(dialogView);
        dialog1.show();

        final TextView tv1 = (TextView) dialogView.findViewById(R.id.tvRemove);

        tv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AndroidUserManager userManager = (AndroidUserManager) Platform.getInstance().getUserManager();
                boolean removed = userManager.removeChatRoomMember(roomJID, jid);

//                memberList.remove(pos);
//                notifyDataSetChanged();

                dialog1.dismiss();

                if (removed) {
                    Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(context, "Something went wrong while removing group member", Toast.LENGTH_SHORT).show();
                }

                refreshViewListener.refreshView();
            }
        });

        TextView tv2 = (TextView) dialogView.findViewById(R.id.tvOwner);
        tv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AndroidUserManager userManager = (AndroidUserManager) Platform.getInstance().getUserManager();
                //TODO: neeed to re handle it
                boolean success = userManager.addChatRoomOwner(roomJID, jid);
                memberList.get(pos);
                notifyDataSetChanged();
                dialog1.dismiss();
                if (success) {

                } else {
                    Toast.makeText(context, "Something went wrong", Toast.LENGTH_LONG).show();
                }

            }
        });
        TextView tv3 = (TextView) dialogView.findViewById(R.id.tvAdmin);
        tv3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AndroidUserManager userManager = (AndroidUserManager) Platform.getInstance().getUserManager();

                //TODO: neeed to re handle it
                boolean makeAdmin = userManager.addChatRoomAdmin(roomJID, jid);
                memberList.get(pos);
                notifyDataSetChanged();
                dialog1.dismiss();
                if (makeAdmin) {

                } else {
                    Toast.makeText(context, "Sommething went wrong", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

}



