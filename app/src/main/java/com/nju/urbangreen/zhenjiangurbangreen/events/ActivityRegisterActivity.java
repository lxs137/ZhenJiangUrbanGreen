package com.nju.urbangreen.zhenjiangurbangreen.events;

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

public class ActivityRegisterActivity extends BaseRegisterActivity {
    @BindView(R.id.ly_activity_register_table)
    TableLayout lyEventInfoTable;
    @BindView(R.id.Toolbar)
    Toolbar toolbar;
    @BindView(R.id.edit_activity_register_code)
    public TextView tvCode;
    @BindView(R.id.edit_activity_register_name)
    public EditText etName;
    @BindView(R.id.droplist_activity_register_type)
    public DropdownEditText dropdownEventType;//事件类型选择的可编辑下拉列表，（已封装，可复用）
    @BindView(R.id.edit_activity_register_location)
    public EditText etLocation;
    @BindView(R.id.edit_activity_register_time)
    public EditText etDateSelect;//日期选择编辑框
    @BindView(R.id.edit_activity_register_end_time)
    public EditText etEndDateSelect;//日期选择编辑框
    @BindView(R.id.edit_activity_register_relevant_person)
    public EditText etRelevantPerson;
    @BindView(R.id.edit_activity_register_relevant_contact)
    public EditText etRelevantContact;
    @BindView(R.id.edit_activity_register_relevant_company)
    public EditText etRelevantCompany;
    @BindView(R.id.edit_activity_register_relevant_address)
    public EditText etRelevantAddress;
    @BindView(R.id.edit_activity_register_description)
    public EditText etDescription;
    @BindView(R.id.btn_activity_register_submit)
    AppCompatButton btnEventRegisterSubmit;



    public DatePickerDialog dtpckEventDate;
    public DatePickerDialog dtpckEventDate2;
    private OneEvent eventObject;
    private int updateState;

    private String ugoIds;
    private String eventId;


    private static final String TAG = "EventRegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_activity_register);
        ButterKnife.bind(this);
        initToolbar();
        ArrayList<String> dropdownList = new ArrayList<>();
        dropdownList.addAll(Arrays.asList(getResources().getStringArray(R.array.eventTypeDropList)));
        dropdownEventType.setDropdownList(dropdownList);
        initDatePicker();
        getEventObject();
        upload();

    }

    private void getEventObject() {
        Intent intent = getIntent();
        Serializable serializableObject = intent.getSerializableExtra("event_object");
        if (serializableObject != null) {
            eventObject = (OneEvent) serializableObject;
            tvCode.setText(eventObject.getUGE_Code());
            etName.setText(eventObject.getUGE_Name());
            dropdownEventType.setText(eventObject.getUGE_Type());
            etLocation.setText(eventObject.getUGE_Location());
            etDateSelect.setText(eventObject.getUGE_Time());
            etEndDateSelect.setText(eventObject.getUGE_Endtime());
            etRelevantPerson.setText(eventObject.getUGE_RelevantPerson());

            etRelevantContact.setText(eventObject.getUGE_RelevantContact());
            etRelevantCompany.setText(eventObject.getUGE_RelevantCompany());
            etRelevantAddress.setText(eventObject.getUGE_RelevantAddress());
            etDescription.setText(eventObject.getUGE_Description());


            eventId = eventObject.getUGE_ID();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.attachment:
                Intent intent = new Intent(ActivityRegisterActivity.this, AttachmentListActivity.class);
                if (eventId != null) {
                    intent.putExtra("id", eventId);
                    startActivity(intent);
                }else{
                    Toast.makeText(this, "请先保存信息再上传附件", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.greenObjects:
                Intent intent2 = new Intent(ActivityRegisterActivity.this, UgoListActivity.class);
                if (eventId != null)
                    intent2.putExtra("id", eventId);
                intent2.putExtra("activity","event");
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
        toolbar.setTitle("活动登记");
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
        dtpckEventDate = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                etDateSelect.setText(year + "-" + (month + 1) + "-" + dayOfMonth);
            }
        }, year, month, day);
        dtpckEventDate.getDatePicker().setMaxDate(currentCalendar.getTimeInMillis());
        etDateSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dtpckEventDate.show();
            }
        });
        etDateSelect.setText(year + "-" + (month + 1) + "-" + day);

        dtpckEventDate2 = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                etEndDateSelect.setText(year + "-" + (month + 1) + "-" + dayOfMonth);
            }
        }, year, month, day);
        etEndDateSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dtpckEventDate2.show();
            }
        });
        etEndDateSelect.setText(year + "-" + (month + 1) + "-" + day);
    }

    //上传（提交）表单
    private void upload() {
        btnEventRegisterSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] errMsg = new String[1];
                if (validateEmpty(Constants.CLICK_UPLOAD_BUTTON)==true) {
                    outputObject();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final Boolean res;
                            if (tvCode.getText().toString()== "") {
                               // eventObject.setUGE_EventOrActivity(false);//false是事件，true是活动
                                res = WebServiceUtils.AddActivity(errMsg, eventObject);
                            } else {
                                res = WebServiceUtils.UpdateActivity(errMsg, eventObject);
                            }

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (res) {
                                        Toast.makeText(ActivityRegisterActivity.this, "上传成功!", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent();
                                        intent.putExtra("upload_status",true);
                                        setResult(RESULT_OK,intent);
                                        finish();
                                    } else {
                                        Toast.makeText(ActivityRegisterActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
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
        eventObject = new OneEvent();
        eventObject.setUGE_Name(etName.getText().toString());
        eventObject.setUGE_Type(dropdownEventType.getText().toString());
        eventObject.setUGE_Time(etDateSelect.getText().toString());

        eventObject.setUGE_Location(etLocation.getText().toString());
        eventObject.setUGE_ID(eventId);


        if (tvCode.getText() != null) {
            eventObject.setUGE_Code(tvCode.getText().toString());
        }
        if (etRelevantPerson.getText() != null) {
            eventObject.setUGE_RelevantPerson((etRelevantPerson.getText()).toString());
        }
        if (etEndDateSelect.getText() != null) {
            eventObject.setUGE_Endtime((etEndDateSelect.getText()).toString());
        }
        if (etRelevantContact.getText() != null) {
            eventObject.setUGE_RelevantContact((etRelevantContact.getText()).toString());
        }
        if (etRelevantCompany.getText() != null) {
            eventObject.setUGE_RelevantCompany((etRelevantCompany.getText()).toString());
        }
        if (etRelevantAddress.getText() != null) {
            eventObject.setUGE_RelevantAddress((etRelevantAddress.getText()).toString());
        }
        if (etDescription.getText() != null) {
            eventObject.setUGE_Description((etDescription.getText()).toString());
        }
        eventObject.setUGO_IDs(getUGOIDs());
    }


    //验证是否为空
    private boolean validateEmpty(int flag) {
        int emptyStatus = 0;
        //个位数为1代表type为空，十位数为1代表staff为空
        if (dropdownEventType.getText().equals(""))
        {   dropdownEventType.setEmptyWarning();
            emptyStatus=1;
        }
        else
        { dropdownEventType.setCommonDrawable();}


        if (etName.getText().toString().equals(""))
        {
            etName.setBackground(getResources().getDrawable(R.drawable.bkg_edittext_empty));
            emptyStatus=1;
        }
        else
        { etName.setBackground(getResources().getDrawable(R.drawable.bkg_edittext));}
        //Boolean x=(etName.getText().toString().equals(""))?true:false;
        if (etLocation.getText().toString().equals(""))
        {
            etLocation.setBackground(getResources().getDrawable(R.drawable.bkg_edittext_empty));
            emptyStatus=1;
        }
        else
        { etLocation.setBackground(getResources().getDrawable(R.drawable.bkg_edittext));}


        if (etDateSelect.getText().toString().equals(""))
        {
            etDateSelect.setBackground(getResources().getDrawable(R.drawable.bkg_edittext_empty));
            emptyStatus=1;
        }
        else
        { etDateSelect.setBackground(getResources().getDrawable(R.drawable.bkg_edittext));}


        //保证必填项不为空
        if (emptyStatus != 0) {
            showPrompt(flag);
            emptyStatus=0;
            return false;
        } else {
            return true;
        }
    }

    //必填项为空错误提示
    private void showPrompt(int flag) {
        if (flag == Constants.CLICK_BACK_BUTTON) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ActivityRegisterActivity.this);
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
