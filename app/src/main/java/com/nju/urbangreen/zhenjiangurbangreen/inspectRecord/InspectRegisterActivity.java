package com.nju.urbangreen.zhenjiangurbangreen.inspectRecord;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.miguelcatalan.materialsearchview.MaterialSearchView;
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

import static com.nju.urbangreen.zhenjiangurbangreen.basisClass.Constants.CLICK_BACK_BUTTON;

public class InspectRegisterActivity extends BaseRegisterActivity {

    @BindView(R.id.Toolbar)
    android.support.v7.widget.Toolbar toolbar;
    @BindView(R.id.material_search_view)
    MaterialSearchView materialSearchView;
    @BindView(R.id.tv_inspectInfo_code)
    TextView tvInspectInfoCode;
    @BindView(R.id.droplist_inspectInfo_type)
    DropdownEditText droplistInspectInfoType;
    @BindView(R.id.et_inspectInfo_date)
    EditText etInspectInfoDate;
    @BindView(R.id.et_inspectInfo_score)
    EditText etInspectInfoScore;
    @BindView(R.id.et_inspectInfo_staff)
    EditText etInspectInfoStaff;
    @BindView(R.id.et_inspectInfo_location)
    EditText etInspectInfoLocation;
    @BindView(R.id.et_inspectInfo_content)
    EditText etInspectInfoContent;
    @BindView(R.id.et_inspectInfo_opinion)
    EditText etInspectInfoOpinion;
    @BindView(R.id.ly_maintain_info_table)
    TableLayout lyMaintainInfoTable;
    @BindView(R.id.btn_inspect_register_submit)
    AppCompatButton btnInspectRegisterSubmit;

    public DatePickerDialog dtpckInspectDate;
    private String inspectId;

    private Inspect inspectObject;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspect_register);
        ButterKnife.bind(this);
        initToolbar();
        initDatePicker();
        getInspectObject();
        initDroplist();
        upload();

    }

    private void initDroplist() {
        ArrayList<String> dropdownList = new ArrayList<>();
        dropdownList.addAll(Arrays.asList(getResources().getStringArray(R.array.insepctTypeDropList)));
        droplistInspectInfoType.setDropdownList(dropdownList);
    }


    private void initToolbar() {
        toolbar.setTitle("巡查记录登记");
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.attachment:
                Intent intent = new Intent(this, AttachmentListActivity.class);
                if (inspectId != null) {
                    intent.putExtra("id", inspectId);
                    startActivity(intent);
                }else{
                    Toast.makeText(this, "请先保存信息再上传附件", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.greenObjects:
                Intent intent2 = new Intent(this, UgoListActivity.class);
                if (inspectId != null)
                    intent2.putExtra("id", inspectId);
                intent2.putExtra("activity","inspect");
                startActivity(intent2);
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    private void getInspectObject() {
        Intent intent = getIntent();
        Serializable serializableObject = intent.getSerializableExtra("inspect_object");
        if (serializableObject != null) {
            inspectObject = (Inspect) serializableObject;
            tvInspectInfoCode.setText(inspectObject.getIR_Code());
            droplistInspectInfoType.setText(inspectObject.getIR_Type());
            etInspectInfoDate.setText(inspectObject.getIR_InspectDate());
            etInspectInfoScore.setText(inspectObject.getIR_Score());
            etInspectInfoContent.setText(inspectObject.getIR_Content());
            etInspectInfoOpinion.setText(inspectObject.getIR_InspectOpinion());
            etInspectInfoStaff.setText(inspectObject.getIR_Inspector());
            etInspectInfoLocation.setText(inspectObject.getIR_Location());
            inspectId = inspectObject.getIR_ID();
        }
    }


    private void initDatePicker() {
        Calendar currentCalendar;
        int year, month, day;
        currentCalendar = Calendar.getInstance();
        year = currentCalendar.get(Calendar.YEAR);
        month = currentCalendar.get(Calendar.MONTH);
        day = currentCalendar.get(Calendar.DAY_OF_MONTH);
        dtpckInspectDate = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                etInspectInfoDate.setText(year + "-" + (month + 1) + "-" + dayOfMonth);
            }
        }, year, month, day);
        dtpckInspectDate.getDatePicker().setMaxDate(currentCalendar.getTimeInMillis());
        etInspectInfoDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dtpckInspectDate.show();
            }
        });
        etInspectInfoDate.setText(year + "-" + (month + 1) + "-" + day);
    }

    //上传（提交）表单
    private void upload() {
        btnInspectRegisterSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] errMsg = new String[1];
                if (validateEmpty(Constants.CLICK_UPLOAD_BUTTON)) {
                    outputObject();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final Boolean res;
                            if (tvInspectInfoCode.getText() == "") {
                                res = WebServiceUtils.AddInspectRecord(errMsg, inspectObject);
                            } else {
                                res = WebServiceUtils.UpdateInspectRecord(errMsg, inspectObject);
                            }

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (res) {
                                        Toast.makeText(InspectRegisterActivity.this, "上传成功!", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent();
                                        intent.putExtra("upload_status", true);
                                        setResult(RESULT_OK, intent);
                                        finish();
                                    } else {
                                        Toast.makeText(InspectRegisterActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
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
        inspectObject = new Inspect();
        inspectObject.setIR_Type(droplistInspectInfoType.getText());
        inspectObject.setIR_InspectDate(etInspectInfoDate.getText().toString());
        inspectObject.setIR_Code(tvInspectInfoCode.getText().toString());
        inspectObject.setIR_ID(inspectId);
        inspectObject.setUGO_IDs(getUGOIDs());
        Log.d("ira", "outputObject: "+getUGOIDs());
        if(etInspectInfoOpinion.getText()!=null){
            inspectObject.setIR_InspectOpinion(etInspectInfoOpinion.getText().toString());
        }
        if(etInspectInfoScore.getText()!=null){
            inspectObject.setIR_Score(etInspectInfoScore.getText().toString());
        }
        if(etInspectInfoStaff.getText()!=null){
            inspectObject.setIR_Inspector(etInspectInfoStaff.getText().toString());
        }
        if(etInspectInfoLocation.getText()!=null){
            inspectObject.setIR_Location(etInspectInfoLocation.getText().toString());
        }
        if(etInspectInfoContent.getText()!=null){
            inspectObject.setIR_Content(etInspectInfoContent.getText().toString());
        }
    }


    //验证是否为空
    private boolean validateEmpty(int flag) {
        int emptyStatus = 0;
        //个位数为1代表type为空
        if (droplistInspectInfoType.getText().equals(""))
            emptyStatus += 1;


        //保证必填项不为空
        if (emptyStatus != 0) {
            if (emptyStatus % 10 == 1)
                droplistInspectInfoType.setEmptyWarning();
            else
                droplistInspectInfoType.setCommonDrawable();
            if (emptyStatus >= 10)
                etInspectInfoStaff.setBackground(getResources().getDrawable(R.drawable.bkg_edittext_empty));
            else
                etInspectInfoStaff.setBackground(getResources().getDrawable(R.drawable.bkg_edittext));

            showPrompt(flag);
            return false;
        } else {
            return true;
        }
    }

    //必填项为空错误提示
    private void showPrompt(int flag) {
        if (flag == CLICK_BACK_BUTTON) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
