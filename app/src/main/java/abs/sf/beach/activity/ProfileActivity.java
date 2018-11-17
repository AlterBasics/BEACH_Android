package abs.sf.beach.activity;

import android.app.Activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.CardView;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import abs.ixi.client.core.Platform;
import abs.ixi.client.core.Session;
import abs.ixi.client.xmpp.JID;
import abs.ixi.client.xmpp.packet.UserProfileData;
import abs.sf.beach.android.R;
import abs.sf.beach.utils.CommonConstants;
import abs.sf.client.android.managers.AndroidUserManager;
import eu.janmuller.android.simplecropimage.CropImage;


public class ProfileActivity extends StringflowActivity {
    private ImageView ivBack, ivNext, editPic;
    private TextView tvHeaders, tvCreatGrp;
    private EditText etMessage;
    private TextView tvUserName, tvUserFirstName, tvUserMiddleName, tvUserLastName, tvUserNickName, tvUserGender, tvUserBday,
            tvUserPhoneNo, tvUserEmail, tvUserAddress, tvUserHome, tvUserStreet, tvUserLocality, tvUserPinCode, tvUserCity, tvUserState, tvUserCountry, tvUserAbout;
    private ScrollView scroller;
    private ImageView ivProfilePic;
    private CardView editInfo;
    private Button updateInfo;

    protected static final int CAMERA_REQUEST = 3;
    protected static final int GALLERY_PICTURE = 4;
    protected static final int CROP_PICTURE = 5;
    protected Uri mFileTempUri;
    public ArrayList<String> galleryList = new ArrayList<String>();
    protected static File mFileTemp = null;
    private String isImagePresent = "0";
    private JID jid, userJID;

    final int CAMERA_CAPTURE = 1;
    final int PIC_CROP = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initView();
        initOnClicklitener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.jid = (JID) getIntent().getSerializableExtra(CommonConstants.JID);
        this.userJID = (JID) Platform.getInstance().getSession().get(Session.KEY_USER_JID);
        showData();

    }

    private void initView() {
        ivBack = (ImageView) findViewById(R.id.ivBack);
        tvCreatGrp = (TextView) findViewById(R.id.tvCreateGroup);
        ivNext = (ImageView) findViewById(R.id.ivNext);
        ivNext.setVisibility(View.GONE);
        tvHeaders = (TextView) findViewById(R.id.tvHeaders);
        tvHeaders.setText("User Profile");
        tvHeaders.setVisibility(View.VISIBLE);
        tvCreatGrp.setVisibility(View.GONE);
        ivProfilePic = (ImageView) findViewById(R.id.ivUser);
        tvUserName = (TextView) findViewById(R.id.tvUsername);
        tvUserFirstName = (TextView) findViewById(R.id.etUserFirstName);
        tvUserMiddleName = (TextView) findViewById(R.id.etUserMiddletName);
        tvUserLastName = (TextView) findViewById(R.id.etUserLastName);
        tvUserNickName = (TextView) findViewById(R.id.etUserNickName);
        tvUserGender = (TextView) findViewById(R.id.etUserGender);
        tvUserBday = (TextView) findViewById(R.id.etUserBday);
        tvUserPhoneNo = (TextView) findViewById(R.id.etUserPhone);
        tvUserEmail = (TextView) findViewById(R.id.etUserEmail);
        tvUserHome = (TextView) findViewById(R.id.etUserHome);
        tvUserStreet = (TextView) findViewById(R.id.etUserStreet);
        tvUserLocality = (TextView) findViewById(R.id.etUserLocality);
        tvUserPinCode = (TextView) findViewById(R.id.etUserPinCode);
        tvUserCity = (TextView) findViewById(R.id.etUserCity);
        tvUserState = (TextView) findViewById(R.id.etUserState);
        tvUserCountry = (TextView) findViewById(R.id.etUserCountry);
        tvUserAbout = (TextView) findViewById(R.id.etUserAbout);
        scroller = (ScrollView) findViewById(R.id.scroller);
        editPic = (ImageView) findViewById(R.id.iv_edit_pic);
        editPic.setVisibility(View.GONE);
        editInfo =(CardView)findViewById(R.id.edit_card);
        editInfo.setVisibility(View.INVISIBLE);
        updateInfo = (Button)findViewById(R.id.btnUpdate);
        if (jid == userJID) {
            editPic.setVisibility(View.VISIBLE);
            editInfo.setVisibility(View.VISIBLE);
        }




        scroller.post(new Runnable() {
            public void run() {
                scroller.fullScroll(ScrollView.FOCUS_UP);
            }
        });

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.hide();
        }

    }

    private void initOnClicklitener() {
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProfileActivity.this.finish();
            }
        });

        editPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    showPicImageDialog();
                }
            }
        });

        editInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvUserFirstName.setCursorVisible(true);
                tvUserFirstName.setEnabled(true);
                tvUserFirstName.setClickable(true);
                tvUserFirstName.setFocusableInTouchMode(true);
                tvUserFirstName.setInputType(InputType.TYPE_CLASS_TEXT);
                tvUserFirstName.requestFocus();

                tvUserMiddleName.setEnabled(true);
                tvUserMiddleName.setClickable(true);
                tvUserMiddleName.setFocusableInTouchMode(true);
                tvUserMiddleName.setInputType(InputType.TYPE_CLASS_TEXT);

                tvUserLastName.setEnabled(true);
                tvUserLastName.setClickable(true);
                tvUserLastName.setFocusableInTouchMode(true);
                tvUserLastName.setInputType(InputType.TYPE_CLASS_TEXT);

                tvUserNickName.setEnabled(true);
                tvUserNickName.setClickable(true);
                tvUserNickName.setFocusableInTouchMode(true);
                tvUserNickName.setInputType(InputType.TYPE_CLASS_TEXT);

                tvUserGender.setEnabled(true);
                tvUserGender.setClickable(true);
                tvUserGender.setFocusableInTouchMode(true);
                tvUserGender.setInputType(InputType.TYPE_CLASS_TEXT);

                tvUserBday.setEnabled(true);
                tvUserBday.setClickable(true);
                tvUserBday.setFocusableInTouchMode(true);
                tvUserBday.setInputType(InputType.TYPE_CLASS_TEXT);

                tvUserPhoneNo.setEnabled(true);
                tvUserPhoneNo.setClickable(true);
                tvUserPhoneNo.setFocusableInTouchMode(true);
                tvUserPhoneNo.setInputType(InputType.TYPE_CLASS_TEXT);

                tvUserFirstName.setEnabled(true);
                tvUserFirstName.setClickable(true);
                tvUserFirstName.setFocusableInTouchMode(true);
                tvUserFirstName.setInputType(InputType.TYPE_CLASS_TEXT);

                tvUserEmail.setEnabled(true);
                tvUserEmail.setClickable(true);
                tvUserEmail.setFocusableInTouchMode(true);
                tvUserEmail.setInputType(InputType.TYPE_CLASS_TEXT);

                tvUserHome.setEnabled(true);
                tvUserHome.setClickable(true);
                tvUserHome.setFocusableInTouchMode(true);
                tvUserHome.setInputType(InputType.TYPE_CLASS_TEXT);

                tvUserStreet.setEnabled(true);
                tvUserStreet.setClickable(true);
                tvUserStreet.setFocusableInTouchMode(true);
                tvUserStreet.setInputType(InputType.TYPE_CLASS_TEXT);

                tvUserLocality.setEnabled(true);
                tvUserLocality.setClickable(true);
                tvUserLocality.setFocusableInTouchMode(true);
                tvUserLocality.setInputType(InputType.TYPE_CLASS_TEXT);

                tvUserPinCode.setEnabled(true);
                tvUserPinCode.setClickable(true);
                tvUserPinCode.setFocusableInTouchMode(true);
                tvUserPinCode.setInputType(InputType.TYPE_CLASS_TEXT);

                tvUserCity.setEnabled(true);
                tvUserCity.setClickable(true);
                tvUserCity.setFocusableInTouchMode(true);
                tvUserCity.setInputType(InputType.TYPE_CLASS_TEXT);

                tvUserState.setEnabled(true);
                tvUserState.setClickable(true);
                tvUserState.setFocusableInTouchMode(true);
                tvUserState.setInputType(InputType.TYPE_CLASS_TEXT);

                tvUserCountry.setEnabled(true);
                tvUserCountry.setClickable(true);
                tvUserCountry.setFocusableInTouchMode(true);
                tvUserCountry.setInputType(InputType.TYPE_CLASS_TEXT);

                tvUserAbout.setEnabled(true);
                tvUserAbout.setClickable(true);
                tvUserAbout.setFocusableInTouchMode(true);
                tvUserAbout.setInputType(InputType.TYPE_CLASS_TEXT);
            }
        });

        updateInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateInfo();
            }
        });

    }

    private UserProfileData showData() {
        AndroidUserManager userManager = (AndroidUserManager) Platform.getInstance().getUserManager();
        UserProfileData userProfileData = userManager.getCachedUserProfileData(jid);

        if (userProfileData == null) {
            userProfileData = userManager.getUserProfileData(jid);

        }
        tvUserName.setText(jid.getNode());
        tvUserFirstName.setText(userProfileData.getFirstName());
        tvUserMiddleName.setText(userProfileData.getMiddleName());
        tvUserNickName.setText(userProfileData.getNickName());
        tvUserLastName.setText(userProfileData.getLastName());
        tvUserGender.setText(userProfileData.getGender());
        tvUserBday.setText(userProfileData.getBday());
        tvUserPhoneNo.setText(userProfileData.getPhone());
        tvUserEmail.setText(userProfileData.getEmail());

        if (userProfileData.getAddress() != null) {
            tvUserHome.setText(userProfileData.getAddress().getHome());
            tvUserStreet.setText(userProfileData.getAddress().getStreet());
            tvUserLocality.setText(userProfileData.getAddress().getLocality());
            tvUserPinCode.setText(userProfileData.getAddress().getPcode());
            tvUserCity.setText(userProfileData.getAddress().getCity());
            tvUserState.setText(userProfileData.getAddress().getState());
            tvUserCountry.setText(userProfileData.getAddress().getCountry());
        }

        tvUserAbout.setText(userProfileData.getDescription());

        this.ivProfilePic.setImageBitmap(userManager.getUserAvatar(this.jid));


        return userProfileData;
    }

    private void showPicImageDialog() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            mFileTemp = new File(Environment.getExternalStorageDirectory(),
                    CommonConstants.TEMP_PHOTO_FILE_NAME);
        } else {
            mFileTemp = new File(context().getFilesDir(),
                    CommonConstants.TEMP_PHOTO_FILE_NAME);
        }
        Intent attachmentIntent = new Intent(context(),
                AttachmentOptionActivity.class);
        attachmentIntent.putExtra("ispresent", isImagePresent);
        startActivityForResult(attachmentIntent,
                AttachmentOptionActivity.REQUEST_ATTACHMENT_OPTION);
    }


    private void performCrop() {
        try {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            //indicate image type and Uri
            cropIntent.setDataAndType(mFileTempUri, "image/*");
            //set crop properties
            cropIntent.putExtra("crop", "true");
            //indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            //indicate output X and Y
            cropIntent.putExtra("outputX", 32);
            cropIntent.putExtra("outputY", 60);
            //retrieve data on return
            cropIntent.putExtra("return-data", true);
            //start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, PIC_CROP);

        } catch (ActivityNotFoundException anfe) {
            //display an error message
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != Activity.RESULT_OK) {
            return;
        }


        switch (requestCode) {
            case CAMERA_CAPTURE:

                mFileTempUri = data.getData();
                startCropImage();
                //performCrop();
                break;
            case GALLERY_PICTURE:
                try {
                    InputStream inputStream = context().getContentResolver()
                            .openInputStream(data.getData());
                    FileOutputStream fileOutputStream = new FileOutputStream(
                            mFileTemp);
                    copyStream(inputStream, fileOutputStream);
                    fileOutputStream.close();
                    inputStream.close();
                    startCropImage();
                    //performCrop();
                } catch (Exception e) {
                    Log.e(CommonConstants.SETTINGS_FRAGMENT_PROFILE_TAG,
                            CommonConstants.ERR_CREATING_TEMP_FILE, e);
                }
                break;
            case PIC_CROP:
                String path = data.getStringExtra(CropImage.IMAGE_PATH);
                if (path == null) {
                    return;
                }
                Bundle extras = data.getExtras();
                Bitmap thePic = extras.getParcelable("data");
                ivProfilePic = (ImageView) findViewById(R.id.ivUser);
                ivProfilePic.setImageBitmap(thePic);

            case AttachmentOptionActivity.REQUEST_ATTACHMENT_OPTION:
                if (null != data) {
                    int action = data.getIntExtra(
                            AttachmentOptionActivity.INTENT_ACTION, 0);
                    if (action == AttachmentOptionActivity.TAKE_PHOTO) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        mFileTempUri = Uri.fromFile(mFileTemp);
                        try {
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, mFileTempUri);
                            fillPhotoList();
                            startActivityForResult(intent, CAMERA_REQUEST);
                        } catch (ActivityNotFoundException e) {
                            Log.d(CommonConstants.CHAT_FRAGMENT_MAIN_TAG,
                                    CommonConstants.CANNOT_TAKE_PIC, e);
                        }
                    } else if (action == AttachmentOptionActivity.CHOOSE_EXISTING) {
                        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                        photoPickerIntent.setType(CommonConstants.IMAGE_);
                        //ContentActivity._tempFragment = this;
                        startActivityForResult(photoPickerIntent, GALLERY_PICTURE);
                    } else if (action == AttachmentOptionActivity.REMOVE_EXISTING) {
                        //CommonUtil.showAlert(context(),"remove image");
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
        //ContentActivity._tempFragment = null;
    }

    public static void copyStream(InputStream input, OutputStream output)
            throws IOException {
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
    }

    protected void startCropImage() {

        Log.d("", "file path is" + mFileTemp.getPath());

        Intent intent = new Intent(context(), CropImage.class);
        intent.putExtra(CropImage.IMAGE_PATH, mFileTemp.getPath());
        intent.setDataAndType(mFileTempUri  , "image/*");
        //set crop properties
        intent.putExtra("crop", "true");
        //indicate aspect of desired crop
        intent.putExtra(CropImage.SCALE, true);
        intent.putExtra(CropImage.ASPECT_X, 1);
        intent.putExtra(CropImage.ASPECT_Y, 1);
        intent.putExtra("outputX", 32);
        intent.putExtra("outputY", 60);
        startActivityForResult(intent, CROP_PICTURE);
    }

    private void fillPhotoList() {
        // initialize the list!
        galleryList.clear();
        String[] projection = {MediaStore.Images.ImageColumns.DISPLAY_NAME};
        // intialize the Uri and the Cursor, and the current expected size.
        Cursor c = null;
        Uri u = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        //
        // Query the Uri to get the data path. Only if the Uri is valid.
        if (u != null) {
            c = getContentResolver().query(u, projection, null, null, ContactsContract.Contacts.DISPLAY_NAME);
        }

        // If we found the cursor and found a record in it (we also have the
        // id).
        if ((c != null) && (c.moveToFirst())) {
            do {
                // Loop each and add to the list.
                galleryList.add(c.getString(0));
            } while (c.moveToNext());
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mFileTempUri != null) {
            outState.putString(CommonConstants.CAMERA_URI, mFileTempUri.toString());
        }
        if (mFileTemp != null) {
            outState.putString(CommonConstants.IMAGE_FILE_PATH, mFileTemp.getPath());
        }
    }

    private void updateInfo(){
        String firstName = tvUserFirstName.getText().toString();
        String middleName = tvUserMiddleName.getText().toString();
        String lastName = tvUserLastName.getText().toString();
        String nickName = tvUserNickName.getText().toString();
        String gender = tvUserGender.getText().toString();
        String bday = tvUserBday.getText().toString();
        String phoneNo = tvUserPhoneNo.getText().toString();
        String email =tvUserEmail.getText().toString();
        String home =tvUserHome.getText().toString();
        String street = tvUserStreet.getText().toString();
        String locality = tvUserLocality.getText().toString();
       String pinCode =tvUserPinCode.getText().toString();
       String city =tvUserCity.getText().toString();
        String state =tvUserState.getText().toString();
        String country =tvUserCountry.getText().toString();
        String about = tvUserAbout.getText().toString();
        AndroidUserManager userManager = (AndroidUserManager)Platform.getInstance().getUserManager();
        userManager.updateUserProfileData(firstName,middleName,lastName,email,phoneNo,gender,bday,home,street,locality,city,state,country,pinCode);
    }


}
