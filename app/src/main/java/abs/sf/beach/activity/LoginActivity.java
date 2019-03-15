package abs.sf.beach.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import abs.ixi.client.Platform;
import abs.ixi.client.core.Callback;
import abs.ixi.client.io.StreamNegotiator;
import abs.ixi.client.util.StringUtils;
import abs.sf.beach.android.R;
import abs.sf.beach.utils.AndroidUtils;
import abs.sf.beach.utils.ApplicationProps;
import abs.sf.beach.utils.SharedPrefs;

public class LoginActivity extends StringflowActivity {
    private static final Logger ROOT_LOGGER = Logger.getLogger("");

    private EditText password;
    private EditText userName;
    private Button login, signup;

    static {
        //TODO We may not configure loggers in production
        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(Level.ALL);
        ROOT_LOGGER.addHandler(handler);
        ROOT_LOGGER.setLevel(Level.ALL);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(LoginActivity.class.getName(), "Initilizing Stringflow SDK Plateform");
        this.initilizeSDK();

        if (!SharedPrefs.getInstance().getLoginStatus()) {
            setContentView(R.layout.activity_login);
            initView();

            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String user = userName.getText().toString();
                    String pwd = password.getText().toString();

                    boolean success = validateInputs(user, pwd);

                    if (success) {
                        login(user, pwd);
                    }
                }
            });

            signup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                    startActivity(intent);
                }
            });


        } else {
            loginBackground();
            startActivity(new Intent(LoginActivity.this, ChatBaseActivity.class));
            finish();
        }
    }

    private boolean login(final String userName, final String pwd) {
        try {
            ProgressDialog progressDialog = getProgressDialog("Authenticating...");
            progressDialog.show();

            Platform.getInstance().login(userName, ApplicationProps.DOMAIN, pwd, new Callback<StreamNegotiator.NegotiationResult, Exception>() {
                @Override
                public void onSuccess(StreamNegotiator.NegotiationResult result) {
                    Log.d(LoginActivity.this.getClass().getName(), "" + result.isSuccess());
                    if (result.isSuccess()) {
                        SharedPrefs.getInstance().setUsername(userName);
                        SharedPrefs.getInstance().setPassword(pwd);
                        SharedPrefs.getInstance().setLoginStatus(true);
                        Log.d(LoginActivity.this.getClass().getName(), "" + result.isSuccess());
                        closeProgressDialog();
                        startActivity(new Intent(LoginActivity.this, ChatBaseActivity.class));
                        finish();

                    } else {
                        Log.d(LoginActivity.this.getClass().getName(), "Else:" + result.isSuccess());
                        final String msg;
                        if (result.getError() == StreamNegotiator.NegotiationError.AUTHENTICATION_FAILED) {
                            msg = "Entered userId Password are incorrect";

                        } else if (result.getError() == StreamNegotiator.NegotiationError.TIME_OUT) {
                            msg = "Server response timed out. try again...";

                        } else {
                            msg = "Something went wrong. Please try after sometime";
                        }

                        LoginActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                AndroidUtils.showToast(context(), msg);
                            }
                        });
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    LoginActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AndroidUtils.showToast(context(), "Something went wrong. Please try after sometime");
                        }
                    });
                }
            });

        } finally {
            Log.d(LoginActivity.this.getClass().getName(), "Finally");
            closeProgressDialog();
        }

        return false;
    }

    private void initView() {
        userName = (EditText) findViewById(R.id.edt_email);
        password = (EditText) findViewById(R.id.edt_password);
        login = (Button) findViewById(R.id.btn_login);
        signup = (Button) findViewById(R.id.btn_SignUp);
    }

    /**
     * Validate input values from user. These are basic validations for
     * Null check; however they can be extended to check alphanumeric
     * chars also.
     *
     * @param email
     * @param password
     * @return true if inputs were validated and were found OK otherwise false.
     */
    private boolean validateInputs(final String email, final String password) {
        if (StringUtils.isNullOrEmpty(email)) {
            AndroidUtils.showToast(LoginActivity.this, "Please enter email");
            return false;

        } else if (StringUtils.isNullOrEmpty(password)) {
            AndroidUtils.showToast(LoginActivity.this, "Please enter password");
            return false;
        }

        return true;
    }

}
