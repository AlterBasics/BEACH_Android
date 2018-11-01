package abs.sf.beach.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import abs.ixi.client.core.Callback;
import abs.ixi.client.core.Platform;
import abs.ixi.client.net.NetworkException;
import abs.ixi.client.util.StringUtils;
import abs.sf.beach.android.R;
import abs.sf.beach.utils.CommonConstants;
import abs.sf.client.android.managers.AndroidUserManager;

public class SignUpActivity extends StringflowActivity {
    private EditText etUsername, etPwd, etEmail, etConfrmPwd;
    private Button btnSignUp;
    private ImageView ivBack, ivNext;
    private TextView tvHeader;
    private Pattern regexPattern;
    private Matcher regMatcher;

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
        etConfrmPwd = (EditText) findViewById(R.id.etConfrmPwd);
        btnSignUp = (Button) findViewById(R.id.btnsignup);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.hide();
        }
    }

    private void initOnclickListener() {

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignUpActivity.this.finish();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String username = etUsername.getText().toString();
                final String pwd = etPwd.getText().toString();
                final String email = etEmail.getText().toString();
                final String cnfrmPwd = etConfrmPwd.getText().toString();

                if ((StringUtils.isNullOrEmpty(email))) {
                    Toast.makeText(SignUpActivity.this, "please fill the email field", Toast.LENGTH_SHORT).show();

                }else if(!validateEmail(email)){
                    etEmail.getText().clear();
                    Toast.makeText(SignUpActivity.this,"email is not valid",Toast.LENGTH_SHORT).show();
                }
                else if (StringUtils.isNullOrEmpty(username)) {
                    Toast.makeText(SignUpActivity.this, "please fill the username field", Toast.LENGTH_SHORT).show();

                } else if (StringUtils.isNullOrEmpty(pwd)) {
                    Toast.makeText(SignUpActivity.this, "please fill the password field", Toast.LENGTH_SHORT).show();

                } else if (StringUtils.isNullOrEmpty(cnfrmPwd)) {
                    Toast.makeText(SignUpActivity.this, "please fill the confirm password field", Toast.LENGTH_SHORT).show();

                } else if (!pwd.equals(cnfrmPwd)) {
                    etPwd.getText().clear();
                    etConfrmPwd.getText().clear();
                    Toast.makeText(SignUpActivity.this, "Password doesn't match", Toast.LENGTH_SHORT).show();
                } else {

                    final ProgressDialog progressDialog = getProgressDialog("...");
                    progressDialog.show();

                    AndroidUserManager userManager = (AndroidUserManager) Platform.getInstance().getUserManager();
                    try {
                        userManager.registerNewUser(username, email, pwd, new Callback<String, String>() {
                            @Override
                            public void onSuccess(final String success) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(SignUpActivity.this, success, Toast.LENGTH_SHORT).show();
                                        closeProgressDialog();
                                        finish();
                                    }
                                });
                               }

                            @Override
                            public void onFailure(final String failure) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(SignUpActivity.this, failure, Toast.LENGTH_SHORT).show();
                                        etEmail.getText().clear();
                                        etPwd.getText().clear();
                                        etUsername.getText().clear();
                                        etConfrmPwd.getText().clear();
                                        closeProgressDialog();
                                    }
                                });

                            }
                        });
                    } catch (NetworkException e) {
                        e.printStackTrace();
                    }

                }

            }
        });
    }

    public boolean validateEmail(String email) {
        regexPattern = Pattern.compile("^[(a-zA-Z-0-9-\\_\\+\\.)]+@[(a-z-A-z)]+\\.[(a-zA-z)]{2,3}$");
        regMatcher = regexPattern.matcher(email);
        if(regMatcher.matches()) {
            return true;
        } else {
            return false;
        }

    }
}
