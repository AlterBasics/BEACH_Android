package abs.sf.beach.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import abs.sf.beach.android.R;

public class AttachmentOptionActivity extends StringflowActivity{
    public static final int REQUEST_ATTACHMENT_OPTION = 7;
    public static final String INTENT_ACTION = "intentAttachmentAction";
    public static final int TAKE_PHOTO = 1;
    public static final int CHOOSE_EXISTING = 2;
    public static final int REMOVE_EXISTING = 3;

    //keep track of camera capture intent
    final int CAMERA_CAPTURE = 1;
    //captured picture uri
    private Uri picUri;
    Uri file;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attachment_option);

        ImageButton cancelAttachmentView = (ImageButton) findViewById(R.id.custom_alert_attachment_option_btn_attachment_cancel);
        ImageButton takePhotoView = (ImageButton) findViewById(R.id.custom_alert_attachment_option_btn_take_photo);
        ImageButton chooseExistingView = (ImageButton) findViewById(R.id.custom_alert_attachment_option_btn_choose_existing);
        TextView removePhoto = (TextView) findViewById(R.id.remove_btn_existing);

        cancelAttachmentView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        takePhotoView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    //use standard intent to capture an image
                    Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    //we will handle the returned data in onActivityResult
                    startActivityForResult(captureIntent, CAMERA_CAPTURE);
                }
                catch(ActivityNotFoundException anfe){
                    //display an error message
                    String errorMessage = "Whoops - your device doesn't support capturing images!";
                    Toast toast = Toast.makeText(context(), errorMessage, Toast.LENGTH_SHORT);
                    toast.show();
                }
//                Intent i = getIntent();
//                i.putExtra(INTENT_ACTION, TAKE_PHOTO);
//                setResult(RESULT_OK, i);
//                finish();
//                Intent imageLoader = (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT)
//                        ? new Intent(MediaStore.ACTION_IMAGE_CAPTURE_SECURE)
//                        : new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(imageLoader, 1);
            }
        });

        chooseExistingView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = getIntent();
                i.putExtra(INTENT_ACTION, CHOOSE_EXISTING);
                setResult(RESULT_OK, i);
                finish();
            }
        });
        removePhoto.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = getIntent();
                i.putExtra(INTENT_ACTION, REMOVE_EXISTING);
                setResult(RESULT_OK, i);
                finish();
            }
        });
    }

}
