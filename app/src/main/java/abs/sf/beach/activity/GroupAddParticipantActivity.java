package abs.sf.beach.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
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
import abs.sf.beach.adapter.AddParticipantAdapter;
import abs.sf.beach.android.R;
import abs.sf.beach.utils.AddParticipantsListner;
import abs.sf.client.android.managers.AndroidUserManager;

public class GroupAddParticipantActivity extends StringflowActivity {
    private ImageView ivBack, ivNext;
    private TextView tvHeader;
    private EditText etMessage;
    private RecyclerView rvAddParticipant;
    private AddParticipantAdapter adapter;
    private  List<Roster.RosterItem> itemList;
    private static JID roomJID;
    private  String groupName;
    private  String groupType;
    private AddParticipantsListner participantsListner;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member);
        initView();
        initClickListener();




        adapter = new AddParticipantAdapter(itemList, participantsListner, null, groupName,context());
        rvAddParticipant.setLayoutManager(new LinearLayoutManager(context()));
        rvAddParticipant.setAdapter(adapter);


    }

    private void initView(){
        ivBack = (ImageView)findViewById(R.id.ivBack);
        ivNext = (ImageView) findViewById(R.id.ivNext);
        tvHeader = (TextView)findViewById(R.id.tvHeader);
        tvHeader.setText(groupName);
        ivNext.setVisibility(View.INVISIBLE);
        etMessage = (EditText)findViewById(R.id.etMessage);
        rvAddParticipant = (RecyclerView)findViewById(R.id.rvAddParticipant);
        participantsListner = (AddParticipantsListner) getApplication();
        this.itemList = getUserRosterItems();
        groupName = getIntent().getStringExtra("group_name");
        groupType = getIntent().getStringExtra("group_type");
    }

    private void initClickListener(){
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                participantsListner.add("backPress", null);
            }
        });

    }
    private List<Roster.RosterItem> getUserRosterItems() {
        AndroidUserManager userManager = (AndroidUserManager) Platform.getInstance().getUserManager();
        return userManager.getRosterItemList();

    }
}
