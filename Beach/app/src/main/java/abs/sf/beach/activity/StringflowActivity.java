package abs.sf.beach.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;

import abs.ixi.client.core.Platform;
import abs.ixi.client.util.TaskExecutor;
import abs.sf.client.android.utils.ContextProvider;

/**
 * Root activity for all the activities defined in Beach application.
 */
public abstract class StringflowActivity extends AppCompatActivity implements ContextProvider {
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
        } else{
          this.pDialog.cancel();
        }

        return pDialog;
    }

    protected void closeProgressDialog(){
        if(this.pDialog != null) {
            this.pDialog.cancel();
        }
    }

    protected void shutDownSDK() {
        Platform.getInstance().shutdown();
    }

    protected void shutdownSdkAsync() {
        TaskExecutor.getInstance().submit(new Runnable() {
            @Override
            public void run() {
                shutDownSDK();
            }
        });
    }

    @Override
    public Context context() {
        return this;
    }
}
