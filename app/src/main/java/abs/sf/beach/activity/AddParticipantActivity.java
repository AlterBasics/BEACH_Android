package abs.sf.beach.activity;

import android.nfc.Tag;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import abs.ixi.client.core.Platform;
import abs.ixi.client.util.CollectionUtils;
import abs.ixi.client.xmpp.JID;
import abs.ixi.client.xmpp.packet.ChatRoom;
import abs.ixi.client.xmpp.packet.Roster;
import abs.sf.beach.adapter.AddParticipantAdapters;
import abs.sf.beach.android.R;
import abs.sf.beach.utils.CommonConstants;
import abs.sf.client.android.managers.AndroidUserManager;

public class AddParticipantActivity extends StringflowActivity {
    private static final String TAG = "AddParticipantActivity";
    private List<Roster.RosterItem> allRosterItems;
    private JID roomJID;
    private ChatRoom chatRoom;
    private AddParticipantAdapters adapter;
    private RecyclerView tvAddParticipant;
    private ImageView ivBack, ivNext;
    private TextView tvHeaders;
    private EditText etMessage;
    private TextView tvAdd;
    private List<JID> selectedGroupMembers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_participant);
        initView();
        initOnClickListener();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "check");
        super.onResume();

        this.roomJID = (JID) getIntent().getSerializableExtra(CommonConstants.JID);

        AndroidUserManager userManager = (AndroidUserManager) Platform.getInstance().getUserManager();
        this.chatRoom = userManager.getChatRoomDetails(this.roomJID);

        this.allRosterItems = getFilteredRosterItems();

        this.selectedGroupMembers = new ArrayList<>();

        setAdapter();
    }

    private void initView() {
        ivBack = (ImageView) findViewById(R.id.ivBack);
        ivNext = (ImageView) findViewById(R.id.ivNext);
        tvHeaders = (TextView) findViewById(R.id.tvHeaders);
        tvHeaders.setText("Add Participant");
        tvHeaders.setVisibility(View.VISIBLE);
        tvHeaders.setGravity(Gravity.CENTER);
        ivNext.setVisibility(View.INVISIBLE);
        etMessage = (EditText) findViewById(R.id.etMessage);
        tvAdd = (TextView) findViewById(R.id.tvCreateGroup);
        tvAdd.setText("Add in Group");

        tvAddParticipant = (RecyclerView) findViewById(R.id.rvAddParticipants);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.hide();
        }
    }

    private void initOnClickListener() {
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddParticipantActivity.this.finish();
            }
        });

        etMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!CollectionUtils.isNullOrEmpty(allRosterItems) && adapter != null) {
                    adapter.filterData(s);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        tvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (CollectionUtils.isNullOrEmpty(selectedGroupMembers)) {

                    Toast.makeText(AddParticipantActivity.this, "No participant selected", Toast.LENGTH_SHORT).show();

                } else {
                    AndroidUserManager userManager = (AndroidUserManager) Platform.getInstance().getUserManager();

                    for (JID memberJID : selectedGroupMembers) {
                        userManager.addChatRoomMember(roomJID, memberJID);
                    }

                }

                AddParticipantActivity.this.finish();
            }
        });
    }

    private List<Roster.RosterItem> getFilteredRosterItems() {
        AndroidUserManager userManager = (AndroidUserManager) Platform.getInstance().getUserManager();
        List<Roster.RosterItem> allRosterItems = userManager.getRosterItemList();

        List<Roster.RosterItem> filteredRosterItems = new ArrayList<>();

        for (Roster.RosterItem item : allRosterItems) {
            if (!this.chatRoom.isRoomMember(item.getJid())) {
                filteredRosterItems.add(item);
            }
        }

        return filteredRosterItems;
    }

    private void setAdapter() {
        adapter = new AddParticipantAdapters(allRosterItems, selectedGroupMembers, context());
        tvAddParticipant.setLayoutManager(new LinearLayoutManager(context()));
        tvAddParticipant.setAdapter(adapter);
    }
}
