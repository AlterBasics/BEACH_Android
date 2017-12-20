package abs.sf.beach.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import abs.ixi.client.util.CollectionUtils;
import abs.ixi.client.xmpp.packet.Roster;
import abs.sf.beach.adapter.PollRecipientAdapter;
import abs.sf.beach.android.R;
import abs.sf.client.android.db.DbManager;


public class PollRecipientActivity extends StringflowActivity {
    private RecyclerView recyclerViewContacts;
    private List<Roster.RosterItem> rosterItems;
    private EditText etMessage;
    private PollRecipientAdapter adapter;
    private ImageView ivNext, ivBack;
    private TextView tvHeader;
    private long pollId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poll_recipient);
        initViews();
        initOnClickListener();
        rosterItems = DbManager.getInstance().getRosterList();
        setContactAdapter();
    }

    private void initViews(){
        etMessage = (EditText) findViewById(R.id.etMessage);
        recyclerViewContacts = (RecyclerView) findViewById(R.id.recyclerViewContacts);
        ivBack = (ImageView) findViewById(R.id.ivBack);
        ivNext = (ImageView) findViewById(R.id.ivNext);
        tvHeader = (TextView) findViewById(R.id.tvHeader);
        ivNext.setImageResource(R.mipmap.send);
        ivNext.setVisibility(View.VISIBLE);
        tvHeader.setText("Recipients");
        pollId = getIntent().getLongExtra("pollId", -1);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.hide();
        }
    }

    private void initOnClickListener(){
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PollRecipientActivity.this.finish();
            }
        });
        ivNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.sendData(pollId);
            }
        });
        etMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!CollectionUtils.isNullOrEmpty(rosterItems) && adapter!=null) {
                    adapter.filterData(s);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void setContactAdapter() {
        if (!CollectionUtils.isNullOrEmpty(this.rosterItems)) {
            recyclerViewContacts.setLayoutManager(new LinearLayoutManager(PollRecipientActivity.this));
            adapter = new PollRecipientAdapter(PollRecipientActivity.this, rosterItems);
            recyclerViewContacts.setAdapter(adapter);
        }
    }

    @Override
    public void onBackPressed() {
        PollRecipientActivity.this.finish();
    }
}
