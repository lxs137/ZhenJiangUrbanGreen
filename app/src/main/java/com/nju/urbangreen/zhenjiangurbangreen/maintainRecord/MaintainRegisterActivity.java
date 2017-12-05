package com.nju.urbangreen.zhenjiangurbangreen.maintainRecord;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nju.urbangreen.zhenjiangurbangreen.R;
import com.nju.urbangreen.zhenjiangurbangreen.attachments.AttachmentListActivity;
import com.nju.urbangreen.zhenjiangurbangreen.basisClass.BaseRegisterActivity;
import com.nju.urbangreen.zhenjiangurbangreen.basisClass.Constants;
import com.nju.urbangreen.zhenjiangurbangreen.basisClass.GreenObject;
import com.nju.urbangreen.zhenjiangurbangreen.ugo.UgoListActivity;
import com.nju.urbangreen.zhenjiangurbangreen.util.CacheUtil;
import com.nju.urbangreen.zhenjiangurbangreen.util.WebServiceUtils;
import com.nju.urbangreen.zhenjiangurbangreen.widget.DropdownEditText;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MaintainRegisterActivity extends BaseRegisterActivity {

    @BindView(R.id.tv_maintainInfo_Code)
    TextView tvMaintainCode;
    @BindView(R.id.et_maintainInfo_date)
    EditText etMaintainDate;
    @BindView(R.id.droplist_maintainInfo_type)
    DropdownEditText dropdownMaintainType;
    @BindView(R.id.et_maintainInfo_staff)
    EditText etMaintainStaff;
    @BindView(R.id.et_maintainInfo_content)
    EditText etMaintainContent;
    @BindView(R.id.ly_maintain_info_table)
    TableLayout lyMaintainInfoTable;
    @BindView(R.id.Toolbar)
    Toolbar toolbar;
    @BindView(R.id.btn_maintain_register_submit)
    AppCompatButton btnMaintainRegisterSubmit;


    public DatePickerDialog dtpckMaintainDate;
    private Maintain maintainObject;
    private int updateState;
    private String ugoIds;
    private String maintainId;


    private static final String TAG = "MaintainRegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maintain_register);
        ButterKnife.bind(this);

        initToolbar();

        ArrayList<String> dropdownList = new ArrayList<>();
        dropdownList.addAll(Arrays.asList(getResources().getStringArray(R.array.maintainTypeDropList)));
        dropdownMaintainType.setDropdownList(dropdownList);

        initDatePicker();
        getMaintainObject();
        upload();

    }

    private void getMaintainObject() {
        Intent intent = getIntent();
        Serializable serializableObject = intent.getSerializableExtra("maintain_object");
        if (serializableObject != null) {
            maintainObject = (Maintain) serializableObject;
            tvMaintainCode.setText(maintainObject.MR_Code);
            dropdownMaintainType.setText(maintainObject.MR_MaintainType);
            etMaintainDate.setText(maintainObject.MR_MaintainDate);
            etMaintainStaff.setText(maintainObject.MR_MaintainStaff);
            etMaintainContent.setText(maintainObject.MR_MaintainContent);
            maintainId = maintainObject.MR_ID;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.attachment:
                Intent intent = new Intent(MaintainRegisterActivity.this, AttachmentListActivity.class);
                if (maintainId != null) {
                    intent.putExtra("id", maintainId);
                    startActivity(intent);
                }else{
                    Toast.makeText(this, "请先保存信息再上传附件", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.greenObjects:
                Intent intent2 = new Intent(MaintainRegisterActivity.this, UgoListActivity.class);
                if (maintainId != null)
                    intent2.putExtra("id", maintainId);
                intent2.putExtra("activity","maintain");
                    startActivity(intent2);
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        validateEmpty(Constants.CLICK_BACK_BUTTON);
    }

    private void initToolbar() {
        toolbar.setTitle("管养记录登记");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initDatePicker() {
        Calendar currentCalendar;
        int year, month, day;
        currentCalendar = Calendar.getInstance();
        year = currentCalendar.get(Calendar.YEAR);
        month = currentCalendar.get(Calendar.MONTH);
        day = currentCalendar.get(Calendar.DAY_OF_MONTH);
        dtpckMaintainDate = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                etMaintainDate.setText(year + "-" + (month + 1) + "-" + dayOfMonth);
            }
        }, year, month, day);
        dtpckMaintainDate.getDatePicker().setMaxDate(currentCalendar.getTimeInMillis());
        etMaintainDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dtpckMaintainDate.show();
            }
        });
        etMaintainDate.setText(year + "-" + (month + 1) + "-" + day);
    }

    //上传（提交）表单
    private void upload() {
        btnMaintainRegisterSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] errMsg = new String[1];
                if (validateEmpty(Constants.CLICK_UPLOAD_BUTTON)) {
                    outputObject();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final Boolean res;
                            if (tvMaintainCode.getText() == "") {
                                res = WebServiceUtils.AddMaintainRecord(errMsg, maintainObject);
                            } else {
                                res = WebServiceUtils.UpdateMaintainRecord(errMsg, maintainObject);
                            }

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (res) {
                                        Toast.makeText(MaintainRegisterActivity.this, "上传成功!", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent();
                                        intent.putExtra("upload_status",true);
                                        setResult(RESULT_OK,intent);
                                        finish();
                                    } else {
                                        Toast.makeText(MaintainRegisterActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                    ).start();
                }
            }
        });
    }


    private void outputObject() {
        maintainObject = new Maintain();
        maintainObject.MR_ID = maintainId;
        maintainObject.MR_MaintainType = dropdownMaintainType.getText();
        maintainObject.MR_MaintainDate = etMaintainDate.getText().toString();
        maintainObject.MR_MaintainStaff = etMaintainStaff.getText().toString();
        if (etMaintainContent.getText() != null) {
            maintainObject.MR_MaintainContent = etMaintainContent.getText().toString();
        }
        if(tvMaintainCode.getText()!=null){
            maintainObject.MR_Code = tvMaintainCode.getText().toString();
        }
        maintainObject.UGO_IDs = getUGOIDs();
    }


    //验证是否为空
    private boolean validateEmpty(int flag) {
        int emptyStatus = 0;
        //个位数为1代表type为空，十位数为1代表staff为空
        if (dropdownMaintainType.getText().equals(""))
            emptyStatus += 1;
        if (etMaintainStaff.getText().toString().equals(""))
            emptyStatus += 10;

        //保证必填项不为空
        if (emptyStatus != 0) {
            if (emptyStatus % 10 == 1)
                dropdownMaintainType.setEmptyWarning();
            else
                dropdownMaintainType.setCommonDrawable();
            if (emptyStatus >= 10)
                etMaintainStaff.setBackground(getResources().getDrawable(R.drawable.bkg_edittext_empty));
            else
                etMaintainStaff.setBackground(getResources().getDrawable(R.drawable.bkg_edittext));
            showPrompt(flag);
            return false;
        } else if( getUGOIDs().equals("")){
            Toast.makeText(this, "绿化对象不能为空！", Toast.LENGTH_SHORT).show();
            return false;
        }
        else {
            return true;
        }
    }

    //必填项为空错误提示
    private void showPrompt(int flag) {
        if (flag == Constants.CLICK_BACK_BUTTON) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MaintainRegisterActivity.this);
            builder.setTitle("温馨提示");
            builder.setMessage("有必填项为空，返回后相关信息不会保存");
            builder.setCancelable(false);
            builder.setPositiveButton("仍然退出", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });
            builder.setNegativeButton("继续编辑", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            builder.show();
        } else {
            Toast.makeText(this, "有必填项为空，无法提交", Toast.LENGTH_SHORT).show();
        }
    }

    //从缓存中读取相关对象
    private String getUGOIDs() {
//        ACache mCache = ACache.get(this);
//        List<GreenObject> ugoSelectedList = mCache.getAsObjectList("ugo_select");
        List<GreenObject> ugoSelectedList = CacheUtil.getRelatedUgos();
        StringBuilder builder = new StringBuilder();

        if (ugoSelectedList != null) {
            for (GreenObject o : ugoSelectedList) {
                if (builder.length() != 0) {
                    builder.append(",");
                }
                builder.append(o.UGO_ID);
            }
            Log.d("tag", builder.toString());
        }
        return builder.toString();
    }

}
