package abs.sf.beach.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import abs.sf.beach.android.R;
import abs.sf.beach.utils.CommonConstants;

public class SignUp extends StringflowActivity {
    private EditText etUsername,etPwd,etEmail;
    private Button btnSignUp;
    private ImageView ivBack, ivNext;
    private TextView tvHeader;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        initView();
        initOnclickListener();

    }

    private void initView() {
        ivBack = (ImageView) findViewById(R.id.ivBack);
        ivNext = (ImageView) findViewById(R.id.ivNext);
        ivNext.setVisibility(View.INVISIBLE);
        tvHeader = (TextView)findViewById(R.id.tvHeader);
        tvHeader.setText(CommonConstants.SIGN_UP);
        etUsername = (EditText)findViewById(R.id.etUsername);
        etEmail = (EditText)findViewById(R.id.etEmail);
        etPwd = (EditText)findViewById(R.id.etPwd);
        btnSignUp = (Button)findViewById(R.id.btnSignup);

    }
    private void initOnclickListener() {
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(SignUp.this,LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}
