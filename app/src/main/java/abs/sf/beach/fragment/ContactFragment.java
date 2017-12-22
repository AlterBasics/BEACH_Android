package abs.sf.beach.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.util.List;

import abs.ixi.client.util.CollectionUtils;
import abs.ixi.client.xmpp.packet.Roster.RosterItem;
import abs.sf.beach.activity.CreateGroupActivity;
import abs.sf.beach.adapter.ContactAdapter;
import abs.sf.beach.android.R;
import abs.sf.client.android.db.DbManager;

public class ContactFragment extends Fragment {
    private RecyclerView recyclerViewContacts;
    private List<RosterItem> rosterItems;
    private EditText etMessage;
    private ContactAdapter adapter;
    private Button btnCreateGroup;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact, container, false);
        initView(view);
        initOnClickListener();
        return view;
    }

    private void initView(View view) {
        etMessage = (EditText) view.findViewById(R.id.etMessage);
        recyclerViewContacts = (RecyclerView) view.findViewById(R.id.recyclerViewContacts);
        btnCreateGroup = (Button) view.findViewById(R.id.btnCreateGroup);
    }

    private void initOnClickListener(){
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

        btnCreateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CreateGroupActivity.class);
                intent.putExtra("name","Create Group");
                startActivity(intent);
            }
        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            rosterItems = DbManager.getInstance().getRosterList();
            setContactAdapter();
        }
    }

    private void setContactAdapter() {
        if (!CollectionUtils.isNullOrEmpty(this.rosterItems)) {
            recyclerViewContacts.setLayoutManager(new LinearLayoutManager(getActivity()));
            adapter = new ContactAdapter(getActivity(), rosterItems);
            recyclerViewContacts.setAdapter(adapter);
        }
    }
}
