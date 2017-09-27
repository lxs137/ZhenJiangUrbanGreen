package com.nju.urbangreen.zhenjiangurbangreen.basisClass;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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
import com.nju.urbangreen.zhenjiangurbangreen.events.EventRegisterActivity;
import com.nju.urbangreen.zhenjiangurbangreen.events.OneEvent;
import com.nju.urbangreen.zhenjiangurbangreen.inspectRecord.Inspect;
import com.nju.urbangreen.zhenjiangurbangreen.maintainRecord.Maintain;
import com.nju.urbangreen.zhenjiangurbangreen.maintainRecord.MaintainListActivity;
import com.nju.urbangreen.zhenjiangurbangreen.maintainRecord.MaintainListAdapter;
import com.nju.urbangreen.zhenjiangurbangreen.maintainRecord.MaintainRegisterActivity;
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

public class BaseListActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    @BindView(R.id.floatingbtn_add)
    FloatingActionButton fbtnAdd;//悬浮按钮
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
    private BaseListAdapter adapter;
    private List<Maintain> maintainList = new ArrayList<>();
    private List<Inspect> inspectList = new ArrayList<>();
    private int page = 2;
    private String type;
    private RecordType record;
    final Map<String, Object> multiQuery = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_list);
        ButterKnife.bind(this);
        initListType();
        initViews();
        initRecyclerView();
    }

    private void initListType() {
        type = getIntent().getStringExtra("type");
        Map<String,Integer> spinners  = new HashMap<>();
        switch (type){
            case "maintain":
                spinners.put("maintainType",R.array.maintainType);
                spinners.put("date",R.array.maintainDate);
                spinners.put("assess",R.array.maintainStatus);
                record = new RecordType("maintain","管养记录列表",MaintainRegisterActivity.class,spinners);
                break;
            case "event":
                spinners.put("eventType",R.array.eventType);
                spinners.put("date",R.array.maintainDate);
                spinners.put("assess",R.array.maintainStatus);
                record = new RecordType("event","事件记录列表",EventRegisterActivity.class,spinners);
                break;
            case "inspect":
                spinners.put("inspectType",R.array.inspectType);
                spinners.put("date",R.array.maintainDate);
                record = new RecordType("inspect","巡查记录列表",MaintainRegisterActivity.class,spinners);
                break;
            default:
        }
    }

    //初始化控件
    public void initViews() {
        ButterKnife.bind(this);

        toolbar.setTitle(record.getTitle());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        fbtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BaseListActivity.this, record.getNextActivity());
                startActivityForResult(intent, GET_REGISTER_RESULT);
            }
        });

//        swipeToLoadLayout.setRefreshEnabled(false);
//        swipeToLoadLayout.setOnRefreshListener(new OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                getMaintainList(multiQuery);
//            }
//        });

        swipeToLoadLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                getMaintainList(multiQuery,page, 8);
                page++;
            }
        });
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
//                    getMaintainList(multiQuery);
                }
                break;
            default:
        }

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar_maintain_search, menu);
        MenuItem item = menu.findItem(R.id.menu_toolbar_item_search);
        searchView.setMenuItem(item);
        return super.onCreateOptionsMenu(menu);
    }

    private List<Maintain> getMaintainList(final Map<String, Object> query, final int page, final int limit) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String[] errMsg = new String[1];
                query.put("page", page);
                query.put("limit", limit);
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
                                Toast.makeText(BaseListActivity.this, "加载完毕", Toast.LENGTH_SHORT).show();
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
//        adapter = new MaintainListAdapter(maintainList);
        recyclerMaintainList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
