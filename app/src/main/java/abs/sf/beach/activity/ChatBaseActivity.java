package abs.sf.beach.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.view.ViewPager;

import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import abs.ixi.client.UserManager;
import abs.ixi.client.core.Platform;
import abs.ixi.client.core.Session;
import abs.ixi.client.xmpp.JID;
import abs.sf.beach.android.R;
import abs.sf.beach.fragment.ContactFragment;
import abs.sf.beach.fragment.ConversationFragment;
import abs.sf.beach.fragment.SearchFragment;
import abs.sf.beach.utils.CommonConstants;
import abs.sf.beach.utils.NotificationUtils;

public class ChatBaseActivity extends StringflowActivity {
    private static final int NUM_PAGES = 3;
    private ViewPager mPager;
    private ImageView ivBack, ivNext, ivConversation, ivContact,ivSearch;
    private TextView tvHeader;
    private LinearLayout llConversation, llContact,llSearch;
    private JID userJID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbase);
        NotificationUtils.clearAllNotifications(ChatBaseActivity.this);
        initView();
        initViewPager();
        initOnclickListeners();

    }

    private void initView() {
        ivBack = (ImageView) findViewById(R.id.ivBack);
        ivNext = (ImageView) findViewById(R.id.ivNext);
        ivConversation = (ImageView) findViewById(R.id.ivConversation);
        ivContact = (ImageView) findViewById(R.id.ivContactImage);
        ivSearch = (ImageView)findViewById(R.id.ivSearch);
        tvHeader = (TextView) findViewById(R.id.tvHeader);
        llConversation = (LinearLayout) findViewById(R.id.llConversation);
        llContact = (LinearLayout) findViewById(R.id.llContact);
        llSearch = (LinearLayout) findViewById(R.id.llSearch);
        mPager = (ViewPager) findViewById(R.id.viewPager);
        ivBack.setVisibility(View.INVISIBLE);
        ivNext.setVisibility(View.VISIBLE);
        userJID = (JID) Platform.getInstance().getSession().get(Session.KEY_USER_JID);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.hide();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setConversationTab();
    }

    private void initViewPager() {
        mPager.setAdapter(new ScreenSlidePagerAdapter(getSupportFragmentManager()));
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        setConversationTab();
                        return;

                    case 1:
                        setContactTab();
                        return;

                    case 2:
                        setSearchTab();
                        return;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }

        });
    }

    private void initOnclickListeners() {
        llConversation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPager.setCurrentItem(0);
            }
        });

        llContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPager.setCurrentItem(1);
            }
        });

        llSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPager.setCurrentItem(2);
            }
        });


        ivNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popUp(v);
//                LayoutInflater myLayout = LayoutInflater.from(context());
//                View dialogView = myLayout.inflate(R.layout.dialog_menu_dots,null);
//                final android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(context());
//                final android.app.AlertDialog dialog1 = dialog.create();
//                dialog1.setView(dialogView);
//                dialog1.show();
//                final TextView tv1 = (TextView) dialogView.findViewById(R.id.tvProfile);
//                final TextView tv2 = (TextView) dialogView.findViewById(R.id.tvSettings);
//                TextView tv3 = (TextView) dialogView.findViewById(R.id.tvLogout);
//
//                tv1.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        Intent intent = new Intent(context(),ProfileActivity.class);
//                        intent.putExtra("jid", CommonConstants.JID);
//                        startActivity(intent);
//                    }
//                });
//
//                tv2.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//
//                    }
//                });
//
//                tv3.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        logout();
//                    }
//                });
//
//                //logout();
            }
        });
    }

    private void setConversationTab(){
        tvHeader.setText("Conversations");
        ivConversation.setImageResource(R.mipmap.chat_invisible);
        ivContact.setImageResource(R.mipmap.contact);
        ivSearch.setVisibility(View.VISIBLE);
    }

    private void setContactTab(){
        tvHeader.setText("Contacts");
        ivConversation.setImageResource(R.mipmap.chat);
        ivContact.setImageResource(R.mipmap.contact_invisible);
        ivSearch.setVisibility(View.VISIBLE);
    }

    private void setSearchTab(){
        tvHeader.setText("Search");
        ivConversation.setImageResource(R.mipmap.chat);
        ivContact.setImageResource(R.mipmap.contact);
        ivSearch.setVisibility(View.VISIBLE);
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getItemPosition(Object object) {
            return super.getItemPosition(object);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    return new ConversationFragment();

                case 1:
                    return new ContactFragment();

                case 2:
                    return new SearchFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }

    private void popUp(View view){
        PopupMenu menu = new PopupMenu(ChatBaseActivity.this, view);
        menu.getMenuInflater().inflate(R.menu.popup_menu, menu.getMenu());
        menu.show();
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.Profile) {
                    Intent intent = new Intent(ChatBaseActivity.this,ProfileActivity.class);
                   intent.putExtra("jid",userJID);
                    startActivity(intent);
                } else if (item.getItemId() == R.id.settings) {


                } else if (item.getItemId() == R.id.logout) {
                    logout();
                }
                return true;
            }
        });
    }
}
