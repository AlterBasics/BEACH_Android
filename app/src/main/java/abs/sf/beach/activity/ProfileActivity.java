package abs.sf.beach.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import abs.ixi.client.core.Platform;
import abs.ixi.client.xmpp.JID;
import abs.ixi.client.xmpp.packet.UserProfileData;
import abs.sf.beach.activity.StringflowActivity;
import abs.sf.beach.android.R;
import abs.sf.beach.utils.CommonConstants;
import abs.sf.client.android.managers.AndroidUserManager;

public class ProfileActivity extends StringflowActivity {
    private ImageView ivBack, ivNext;
    private TextView tvHeaders;
    private EditText etMessage;
    private TextView tvUSerName, tvUSerFirstName, tvUSerMiddleName, tvUSerLastName, tvUSerNickName, tvUSerGender, tvUSerBday,
            tvUSerPhoneNo, tvUSerEmail, tvUSerHome, tvUSerStreet, tvUSerLocality, tvUSerPinCode, tvUSerCity, tvUSerState, tvUSerCountry, tvUSerAbout;
    private ScrollView scrollView;
    private ImageView ivProfilePic;
    private boolean cached;
    private JID jid;
    private String Name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initView();
        initOnClicklitener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        jid = (JID) getIntent().getSerializableExtra(CommonConstants.JID);
        ShowData();
    }


    private void initView() {
        ivBack = (ImageView) findViewById(R.id.ivBack);
        ivNext = (ImageView) findViewById(R.id.ivNext);
        tvHeaders = (TextView) findViewById(R.id.tvHeaders);
        tvHeaders.setText("User Profile");
        tvHeaders.setVisibility(View.VISIBLE);
        tvHeaders.setGravity(Gravity.CENTER);
        ivNext.setVisibility(View.INVISIBLE);
        ivProfilePic = (ImageView) findViewById(R.id.ivUser);
//        tvUSerName = (TextView) findViewById(R.id.etUsername);
//        // tvUSerName.setMovementMethod(new ScrollingMovementMethod());
//        tvUSerFirstName = (TextView) findViewById(R.id.etUserFirstName);
//        tvUSerMiddleName = (TextView) findViewById(R.id.etUserMiddletName);
//        tvUSerLastName = (TextView) findViewById(R.id.etUserMiddletName);
//        tvUSerNickName = (TextView) findViewById(R.id.etUserNickName);
//        tvUSerNickName.setMovementMethod(new ScrollingMovementMethod());
//        tvUSerGender = (TextView) findViewById(R.id.etUserGender);
//        tvUSerBday = (TextView) findViewById(R.id.etUserBday);
//        tvUSerPhoneNo = (TextView) findViewById(R.id.etUserPhone);
//        tvUSerEmail = (TextView) findViewById(R.id.etUserEmail);
//        tvUSerHome = (TextView) findViewById(R.id.etUserHome);
//        tvUSerStreet = (TextView) findViewById(R.id.etUserStreet);
//        tvUSerLocality = (TextView) findViewById(R.id.etUserLocality);
//        tvUSerPinCode = (TextView) findViewById(R.id.etUserPinCode);
//        tvUSerCity = (TextView) findViewById(R.id.etUserCity);
//        tvUSerState = (TextView) findViewById(R.id.etUserState);
//        tvUSerCountry = (TextView) findViewById(R.id.etUserCountry);
//        tvUSerAbout = (TextView) findViewById(R.id.etUserAbout);
//        tvUSerAbout.setMovementMethod(new ScrollingMovementMethod());

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

    }

    private void initOnClicklitener() {
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProfileActivity.this.finish();
            }
        });

    }

    private void ShowData() {

        if (cached) {
            AndroidUserManager userManager = (AndroidUserManager) Platform.getInstance().getUserManager();
            userManager.getCachedUserProfileData(jid);
        } else {
            AndroidUserManager userManager = (AndroidUserManager) Platform.getInstance().getUserManager();
            userManager.getUserProfileData(jid);

        }

    }

}
