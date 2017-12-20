package abs.sf.beach.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.List;

import abs.ixi.client.util.CollectionUtils;
import abs.sf.beach.adapter.PollSavedAdapter;
import abs.sf.beach.android.R;
import abs.sf.beach.utils.PollActionPerformedListener;
import abs.sf.client.android.db.DbManager;
import abs.sf.client.android.messaging.PollContent;


public class PollSavedFragment extends Fragment {
    private RecyclerView recyclerViewPolls;
    private EditText etMessage;
    private List<PollContent> pollContentsList;
    private PollSavedAdapter adapter;
    private PollActionPerformedListener apl;

    public PollSavedFragment(){}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_saved_poll,container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        initOnClickListener();
        pollContentsList = DbManager.getInstance().getSavedPolls();
        setPollAdapter();
    }

    private void initViews(View view){
        etMessage = (EditText) view.findViewById(R.id.etMessage);
        recyclerViewPolls = (RecyclerView) view.findViewById(R.id.recyclerViewPolls);
        apl = (PollActionPerformedListener) getActivity();
    }

    private void initOnClickListener(){
        etMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!CollectionUtils.isNullOrEmpty(pollContentsList) && adapter!=null) {
                    adapter.filterData(s);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void setPollAdapter(){
        if (!CollectionUtils.isNullOrEmpty(this.pollContentsList)) {
            recyclerViewPolls.setLayoutManager(new LinearLayoutManager(getActivity()));
            adapter = new PollSavedAdapter(getActivity(), pollContentsList, apl);
            recyclerViewPolls.setAdapter(adapter);
        }
    }


    public static PollSavedFragment newInstance() {

        Bundle args = new Bundle();

        PollSavedFragment fragment = new PollSavedFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
