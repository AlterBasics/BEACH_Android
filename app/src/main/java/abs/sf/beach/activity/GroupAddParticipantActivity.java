package abs.sf.beach.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import abs.ixi.client.core.Platform;
import abs.ixi.client.util.CollectionUtils;
import abs.ixi.client.util.StringUtils;
import abs.ixi.client.xmpp.JID;
import abs.ixi.client.xmpp.packet.ChatRoom;
import abs.ixi.client.xmpp.packet.Roster;
import abs.sf.beach.adapter.GroupAddParticipantAdapter;
import abs.sf.beach.android.R;
import abs.sf.beach.utils.AndroidUtils;
import abs.sf.client.android.managers.AndroidUserManager;

public class GroupAddParticipantActivity extends StringflowActivity {
    private ImageView ivBack, ivNext;
    private TextView tvHeaders, tvCreategroup;
    private EditText etMessage;
    private RecyclerView rvAddParticipant;
    private GroupAddParticipantAdapter adapter;
    private List<Roster.RosterItem> allRosterItems;
    private String groupName;
    private String groupType;
    private  List<JID> selectedGroupMembers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_add_participant);
        initView();
        initClickListener();
        adapter = new GroupAddParticipantAdapter(allRosterItems, selectedGroupMembers, context());
        rvAddParticipant.setLayoutManager(new LinearLayoutManager(context()));
        rvAddParticipant.setAdapter(adapter);


    }

    private void initView() {
        ivBack = (ImageView) findViewById(R.id.ivBack);
        ivNext = (ImageView) findViewById(R.id.ivNext);
        tvHeaders = (TextView) findViewById(R.id.tvHeaders);
        tvCreategroup = (TextView) findViewById(R.id.tvCreateGroup);
        tvHeaders.setText("Add Participant");
        tvHeaders.setVisibility(View.VISIBLE);
        tvHeaders.setGravity(Gravity.CENTER);
        ivNext.setVisibility(View.INVISIBLE);
        etMessage = (EditText) findViewById(R.id.etMessage);
        rvAddParticipant = (RecyclerView) findViewById(R.id.rvAddParticipant);
        this.allRosterItems = getUserRosterItems();
        this.groupName = getIntent().getStringExtra("group_name");
        this.groupType = getIntent().getStringExtra("group_type");
        this.selectedGroupMembers = new ArrayList<>();

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.hide();
        }
    }

    private void initClickListener() {
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GroupAddParticipantActivity.this.finish();
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

        tvCreategroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean created = false;

                AndroidUserManager userManager = (AndroidUserManager) Platform.getInstance().getUserManager();

                if (StringUtils.safeEquals(groupType, ChatRoom.AccessMode.PUBLIC.val(), false)) {
                    created = userManager.createPublicGroup(groupName, selectedGroupMembers);

                } else if (StringUtils.safeEquals(groupType, ChatRoom.AccessMode.PRIVATE.val(), false)) {
                    created = userManager.createPrivateGroup(groupName, selectedGroupMembers);
                }

                if (created) {
                    AndroidUtils.showToast(GroupAddParticipantActivity.this, "Group created successfully");

                } else {
                    AndroidUtils.showToast(GroupAddParticipantActivity.this, "Something went wrong");
                }

                startActivity(new Intent(GroupAddParticipantActivity.this, ChatBaseActivity.class));
                finish();
            }
        });
    }

    private List<Roster.RosterItem> getUserRosterItems() {
        AndroidUserManager userManager = (AndroidUserManager) Platform.getInstance().getUserManager();
        return userManager.getRosterItemList();
    }

}
