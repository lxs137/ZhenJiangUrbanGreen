package com.nju.urbangreen.zhenjiangurbangreen.events;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

public class EventListActivity extends BaseActivity {

    @BindView(R.id.floatingbtn_add_event)
    FloatingActionButton fbtnAddEvent;//悬浮按钮
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
    RecyclerView recyclerEventList;
    @BindView(R.id.swipeToLoadLayout)
    SwipeToLoadLayout swipeToLoadLayout;
    @BindView(R.id.swipe_refresh_header)
    RefreshHeaderView swipeRefreshHeader;
    @BindView(R.id.swipe_load_more_footer)
    LoadMoreFooterView swipeLoadMoreFooter;

    public static final int GET_REGISTER_RESULT = 1;
    private EventListAdapter adapter;
    private List<OneEvent> eventList = new ArrayList<>();
    private List<OneEvent> eventSugList = new ArrayList<>();
    private int page = 2;

    private String id;

    final Map<String, Object> multiQuery = new HashMap<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);
        ButterKnife.bind(this);
        initViews();
        initRecyclerView();


        Intent intent = getIntent();
        id = intent.getStringExtra("UGO_Ucode");

        getEvent(multiQuery);

    }

    @Override
    protected void onStart() {
        super.onStart();
        CacheUtil.removeRelatedUgos();
        getEvent(multiQuery);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case GET_REGISTER_RESULT:
                if(resultCode == RESULT_OK){
                    getEvent(multiQuery);
                }
                break;
            default:
        }

    }

    //初始化控件
    public void initViews() {
        ButterKnife.bind(this);
        toolbar.setTitle("事件记录列表");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        fbtnAddEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(fbtnAddEvent);
            }
        });

        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                String[] languages = getResources().getStringArray(R.array.eventType);

                if(pos>0){

                    multiQuery.put("eventType",languages[pos]);
                }
                if(pos==0&&multiQuery.containsKey("eventType")){
                    multiQuery.remove("eventType");

                }
                getEvent(multiQuery);
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
                String[] languages = getResources().getStringArray(R.array.eventDate);
                if(pos>0){
                    multiQuery.put("date",languages[pos]);
                }
                if(pos==0&&multiQuery.containsKey("date")){

                    multiQuery.remove("date");
                }
                getEvent(multiQuery);
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
                String[] languages = getResources().getStringArray(R.array.eventStatus);
                if(pos==1){
                    multiQuery.put("is_conclude",true);
                }else if(pos==2){
                    multiQuery.put("is_conclude",false);
                }
                if(pos==0&&multiQuery.containsKey("is_conclude")){
                    multiQuery.remove("is_conclude");

                }
                getEvent(multiQuery);
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
                getEvent(multiQuery);
            }
        });

        swipeToLoadLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                getEvent(multiQuery,page, 8);
                page++;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //添加搜索
        getMenuInflater().inflate(R.menu.menu_toolbar_event_search, menu);
        MenuItem item = menu.findItem(R.id.menu_toolbar_item_search);

        //添加spinner
        final MenuItem spinnerItem = menu.findItem(R.id.event_spinner);
        Spinner event_spinner = (Spinner) MenuItemCompat.getActionView(spinnerItem);
        String[] data = {"事件", "活动"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.my_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        event_spinner.setAdapter(adapter);
        //spinner点击事件
        event_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    multiQuery.put("is_activity", false);
                } else {
                    multiQuery.put("is_activity", true);
                }
                getEvent(multiQuery);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        searchView.setMenuItem(item);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {
                //从服务端获取object
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String[] errMsg = new String[1];
                        final OneEvent event = WebServiceUtils.searchEventRecord(errMsg,query);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(event == null){
                                    return ;
                                }
                                if (event.isUGE_EventOrActivity()) {
                                    Intent intent = new Intent(EventListActivity.this, ActivityRegisterActivity.class);
                                    intent.putExtra("event_object", event);
                                    startActivity(intent);
                                }else {
                                    Intent intent2 = new Intent(EventListActivity.this, EventRegisterActivity.class);
                                    intent2.putExtra("event_object", event);
                                    startActivity(intent2);
                                }
                            }
                        });
                    }

                }).start();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                getEventSug();
            }

            @Override
            public void onSearchViewClosed() {

            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    //获得列表第一页数据
    private void getEvent(final Map<String, Object> query) {
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

                final List<OneEvent> tempList = WebServiceUtils.getEvent(query, errMsg);

                if (tempList != null) {
                    eventList.clear();
                    eventList.addAll(tempList);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(tempList!=null){

                            findViewById(R.id.task_list_emptyview).setVisibility(View.INVISIBLE);
                            findViewById(R.id.floatingbtn_add_event).setVisibility(View.VISIBLE);
                        }
                        if(tempList==null){

                            eventList.clear();
                            findViewById(R.id.floatingbtn_add_event).setVisibility(View.INVISIBLE);
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


    private List<OneEvent> getEvent(final Map<String, Object> query, final int page, final int limit) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String[] errMsg = new String[1];
                query.put("page", page);
                query.put("limit", limit);
                if(id != ""){
                    query.put("UGO_ID", id);
                }
                final List<OneEvent> newEventList = WebServiceUtils.getEvent(query, errMsg);
                if (newEventList != null) {
                    eventList.addAll(newEventList);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                            swipeToLoadLayout.setLoadingMore(false);
                            if (newEventList.size() < limit) {
                                swipeToLoadLayout.setLoadMoreEnabled(false);
                                Toast.makeText(EventListActivity.this, "没有更多了", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else{
                    swipeToLoadLayout.setLoadMoreEnabled(false);
                    Toast.makeText(EventListActivity.this, "没有更多了", Toast.LENGTH_SHORT).show();
                }
            }
        }).start();
        return eventList;
    }


    private void initRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerEventList.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerEventList.getContext(),
                linearLayoutManager.getOrientation());
        recyclerEventList.addItemDecoration(dividerItemDecoration);
        adapter = new EventListAdapter(eventList);
        recyclerEventList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void showPopupMenu(View view) {
        // View当前PopupMenu显示的相对View的位置
        PopupMenu popupMenu = new PopupMenu(this, view);
        // menu布局
        popupMenu.getMenuInflater().inflate(R.menu.main, popupMenu.getMenu());
        // menu的item点击事件
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                String title = item.getTitle().toString();
                switch (item.getItemId())
                {
                    case R.id.action_event:
                        Intent i_event = new Intent(EventListActivity.this , EventRegisterActivity.class);
                        startActivity(i_event);
                        break;
                    case R.id.action_activity:
                        Intent i_activity = new Intent(EventListActivity.this , ActivityRegisterActivity.class);
                        startActivity(i_activity);
                        break;

                }
                return false;
            }
        });
        // PopupMenu关闭事件
        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
            }
        });

        popupMenu.show();
    }

    private void getEventSug() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String[] errMsg = new String[1];
                eventSugList = WebServiceUtils.getEventSug(errMsg);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setEventSug();
                    }
                });
            }
        }).start();
    }

    private void setEventSug(){
        String [] eventSuggestions = new String[eventSugList.size()];
        for(int i =0; i < eventSugList.size(); i++){
            eventSuggestions[i] = eventSugList.get(i).getUGE_Code();
        }
        searchView.setSuggestions(eventSuggestions);
    }
}
