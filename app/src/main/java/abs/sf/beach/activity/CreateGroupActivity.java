package abs.sf.beach.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import abs.ixi.client.util.StringUtils;
import abs.sf.beach.android.R;
import abs.sf.beach.utils.AndroidUtils;
import abs.sf.client.android.managers.AndroidUserManager;

public class CreateGroupActivity extends StringflowActivity {
    private Spinner spGroupType;
    private ImageView ivGroup;
    private Button btnCreateGroup;
    private String selectedGroupType;
    private EditText etGroupName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        initView();
        initOnclickListener();
    }

    private void initView(){
        spGroupType = (Spinner) findViewById(R.id.spGroupType);
        ivGroup = (ImageView) findViewById(R.id.ivGroup);
        btnCreateGroup = (Button) findViewById(R.id.btnCreateGroup);
        etGroupName = (EditText) findViewById(R.id.etGroupName);
    }

    private void initOnclickListener(){
        spGroupType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedGroupType = adapterView.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btnCreateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String groupName = etGroupName.getText().toString();
                if(StringUtils.isNullOrEmpty(groupName)){
                    AndroidUtils.showToast(CreateGroupActivity.this, "Please enter group name");
                }else{
                    boolean status = AndroidUserManager.getInstance().createChatRoom(groupName);
                }
            }
        });
    }
}
