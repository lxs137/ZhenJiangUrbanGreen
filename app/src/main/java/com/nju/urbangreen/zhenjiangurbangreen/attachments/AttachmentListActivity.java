package com.nju.urbangreen.zhenjiangurbangreen.attachments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.nju.urbangreen.zhenjiangurbangreen.R;
import com.nju.urbangreen.zhenjiangurbangreen.basisClass.BaseActivity;
import com.nju.urbangreen.zhenjiangurbangreen.util.FileUtil;

import java.io.File;

public class AttachmentListActivity extends BaseActivity {

    @BindView(R.id.Toolbar_simple)
    public Toolbar toolbar;

    @BindView(R.id.rcv_attachment_list)
    public RecyclerView rcvAttachmentRecords;
    private AttachmentAdapter adapter;

    @BindView(R.id.floatingbtn_add_attach)
    public FloatingActionButton floatingbtnAddAttach;

    @BindView(R.id.refresh_attachment_list)
    public SwipeRefreshLayout refreshLayout;

//    private String parentID = "0420b1b8-5b20-4213-9b5f-586dbca94ef7";
    private String parentID ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attachment_list);
        ButterKnife.bind(this);

        initRefreshButton();
        initToolbar();
        setUploadButton();

        Intent intent = getIntent();
        parentID = intent.getStringExtra("id");

        rcvAttachmentRecords.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AttachmentAdapter(AttachmentListActivity.this, parentID);
        rcvAttachmentRecords.setAdapter(adapter);
        // Init AttachList
        final ProgressDialog loading = new ProgressDialog(AttachmentListActivity.this);
        loading.setMessage("加载数据中，请稍候...");
        loading.show();
        adapter.initDataFromWeb(new AttachmentAdapter.Callback() {
            @Override
            public void refreshDone() {
                AttachmentListActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loading.dismiss();
                        adapter.notifyDataSetChanged();
                    }
                });
            }
            @Override
            public void refreshError(final String msg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loading.dismiss();
                        adapter.notifyDataSetChanged();
                        Toast.makeText(AttachmentListActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        adapter.saveAttachToLocal();
    }

    private void initRefreshButton() {
        refreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent, R.color.green_land_border);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.refreshItems(new AttachmentAdapter.Callback() {
                    @Override
                    public void refreshDone() {
                        AttachmentListActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
                                refreshLayout.setRefreshing(false);
                            }
                        });
                    }
                    @Override
                    public void refreshError(final String msg) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
                                refreshLayout.setRefreshing(false);
                                Toast.makeText(AttachmentListActivity.this, msg, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        });
    }


    private void setUploadButton() {
        floatingbtnAddAttach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //添加选择文件的操作
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                try {
                    startActivityForResult(Intent.createChooser(intent,"请选择待上传的文件"),1);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(AttachmentListActivity.this, "请安装文件管理APP", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //选择完文件后的回调函数
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == 1) {
            Uri uri = data.getData();
            String path = FileUtil.getPath(uri);
            if(path != null) {
                File file = new File(path);
                adapter.addItemAndRefreshUI(new AttachmentRecord(file, parentID));
            } else {
                Toast.makeText(AttachmentListActivity.this, "文件损坏，请重新添加", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initToolbar() {
        toolbar.setTitle("附件列表");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
