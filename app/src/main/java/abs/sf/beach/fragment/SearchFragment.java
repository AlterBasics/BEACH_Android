package abs.sf.beach.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import abs.sf.beach.android.R;

public class SearchFragment extends Fragment {
    private EditText etFirstName, etLastName, etNickName, et_email;
    private Button btnSearch;

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
    }

    private void initOnClickListener() {
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }
}
