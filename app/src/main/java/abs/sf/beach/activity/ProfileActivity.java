package abs.sf.beach.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import abs.ixi.client.core.Platform;
import abs.ixi.client.xmpp.JID;
import abs.ixi.client.xmpp.packet.UserProfileData;
import abs.sf.beach.android.R;
import abs.sf.beach.utils.CommonConstants;
import abs.sf.client.android.managers.AndroidUserManager;

public class ProfileActivity extends StringflowActivity {
    private ImageView ivBack, ivNext;
    private TextView tvHeaders, tvCreatGrp;
    private EditText etMessage;
    private TextView tvUSerName, tvUSerFirstName, tvUSerMiddleName, tvUSerLastName, tvUSerNickName, tvUSerGender, tvUSerBday,
            tvUSerPhoneNo, tvUSerEmail, tvUSerAddress, tvUSerHome, tvUSerStreet, tvUSerLocality, tvUSerPinCode, tvUSerCity, tvUSerState, tvUSerCountry, tvUSerAbout;
    private ScrollView scroller;
    private ImageView ivProfilePic;

    private JID jid;

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
        this.jid = (JID) getIntent().getSerializableExtra(CommonConstants.JID);
        showData();

    }


    private void initView() {
        ivBack = (ImageView) findViewById(R.id.ivBack);
        tvCreatGrp = (TextView) findViewById(R.id.tvCreateGroup);
        ivNext = (ImageView) findViewById(R.id.ivNext);
        ivNext.setVisibility(View.GONE);
        tvHeaders = (TextView) findViewById(R.id.tvHeaders);
        tvHeaders.setText("User Profile");
        tvHeaders.setVisibility(View.VISIBLE);
        tvHeaders.setGravity(Gravity.CENTER);
        tvCreatGrp.setVisibility(View.GONE);
        ivProfilePic = (ImageView) findViewById(R.id.ivUser);
        tvUSerName = (TextView) findViewById(R.id.tvUsername);
        tvUSerFirstName = (TextView) findViewById(R.id.etUserFirstName);
        tvUSerMiddleName = (TextView) findViewById(R.id.etUserMiddletName);
        tvUSerLastName = (TextView) findViewById(R.id.etUserLastName);
        tvUSerNickName = (TextView) findViewById(R.id.etUserNickName);
        tvUSerGender = (TextView) findViewById(R.id.etUserGender);
        tvUSerBday = (TextView) findViewById(R.id.etUserBday);
        tvUSerPhoneNo = (TextView) findViewById(R.id.etUserPhone);
        tvUSerEmail = (TextView) findViewById(R.id.etUserEmail);
        tvUSerHome = (TextView) findViewById(R.id.etUserHome);
        tvUSerStreet = (TextView) findViewById(R.id.etUserStreet);
        tvUSerLocality = (TextView) findViewById(R.id.etUserLocality);
        tvUSerPinCode = (TextView) findViewById(R.id.etUserPinCode);
        tvUSerCity = (TextView) findViewById(R.id.etUserCity);
        tvUSerState = (TextView) findViewById(R.id.etUserState);
        tvUSerCountry = (TextView) findViewById(R.id.etUserCountry);
        tvUSerAbout = (TextView) findViewById(R.id.etUserAbout);
//        scroller=(ScrollView)findViewById(R.id.scroller);
//
//        //scroller.post(new Runnable() {
//            public void run() {
//                scroller.fullScroll(ScrollView.FOCUS_UP);
//            }
//        });

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

    private UserProfileData showData() {
        AndroidUserManager userManager = (AndroidUserManager) Platform.getInstance().getUserManager();
        UserProfileData userProfileData = userManager.getCachedUserProfileData(jid);

        if (userProfileData == null) {
            userProfileData = userManager.getUserProfileData(jid);

        }
        tvUSerName.setText(jid.getNode());
        tvUSerFirstName.setText(userProfileData.getFirstName());
        tvUSerMiddleName.setText(userProfileData.getMiddleName());
        tvUSerNickName.setText(userProfileData.getNickName());
        tvUSerLastName.setText(userProfileData.getLastName());
        tvUSerGender.setText(userProfileData.getGender());
        tvUSerBday.setText(userProfileData.getBday());
        tvUSerPhoneNo.setText(userProfileData.getPhone());
        tvUSerEmail.setText(userProfileData.getEmail());

        if(userProfileData.getAddress() != null) {
            tvUSerHome.setText(userProfileData.getAddress().getHome());
            tvUSerStreet.setText(userProfileData.getAddress().getStreet());
            tvUSerLocality.setText(userProfileData.getAddress().getLocality());
            tvUSerPinCode.setText(userProfileData.getAddress().getPcode());
            tvUSerCity.setText(userProfileData.getAddress().getCity());
            tvUSerState.setText(userProfileData.getAddress().getState());
            tvUSerCountry.setText(userProfileData.getAddress().getCountry());
        }

        tvUSerAbout.setText(userProfileData.getDescription());

        this.ivProfilePic.setImageBitmap(userManager.getUserAvatar(this.jid));


        return userProfileData;
    }


}
