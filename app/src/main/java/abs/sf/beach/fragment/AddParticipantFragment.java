package abs.sf.beach.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import abs.ixi.client.util.CollectionUtils;
import abs.ixi.client.xmpp.JID;
import abs.ixi.client.xmpp.packet.Roster;
import abs.sf.beach.adapter.AddParticipantAdapter;
import abs.sf.beach.android.R;
import abs.sf.beach.utils.AddParticipantsListner;

/**
 * This fragment is use to display all the possible participants that can be
 * added into the group. This will be called from GroupDetailsActivity.
 */

public class AddParticipantFragment extends Fragment {
    private ImageView ivBack, ivNext;
    private TextView tvHeader;
    private EditText etMessage;
    private RecyclerView rvAddParticipant;
    private AddParticipantAdapter adapter;
    private static List<Roster.RosterItem> itemList;
    private static JID roomJID;
    private static String groupName;
    private AddParticipantsListner participantsListner;

    public static Fragment newInstance(List<Roster.RosterItem> items, JID rJID, String name){
        itemList = items;
        roomJID = rJID;
        groupName = name;
        return new AddParticipantFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_participant, container, false);
        initView(view);
        initClickListener();
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = new AddParticipantAdapter(itemList, participantsListner, roomJID, groupName, getActivity());
        rvAddParticipant.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvAddParticipant.setAdapter(adapter);
    }

    private void initView(View view){
        ivBack = (ImageView) view.findViewById(R.id.ivBack);
        ivNext = (ImageView) view.findViewById(R.id.ivNext);
        tvHeader = (TextView) view.findViewById(R.id.tvHeader);
        tvHeader.setText(groupName);
        ivNext.setVisibility(View.INVISIBLE);
        etMessage = (EditText) view.findViewById(R.id.etMessage);
        rvAddParticipant = (RecyclerView) view.findViewById(R.id.rvAddParticipant);

        participantsListner = (AddParticipantsListner) getActivity();
    }

    private void initClickListener(){
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                participantsListner.add("backPress", null);
            }
        });
        etMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!CollectionUtils.isNullOrEmpty(itemList) && adapter!=null) {
                    adapter.filterData(s);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

}

