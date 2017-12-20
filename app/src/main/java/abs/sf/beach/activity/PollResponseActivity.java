package abs.sf.beach.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import abs.ixi.client.core.PollType;
import abs.ixi.client.util.DateUtils;
import abs.ixi.client.util.StringUtils;
import abs.sf.beach.android.R;
import abs.sf.beach.utils.AndroidUtils;
import abs.sf.client.android.db.DbManager;
import abs.sf.client.android.managers.AndroidUserManager;
import abs.sf.client.android.messaging.PollContent;

public class PollResponseActivity extends StringflowActivity {
    private long pollId;
    private ImageView ivBack, ivNext;
    private TextView tvHeader;
    private PollContent content;
    private TextView tvExpiryTime, tvPollQuestion, tvOptionOne, tvOptionTwo, tvOptionThree, tvOptionFour, tvYourResponse;
    private Button btnRespond;
    private LinearLayout llCheckbox, llRadioButton;
    private RadioGroup rgOptions;
    private CheckBox cbOptionOne, cbOptionTwo, cbOptionThree, cbOptionFour;
    private EditText etDescriptive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poll_response);
        initView();
        initClickListener();
        setPollData();
    }

    private void initView() {
        pollId = getIntent().getLongExtra("pollId", 0);
        content = DbManager.getInstance().getPollDetails(pollId);

        ivBack = (ImageView) findViewById(R.id.ivBack);
        ivNext = (ImageView) findViewById(R.id.ivNext);
        tvHeader = (TextView) findViewById(R.id.tvHeader);
        ivNext.setVisibility(View.INVISIBLE);
        tvHeader.setText("Poll Response");

        tvExpiryTime = (TextView) findViewById(R.id.tvExpiryTime);
        tvPollQuestion = (TextView) findViewById(R.id.tvPollQuestion);
        tvOptionOne = (TextView) findViewById(R.id.tvOptionOne);
        tvOptionTwo = (TextView) findViewById(R.id.tvOptionTwo);
        tvOptionThree = (TextView) findViewById(R.id.tvOptionThree);
        tvOptionFour = (TextView) findViewById(R.id.tvOptionFour);
        btnRespond = (Button) findViewById(R.id.btnRespond);
        llCheckbox = (LinearLayout) findViewById(R.id.llCheckbox);
        llRadioButton = (LinearLayout) findViewById(R.id.llRadioButton);
        rgOptions = (RadioGroup) findViewById(R.id.rgOptions);
        cbOptionOne = (CheckBox) findViewById(R.id.cbOptionOne);
        cbOptionTwo = (CheckBox) findViewById(R.id.cbOptionTwo);
        cbOptionThree = (CheckBox) findViewById(R.id.cbOptionThree);
        cbOptionFour = (CheckBox) findViewById(R.id.cbOptionFour);
        tvYourResponse = (TextView) findViewById(R.id.tvYourResponse);
        etDescriptive = (EditText) findViewById(R.id.etDescriptive);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    private void initClickListener() {
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PollResponseActivity.this.finish();
            }
        });
        btnRespond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (content.getPollType().equals(PollType.MCQ)){
                    if(!cbOptionOne.isChecked() || !cbOptionTwo.isChecked() || !cbOptionThree.isChecked() || !cbOptionFour.isChecked()){
                        AndroidUtils.showToast(PollResponseActivity.this,"Please select at least one option");
                    }else{
                        //AndroidUserManager.getInstance().storePollResponse(pollId,
                    }
                }
                else if (content.getPollType().equals(PollType.DESCRIPTIVE)) {
                    String response = etDescriptive.getText().toString();
                    if(StringUtils.isNullOrEmpty(response)){
                        AndroidUtils.showToast(PollResponseActivity.this,"Please enter your response");
                    }else{
                        AndroidUserManager.getInstance().storePollResponse(pollId,response);
                    }
                }
                else if(rgOptions.getCheckedRadioButtonId() != R.id.rdOptionOne
                        || rgOptions.getCheckedRadioButtonId() != R.id.rdOptionTwo
                        || rgOptions.getCheckedRadioButtonId() != R.id.rdOptionThree
                        || rgOptions.getCheckedRadioButtonId() != R.id.rdOptionFour){
                    AndroidUtils.showToast(PollResponseActivity.this,"Please select at least one option");
                }

            }
        });
    }

    private void setPollData(){
        if(content!=null){
            tvExpiryTime.setText(DateUtils.getDisplayTime(content.getExpiryTime()));
            tvPollQuestion.setText(content.getQuestion());
            tvOptionOne.setText(content.getOption1());
            tvOptionTwo.setText(content.getOption2());
            tvOptionThree.setText(content.getOption3());
            tvOptionFour.setText(content.getOption4());

            if(content.getStatus().equals(PollContent.PollStatus.RESPONDED)){
                tvYourResponse.setText(content.getYourResponse());
                llCheckbox.setVisibility(View.GONE);
                llRadioButton.setVisibility(View.GONE);
                etDescriptive.setVisibility(View.GONE);
                return;
            }

            if (content.getPollType().equals(PollType.MCQ)) {
                llCheckbox.setVisibility(View.VISIBLE);
                llRadioButton.setVisibility(View.GONE);
                etDescriptive.setVisibility(View.GONE);
            }

            if (content.getPollType().equals(PollType.DESCRIPTIVE)) {
                llCheckbox.setVisibility(View.GONE);
                llRadioButton.setVisibility(View.GONE);
                etDescriptive.setVisibility(View.VISIBLE);
            }
        }
    }
}
