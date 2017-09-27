package com.nju.urbangreen.zhenjiangurbangreen.inspectRecord;

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
import android.widget.AdapterView;
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

public class InspectListActivity extends BaseActivity {

    @BindView(R.id.floatingbtn_add_inspect)
    FloatingActionButton fbtnAddInspect;//悬浮按钮
    @BindView(R.id.Toolbar)
    Toolbar toolbar;
    @BindView(R.id.material_search_view)
    MaterialSearchView searchView;
    @BindView(R.id.spinner1)
    Spinner spinner1;
    @BindView(R.id.spinner2)
    Spinner spinner2;
    @BindView(R.id.swipe_target)
    RecyclerView recyclerInspectList;
    @BindView(R.id.swipeToLoadLayout)
    SwipeToLoadLayout swipeToLoadLayout;
    @BindView(R.id.swipe_refresh_header)
    RefreshHeaderView swipeRefreshHeader;
    @BindView(R.id.swipe_load_more_footer)
    LoadMoreFooterView swipeLoadMoreFooter;

    public static final int GET_REGISTER_RESULT = 1;
    private InspectListAdapter adapter;
    private List<Inspect> inspectList = new ArrayList<>();
    private int page = 2;
    final Map<String, Object> multiQuery = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspect_list);
        ButterKnife.bind(this);
        initViews();
        initRecyclerView();
        getInspectList(multiQuery);
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
                    getInspectList(multiQuery);
                }
                break;
            default:
        }

    }

    //初始化控件
    public void initViews() {
        ButterKnife.bind(this);
        toolbar.setTitle("巡查记录列表");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        fbtnAddInspect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InspectListActivity.this, InspectRegisterActivity.class);
                startActivityForResult(intent, GET_REGISTER_RESULT);
            }
        });

        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                String[] languages = getResources().getStringArray(R.array.inspectType);

                if(pos>0){

                    multiQuery.put("type",languages[pos]);
                }
                if(pos==0&&multiQuery.containsKey("type")){
                    multiQuery.remove("type");

                }
                getInspectList(multiQuery);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                String[] languages = getResources().getStringArray(R.array.maintainDate);
                if(pos>0){
                    multiQuery.put("date",languages[pos]);
                }
                if(pos==0&&multiQuery.containsKey("date")){

                    multiQuery.remove("date");
                }
                getInspectList(multiQuery);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });

//        swipeToLoadLayout.setRefreshEnabled(false);
        swipeToLoadLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                getInspectList(multiQuery);
            }
        });

        swipeToLoadLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                getInspectList(multiQuery,page, 8);
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
    private void getInspectList(final Map<String, Object> query) {
        page = 2;
        swipeToLoadLayout.setLoadMoreEnabled(true);

        final ProgressDialog loading = new ProgressDialog(this);
        loading.setMessage("加载数据中，请稍候...");
        loading.show();


        new Thread(new Runnable() {
            @Override
            public void run() {

                String[] errMsg = new String[1];
                query.put("page", 1);
                query.put("limit", 8);
                final List<Inspect> tempList = WebServiceUtils.getInspectRecord(query, errMsg);
                if (tempList != null) {
                    inspectList.clear();
                    inspectList.addAll(tempList);
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(tempList!=null){

                            findViewById(R.id.task_list_emptyview).setVisibility(View.INVISIBLE);
                        }
                        if(tempList==null){

                            inspectList.clear();
                            findViewById(R.id.floatingbtn_add_inspect).setVisibility(View.INVISIBLE);
                            findViewById(R.id.task_list_emptyview).setVisibility(View.VISIBLE);
                        }
                        loading.dismiss();
                        adapter.notifyDataSetChanged();
                        swipeToLoadLayout.setRefreshing(false);
                    }
                });
            }
        }).start();
    }


    private List<Inspect> getInspectList(final Map<String, Object> query, final int page, final int limit) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String[] errMsg = new String[1];
                query.put("page", page);
                query.put("limit", limit);
                final List<Inspect> newInspectList = WebServiceUtils.getInspectRecord(query, errMsg);
                if (newInspectList != null) {
                    inspectList.addAll(newInspectList);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                            swipeToLoadLayout.setLoadingMore(false);
                            if (newInspectList.size() < limit) {
                                swipeToLoadLayout.setLoadMoreEnabled(false);
                                Toast.makeText(InspectListActivity.this, "没有更多了", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            swipeToLoadLayout.setLoadingMore(false);
                            swipeToLoadLayout.setLoadMoreEnabled(false);
                            Toast.makeText(InspectListActivity.this, "没有更多了", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
        return inspectList;
    }

    private void initRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerInspectList.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerInspectList.getContext(),
                linearLayoutManager.getOrientation());
        recyclerInspectList.addItemDecoration(dividerItemDecoration);
        adapter = new InspectListAdapter(inspectList);
        recyclerInspectList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

}
