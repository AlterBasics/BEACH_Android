package abs.sf.beach.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import abs.ixi.client.core.Platform;
import abs.ixi.client.util.StringUtils;
import abs.ixi.client.xmpp.packet.ChatRoom;
import abs.sf.beach.android.R;
import abs.sf.beach.utils.AndroidUtils;
import abs.sf.client.android.managers.AndroidUserManager;

public class CreateGroupActivity extends StringflowActivity{
    private Spinner spGroupType;
    private ImageView ivGroup;
    private Button btnAddParticipant;
    private String selectedGroupType;
    private EditText etGroupName;
    private ImageView ivBack, ivNext;
    private TextView tvHeader;

    private AndroidUserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        initView();
        initOnclickListener();

        this.userManager = (AndroidUserManager) Platform.getInstance().getUserManager();
    }

    private void initView(){
        spGroupType = (Spinner) findViewById(R.id.spGroupType);
        ivGroup = (ImageView) findViewById(R.id.ivGroup);
        btnAddParticipant = (Button) findViewById(R.id.btnAddParticipant);
        etGroupName = (EditText) findViewById(R.id.etGroupName);
        ivBack = (ImageView) findViewById(R.id.ivBack);
        ivNext = (ImageView) findViewById(R.id.ivNext);
        ivNext.setVisibility(View.INVISIBLE);
        tvHeader = (TextView) findViewById(R.id.tvHeader);
        tvHeader.setText(getIntent().getStringExtra("name"));

        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.hide();
        }
    }

    private void initOnclickListener(){
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateGroupActivity.this.finish();
            }
        });
        spGroupType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedGroupType = adapterView.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btnAddParticipant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String groupName = etGroupName.getText().toString();

                //TODO : Start taking group members list at he time of group creation.

                if(StringUtils.isNullOrEmpty(groupName)){
                    AndroidUtils.showToast(CreateGroupActivity.this, "Please enter group name");

                }else{
                    Intent i1 = new Intent(CreateGroupActivity.this,AddMember.class);
                    i1.putExtra("group name ", etGroupName.getText().toString());
                    startActivity(i1);


                   /* boolean created = false;

                    if(StringUtils.safeEquals(selectedGroupType, ChatRoom.AccessMode.PUBLIC.val(), false)){
                       userManager.createPublicGroup(groupName, null);

                    } else if(StringUtils.safeEquals(selectedGroupType, ChatRoom.AccessMode.PRIVATE.val(), false)){
                        userManager.createPrivateGroup(groupName, null);
                    }

                    if(created) {
                        //TODO : Show popup group created successfully
                        Intent i1 = new Intent(CreateGroupActivity.this,AddMember.class);
                        i1.putExtra("group name ", etGroupName.getText().toString());
                        i1.putExtra("grp Type",spGroupType.getBaseline());
                        startActivity(i1);

                        CreateGroupActivity.this.finish();

                    } else {
                        //TODO : Show popup group error pouup
                    }*/
                }
            }
        });
    }
}
