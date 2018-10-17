package abs.sf.beach.activity;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.jar.Attributes;

import abs.ixi.client.core.Platform;
import abs.ixi.client.core.Session;
import abs.ixi.client.util.DateUtils;
import abs.ixi.client.util.StringUtils;
import abs.ixi.client.util.UUIDGenerator;
import abs.ixi.client.xmpp.JID;
import abs.sf.beach.adapter.ChatAdapter;
import abs.sf.beach.android.R;
import abs.sf.beach.fragment.DisplayPictureFragment;
import abs.sf.beach.notification.NotificationGenerator;
import abs.sf.beach.utils.ApplicationProps;
import abs.sf.beach.utils.CommonConstants;
import abs.sf.beach.utils.CustomTypingEditText;
import abs.sf.beach.utils.FragmentListeners;
import abs.sf.beach.utils.VerticalSpaceDecorator;
import abs.sf.client.android.managers.AndroidChatManager;
import abs.sf.client.android.managers.AndroidUserManager;
import abs.sf.client.android.messaging.ChatLine;
import abs.sf.client.android.messaging.ChatListener;
import abs.sf.client.android.notification.fcm.SFFcmService;
import eu.janmuller.android.simplecropimage.CropImage;


public class ChatActivity extends StringflowActivity implements ChatListener, FragmentListeners {
    private static final String CONTACT_JID = "jid";
    private static final String NAME = "name";

    private static final String FROM = "from";
    private static final String NOTIFICATION_UTILS = "NotificationUtils";
    private static final String CONVERSATION_ID = "conversationId";
    private static final String IS_GROUP_MEMBER = "isGroupMember";

    private RecyclerView recyclerView;
    private ChatAdapter adapter;
    private CustomTypingEditText etMessage;
    private Button btnSend, btnAttach;
    private ImageView ivBack, ivNext;
    private TextView tvHeader, tvTyping;
    private List<ChatLine> chatLines;
    private LinearLayout llAttach, llCamera, llGallery;
    private RelativeLayout rlMainChat;
    private Boolean isAttachOpen;
    private Fragment fragment;
    private boolean isFragmentOpen;
    private JID contactJID, userJID;
    private FrameLayout displayPictureContainer;

    private String conversationId;

    private boolean isCSNActive;

    private boolean isGroup;

    private AndroidChatManager chatManager;
    private AndroidUserManager userManager;

    private final static int GROUP_DETAILS = 1;

    private static int REQUEST_IMAGE_CAPTURE = 2;
    private static int REQUEST_IMAGE_SELECT = 3;
    private static int CROP_PICTURE = 4;
    private String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("Chat activity on create");
        setContentView(R.layout.activity_chat);
        initView();
        initOnclickListener();
    }

    private void initView() {
        ivBack = (ImageView) findViewById(R.id.ivBack);
        ivNext = (ImageView) findViewById(R.id.ivNext);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        etMessage = (CustomTypingEditText) findViewById(R.id.etMessage);
        btnSend = (Button) findViewById(R.id.btnSend);
        btnAttach = (Button) findViewById(R.id.btnAttach);
        tvHeader = (TextView) findViewById(R.id.tvHeader);
        tvHeader.setGravity(Gravity.LEFT);
        ivNext.setVisibility(View.INVISIBLE);
        contactJID = (JID) getIntent().getSerializableExtra(CONTACT_JID);
        conversationId = UUIDGenerator.secureId();
        tvHeader.setText(getIntent().getStringExtra(NAME));
        tvTyping = (TextView) findViewById(R.id.tvTyping);
        llAttach = (LinearLayout) findViewById(R.id.llAttach);
        llCamera = (LinearLayout) findViewById(R.id.llCamera);
        llGallery = (LinearLayout) findViewById(R.id.llGallery);
        rlMainChat = (RelativeLayout) findViewById(R.id.rlMainChat);
        displayPictureContainer = (FrameLayout) findViewById(R.id.displayPictureContainer);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        isAttachOpen = false;
    }


    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("Chat activity on resume");
        String from = getIntent().getStringExtra(FROM);

        loadSDK();

        if (!StringUtils.isNullOrEmpty(from) &&
                StringUtils.safeEquals(from, NOTIFICATION_UTILS, false)) {

            contactJID = (JID) getIntent().getSerializableExtra(CONTACT_JID);

            conversationId = (String) getIntent().getSerializableExtra(CONVERSATION_ID);

            tvHeader.setText(contactJID.getNode());

            loginBackground();
        }

        this.chatManager = (AndroidChatManager) Platform.getInstance().getChatManager();
        this.userManager = (AndroidUserManager) Platform.getInstance().getUserManager();

        subscribeForChatline();

        NotificationGenerator.setChatActivity(this);

        setChatAdapter();
    }

    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("Chat activity on stop");
    }

    @Override
    protected void onPause() {
        super.onPause();

        unsubscibeForChatLine();

        NotificationGenerator.removeChatActivity();

        this.chatManager.markNoUnreadConversation(contactJID);

        System.out.println("Chat activity on pause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("Chat activity on destroy");
    }

    private void subscribeForChatline() {
        this.chatManager.addChatListener(this);
        SFFcmService.addChatListener(this);
    }

    private void unsubscibeForChatLine() {
        this.chatManager.removeChatListener(this);
        SFFcmService.removeChatListener(this);
    }

    public JID getJID() {
        return this.contactJID;
    }


    private void setChatAdapter() {
        isGroup = this.userManager.checkIsChatRoom(contactJID);
        userJID = (JID) Platform.getInstance().getSession().get(Session.KEY_USER_JID);

        if (isGroup) {
            boolean isGroupMember = this.userManager.checkIsChatRoomMember(userJID, contactJID);

            String from = getIntent().getStringExtra(FROM);

            //TODO: understand it
            if (!StringUtils.isNullOrEmpty(from) &&
                    StringUtils.safeEquals(from, "UserSearch", false)) {
                isGroupMember = true;
            }

            ivNext.setImageResource(R.mipmap.ic_info);
            ivNext.setVisibility(View.VISIBLE);
            chatMemberViewsHideShowOperation(isGroupMember);

        } else {
            ivNext.setVisibility(View.INVISIBLE);
        }

        this.chatLines = this.chatManager.getAllConversationChatLines(contactJID, isGroup);

        adapter = new ChatAdapter(ChatActivity.this, chatLines, isGroup);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(ChatActivity.this);
        mLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new VerticalSpaceDecorator(5));
        recyclerView.setAdapter(adapter);


        if (chatLines.size() > 0) {
            recyclerView.scrollToPosition(chatLines.size() - 1);

            //TODO: need to think that we should make csn active by watching last inactive chatline or not at this level.
            ChatLine lastReceivedChatLine = getLastReceivedChatLine();
            if(lastReceivedChatLine != null && lastReceivedChatLine.isCsnActive()) {
                this.isCSNActive = true;
            }

            sendAllUnReadMessageReadReceipt();
        }
    }

    private ChatLine getLastReceivedChatLine() {
        for(int i = this.chatLines.size() - 1; i >= 0; i-- ) {
            ChatLine chatLine = this.chatLines.get(i);

            if(chatLine.getDirection() == ChatLine.Direction.RECEIVE) {
                return chatLine;
            }
        }

        return null;
    }

    private void sendAllUnReadMessageReadReceipt() {
        this.chatManager.sendAllUnReadMessageReadReceipt(this.contactJID);
    }

    private void initOnclickListener() {
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ChatActivity.this.isTaskRoot()) {
                    startActivity(new Intent(ChatActivity.this, ChatBaseActivity.class));
                }

                finish();
            }
        });

        ivNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isGroup) {
                    Intent intent = new Intent(ChatActivity.this, GroupDetailsActivity.class);
                    intent.putExtra(CommonConstants.JID, contactJID);
                    startActivityForResult(intent, GROUP_DETAILS);
                }
            }
        });

        rlMainChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionOnAttachLayout();
            }
        });

        btnAttach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionOnAttachLayout();
            }
        });

        llCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                camera();
            }
        });

        llGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gallery();
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etMessage.getText() == null || StringUtils.safeEquals(etMessage.getText().toString(), StringUtils.EMPTY)) {
                    return;
                }

                try {
                    ChatLine chatLine;

                    if(isGroup) {
                        chatLine = chatManager.sendTextMessage(conversationId,
                                etMessage.getText().toString(), contactJID, isGroup);

                    } else {
                        chatLine = chatManager.sendTextMessage(conversationId,
                                etMessage.getText().toString(), contactJID, isGroup);
                    }

                    chatLines.add(chatLine);

                    etMessage.setText("");
                    adapter.notifyItemInserted(chatLines.size() - 1);
                    recyclerView.scrollToPosition(chatLines.size() - 1);
                   // chatManager.sendComposingCSN(contactJID);
                } catch (Exception e) {
                    //swallow
                }
            }
        });

        //TODO : need to verify this mechanism.
        etMessage.setOnTypingModified(new CustomTypingEditText.OnTypingModified() {

            @Override
            public void onIsTypingModified(EditText view, boolean isTyping) {
                if (!isCSNActive) {
                    return;
                }
                if (isTyping) {
                    chatManager.sendMessageTypingStarted(contactJID);

                } else {
                    chatManager.sendMessageTypingStopped(contactJID);
                }
            }
        });
    }

    private void chatMemberViewsHideShowOperation(boolean isGroupMember) {
        if (!isGroupMember) {
            etMessage.setHint(R.string.no_rec_msg);
            etMessage.setGravity(Gravity.CENTER);
            etMessage.setEnabled(false);
            btnSend.setVisibility(View.GONE);
        } else {
            etMessage.setEnabled(true);
            btnSend.setEnabled(true);
        }
    }

    @Override
    public void onNewMessageReceived(ChatLine chatLine) {
        if (StringUtils.safeEquals(chatLine.getPeerBareJid(), this.contactJID.getBareJID(), false)) {
            chatLines.add(chatLine);

            this.isCSNActive = chatLine.isCsnActive();

            conversationId = chatLine.getConversationId();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    recyclerView.scrollToPosition(chatLines.size() - 1);
                    adapter.notifyDataSetChanged();
                }
            });

            sendReadReceipt(chatLine);
        }
    }

    @Override
    public void onMessageSent(final String messageId, final JID contactJID) {
        if (StringUtils.safeEquals(this.contactJID.getBareJID(), contactJID.getBareJID())) {
            for (int position = chatLines.size() - 1; position >= 0; position--) {
                ChatLine line = chatLines.get(position);

                if (StringUtils.safeEquals(line.getMessageId(), messageId)) {
                    line.setMessageStatus(ChatLine.MessageStatus.DELIVERED_TO_SERVER);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });
                    break;
                }

            }
        }
    }

    @Override
    public void onMessageDeliveredToReceiver(final String messageId, final JID contactJID) {
        if (StringUtils.safeEquals(this.contactJID.getBareJID(), contactJID.getBareJID())) {
            for (int position = chatLines.size() - 1; position >= 0; position--) {
                ChatLine line = chatLines.get(position);

                if (StringUtils.safeEquals(line.getMessageId(), messageId)) {
                    line.setMessageStatus(ChatLine.MessageStatus.DELIVERED_TO_RECEIVER);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });
                    break;
                }
            }
        }
    }

    @Override
    public void onMessageAcknowledgedToReceiver(final String messageId, final JID contactJID) {
        if (StringUtils.safeEquals(this.contactJID.getBareJID(), contactJID.getBareJID())) {
            for (int position = chatLines.size() - 1; position >= 0; position--) {
                ChatLine line = chatLines.get(position);

                if (StringUtils.safeEquals(line.getMessageId(), messageId)) {
                    line.setMessageStatus(ChatLine.MessageStatus.RECEIVER_IS_ACKNOWLEDGED);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });
                    break;
                }
            }
        }
    }

    @Override
    public void onMessageViewedByReceiver(final String messageId, final JID contactJID) {
        if (StringUtils.safeEquals(this.contactJID.getBareJID(), contactJID.getBareJID())) {
            for (int position = chatLines.size() - 1; position >= 0; position--) {
                ChatLine line = chatLines.get(position);

                if (StringUtils.safeEquals(line.getMessageId(), messageId)) {
                    line.setMessageStatus(ChatLine.MessageStatus.RECEIVER_HAS_VIEWED);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });
                    break;
                }
            }
        }
    }

    @Override
    public void onContactTypingStarted(JID contactJId) {
        if (StringUtils.safeEquals(this.contactJID.getBareJID(), contactJId.getBareJID())) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvTyping.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    @Override
    public void onContactTypingPaused(JID contactJId) {
        if (StringUtils.safeEquals(this.contactJID.getBareJID(), contactJId.getBareJID())) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvTyping.setVisibility(View.GONE);
                }
            });
        }
    }

    @Override
    public void onContactInactivityInUserChat(JID contactJId) {
        if (StringUtils.safeEquals(this.contactJID.getBareJID(), contactJId.getBareJID())) {
            isCSNActive = false;

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvTyping.setVisibility(View.GONE);
                }
            });
        }
    }

    @Override
    public void onContactGoneFromUserChat(JID contactJId) {
        if (StringUtils.safeEquals(this.contactJID.getBareJID(), contactJId.getBareJID())) {
            isCSNActive = false;

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvTyping.setVisibility(View.GONE);
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        if (ChatActivity.this.isTaskRoot()) {
            startActivity(new Intent(ChatActivity.this, ChatBaseActivity.class));
        }

        if (isFragmentOpen) {
            closeFragment();
            return;
        }

        this.finish();
    }

    public void sendReadReceipt(ChatLine chatLine) {
        this.chatManager.sendMessageReadReceipt(chatLine);
    }

    private void camera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {

                Uri photoURI = Uri.fromFile(photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private void gallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_IMAGE_SELECT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GROUP_DETAILS && resultCode == RESULT_OK) {
            boolean isGroupDeleted = data.getBooleanExtra("isGroupDeleted", false);
            if (isGroupDeleted) {
                ChatActivity.this.finish();
            }
            boolean isGroupMember = data.getBooleanExtra("isGroupMember", true);
            chatMemberViewsHideShowOperation(isGroupMember);
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            addPicInGallery(mCurrentPhotoPath);
            startCropImage(mCurrentPhotoPath);
            /*Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            try {
                FileOutputStream os = new FileOutputStream(mCurrentPhotoPath);
                Bitmap.createBitmap(imageBitmap);
                imageBitmap.compress(Bitmap.CompressFormat.JPEG,100,os);
                os.flush();
                os.close();
            }catch (IOException ie){
                ie.printStackTrace();
            }*/
        } else if (requestCode == REQUEST_IMAGE_SELECT && resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                // Get the path from the Uri
                String path = getPathFromURI(selectedImageUri);
                Log.i("", "Image Path : " + path);
                // Set the image in ImageView
                //imgView.setImageURI(selectedImageUri);
                try {
                    //FileInputStream is = new FileInputStream(selectedImageUri.getPath());
                    Bitmap imageBitmap = BitmapFactory.decodeFile(selectedImageUri.getPath());
                    createImageFile();
                    //mCurrentPhotoPath = file.getPath();
                    FileOutputStream os = new FileOutputStream(mCurrentPhotoPath);
                    imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
                    os.flush();
                    os.close();
                    startCropImage(mCurrentPhotoPath);
                } catch (IOException ie) {
                    ie.printStackTrace();
                } catch (NullPointerException ne) {
                    ne.printStackTrace();
                }
            }
        } else if (requestCode == CROP_PICTURE && resultCode == RESULT_OK) {
            String path = data.getStringExtra(CropImage.IMAGE_PATH);
            if (path == null) {
                return;
            }
            Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
            openFragment(DisplayPictureFragment.getInstance(mCurrentPhotoPath));
            isFragmentOpen = true;
            //profilePic.setImageBitmap(bitmap);
            return;
        }
    }

    private void startCropImage(String path) {
        Intent intent = new Intent(this, CropImage.class);
        intent.putExtra(CropImage.IMAGE_PATH, path);
        intent.putExtra(CropImage.SCALE, true);
        intent.putExtra(CropImage.ASPECT_X, 1);
        intent.putExtra(CropImage.ASPECT_Y, 1);
        startActivityForResult(intent, CROP_PICTURE);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = DateUtils.currentTimestamp().toString();
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStorageDirectory() + ApplicationProps.FILE_SEPARATOR + ApplicationProps.APP_NAME
                + ApplicationProps.FILE_SEPARATOR + "Media" + ApplicationProps.FILE_SEPARATOR + "Images" + ApplicationProps.FILE_SEPARATOR + "Sent");
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }

        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void addPicInGallery(String filePath) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(filePath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private String getPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }

    private void actionOnAttachLayout() {
        if (!isAttachOpen) {
            isAttachOpen = true;
            llAttach.setVisibility(View.VISIBLE);
            // slideUp(llAttach);
        } else {
            isAttachOpen = false;
            //slideDown(llAttach);
            llAttach.setVisibility(View.GONE);
        }
    }

    // slide the view from below itself to the current position
    public void slideUp(View view) {
        view.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                view.getHeight(),  // fromYDelta
                0);                // toYDelta
        animate.setDuration(100);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }

    // slide the view from its current position to below itself
    public void slideDown(View view) {
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                0,                 // fromYDelta
                view.getHeight()); // toYDelta
        animate.setDuration(100);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }

    private void openFragment(Fragment fragment) {
        this.fragment = fragment;
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.displayPictureContainer, fragment);
        fragmentTransaction.commit();
        displayPictureContainer.setVisibility(View.VISIBLE);
        isFragmentOpen = true;
    }

    private void closeFragment() {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.remove(fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
        ft.commit();
        displayPictureContainer.setVisibility(View.GONE);
        isFragmentOpen = false;
    }

    @Override
    public void onCloseFragment() {
        actionOnAttachLayout();
        closeFragment();
        sendImage();
        //TODO: 1. Resize captured image as per display 2. add image in chatline 3. send it
    }

    private void sendImage() {
        try {
            //TODO: handle it later
//            ChatLine chatLine = chatManager.sendTextMessage(conversationId,
//                    etMessage.getText().toString(), contactJID.getBareJID(), isGroup, true, true);
//
//            chatLines.add(chatLine);
//
//            etMessage.setText("");
//            adapter.notifyItemInserted(chatLines.size() - 1);
//            recyclerView.scrollToPosition(chatLines.size() - 1);
//            chatManager.sendComposingCSN(contactJID);
        } catch (Exception e) {
            //swallow
        }
    }
}
