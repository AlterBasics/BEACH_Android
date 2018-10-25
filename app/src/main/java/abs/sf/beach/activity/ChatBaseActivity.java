package abs.sf.beach.activity;

import android.support.v4.view.ViewPager;

import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import abs.sf.beach.android.R;
import abs.sf.beach.fragment.ContactFragment;
import abs.sf.beach.fragment.ConversationFragment;
import abs.sf.beach.fragment.SearchFragment;
import abs.sf.beach.utils.NotificationUtils;

public class ChatBaseActivity extends StringflowActivity {
    private static final int NUM_PAGES = 3;
    private ViewPager mPager;
    private ImageView ivBack, ivNext, ivConversation, ivContact,ivSearch;
    private TextView tvHeader;
    private LinearLayout llConversation, llContact,llSearch;


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
                logout();
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
}
