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
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
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
    FloatingActionButton fbtnAddMaintain;//悬浮按钮
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
    private MaintainListAdapter adapter;
    private List<Maintain> maintainList = new ArrayList<>();
    private int page = 2;

    private String id;

    final Map<String, Object> multiQuery = new HashMap<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintain_list);
        ButterKnife.bind(this);
        initViews();
        initRecyclerView();


        Intent intent = getIntent();
        id = intent.getStringExtra("UGO_Ucode");

        getMaintainList(multiQuery);

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
                    getMaintainList(multiQuery);
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

        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                String[] languages = getResources().getStringArray(R.array.maintainType);

                if(pos>0){

                    multiQuery.put("maintainType",languages[pos]);
                }
                if(pos==0&&multiQuery.containsKey("maintainType")){
                    multiQuery.remove("maintainType");

                }
                getMaintainList(multiQuery);
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
                getMaintainList(multiQuery);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });

        spinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                String[] languages = getResources().getStringArray(R.array.maintainStatus);
                if(pos==1){
                    multiQuery.put("assess",true);
                }else if(pos==2){
                    multiQuery.put("assess",false);
                }
                if(pos==0&&multiQuery.containsKey("assess")){
                    multiQuery.remove("assess");

                }
                getMaintainList(multiQuery);
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
                getMaintainList(multiQuery);
            }
        });

        swipeToLoadLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                getMaintainList(multiQuery,page, 8);
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
    private void getMaintainList(final Map<String, Object> query) {
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

                if(id != ""){
                    query.put("UGO_ID", id);
                }

               final List<Maintain> tempList = WebServiceUtils.getMaintainRecord(query, errMsg);

                if (tempList != null) {
                    maintainList.clear();
                    maintainList.addAll(tempList);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(tempList!=null){

                            findViewById(R.id.task_list_emptyview).setVisibility(View.INVISIBLE);
                        }
                        if(tempList==null){

                            maintainList.clear();
                            findViewById(R.id.floatingbtn_add_maintain).setVisibility(View.INVISIBLE);
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


    private List<Maintain> getMaintainList(final Map<String, Object> query, final int page, final int limit) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String[] errMsg = new String[1];
                query.put("page", page);
                query.put("limit", limit);
                if(id != ""){
                    query.put("UGO_ID", id);
                }
                final List<Maintain> newMaintainList = WebServiceUtils.getMaintainRecord(query, errMsg);
                if (newMaintainList != null) {
                    maintainList.addAll(newMaintainList);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                            swipeToLoadLayout.setLoadingMore(false);
                            if (newMaintainList.size() < limit) {
                                swipeToLoadLayout.setLoadMoreEnabled(false);
                                Toast.makeText(MaintainListActivity.this, "没有更多了", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else{
                    swipeToLoadLayout.setLoadMoreEnabled(false);
                    Toast.makeText(MaintainListActivity.this, "没有更多了", Toast.LENGTH_SHORT).show();
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
        adapter = new MaintainListAdapter(maintainList);
        recyclerMaintainList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }


}
