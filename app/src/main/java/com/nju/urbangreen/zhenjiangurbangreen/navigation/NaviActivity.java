package com.nju.urbangreen.zhenjiangurbangreen.navigation;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import butterknife.BindView;
import butterknife.ButterKnife;

import com.nju.urbangreen.zhenjiangurbangreen.R;
import com.nju.urbangreen.zhenjiangurbangreen.inspectRecord.InspectListActivity;
import com.nju.urbangreen.zhenjiangurbangreen.maintainRecord.MaintainListActivity;
import com.nju.urbangreen.zhenjiangurbangreen.message.MessageListActivity;
import com.nju.urbangreen.zhenjiangurbangreen.settings.SettingsActivity;
import com.nju.urbangreen.zhenjiangurbangreen.settings.SystemFileActivity;
import com.nju.urbangreen.zhenjiangurbangreen.widget.TitleBarLayout;

public class NaviActivity extends AppCompatActivity implements View.OnClickListener{

    @BindView(R.id.ly_navi_title_bar)
    public TitleBarLayout titleBarLayout;

    @BindView(R.id.imgbtn_settings)
    public ImageButton imgBtnSettings;

    @BindView(R.id.imgbtn_events_record)
    public ImageButton imgBtnEvents;

    @BindView(R.id.imgbtn_maintain_record)
    public ImageButton imgBtnMaintain;

    @BindView(R.id.imgbtn_inspection_record)
    public ImageButton imgBtnInspect;

    @BindView(R.id.imgbtn_message_mag)
    public ImageButton imgBtnMessage;

    @BindView(R.id.imgbtn_file_mag)
    public ImageButton imgBtnFile;

    Intent intent = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navi);
        ButterKnife.bind(this);

        imgBtnSettings.setOnClickListener(this);

        imgBtnEvents.setOnClickListener(this);

        imgBtnMaintain.setOnClickListener(this);

        imgBtnInspect.setOnClickListener(this);

        imgBtnFile.setOnClickListener(this);

        imgBtnMessage.setOnClickListener(this);

        titleBarLayout.setTitleText("功能选择");
        titleBarLayout.setBtnBackClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.imgbtn_settings:
                intent = new Intent(NaviActivity.this, SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.imgbtn_events_record:
                intent = new Intent(NaviActivity.this, InspectListActivity.class);
                intent.putExtra("type","event");
                startActivity(intent);
                break;
            case R.id.imgbtn_maintain_record:
                intent = new Intent(NaviActivity.this, MaintainListActivity.class);
                startActivity(intent);
                break;
            case R.id.imgbtn_inspection_record:
                intent = new Intent(NaviActivity.this, InspectListActivity.class);
                startActivity(intent);
                break;
            case R.id.imgbtn_file_mag:
                intent = new Intent(NaviActivity.this, SystemFileActivity.class);
                startActivity(intent);
                break;
            case R.id.imgbtn_message_mag:
                intent=new Intent(NaviActivity.this, MessageListActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}
