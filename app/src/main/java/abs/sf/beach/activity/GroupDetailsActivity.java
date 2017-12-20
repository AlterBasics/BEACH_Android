package abs.sf.beach.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import abs.ixi.client.core.Platform;
import abs.ixi.client.core.Session;
import abs.ixi.client.util.StringUtils;
import abs.ixi.client.xmpp.JID;
import abs.ixi.client.xmpp.packet.ChatRoom;
import abs.ixi.client.xmpp.packet.Roster;
import abs.sf.beach.adapter.GroupDetailsAdapter;
import abs.sf.beach.android.R;
import abs.sf.beach.fragment.AddParticipantFragment;
import abs.sf.beach.utils.AddParticipantsListner;
import abs.sf.beach.utils.AndroidUtils;
import abs.sf.client.android.db.DbManager;
import abs.sf.client.android.managers.AndroidUserManager;


public class GroupDetailsActivity extends StringflowActivity implements AddParticipantsListner {
    private RecyclerView recyclerView;
    private ImageView ivBack, ivNext, ivContactImage;
    private TextView tvHeader, tvParticipants, tvAddParticipants, tvGroupName, tvGroupType;
    private CardView cvExitGroup, cvReportSpam;
    private FrameLayout addParticipantContainer;
    private ChatRoom chatRoom;
    private List<ChatRoom.ChatRoomMember> memberList;
    private JID roomJID;
    private Fragment fragment;
    private boolean isFragmentOpen;
    private boolean isGroupMember;
    private GroupDetailsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_details);
        initView();
        setChatAdapter();
        initOnclickListener();
    }

    @Override
    public void add(String action, Roster.RosterItem item) {
        if(StringUtils.safeEquals(action, "requestAddParticipant")){
            ChatRoom.ChatRoomMember member =  chatRoom.new ChatRoomMember(item.getJid(),item.getJid().getNode());
            memberList.add(member);
            if(adapter!=null){
                adapter.notifyDataSetChanged();
            }
        }
        closeFragment();
    }

    @Override
    public void remove(int pos){
        memberList.remove(pos);
    }

    private void initView() {
        ivBack = (ImageView) findViewById(R.id.ivBack);
        ivNext = (ImageView) findViewById(R.id.ivNext);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        tvHeader = (TextView) findViewById(R.id.tvHeader);
        tvParticipants = (TextView) findViewById(R.id.tvParticipants);
        tvAddParticipants = (TextView) findViewById(R.id.tvAddParticipants);
        cvExitGroup = (CardView) findViewById(R.id.cvExitGroup);
        cvReportSpam = (CardView) findViewById(R.id.cvReportSpam);
        addParticipantContainer = (FrameLayout) findViewById(R.id.addParticipantContainer);
        ivNext.setVisibility(View.INVISIBLE);
        tvGroupName = (TextView) findViewById(R.id.tvGroupName);
        tvGroupType = (TextView) findViewById(R.id.tvGroupType);
        ivContactImage = (ImageView) findViewById(R.id.ivContactImage);
        roomJID = (JID) getIntent().getSerializableExtra("jid");
        tvGroupName.setText(getIntent().getStringExtra("name"));
        tvGroupType.setText("Public");
        tvHeader.setVisibility(View.GONE);
        isGroupMember = getIntent().getBooleanExtra("isGroupMember", false);
        isFragmentOpen = false;
        if (!isGroupMember) {
            ((TextView) findViewById(R.id.tvExitGroup)).setText("Delete Group");
        }

        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.hide();
        }
    }

    private void setChatAdapter() {
        chatRoom = DbManager.getInstance().getChatRoomDetails(roomJID.getBareJID());
        Set<ChatRoom.ChatRoomMember> members = chatRoom.getMembers();
        tvParticipants.setText(members.size() + " Participants");
        memberList = new ArrayList<>(members);
        ChatRoom.ChatRoomMember cm = getChatRoomMember(memberList);
        if (cm != null) {
            if (!StringUtils.isNullOrEmpty(cm.getAffiliation().val()) && StringUtils.safeEquals(cm.getAffiliation().val(), ChatRoom.Affiliation.ADMIN.val()) ||
                    StringUtils.safeEquals(cm.getAffiliation().val(), ChatRoom.Affiliation.OWNER.val())) {
                tvAddParticipants.setVisibility(View.VISIBLE);
            }
        }
        adapter = new GroupDetailsAdapter(GroupDetailsActivity.this, memberList, roomJID, cm);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(GroupDetailsActivity.this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void initOnclickListener() {
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GroupDetailsActivity.this.finish();
            }
        });
        cvExitGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isGroupMember) {
                    AndroidUserManager.getInstance().deleteChatRoom(roomJID);
                    goBack(true, false);
                } else {
                    showExitGroupAlert();
                }
            }
        });
        cvReportSpam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showReportSpamAlert();
            }
        });
        tvAddParticipants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Roster.RosterItem> items = getAddRecipients();
                openFragment(AddParticipantFragment.newInstance(items, roomJID, getIntent().getStringExtra("name")));
            }
        });
    }

    private ChatRoom.ChatRoomMember getChatRoomMember(List<ChatRoom.ChatRoomMember> chatRoomMembers) {
        JID jid = (JID) Platform.getInstance().getSession().get(Session.KEY_USER_JID);
        for (ChatRoom.ChatRoomMember member : chatRoomMembers) {
            if (StringUtils.safeEquals(jid.getBareJID(), member.getUserJID().getBareJID())) {
                return member;
            }
        }
        return null;
    }

    private List<Roster.RosterItem> getAddRecipients() {
        List<Roster.RosterItem> items = new ArrayList<>();

        List<Roster.RosterItem> rosterItems = DbManager.getInstance().getRosterList();
        if (rosterItems != null && rosterItems.size() > 0 && memberList.size() > 0) {
            for (Roster.RosterItem rItem : rosterItems) {
                boolean isExist = false;
                for (ChatRoom.ChatRoomMember cm : memberList) {
                    if (StringUtils.safeEquals(rItem.getJid().getBareJID(), cm.getUserJID().getBareJID())) {
                        isExist = true;
                        break;
                    }
                }
                if (!isExist) {
                    items.add(rItem);
                }
            }
        }
        return items;
    }

    private void openFragment(Fragment fragment) {
        this.fragment = fragment;
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.addParticipantContainer, fragment);
        fragmentTransaction.commit();
        addParticipantContainer.setVisibility(View.VISIBLE);
        isFragmentOpen = true;
    }

    private void closeFragment() {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.remove(fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
        ft.commit();
        addParticipantContainer.setVisibility(View.GONE);
        isFragmentOpen = false;
    }

    private void showReportSpamAlert() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(GroupDetailsActivity.this);
        dialog.setMessage("Report spam and leave this group?");
        dialog.setCancelable(true);
        dialog.setPositiveButton("REPORT AND LEAVE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AndroidUtils.showToast(GroupDetailsActivity.this, "Report has been taken against this group.");
                AndroidUserManager.getInstance().leaveChatRoom(roomJID);
                AndroidUserManager.getInstance().deleteChatRoom(roomJID);
                goBack(true, false);
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

    private void showExitGroupAlert() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(GroupDetailsActivity.this);
        dialog.setMessage("Exit " + getIntent().getStringExtra("name") + " group?");
        dialog.setCancelable(true);
        dialog.setPositiveButton("EXIT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AndroidUserManager.getInstance().leaveChatRoom(roomJID);
                goBack(false, false);
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

    private void goBack(boolean isGroupDeleted, boolean isGroupMember) {
        Intent intent = new Intent();
        intent.putExtra("isGroupDeleted", isGroupDeleted);
        intent.putExtra("isGroupMember", isGroupMember);
        setResult(RESULT_OK, intent);
        GroupDetailsActivity.this.finish();
    }

    @Override
    public void onBackPressed() {
        if (isFragmentOpen) {
            closeFragment();
            return;
        }
        this.finish();
    }
}

