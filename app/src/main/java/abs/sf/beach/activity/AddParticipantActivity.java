package abs.sf.beach.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
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
    private List<Roster.RosterItem> allRosterItems;
    private JID roomJID;
    private ChatRoom chatRoom;
    private AddParticipantAdapters adapter;
    private RecyclerView tvAddParticipant;
    private ImageView ivBack, ivNext;
    private TextView tvHeaders;
    private EditText etMessage;
    private String groupName;
    private String groupType;
    private Button btnAdd;
    private  List<JID> selectedGroupMembers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_participant);
        initView();
        initOnClickListener();
    }




    @Override
    protected void onResume() {
        super.onResume();
        this.allRosterItems = getUserRosterItems();
        this.groupName = getIntent().getStringExtra(CommonConstants.GROUP_NAME);
        //this.groupType = getIntent().getStringExtra(CommonConstants.GROUP_TYPE);
        this.selectedGroupMembers = new ArrayList<>();
        setAdapter();
    }

    private void initView() {
//        ivBack = (ImageView) findViewById(R.id.ivBack);
//        ivNext = (ImageView) findViewById(R.id.ivNext);
//        tvHeaders = (TextView) findViewById(R.id.tvHeaders);
//       // tvHeaders.setText("Add Participant");
//        tvHeaders.setVisibility(View.VISIBLE);
//        tvHeaders.setGravity(Gravity.CENTER);
//        ivNext.setVisibility(View.INVISIBLE);
        etMessage = (EditText) findViewById(R.id.etMessage);
        btnAdd = (Button)findViewById(R.id.btnAdd);
        tvAddParticipant = (RecyclerView) findViewById(R.id.rvAddParticipant);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.hide();
        }
    }

    private void initOnClickListener() {
//        ivBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                AddParticipantActivity.this.finish();
//            }
//        });

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

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AndroidUserManager chatManager = (AndroidUserManager) Platform.getInstance().getUserManager();

                //TODO: need to re handle this
                boolean added = chatManager.addChatRoomMember(roomJID, (JID) selectedGroupMembers);
                if (added){
                    Toast.makeText(AddParticipantActivity.this,"Successfully Added",Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(AddParticipantActivity.this,"Something went wrong",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private List<Roster.RosterItem> getUserRosterItems() {
        AndroidUserManager userManager = (AndroidUserManager) Platform.getInstance().getUserManager();
        return userManager.getRosterItemList();
    }
    private  void setAdapter(){
        adapter = new AddParticipantAdapters( allRosterItems,selectedGroupMembers,context());
        tvAddParticipant.setLayoutManager(new LinearLayoutManager(context()));
        tvAddParticipant.setAdapter(adapter);


    }
}
