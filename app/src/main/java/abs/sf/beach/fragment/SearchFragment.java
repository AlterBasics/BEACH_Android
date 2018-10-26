package abs.sf.beach.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


import java.util.List;

import abs.ixi.client.util.CollectionUtils;
import abs.ixi.client.xmpp.packet.Roster;
import abs.sf.beach.adapter.SearchAdapter;
import abs.sf.beach.android.R;
import abs.sf.client.android.managers.AndroidUserManager;

public class SearchFragment extends Fragment {
    private EditText etFirstName, etLastName, etNickName, et_email;
    private Button btnSearch;
    private RecyclerView rvSearchlist;
    private List<Roster.RosterItem> rosterItems;
    private SearchAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        initView(view);
        initOnClickListener();
        return view;
    }

    private void initView(View view) {
        etFirstName = (EditText) view.findViewById(R.id.etFirstName);
        etLastName = (EditText) view.findViewById(R.id.etLastName);
        etNickName = (EditText) view.findViewById(R.id.etNickName);
        et_email = (EditText) view.findViewById(R.id.et_email);
        btnSearch = (Button) view.findViewById(R.id.btnSearch);
        rvSearchlist = (RecyclerView) view.findViewById(R.id.rvSearchList);
    }

    private void initOnClickListener() {

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }


    private void setSearchAdapter() {
        if (!CollectionUtils.isNullOrEmpty(this.rosterItems)) {
            rvSearchlist.setLayoutManager(new LinearLayoutManager(getActivity()));
            adapter = new SearchAdapter(getActivity(), rosterItems);
            rvSearchlist.setAdapter(adapter);
        }
    }
}
