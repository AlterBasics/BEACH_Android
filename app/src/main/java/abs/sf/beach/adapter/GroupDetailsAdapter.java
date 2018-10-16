package abs.sf.beach.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import abs.ixi.client.core.Platform;
import abs.ixi.client.core.Session;
import abs.ixi.client.util.StringUtils;
import abs.ixi.client.xmpp.JID;
import abs.ixi.client.xmpp.packet.ChatRoom;
import abs.sf.beach.activity.GroupDetailsActivity;
import abs.sf.beach.android.R;
import abs.sf.client.android.managers.AndroidUserManager;

public class GroupDetailsAdapter extends RecyclerView.Adapter<GroupDetailsAdapter.ViewHolder> {

    private  Context context;
    private List<ChatRoom.ChatRoomMember> memberList;
    private JID roomJID, myJID;
    private ChatRoom.ChatRoomMember member;

    public GroupDetailsAdapter(Context context, List<ChatRoom.ChatRoomMember> memberList, JID roomJID, ChatRoom.ChatRoomMember member) {
        this.context = context;
        this.memberList = memberList;
        this.roomJID = roomJID;
        this.member = member;
        this.myJID = (JID) Platform.getInstance().getSession().get(Session.KEY_USER_JID);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public RelativeLayout rlContactRow;
        public ImageView ivContactImage, ivRemoveParticipants;
        public TextView  tvContactName,tvAffiliation;


        public ViewHolder(View itemView, Context c) {
            super(itemView);

            rlContactRow = (RelativeLayout) itemView.findViewById(R.id.rlContactRow);
            ivContactImage = (ImageView) itemView.findViewById(R.id.ivContactImage);
            tvContactName = (TextView) itemView.findViewById(R.id.tvContactName);
            tvAffiliation = (TextView) itemView.findViewById(R.id.tvAffiliation);

            rlContactRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!StringUtils.isNullOrEmpty(member.getAffiliation().val()) && StringUtils.safeEquals(member.getAffiliation().val(), ChatRoom.Affiliation.ADMIN.val()) ||
                            StringUtils.safeEquals(member.getAffiliation().val(), ChatRoom.Affiliation.OWNER.val())){
                        ChatRoom.ChatRoomMember m = memberList.get(getAdapterPosition());

                        if(!StringUtils.safeEquals(member.getUserJID().getBareJID(), m.getUserJID().getBareJID())){
                            showRemoveParticipantAlert(m.getUserJID(), getAdapterPosition());

                        } else {
                            //Do nothing
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
        String userName;
        if(!StringUtils.isNullOrEmpty(member.getUserJID().getBareJID()) && StringUtils.safeEquals(member.getUserJID().getBareJID(),
                myJID.getBareJID())){
            userName = "you";
        }else {
            AndroidUserManager userManager = (AndroidUserManager) Platform.getInstance().getUserManager();
            userName = userManager.getRosterItemName(member.getUserJID());
        }
        holder.tvContactName.setText(userName);
        holder.tvAffiliation.setText(member.getAffiliation().val());
    }

    @Override
    public int getItemCount() {
        return memberList.size();
    }

    private void showRemoveParticipantAlert(final JID jid, final int pos) {
        LayoutInflater myLayout = LayoutInflater.from(context);
        final  View dialogView = myLayout.inflate(R.layout.dialog_layout,null);
        final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
       // final AlertDialog dialog1 = dialog.create();
        dialog.setView(dialogView);

       final TextView tv1 = (TextView) dialogView.findViewById(R.id.tvRemove);
        tv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AndroidUserManager userManager = (AndroidUserManager) Platform.getInstance().getUserManager();
                //TODO: neeed to re handle it
                 boolean removed =  userManager.removeChatRoomMember(roomJID, jid);
                 if (removed){
                    memberList.remove(pos);
                    notifyDataSetChanged();
                    //dialog.dismiss();
                }
                 else{
                    Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
        });

        TextView tv2 = (TextView) dialogView.findViewById(R.id.tvOwner);
        tv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AndroidUserManager userManager = (AndroidUserManager) Platform.getInstance().getUserManager();
                //TODO: neeed to re handle it
                boolean success = userManager.addChatRoomOwner(roomJID, jid);
                 if (success){
                     memberList.get(pos);
                     notifyDataSetChanged();
                     // dialog.dismiss();
                 }
                 else {
                     Toast.makeText(context,"Something went wrong",Toast.LENGTH_LONG).show();
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
                if (makeAdmin){
                    memberList.get(pos);
                    notifyDataSetChanged();
                    //dialog1.dismiss();
                }
                else{
                    Toast.makeText(context,"Sommething went wrong",Toast.LENGTH_SHORT).show();
                }

            }
        });
        dialog.show();
       /* dialog.setPositiveButton("EXIT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AndroidUserManager userManager = (AndroidUserManager) Platform.getInstance().getUserManager();

                //TODO: neeed to re handle it
                boolean removed = userManager.removeChatRoomMember(roomJID, jid);

                memberList.remove(pos);
                notifyDataSetChanged();
                dialog.dismiss();
            }
        });
        dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog1.dismiss();
            }
        }); */


    }

}



