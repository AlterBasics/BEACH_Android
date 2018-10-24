package abs.sf.beach.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
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
import abs.sf.beach.utils.CommonConstants;
import abs.sf.client.android.managers.AndroidUserManager;

public class CreateGroupActivity extends StringflowActivity{
    private static final String TAG = "CreateGroupActivity";
    private Spinner spGroupType;
    private ImageView ivGroup;
    private Button btnAddParticipant;
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
        btnAddParticipant = (Button) findViewById(R.id.btnAddParticipant);
        etGroupName = (EditText) findViewById(R.id.etGroupName);
        ivBack = (ImageView) findViewById(R.id.ivBack);
        ivNext = (ImageView) findViewById(R.id.ivNext);
        ivNext.setVisibility(View.INVISIBLE);
        tvHeader = (TextView) findViewById(R.id.tvHeader);
        tvHeader.setText("Create Group");

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
            Log.d(TAG,"ADD");
                final String groupName = etGroupName.getText().toString();

                if(StringUtils.isNullOrEmpty(groupName)){
                    AndroidUtils.showToast(CreateGroupActivity.this, "Please enter group name");

                } else {
                    Intent intent = new Intent(CreateGroupActivity.this, GroupAddParticipantActivity.class);
                    intent.putExtra(CommonConstants.GROUP_NAME, groupName);
                    intent.putExtra(CommonConstants.GROUP_TYPE, selectedGroupType);  //spGroupType.getBaseline());
                    startActivity(intent);
                }
            }
        });
    }
}
