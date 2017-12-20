package abs.sf.beach.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import abs.ixi.client.UserManager;
import abs.ixi.client.core.Platform;
import abs.ixi.client.io.StreamNegotiator;
import abs.ixi.client.util.StringUtils;
import abs.ixi.client.util.TaskExecutor;
import abs.ixi.client.util.UUIDGenerator;
import abs.ixi.client.xmpp.packet.IQPushRegistration.PushNotificationService;
import abs.sf.beach.android.R;
import abs.sf.beach.utils.AndroidUtils;
import abs.sf.beach.utils.ApplicationProps;
import abs.sf.beach.utils.SharedPrefs;
import abs.sf.client.android.managers.AndroidUserManager;
import abs.sf.client.android.utils.SDKLoader;

public class LoginActivity extends StringflowActivity {
    private static final Logger ROOT_LOGGER = Logger.getLogger("");

    private EditText password;
    private EditText userName;

    private Button login;

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

        Log.i(LoginActivity.class.getName(), "Loading Stringflow SDK");
        loadSDK();

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
                        boolean authenticated = login(user, pwd);

                        if (authenticated) {
                            SharedPrefs.getInstance().setUsername(user);
                            SharedPrefs.getInstance().setPassword(pwd);
                            SharedPrefs.getInstance().setLoginStatus(true);

                            initApplication();
                            startActivity(new Intent(LoginActivity.this, ChatBaseActivity.class));
                            finish();
                        }

                    }
                }
            });

        } else {
            AndroidUserManager.getInstance().loginInBackground(SharedPrefs.getInstance().getUsername(), SharedPrefs.getInstance().getPassword(), ApplicationProps.DOMAIN);
            startActivity(new Intent(LoginActivity.this, ChatBaseActivity.class));
            finish();
        }
    }

    private boolean login(String user, String pwd) {
        try {
            getProgressDialog("Authenticating...").show();

            Future<StreamNegotiator.NegotiationResult> future = AndroidUserManager.getInstance().loginAsync(user, pwd, ApplicationProps.DOMAIN);

            StreamNegotiator.NegotiationResult result = null;
            try {
                result = future.get();

            } catch (InterruptedException | ExecutionException e) {
                AndroidUtils.showToast(context(), "Something went wrong. Please try after sometime");
                return false;
            }

            if (result.isSuccess()) {
                return true;

            } else {

                if (result.getError() == StreamNegotiator.NegotiationError.AUTHENTICATION_FAILED) {
                    AndroidUtils.showToast(context(), "Entered userId Password are incorrect");

                } else if (result.getError() == StreamNegotiator.NegotiationError.TIME_OUT) {
                    AndroidUtils.showToast(context(), "Server response timed out. try again...");

                } else {
                    AndroidUtils.showToast(context(), "Something went wrong. Please try after sometime");
                }
            }


        } finally {
            closeProgressDialog();
        }

        return false;
    }

    private void initView() {
        userName = (EditText) findViewById(R.id.edt_email);
        password = (EditText) findViewById(R.id.edt_password);
        login = (Button) findViewById(R.id.btn_login);
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

    /**
     * Load Stringflow android sdk. Stringflow android sdk loading does not
     * involve network operations such as TCP connection initiation; therefore
     * SDK loading can be executed on Android main thread.
     */
    private void loadSDK() {
        SDKLoader.loadSDK(ApplicationProps.SERVER, 5222, this);
    }

    /**
     * Load SDK asynchronously. Loading SDK generally does not require anything on
     * network therefore can be executed synchronously also. Returned {@link Future}
     * can be used to find out the status of the loader task execution.
     */
    private Future<Boolean> loadSDKAsync() {
        return TaskExecutor.submit(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                SDKLoader.loadSDK(ApplicationProps.SERVER, 5222, LoginActivity.this);
                return true;
            }
        });
    }

    private void initApplication() {
        Platform.getInstance().getUserManager().updateDeviceToken(SharedPrefs.getInstance().getFCMToken(), PushNotificationService.FCM);
        Platform.getInstance().getUserManager().getFullRoster();
        AndroidUserManager.getInstance().sendGetChatRoomsRequest();
    }
}
