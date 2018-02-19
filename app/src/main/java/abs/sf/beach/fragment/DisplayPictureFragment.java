package abs.sf.beach.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import abs.ixi.client.util.CollectionUtils;
import abs.sf.beach.android.R;
import abs.sf.beach.utils.FragmentListeners;
import abs.sf.beach.utils.PollActionPerformedListener;

import static abs.sf.beach.android.R.id.etMessage;
import static abs.sf.beach.android.R.id.recyclerViewPolls;


public class DisplayPictureFragment extends Fragment {
    private ImageView ivPicture;
    private Button send;
    private String path;

    public DisplayPictureFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_display_picture,container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        path = getArguments().getString("path");
        initViews(view);
        initOnClickListener();
    }

    private void initViews(View view){
        ivPicture = (ImageView) view.findViewById(R.id.ivPicture);
        send = (Button) view.findViewById(R.id.btnSend);
        if (path==null) return;
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        ivPicture.setImageBitmap(bitmap);
    }

    private void initOnClickListener(){
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentListeners fl = (FragmentListeners) getActivity();
                fl.onCloseFragment();
            }
        });
    }

    public static Fragment getInstance(String path){
        Bundle bundle = new Bundle();
        bundle.putString("path",path);
        DisplayPictureFragment dpf = new DisplayPictureFragment();
        dpf.setArguments(bundle);
        return dpf;
    }
}
