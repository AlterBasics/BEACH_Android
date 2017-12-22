package abs.sf.beach.activity;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import abs.ixi.client.util.StringUtils;
import abs.ixi.client.xmpp.packet.ChatRoom;
import abs.sf.beach.android.R;
import abs.sf.beach.utils.AndroidUtils;
import abs.sf.client.android.managers.AndroidUserManager;

import static abs.sf.beach.android.R.id.fragmentContainer;


public class CreateGroupActivity extends StringflowActivity{
    private Spinner spGroupType;
    private ImageView ivGroup;
    private Button btnCreateGroup;
    private String selectedGroupType;
    private EditText etGroupName;
    private ImageView ivBack, ivNext;
    private TextView tvHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        initView();
        initOnclickListener();
    }

    private void initView(){
        spGroupType = (Spinner) findViewById(R.id.spGroupType);
        ivGroup = (ImageView) findViewById(R.id.ivGroup);
        btnCreateGroup = (Button) findViewById(R.id.btnCreateGroup);
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

        btnCreateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String groupName = etGroupName.getText().toString();
                if(StringUtils.isNullOrEmpty(groupName)){
                    AndroidUtils.showToast(CreateGroupActivity.this, "Please enter group name");
                }else{
                    boolean isCreated = AndroidUserManager.getInstance().createChatRoom(groupName);
                    if(isCreated){
                        ChatRoom.AccessMode accessMode = ChatRoom.AccessMode.PUBLIC;
                        if(StringUtils.safeEquals(selectedGroupType, ChatRoom.AccessMode.PUBLIC.val())){
                            accessMode = ChatRoom.AccessMode.PRIVATE;
                        }
                        AndroidUserManager.getInstance().updateRoomAccessMode(groupName, accessMode);
                        CreateGroupActivity.this.finish();
                    }
                }
            }
        });
    }
}
