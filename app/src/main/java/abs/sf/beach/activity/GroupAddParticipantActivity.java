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
import abs.ixi.client.xmpp.packet.Roster;
import abs.sf.beach.adapter.AddParticipantAdapter;
import abs.sf.beach.adapter.GroupAddParticipantAdapter;
import abs.sf.beach.android.R;
import abs.sf.client.android.managers.AndroidUserManager;

public class GroupAddParticipantActivity extends StringflowActivity {
    private ImageView ivBack, ivNext;
    private TextView tvHeader;
    private EditText etMessage;
    private RecyclerView rvAddParticipant;
    private GroupAddParticipantAdapter adapter;
    private  List<Roster.RosterItem> itemList;
    private  String groupName;
    private  String groupType;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member);
        initView();
        initClickListener();
        adapter = new GroupAddParticipantAdapter(itemList, null, groupName,context());
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
        this.itemList = getUserRosterItems();
        groupName = getIntent().getStringExtra("group_name");
        groupType = getIntent().getStringExtra("group_type");
    }

    private void initClickListener(){

        etMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!CollectionUtils.isNullOrEmpty(itemList) && adapter!=null) {
                    adapter.filterData(s);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private List<Roster.RosterItem> getUserRosterItems() {
        AndroidUserManager userManager = (AndroidUserManager) Platform.getInstance().getUserManager();
        return userManager.getRosterItemList();

    }
}
