package com.nju.urbangreen.zhenjiangurbangreen.inspectRecord;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
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


    public DatePickerDialog dtpckInspectDate;
    @BindView(R.id.Toolbar)
    Toolbar toolbar;
    @BindView(R.id.material_search_view)
    MaterialSearchView materialSearchView;
    @BindView(R.id.tv_inspectInfo_ID)
    AppCompatTextView tvInspectID;
    @BindView(R.id.tv_inspectInfo_Code)
    AppCompatTextView tvInspectCode;

    @BindView(R.id.tv_inspectInfo_date)
    AppCompatTextView tvInspectDate;
    @BindView(R.id.et_inspectInfo_inspector)
    TextInputLayout etInspector;
    @BindView(R.id.et_inspectInfo_score)
    TextInputLayout etInspectScore;
    @BindView(R.id.et_inspectInfo_content)
    TextInputLayout etInspectContent;
    @BindView(R.id.et_inspectInfo_opinion)
    TextInputLayout etEtInspectOpinion;
    @BindView(R.id.tv_inspectInfo_LoggerPID)
    AppCompatTextView tvInspectLoggerPID;
    @BindView(R.id.tv_inspectInfo_LogTime)
    AppCompatTextView tvInspectInfoLogTime;
    @BindView(R.id.tv_inspectInfo_LastEditorPID)
    AppCompatTextView tvInspectLastEditorPID;
    @BindView(R.id.btn_inspect_register_submit)
    AppCompatButton btnInspectRegisterSubmit;
    @BindView(R.id.droplist_inspectInfo_type)
    DropdownEditText droplistInspectInfoType;
    private String[] typeArray;
    private ArrayAdapter<String> typeAdapter;
    private String inspectId;

    private Inspect inspectObject;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspect_register);
        ButterKnife.bind(this);
        initView();
        initToolbar();
        initDatePicker();
        getInspectObject();
        upload();

    }

    private void initView() {
        ArrayList<String> dropdownList = new ArrayList<>();
        dropdownList.addAll(Arrays.asList(getResources().getStringArray(R.array.inspectType)));
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
                if (inspectId != null)
                    intent.putExtra("id", inspectId);
                startActivity(intent);
                break;
            case R.id.greenObjects:
                Intent intent2 = new Intent(this, UgoListActivity.class);
                if (inspectId != null)
                    intent2.putExtra("id", inspectId);
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
            tvInspectID.setText(inspectObject.getIR_ID());
            tvInspectCode.setText(inspectObject.getIR_Code());
            etInspector.getEditText().setText(inspectObject.getIR_Inspector());
            etInspectScore.getEditText().setText(inspectObject.getIR_Score());
            etInspectContent.getEditText().setText(inspectObject.getIR_Content());
            etEtInspectOpinion.getEditText().setText(inspectObject.getIR_InspectOpinion());
            inspectId = inspectObject.getIR_ID();
        }
    }


    private void initDatePicker() {
        Calendar currentCalendar;
        int year, month, day;
        currentCalendar = Calendar.getInstance();
        year = currentCalendar.get(Calendar.YEAR);
        month = currentCalendar.get(Calendar.MONTH) + 1;
        day = currentCalendar.get(Calendar.DAY_OF_MONTH);
        dtpckInspectDate = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                tvInspectDate.setText(year + "-" + (month + 1) + "-" + dayOfMonth);
            }
        }, year, month, day);
        tvInspectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dtpckInspectDate.show();
            }
        });
        tvInspectDate.setText(year + "-" + (month + 1) + "-" + day);
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
                            if (tvInspectID.getText() == "") {
                                res = WebServiceUtils.AddInspectRecord(errMsg, inspectObject);
                            } else {
                                res = WebServiceUtils.UpdateInspetRecord(errMsg, inspectObject);
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
        inspectObject.setIR_InspectDate(tvInspectDate.getText().toString());
        inspectObject.setIR_ID(tvInspectID.getText().toString());
        if (etInspectContent.getEditText().getText() != null) {
            inspectObject.setIR_Content(etInspectContent.getEditText().getText().toString());
        }
        inspectObject.setUGO_IDs(getUGOIDs());
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
