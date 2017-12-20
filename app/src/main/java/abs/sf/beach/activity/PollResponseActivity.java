package abs.sf.beach.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
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

import static abs.sf.beach.android.R.id.llOptions;
import static abs.sf.beach.android.R.id.rdOptionOne;
import static abs.sf.beach.android.R.id.rgOptions;
import static com.google.android.gms.common.zze.rg;

public class PollResponseActivity extends StringflowActivity {
    private long pollId;
    private ImageView ivBack, ivNext;
    private TextView tvHeader;
    private PollContent content;
    private TextView tvExpiryTime, tvPollQuestion, tvOptionOne, tvOptionTwo, tvOptionThree, tvOptionFour, tvYourResponse;
    private Button btnRespond;
    private LinearLayout llOptions, llCheckbox, llRadioButton;
    private RadioGroup rgOptions;
    private CheckBox cbOptionOne, cbOptionTwo, cbOptionThree, cbOptionFour;
    private RadioButton rdOptionOne, rdOptionTwo, rdOptionThree, rdOptionFour;
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
        llOptions= (LinearLayout) findViewById(R.id.llOptions);
        llCheckbox = (LinearLayout) findViewById(R.id.llCheckbox);
        llRadioButton = (LinearLayout) findViewById(R.id.llRadioButton);
        rgOptions = (RadioGroup) findViewById(R.id.rgOptions);
        rdOptionOne = (RadioButton) findViewById(R.id.rdOptionOne);
        rdOptionTwo = (RadioButton) findViewById(R.id.rdOptionTwo);
        rdOptionThree = (RadioButton) findViewById(R.id.rdOptionThree);
        rdOptionFour = (RadioButton) findViewById(R.id.rdOptionFour);
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
                    if(!cbOptionOne.isChecked() && !cbOptionTwo.isChecked() && !cbOptionThree.isChecked() && !cbOptionFour.isChecked()){
                        AndroidUtils.showToast(PollResponseActivity.this,"Please select at least one option");
                    }else{
                        StringBuilder sb = new StringBuilder();
                        if(cbOptionOne.isChecked()){
                            sb.append(tvOptionOne.getText()+",");
                        }
                        if(cbOptionTwo.isChecked()){
                            sb.append(tvOptionTwo.getText()+",");
                        }
                        if(cbOptionThree.isChecked()){
                            sb.append(tvOptionThree.getText()+",");
                        }
                        if(cbOptionFour.isChecked()){
                            sb.append(tvOptionFour.getText());
                        }
                        savePollResponse(sb.toString());
                    }
                }
                else if (content.getPollType().equals(PollType.DESCRIPTIVE)) {
                    String response = etDescriptive.getText().toString();
                    if(StringUtils.isNullOrEmpty(response)){
                        AndroidUtils.showToast(PollResponseActivity.this,"Please enter your response");
                    }else{
                        savePollResponse(response);
                    }
                }
                else if(content.getPollType().equals(PollType.SCQ)){
                    if(rgOptions.getCheckedRadioButtonId() == -1){
                        AndroidUtils.showToast(PollResponseActivity.this,"Please select at least one option");
                    }else{
                        String response;
                        if(rgOptions.getCheckedRadioButtonId() == R.id.rdOptionOne){
                            response = tvOptionOne.getText().toString();
                        }else if(rgOptions.getCheckedRadioButtonId() == R.id.rdOptionTwo){
                            response = tvOptionTwo.getText().toString();
                        }else if(rgOptions.getCheckedRadioButtonId() == R.id.rdOptionThree){
                            response = tvOptionThree.getText().toString();
                        }else{
                            response = tvOptionFour.getText().toString();
                        }
                        savePollResponse(response);
                    }
                }
            }
        });
    }

    /**
     * This method is for temporary basis. Once server timeout exception will resolved.
     * This will be removed and sdk methods will come in use.
     * @param pollResponse
     */
    private void savePollResponse(String pollResponse){
        AndroidUserManager.getInstance().storePollResponse(pollId, pollResponse);
        DbManager.getInstance().updatePollStatus(pollId, PollContent.PollStatus.RESPONDED);
        DbManager.getInstance().storePollResponse(pollId, pollResponse, AndroidUserManager.getInstance().getUserJID());
        PollResponseActivity.this.finish();
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
                tvYourResponse.setVisibility(View.VISIBLE);
                llOptions.setVisibility(View.GONE);
                llCheckbox.setVisibility(View.GONE);
                llRadioButton.setVisibility(View.GONE);
                etDescriptive.setVisibility(View.GONE);
                btnRespond.setVisibility(View.GONE);
                return;
            }

            if (content.getPollType().equals(PollType.MCQ)) {
                llCheckbox.setVisibility(View.VISIBLE);
                llRadioButton.setVisibility(View.GONE);
                etDescriptive.setVisibility(View.GONE);
                if(StringUtils.isNullOrEmpty(content.getOption1())){
                    tvOptionOne.setVisibility(View.GONE);
                    cbOptionOne.setVisibility(View.GONE);
                }
                if(StringUtils.isNullOrEmpty(content.getOption2())){
                    tvOptionTwo.setVisibility(View.GONE);
                    cbOptionTwo.setVisibility(View.GONE);
                }
                if(StringUtils.isNullOrEmpty(content.getOption3())){
                    tvOptionThree.setVisibility(View.GONE);
                    cbOptionThree.setVisibility(View.GONE);
                }
                if(StringUtils.isNullOrEmpty(content.getOption4())){
                    tvOptionFour.setVisibility(View.GONE);
                    cbOptionFour.setVisibility(View.GONE);
                }
            }

            if (content.getPollType().equals(PollType.SCQ)) {
                llCheckbox.setVisibility(View.GONE);
                llRadioButton.setVisibility(View.VISIBLE);
                etDescriptive.setVisibility(View.GONE);
                if(StringUtils.isNullOrEmpty(content.getOption1())){
                    tvOptionOne.setVisibility(View.GONE);
                    rdOptionOne.setVisibility(View.GONE);
                }
                if(StringUtils.isNullOrEmpty(content.getOption2())){
                    tvOptionTwo.setVisibility(View.GONE);
                    rdOptionTwo.setVisibility(View.GONE);
                }
                if(StringUtils.isNullOrEmpty(content.getOption3())){
                    tvOptionThree.setVisibility(View.GONE);
                    rdOptionThree.setVisibility(View.GONE);
                }
                if(StringUtils.isNullOrEmpty(content.getOption4())){
                    tvOptionFour.setVisibility(View.GONE);
                    rdOptionFour.setVisibility(View.GONE);
                }
            }

            if (content.getPollType().equals(PollType.DESCRIPTIVE)) {
                llCheckbox.setVisibility(View.GONE);
                llRadioButton.setVisibility(View.GONE);
                llOptions.setVisibility(View.GONE);
                etDescriptive.setVisibility(View.VISIBLE);
            }
        }
    }
}
