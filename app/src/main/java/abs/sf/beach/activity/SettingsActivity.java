package abs.sf.beach.activity;

import android.net.sip.SipSession;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import abs.sf.beach.android.R;
import abs.sf.client.android.utils.SharedPrefProxy;

public class SettingsActivity extends StringflowActivity {
    private TextView tvChatStateNotification, tvMessageDelivery, tvChatMarksers, tvMediaTransfer;
    private CheckBox cb1, cb2, cb3, cb4;
    private ImageView ivBack, ivNext;
    private TextView tvHeader;
    private boolean check;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initView();
        initOnClickListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        chekedCheckBox();
    }

    private void initView() {
        ivBack = (ImageView) findViewById(R.id.ivBack);
        ivNext = (ImageView) findViewById(R.id.ivNext);
        ivNext.setVisibility(View.INVISIBLE);
        tvHeader = (TextView) findViewById(R.id.tvHeader);
        tvHeader.setText("Settings");
        tvChatStateNotification = (TextView) findViewById(R.id.enable_Chat_State_Notification);
        tvMessageDelivery = (TextView) findViewById(R.id.enable_message_delivery);
        tvChatMarksers = (TextView) findViewById(R.id.chat_markers);
        tvMediaTransfer = (TextView) findViewById(R.id.enable_media_transfer);
        cb1 = (CheckBox) findViewById(R.id.cb1);
        cb2 = (CheckBox) findViewById(R.id.cb2);
        cb3 = (CheckBox) findViewById(R.id.cb3);
        cb4 = (CheckBox) findViewById(R.id.cb4);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.hide();
        }

    }

    private void initOnClickListener() {
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SettingsActivity.this.finish();
            }
        });


    }

    private void chekedCheckBox() {
            cb1.setChecked(SharedPrefProxy.getInstance().isChatStateNotificationEnabled());
            cb2.setChecked(SharedPrefProxy.getInstance().isMessageDeliveryReceiptEnabled());
            cb3.setChecked(SharedPrefProxy.getInstance().isChatMarkersEnabled());
            cb4.setChecked(SharedPrefProxy.getInstance().isMediaTransferEnabled());
            if (!cb1.isChecked()){
                SharedPrefProxy.getInstance().disableChatStateNotification();
            } if (!cb2.isChecked()){
                SharedPrefProxy.getInstance().disableMessageDeliveryReceipt();
            }
            if (!cb3.isChecked()){
                SharedPrefProxy.getInstance().disableChatMarkers();
            } if (!cb4.isChecked()){
                SharedPrefProxy.getInstance().disableMediaTransfer();
            }






    }

}
