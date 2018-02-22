package abs.sf.beach.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.widget.Toast;

/**
 * Holds Convenience methods related to Android
 */
public class AndroidUtils {
    /**
     * Show an alert box. The alert box will be displayed within invocation thread.
     *
     * @param ctx
     * @param alert
     */
    public static void showAlert(Context ctx, String alert, String title) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(ctx);
        dialog
                .setMessage(alert)
                .setTitle(title)
                .setCancelable(false)
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
    }

    public static void showToast(Context ctx, String alert) {
        Toast.makeText(ctx, alert, Toast.LENGTH_LONG).show();
    }

    public static int screenWidth(Context ctx){
        DisplayMetrics displayMetrics = getDisplayMetrics(ctx);
        return displayMetrics.widthPixels;
    }

    public static int screenHeight(Context ctx){
        DisplayMetrics displayMetrics = getDisplayMetrics(ctx);
        return displayMetrics.heightPixels;
    }

    private static DisplayMetrics getDisplayMetrics(Context ctx){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) ctx).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics;
    }

}
