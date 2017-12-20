package abs.sf.beach.core;

import android.app.Application;
import android.content.Context;

import abs.sf.client.android.utils.ContextProvider;

public class BasicApplication extends Application implements ContextProvider {
    private static BasicApplication instance;

    public BasicApplication() {
        instance = this;
    }

    /**
     * Returns this application instance as context
     *
     * @return
     */
    public Context context() {
        return instance;
    }

    public static synchronized BasicApplication getInstance() {
        return instance;
    }

    public static Context getContext(){
        return instance;
    }

}
