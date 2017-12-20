package abs.sf.beach.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import abs.ixi.client.core.PollType;
import abs.ixi.client.util.DateUtils;
import abs.ixi.client.util.StringUtils;
import abs.sf.beach.android.R;
import abs.sf.beach.fragment.PollSavedFragment;
import abs.sf.beach.utils.PollActionPerformedListener;
import abs.sf.beach.utils.AndroidUtils;
import abs.sf.client.android.db.DbManager;


public class PollActivity extends StringflowActivity implements PollActionPerformedListener {
    private ScrollView scrollViewPoll;
    private ImageView ivBack, ivNext;
    private TextView tvHeader;
    private boolean isFragmentOpen;
    private EditText etPollQues, etOptionOne, etOptionTwo, etOptionThree, etOptionFour;
    private TextView tvResponseType, tvTextBoxMulChoice, tvTextBoxRBtn, tvTextBoxNumeric, tvTextBox, tvRecipientMsg;
    private LinearLayout llResponseType, llMultipleChoice;
    private Button btnAddMoreChoice, btnPollExpiryTime, btnViewSaved, btnSend, btnSave;
    private int opt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poll);
        initViews();
        initOnClickListener();
    }

    @Override
    public void actionDone(String message, long id) {
        if(!StringUtils.isNullOrEmpty(message) && StringUtils.safeEquals(message, "edit_saved_poll")){
            closeFragment();
            selectPollRecipient(id);
        }
    }

    private void initViews(){
        ivBack = (ImageView) findViewById(R.id.ivBack);
        ivNext = (ImageView) findViewById(R.id.ivNext);
        ivNext.setVisibility(View.INVISIBLE);
        tvHeader = (TextView) findViewById(R.id.tvHeader);
        tvHeader.setText(getIntent().getStringExtra("poll").toUpperCase());
        scrollViewPoll = (ScrollView) findViewById(R.id.scrollViewPoll);
        etPollQues = (EditText) findViewById(R.id.etPollQues);
        tvResponseType = (TextView) findViewById(R.id.tvResponseType);
        llResponseType = (LinearLayout) findViewById(R.id.llResponseType);
        tvTextBoxMulChoice = (TextView) findViewById(R.id.tvTextBoxMulChoice);
        tvTextBoxRBtn = (TextView) findViewById(R.id.tvTextBoxRBtn);
        tvTextBoxNumeric = (TextView) findViewById(R.id.tvTextBoxNumeric);
        tvTextBox = (TextView) findViewById(R.id.tvTextBox);
        llMultipleChoice = (LinearLayout) findViewById(R.id.llMultipleChoice);
        etOptionOne = (EditText) findViewById(R.id.etOptionOne);
        etOptionTwo = (EditText) findViewById(R.id.etOptionTwo);
        etOptionThree = (EditText) findViewById(R.id.etOptionThree);
        etOptionFour = (EditText) findViewById(R.id.etOptionFour);
        btnViewSaved = (Button) findViewById(R.id.btnViewSaved);
        btnAddMoreChoice = (Button) findViewById(R.id.btnAddMoreChoice);
        btnPollExpiryTime = (Button) findViewById(R.id.btnPollExpiryTime);
        btnSend = (Button) findViewById(R.id.btnSend);
        btnSave = (Button) findViewById(R.id.btnSave);
        tvRecipientMsg = (TextView) findViewById(R.id.tvRecipientMsg);
        opt = 0;
        isFragmentOpen = false;
        long millis =  System.currentTimeMillis() + 7*24*60*60*1000;
        btnPollExpiryTime.setText(DateUtils.getDisplayTime(millis));
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.hide();
        }
    }

    private void initOnClickListener(){
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isFragmentOpen){
                    closeFragment();
                }else{
                    PollActivity.this.finish();
                }
            }
        });
        tvResponseType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvResponseType.setText(R.string.choose_resp);
                llResponseType.setVisibility(View.VISIBLE);
                llMultipleChoice.setVisibility(View.GONE);
                etOptionThree.setVisibility(View.GONE);
                etOptionFour.setVisibility(View.GONE);
                btnAddMoreChoice.setVisibility(View.VISIBLE);
                tvRecipientMsg.setVisibility(View.GONE);
            }
        });
        tvTextBoxMulChoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvResponseType.setText(R.string.mul_choice);
                hideShowViews();
            }
        });
        tvTextBoxRBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvResponseType.setText(R.string.radio_buttons);
                hideShowViews();
            }
        });
        btnAddMoreChoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(opt == 0){
                    etOptionThree.setVisibility(View.VISIBLE);
                    opt++;
                }
                else if(opt == 1){
                    etOptionFour.setVisibility(View.VISIBLE);
                    btnAddMoreChoice.setVisibility(View.GONE);
                    opt = 0;
                }
            }
        });
        tvTextBoxNumeric.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvResponseType.setText(R.string.textbox_numeric);
                tvRecipientMsg.setVisibility(View.VISIBLE);
                llResponseType.setVisibility(View.GONE);
            }
        });
        tvTextBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvResponseType.setText(R.string.textbox);
                tvRecipientMsg.setVisibility(View.VISIBLE);
                llResponseType.setVisibility(View.GONE);
            }
        });
        btnPollExpiryTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDatePicker();
            }
        });
        btnViewSaved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvHeader.setText("Saved Polls");
                openFragment(PollSavedFragment.newInstance());
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePoll(false);
            }
        });
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePoll(true);
            }
        });
    }

    private void savePoll(boolean isSend){
        if(StringUtils.isNullOrEmpty(etPollQues.getText().toString())){
            AndroidUtils.showToast(PollActivity.this, "Please enter poll question.");
            return;
        }

        String resType = tvResponseType.getText().toString();
        PollType pollType = null;
        long millis;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        try {
            Date date = sdf.parse(btnPollExpiryTime.getText().toString());
            millis = date.getTime();
        }catch (Exception e){
            millis = DateUtils.currentTimeInMiles() + 7*24*60*60*1000;
        }
        if(resType.equalsIgnoreCase("Multiple Choice")){
            pollType = PollType.MCQ;
        }
        if(resType.equalsIgnoreCase("Single Choice")){
            pollType = PollType.SCQ;
        }
        if(resType.equalsIgnoreCase("Textbox")){
            pollType = PollType.DESCRIPTIVE;
        }
        /*if(resType.equalsIgnoreCase("Multiple Choice") || resType.equalsIgnoreCase("Radio Button")){
            AndroidUserManager.getInstance().createPoll(
                    PollType.MCQ,
                    etPollQues.getText().toString(),
                    etOptionOne.getText().toString(),
                    etOptionTwo.getText().toString(),
                    etOptionThree.getText().toString(),
                    etOptionFour.getText().toString(),
                    null,
                    System.currentTimeMillis(),
                    millis,
                    PollContent.ResponseType.OPEN);
        }else{
            AndroidUserManager.getInstance().createPoll(
                    PollType.DESCRIPTIVE,
                    etPollQues.getText().toString(),
                    null,
                    null,
                    null,
                    null,
                    null,
                    System.currentTimeMillis(),
                    millis,
                    PollContent.ResponseType.OPEN);
        }*/
        long pollId = DbManager.getInstance().storeCreatedPoll(new Random().nextInt(), pollType, etPollQues.getText().toString(),
                etOptionOne.getText().toString(),
                etOptionTwo.getText().toString(),
                etOptionThree.getText().toString(),
                etOptionFour.getText().toString(),
                null, System.currentTimeMillis(), millis);
        //AndroidUtils.showToast(PollActivity.this, "Poll saved request sent.");
        resetPollData();
        if(isSend){
            selectPollRecipient(pollId);
        }
    }

    private void selectPollRecipient(long pollId){
        Intent intent = new Intent(PollActivity.this, PollRecipientActivity.class);
        intent.putExtra("pollId",pollId);
        startActivity(intent);
    }

    private void editPollData(){
        tvResponseType.setText(R.string.choose_resp);
        llResponseType.setVisibility(View.VISIBLE);
        llMultipleChoice.setVisibility(View.GONE);
        etOptionThree.setVisibility(View.GONE);
        etOptionFour.setVisibility(View.GONE);
        btnAddMoreChoice.setVisibility(View.VISIBLE);
        tvRecipientMsg.setVisibility(View.GONE);
        etPollQues.setText("");
        etOptionOne.setText("");
        etOptionTwo.setText("");
        etOptionThree.setText("");
        etOptionFour.setText("");
        long millis =  System.currentTimeMillis() + 7*24*60*60*1000;
        btnPollExpiryTime.setText(DateUtils.getDisplayTime(millis));
        etPollQues.setFocusable(true);
    }

    private void resetPollData(){
        tvResponseType.setText(R.string.choose_resp);
        llResponseType.setVisibility(View.VISIBLE);
        llMultipleChoice.setVisibility(View.GONE);
        etOptionThree.setVisibility(View.GONE);
        etOptionFour.setVisibility(View.GONE);
        btnAddMoreChoice.setVisibility(View.VISIBLE);
        tvRecipientMsg.setVisibility(View.GONE);
        etPollQues.setText("");
        etOptionOne.setText("");
        etOptionTwo.setText("");
        etOptionThree.setText("");
        etOptionFour.setText("");
        long millis =  System.currentTimeMillis() + 7*24*60*60*1000;
        btnPollExpiryTime.setText(DateUtils.getDisplayTime(millis));
        etPollQues.setFocusable(true);
    }

    private void openDatePicker() {
        final Calendar c = Calendar.getInstance();
        final int mYear = c.get(Calendar.YEAR);
        final int mMonth = c.get(Calendar.MONTH);
        final int mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        btnPollExpiryTime.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                        openTimePicker();
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    private void openTimePicker(){
        final Calendar c = Calendar.getInstance();
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        String date = btnPollExpiryTime.getText().toString();
                        btnPollExpiryTime.setText(date+" "+ hourOfDay + ":" + minute);
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }

    private void hideShowViews(){
        llResponseType.setVisibility(View.GONE);
        llMultipleChoice.setVisibility(View.VISIBLE);
        etOptionThree.setVisibility(View.GONE);
        etOptionFour.setVisibility(View.GONE);
        btnAddMoreChoice.setVisibility(View.VISIBLE);
        tvRecipientMsg.setVisibility(View.GONE);
        opt = 0;
    }

    private void openFragment(Fragment fragment){
        scrollViewPoll.setVisibility(View.GONE);
        isFragmentOpen = true;
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragmentContainer, fragment);
        fragmentTransaction.commit();
    }

    private void closeFragment(){
        isFragmentOpen = false;
        scrollViewPoll.setVisibility(View.VISIBLE);
        ivNext.setVisibility(View.INVISIBLE);
        tvHeader.setText(getIntent().getStringExtra("poll").toUpperCase());
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragmentContainer);
        fragmentTransaction.remove(fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        if(isFragmentOpen){
            closeFragment();
        }else{
            PollActivity.this.finish();
        }
    }
}

