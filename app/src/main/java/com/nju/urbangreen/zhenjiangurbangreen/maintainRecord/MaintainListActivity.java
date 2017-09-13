package com.nju.urbangreen.zhenjiangurbangreen.maintainRecord;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Spinner;
import android.widget.Toast;

import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.nju.urbangreen.zhenjiangurbangreen.R;
import com.nju.urbangreen.zhenjiangurbangreen.basisClass.BaseActivity;
import com.nju.urbangreen.zhenjiangurbangreen.util.CacheUtil;
import com.nju.urbangreen.zhenjiangurbangreen.util.WebServiceUtils;
import com.nju.urbangreen.zhenjiangurbangreen.widget.LoadMoreFooterView;
import com.nju.urbangreen.zhenjiangurbangreen.widget.RefreshHeaderView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MaintainListActivity extends BaseActivity {

    @BindView(R.id.floatingbtn_add_maintain)
    public FloatingActionButton fbtnAddMaintain;//悬浮按钮
    @BindView(R.id.Toolbar)
    Toolbar toolbar;
    @BindView(R.id.material_search_view)
    MaterialSearchView searchView;
    @BindView(R.id.spinner1)
    Spinner spinner1;
    @BindView(R.id.spinner2)
    Spinner spinner2;
    @BindView(R.id.spinner3)
    Spinner spinner3;
    @BindView(R.id.swipe_target)
    RecyclerView recyclerMaintainList;
    @BindView(R.id.swipeToLoadLayout)
    SwipeToLoadLayout swipeToLoadLayout;
    @BindView(R.id.swipe_refresh_header)
    RefreshHeaderView swipeRefreshHeader;
    @BindView(R.id.swipe_load_more_footer)
    LoadMoreFooterView swipeLoadMoreFooter;

    public static final int GET_REGISTER_RESULT = 1;
    private MaintainListAdapter2 adapter2;
    private List<Maintain> maintainList = new ArrayList<>();
    private int page = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintain_list);
        ButterKnife.bind(this);
        initViews();
        initRecyclerView();
        getMaintainList();
    }

    @Override
    protected void onStart() {
        super.onStart();
        CacheUtil.removeRelatedUgos();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case GET_REGISTER_RESULT:
                if(resultCode == RESULT_OK){
                    getMaintainList();
                }
                break;
            default:
        }

    }

    //初始化控件
    public void initViews() {
        ButterKnife.bind(this);
        toolbar.setTitle("管养记录列表");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        fbtnAddMaintain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MaintainListActivity.this, MaintainRegisterActivity.class);
                startActivityForResult(intent, GET_REGISTER_RESULT);
            }
        });

//        swipeToLoadLayout.setRefreshEnabled(false);
        swipeToLoadLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                getMaintainList();
            }
        });

        swipeToLoadLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                getMaintainList(page, 8);
                page++;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar_maintain_search, menu);
        MenuItem item = menu.findItem(R.id.menu_toolbar_item_search);
        searchView.setMenuItem(item);
        return super.onCreateOptionsMenu(menu);
    }

    //获得列表第一页数据
    private void getMaintainList() {
        final ProgressDialog loading = new ProgressDialog(this);
        loading.setMessage("加载数据中，请稍候...");
        loading.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                Map<String, Object> query = new HashMap<>();
                String[] errMsg = new String[1];
                query.put("page", 1);
                query.put("limit", 8);
                List<Maintain> tempList = WebServiceUtils.getMaintainRecord(query, errMsg);
                if (tempList != null) {
                    maintainList.clear();
                    maintainList.addAll(tempList);
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        loading.dismiss();
                        adapter2.notifyDataSetChanged();
                        swipeToLoadLayout.setRefreshing(false);
                    }
                });
            }
        }).start();
    }


    private List<Maintain> getMaintainList(final int page, final int limit) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Map<String, Object> query = new HashMap<>();
                String[] errMsg = new String[1];
                query.put("page", page);
                query.put("limit", limit);
                final List<Maintain> newMaintainList = WebServiceUtils.getMaintainRecord(query, errMsg);
                if (newMaintainList != null) {
                    maintainList.addAll(newMaintainList);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter2.notifyDataSetChanged();
                            swipeToLoadLayout.setLoadingMore(false);
                            if (newMaintainList.size() < limit) {
                                swipeToLoadLayout.setLoadMoreEnabled(false);
                                Toast.makeText(MaintainListActivity.this, "加载完毕", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        }).start();
        return maintainList;
    }

    private void initRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerMaintainList.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerMaintainList.getContext(),
                linearLayoutManager.getOrientation());
        recyclerMaintainList.addItemDecoration(dividerItemDecoration);
        adapter2 = new MaintainListAdapter2(maintainList);
        recyclerMaintainList.setAdapter(adapter2);
        adapter2.notifyDataSetChanged();
    }

}
