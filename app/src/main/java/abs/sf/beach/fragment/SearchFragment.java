package abs.sf.beach.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;

import abs.ixi.client.core.Platform;
import abs.ixi.client.util.CollectionUtils;
import abs.ixi.client.util.StringUtils;
import abs.ixi.client.xmpp.JID;
import abs.ixi.client.xmpp.packet.Roster;
import abs.ixi.client.xmpp.packet.UserSearchData;
import abs.sf.beach.adapter.SearchAdapter;
import abs.sf.beach.android.R;
import abs.sf.beach.utils.CommonConstants;
import abs.sf.beach.utils.OnRefreshViewListener;
import abs.sf.client.android.managers.AndroidUserManager;

public class SearchFragment extends Fragment implements OnRefreshViewListener {
    private EditText etFirstName, etLastName, etNickName, et_email;
    private Button btnSearch;
    private RecyclerView rvSearchlist;
    private List<UserSearchData.Item> usersList;
    private SearchAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        initView(view);
        initOnClickListener();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

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
                String firstName = etFirstName.getText().toString();
                String lastName = etLastName.getText().toString();
                String nickName = etNickName.getText().toString();
                String email = et_email.getText().toString();

                if (StringUtils.isNullOrEmpty(firstName) && StringUtils.isNullOrEmpty(lastName) && StringUtils.isNullOrEmpty(nickName) && StringUtils.isNullOrEmpty(email)) {
                    Toast.makeText(getActivity(), "Please fill any search detail", Toast.LENGTH_SHORT).show();

                } else {
                    AndroidUserManager userManager = (AndroidUserManager) Platform.getInstance().getUserManager();
                    usersList = userManager.searchUser(firstName, lastName, nickName, email);


                    if (usersList == null) {
                        Toast.makeText(getActivity(), "No result found", Toast.LENGTH_SHORT).show();

                    } else {
                        setSearchAdapter();
                    }
                }
            }
        });

    }

    private void setSearchAdapter() {
        if (!CollectionUtils.isNullOrEmpty(this.usersList)) {
            rvSearchlist.setLayoutManager(new LinearLayoutManager(getActivity()));
            adapter = new SearchAdapter(getActivity(), usersList);
            rvSearchlist.setAdapter(adapter);
        }
    }

    @Override
    public void refreshView() {
        AndroidUserManager userManager = (AndroidUserManager) Platform.getInstance().getUserManager();
        //this.chatRoom = userManager.getChatRoomDetails(this.roomJID);

        // this.memberList = new ArrayList<>(this.chatRoom.getMembers());
        // this.isGroupMember = this.chatRoom.isRoomMember(Platform.getInstance().getUserJID());

        setSearchAdapter();
    }

}
