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

import abs.ixi.client.Platform;
import abs.ixi.client.util.StringUtils;
import abs.ixi.client.xmpp.JID;
import abs.ixi.client.xmpp.packet.ChatRoom;
import abs.sf.beach.activity.ChatActivity;
import abs.sf.beach.activity.ProfileActivity;
import abs.sf.beach.android.R;
import abs.sf.beach.utils.CommonConstants;
import abs.sf.beach.utils.OnRefreshViewListener;
import abs.sf.client.android.managers.AndroidUserManager;

public class GroupDetailsAdapter extends RecyclerView.Adapter<GroupDetailsAdapter.ViewHolder> {
    private Context context;
    private OnRefreshViewListener refreshViewListener;
    private List<ChatRoom.ChatRoomMember> memberList;
    private JID roomJID, myJid;
    private String groupName;
    private ChatRoom.ChatRoomMember loggedInMember;

    public GroupDetailsAdapter(Context context, List<ChatRoom.ChatRoomMember> memberList, JID roomJID, String groupName, ChatRoom.ChatRoomMember loggedInMember) {
        this.context = context;
        this.refreshViewListener = (OnRefreshViewListener) context;
        this.memberList = memberList;
        this.roomJID = roomJID;
        this.groupName = groupName;
        this.loggedInMember = loggedInMember;
        this.myJid = Platform.getInstance().getUserJID();
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
                        ChatRoom.ChatRoomMember selectMember = memberList.get(getAdapterPosition());

                        if (!loggedInMember.equals(selectMember)) {

                            if (loggedInMember.getAffiliation() == ChatRoom.Affiliation.ADMIN || loggedInMember.getAffiliation() == ChatRoom.Affiliation.OWNER) {
                                showOnClickDialog(selectMember);

                            } else if(loggedInMember.getAffiliation() == ChatRoom.Affiliation.MEMBER) {
                                showMemberClickDialog(selectMember);
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

    private void showMemberClickDialog(final ChatRoom.ChatRoomMember selectedMember) {
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
        tv1.setVisibility(View.GONE);

        TextView tv2 = (TextView) dialogView.findViewById(R.id.tvOwner);
        tv2.setVisibility(View.GONE);

        TextView tv3 = (TextView) dialogView.findViewById(R.id.tvAdmin);
        tv3.setVisibility(View.GONE);

        tv4.append("Message" + " " + selectMemberName);
        tv4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("jid", selectedMember.getUserJID());
                intent.putExtra("name", selectMemberName);
                intent.putExtra("from", "Group Detail");
                context.startActivity(intent);
            }
        });

        tv5.append("View" + " " + selectMemberName);
        tv5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,ProfileActivity.class);
                intent.putExtra(CommonConstants.JID, selectedMember.getUserJID());
                context.startActivity(intent);
                dialog1.dismiss();
            }
        });
    }

    private void showOnClickDialog(final ChatRoom.ChatRoomMember selectedMember) {
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


        tv4.append("Message" + " " + selectMemberName);
        tv4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("jid", selectedMember.getUserJID());
                intent.putExtra("name", selectMemberName);
                intent.putExtra("from", "Group Detail");
                context.startActivity(intent);
            }
        });

        tv5.append("View" + " " + selectMemberName);
        tv5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,ProfileActivity.class);
                intent.putExtra(CommonConstants.JID, selectedMember.getUserJID());
                context.startActivity(intent);
                dialog1.dismiss();
            }
        });


        tv1.append("Remove" + " " + selectMemberName);
        tv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                dialog.setMessage("Remove " + selectMemberName + " from " + groupName + " group?");
                dialog.setCancelable(true);

                dialog.setPositiveButton(" YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AndroidUserManager userManager = (AndroidUserManager) Platform.getInstance().getUserManager();

                        boolean removed = userManager.removeChatRoomMember(roomJID, selectedMember.getUserJID());

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

        if (selectedMember.getAffiliation() == ChatRoom.Affiliation.MEMBER) {
            tv2.setText("make group owner");
            tv2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                    dialog.setMessage("make " + selectMemberName + " owner of " + groupName  + " group? ");
                    dialog.setCancelable(true);

                    dialog.setPositiveButton(" YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            AndroidUserManager userManager = (AndroidUserManager) Platform.getInstance().getUserManager();
                            boolean success = userManager.addChatRoomOwner(roomJID, selectedMember.getUserJID());

                            if (success) {
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

            tv3.setText("make group admin");
            tv3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                    dialog.setMessage("make " + selectMemberName + " admin of " + groupName  + " group? ");
                    dialog.setCancelable(true);

                    dialog.setPositiveButton(" YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            AndroidUserManager userManager = (AndroidUserManager) Platform.getInstance().getUserManager();
                            boolean success = userManager.addChatRoomAdmin(roomJID, selectedMember.getUserJID());

                            if (success) {
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

        } else if (selectedMember.getAffiliation() == ChatRoom.Affiliation.ADMIN) {
            tv2.setText("make group owner");
            tv2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                    dialog.setMessage("make " + selectMemberName + " owner of " + groupName  + " group? ");
                    dialog.setCancelable(true);

                    dialog.setPositiveButton(" YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            AndroidUserManager userManager = (AndroidUserManager) Platform.getInstance().getUserManager();
                            boolean success = userManager.addChatRoomOwner(roomJID, selectedMember.getUserJID());

                            if (success) {
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

            tv3.setText("Dissmis as Admin");
            tv3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                    dialog.setMessage("Dismiss " + selectMemberName + " from " + groupName + " group admin?");
                    dialog.setCancelable(true);

                    dialog.setPositiveButton(" YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            AndroidUserManager userManager = (AndroidUserManager) Platform.getInstance().getUserManager();
                            boolean admin = userManager.addChatRoomMember(roomJID, selectedMember.getUserJID());

                            if (admin) {
                                Toast.makeText(context, "Successfully Dismiss ", Toast.LENGTH_SHORT).show();
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

        } else if (selectedMember.getAffiliation() == ChatRoom.Affiliation.OWNER) {
            tv2.setText("Dissmis as owner");
            tv2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                    dialog.setMessage("Dismiss " + selectMemberName + " from " + groupName + " group owner?");
                    dialog.setCancelable(true);

                    dialog.setPositiveButton(" YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            AndroidUserManager userManager = (AndroidUserManager) Platform.getInstance().getUserManager();
                            boolean admin = userManager.addChatRoomMember(roomJID, selectedMember.getUserJID());

                            if (admin) {
                                Toast.makeText(context, "Successfully Dismiss ", Toast.LENGTH_SHORT).show();
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

            tv3.setText("make group admin");
            tv3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                    dialog.setMessage("make " + selectMemberName + " admin of " + groupName  + " group? ");
                    dialog.setCancelable(true);

                    dialog.setPositiveButton(" YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            AndroidUserManager userManager = (AndroidUserManager) Platform.getInstance().getUserManager();
                            boolean success = userManager.addChatRoomAdmin(roomJID, selectedMember.getUserJID());

                            if (success) {
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
}



