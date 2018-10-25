package abs.sf.beach.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import abs.ixi.client.core.Platform;
import abs.ixi.client.net.NetworkException;
import abs.ixi.client.util.StringUtils;
import abs.sf.beach.android.R;
import abs.sf.beach.utils.CommonConstants;
import abs.sf.client.android.managers.AndroidUserManager;

public class SignUp extends StringflowActivity {
    private EditText etUsername, etPwd, etEmail;
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
        tvHeader = (TextView) findViewById(R.id.tvHeader);
        tvHeader.setText(CommonConstants.SIGN_UP);
        etUsername = (EditText) findViewById(R.id.etUsername);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPwd = (EditText) findViewById(R.id.etPwd);
        btnSignUp = (Button) findViewById(R.id.btnSignup);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.hide();
        }
    }

    private void initOnclickListener() {

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignUp.this.finish();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String username = etUsername.getText().toString();
                final String pwd = etPwd.getText().toString();
                final String email = etEmail.getText().toString();

                if (StringUtils.isNullOrEmpty(username)) {
                    Toast.makeText(SignUp.this, "please fill the username field", Toast.LENGTH_SHORT).show();

                } else if (StringUtils.isNullOrEmpty(pwd)) {
                    Toast.makeText(SignUp.this, "please fill the password field", Toast.LENGTH_SHORT).show();
                } else if (StringUtils.isNullOrEmpty(email)) {
                    Toast.makeText(SignUp.this, "please fill the email field", Toast.LENGTH_SHORT).show();
                } else {
                    Thread thread = new Thread(new Runnable() {
                        AndroidUserManager userManager = (AndroidUserManager) Platform.getInstance().getUserManager();

                        @Override
                        public void run() {
                            try {

                                boolean signup = userManager.registerNewUser(username, email, pwd);
                                if (signup) {
                                    Toast.makeText(SignUp.this, "Signup Successfully", Toast.LENGTH_SHORT).show();
                                    SignUp.this.finish();
                                }
                                //Your code goes here
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    thread.start();
                }
                finish();
            }
        });
    }
}
