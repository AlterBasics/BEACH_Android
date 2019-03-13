package abs.sf.beach.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import abs.ixi.client.Platform;
import abs.ixi.client.core.InitializationErrorException;
import abs.ixi.client.io.StreamNegotiator;
import abs.ixi.client.net.NetworkException;
import abs.ixi.client.util.TaskExecutor;
import abs.sf.beach.utils.AndroidUtils;
import abs.sf.beach.utils.ApplicationProps;
import abs.sf.beach.utils.SharedPrefs;
import abs.sf.client.android.managers.AndroidUserManager;
import abs.sf.client.android.utils.AndroidSdkInitializer;

/**
 * Root activity for all the activities defined in Beach application.
 */
public abstract class StringflowActivity extends AppCompatActivity implements ContextProvider{
    private ProgressDialog pDialog;

    /**
     * A common progress dialog for all the activities defined in Beach application
     *
     * @return
     */
    protected ProgressDialog getProgressDialog(String msg) {
        return getProgressDialog(msg, false);
    }

    /**
     * A common progress dialog for all the activities defined in Beach application
     *
     * @return
     */
    protected ProgressDialog getProgressDialog(String msg, boolean cancelable) {
        if (pDialog == null) {
            this.pDialog = new ProgressDialog(StringflowActivity.this);
            pDialog.setMessage(msg);
            pDialog.setCancelable(cancelable);
        } else {
            this.pDialog.cancel();
        }

        return pDialog;
    }

    protected void closeProgressDialog() {
        if (this.pDialog != null) {
            this.pDialog.cancel();
        }
    }

    protected boolean initilizeSDK() throws InitializationErrorException {
        return Platform.initialize(new AndroidSdkInitializer(ApplicationProps.XMPP_SERVER, ApplicationProps.XMPP_SERVER_PORT,
                ApplicationProps.MEDIA_SERVER, ApplicationProps.MEDIA_SERVER_PORT, this));
    }

    protected void loginBackground() {
        if (SharedPrefs.getInstance().getLoginStatus()) {
            if(!Platform.getInstance().isLoggedIn()) {
                TaskExecutor.submit(new Runnable() {
                    @Override
                    public void run() {
                        while (!Platform.getInstance().isLoggedIn() && Thread.interrupted()) {
                            try {
                                StreamNegotiator.NegotiationResult result = Platform.getInstance().login(SharedPrefs.getInstance().getUsername(),
                                        SharedPrefs.getInstance().getPassword(), ApplicationProps.DOMAIN);

                                if(result.isError()) {
                                    waitForASec();
                                }

                            } catch (NetworkException e) {
                                System.out.println("Network Exception while try for background login : " + e.getReason());
                                waitForASec();
                            }
                        }
                    }
                });

            } else {
                System.out.println("Already logged in");
            }

        } else {
            AndroidUtils.showToast(this, "Login first");
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    private void waitForASec() {
        try{

            Thread.sleep(1000);

        } catch (InterruptedException e) {
            //swellow exception
        }
    }
    protected void logout() {
        final AndroidUserManager userManager = (AndroidUserManager) Platform.getInstance().getUserManager();

        TaskExecutor.getInstance().submit(new Runnable() {
            @Override
            public void run() {
                userManager.logoutUser();
            }
        });

        SharedPrefs.getInstance().clear();
        startActivity(new Intent(this, LoginActivity.class));
        this.finish();
    }

    @Override
    public Context context() {
        return this;
    }
}
