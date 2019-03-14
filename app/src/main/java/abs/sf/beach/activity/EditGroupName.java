package abs.sf.beach.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import abs.ixi.client.Platform;
import abs.ixi.client.util.StringUtils;
import abs.ixi.client.xmpp.JID;
import abs.sf.beach.android.R;
import abs.sf.beach.utils.CommonConstants;
import abs.sf.client.android.managers.AndroidUserManager;

public class EditGroupName extends StringflowActivity {
    private EditText etgroupName;
    private TextView tvHeader;
    private CardView cvCancel, cvOk;
    private ImageView ivBack, ivNext;
    private JID roomJID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_group_name);
        initView();
        initOnClickListener();
    }

    private void initView() {
        etgroupName = (EditText) findViewById(R.id.etgroupName);
        ivBack = (ImageView) findViewById(R.id.ivBack);
        ivNext = (ImageView) findViewById(R.id.ivNext);
        ivNext.setVisibility(View.INVISIBLE);
        tvHeader = (TextView) findViewById(R.id.tvHeader);
        tvHeader.setText(CommonConstants.ENTER_NEW_SUBJECT);
        cvCancel = (CardView) findViewById(R.id.cvCancel);
        cvOk = (CardView) findViewById(R.id.cvOk);
        this.roomJID = (JID) getIntent().getSerializableExtra(CommonConstants.JID);


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

    }

    private void initOnClickListener() {
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditGroupName.this.finish();
            }
        });

        cvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditGroupName.this.finish();
            }
        });

        cvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newSubject = etgroupName.getText().toString();

                if (StringUtils.isNullOrEmpty(newSubject)) {
                    Toast.makeText(EditGroupName.this, "Please enter the group name", Toast.LENGTH_SHORT).show();
                } else {
                    AndroidUserManager userManager = (AndroidUserManager) Platform.getInstance().getUserManager();
                    boolean nameChanged = userManager.updateRoomSubject(roomJID, newSubject);
                    if (nameChanged) {
                        Toast.makeText(EditGroupName.this, "Group Name changed", Toast.LENGTH_SHORT).show();

                    }

                    EditGroupName.this.finish();
                }
            }
        });
    }
}
