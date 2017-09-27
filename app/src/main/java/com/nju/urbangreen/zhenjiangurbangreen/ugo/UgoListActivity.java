package com.nju.urbangreen.zhenjiangurbangreen.ugo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.goyourfly.multiple.adapter.MultipleAdapter;
import com.goyourfly.multiple.adapter.MultipleSelect;
import com.goyourfly.multiple.adapter.menu.SimpleDeleteMenuBar;
import com.goyourfly.multiple.adapter.viewholder.view.CheckBoxFactory;
import com.nju.urbangreen.zhenjiangurbangreen.R;
import com.nju.urbangreen.zhenjiangurbangreen.basisClass.BaseActivity;
import com.nju.urbangreen.zhenjiangurbangreen.basisClass.GreenObject;
import com.nju.urbangreen.zhenjiangurbangreen.util.ACache;
import com.nju.urbangreen.zhenjiangurbangreen.util.CacheUtil;
import com.nju.urbangreen.zhenjiangurbangreen.util.WebServiceUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UgoListActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recycler_ugo_list)
    RecyclerView recyclerUgoList;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefresh;


    private List<GreenObject> ugObjectList = new ArrayList<>();
    private UgoListAdapter adapter;
    private ProgressDialog progressDialog;
    private MultipleAdapter multipleAdapter;
    private String maintainId;
    ACache mCache;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ugo_list);
        ButterKnife.bind(this);

        initToolbar();
        initRecyclerView();

        Intent intent = getIntent();
        maintainId = intent.getStringExtra("id");
//        mCache = ACache.get(this);

        if (CacheUtil.hasRelatedUgos() && maintainId !=null) {
            getUgosFromCache();
        } else if (maintainId != null) {
            getUgosFromWeb();
        }

    }

    private void initToolbar() {
        toolbar.setTitle("绿化对象列表");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CacheUtil.putRelatedUgos(ugObjectList);
//                mCache.put("ugo_select", ugObjectList.toArray());
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
//                    String returnData = data.getStringExtra("selectUgos");
                    List<GreenObject> tempList = (List<GreenObject>) data.getSerializableExtra("selectUgo");
                    ugObjectList.addAll(tempList);
                    multipleAdapter.notifyDataSetChanged();
                }
                break;
            default:
        }
    }

    //刷新养护对象数据
    private void refreshUgos() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        }).start();
    }

    //从服务端获取数据
    private void getUgosFromWeb() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在加载列表");
        progressDialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                String[] errMsg = new String[1];
                List<GreenObject> tempList = WebServiceUtils.GetMaintainRecordUGO(maintainId, errMsg);
                if (tempList != null) {
                    ugObjectList.addAll(tempList);
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        multipleAdapter.notifyDataSetChanged();
                        progressDialog.dismiss();
                    }
                });
            }
        }).start();

    }

    //从cache获取数据
    private void getUgosFromCache() {
        List<GreenObject> tempList = CacheUtil.getRelatedUgos();
        if (tempList != null) {
            ugObjectList.addAll(tempList);
            multipleAdapter.notifyDataSetChanged();
        }
    }

    private void initRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerUgoList.setLayoutManager(linearLayoutManager);
        adapter = new UgoListAdapter(ugObjectList);
        multipleAdapter = MultipleSelect.with(this)
                .adapter(adapter)
                .decorateFactory(new CheckBoxFactory(R.color.colorPrimary))
                .linkList(ugObjectList)
                .customMenu(new SimpleDeleteMenuBar(this, R.color.colorPrimary, Gravity.BOTTOM))
                .build();
        recyclerUgoList.setAdapter(multipleAdapter);

        //列表项分隔线
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerUgoList.getContext(),
                linearLayoutManager.getOrientation());
        recyclerUgoList.addItemDecoration(dividerItemDecoration);

        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshUgos();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar_ugo_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_ugo:
                Intent intent = new Intent(UgoListActivity.this, SearchUgoActivity.class);
                startActivityForResult(intent, 1);
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean hasCache() {
        return mCache.getAsObjectList("ugo_select") != null;
    }
}
