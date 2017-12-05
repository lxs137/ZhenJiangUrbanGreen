package com.nju.urbangreen.zhenjiangurbangreen.ugo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.goyourfly.multiple.adapter.MultipleAdapter;
import com.goyourfly.multiple.adapter.MultipleSelect;
import com.goyourfly.multiple.adapter.StateChangeListener;
import com.goyourfly.multiple.adapter.viewholder.view.CheckBoxFactory;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.nju.urbangreen.zhenjiangurbangreen.R;
import com.nju.urbangreen.zhenjiangurbangreen.basisClass.BaseActivity;
import com.nju.urbangreen.zhenjiangurbangreen.basisClass.GreenObject;
import com.nju.urbangreen.zhenjiangurbangreen.basisClass.GreenObjectSug;
import com.nju.urbangreen.zhenjiangurbangreen.util.ACache;
import com.nju.urbangreen.zhenjiangurbangreen.util.CacheUtil;
import com.nju.urbangreen.zhenjiangurbangreen.util.ListUtil;
import com.nju.urbangreen.zhenjiangurbangreen.util.PermissionsUtil;
import com.nju.urbangreen.zhenjiangurbangreen.util.WebServiceUtils;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchUgoActivity extends BaseActivity {

    @BindView(R.id.Toolbar)
    Toolbar toolbar;
    @BindView(R.id.material_search_view)
    MaterialSearchView searchView;
    @BindView(R.id.recycler_ugo_search_result)
    RecyclerView recyclerUgoSearchResult;

    private String sugCodes[];
    private String sugAddresses[];
    private ProgressDialog loadingDialog;
    private UgoListAdapter adapter;
    private MultipleAdapter multipleAdapter;
    private List<GreenObject> searchResult = new ArrayList<>();
    private List<GreenObject> selectResult = new ArrayList<>();
    public String flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PermissionsUtil.setPermissions(this);
        setContentView(R.layout.activity_search_ugo);
        ButterKnife.bind(this);

        loadingDialog = new ProgressDialog(this);
        loadingDialog.setMessage("请稍候...");
        initToolbar();
        initSearchView();
        initSuggestionList();
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if(REQUEST_CODE == requestCode && resultCode == 200) {
//            ArrayList<GreenObject> result = (ArrayList<GreenObject>) data.getSerializableExtra("selectedUGOs");
//            searchResult.addAll(result);
//            if(multipleAdapter == null) {
//                initRecyclerView();
//            }
//            adapter.notifyDataSetChanged();
//            multipleAdapter.notifyDataSetChanged();
//        }
//    }

    private void initToolbar() {
        toolbar.setTitle("添加绿化对象");
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


    private void initSearchView() {
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {
                loadingDialog.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String errorMsg[] = new String[1];
                        try {
                            searchResult = WebServiceUtils.searchUGOInfo_2(errorMsg, query, flag);
//                            boolean[] type={true,true,true};
//                            searchResult = WebServiceUtils.searchUGOByCode(query,type,errorMsg);
//                            for(GreenObject o : searchResult){
//                                Log.d("tag",o.UGO_Ucode);
//                            }
//                            Log.d("tag", "searchresultbefore" + searchResult.size() + "");
//                            Log.d("tag", "selectresult" + selectResult.size());
                            ACache mCache = ACache.get(SearchUgoActivity.this);
                            List<GreenObject> selectList = mCache.getAsObjectList("ugo_select");
                            for(GreenObject o : selectList){
                                Log.d("selectlist ",o.UGO_Address);
                            }
                            searchResult = ListUtil.trim(searchResult, selectList);  //去除已经选择的item
                            adapter.notifyDataSetChanged();
                            for(GreenObject o : searchResult){
                                Log.d("selectlist ",o.UGO_Address);
                            }
//                            searchResult.removeAll(selectList); //去除已经选择的item
//                            searchResult.removeAll(selectResult);     //去除已经选择的item
//                            Log.d("tag", "searchresultafter" + searchResult.size() + "");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loadingDialog.dismiss();
                                if (searchResult == null) {
                                    Toast.makeText(SearchUgoActivity.this, "找不到结果(是否没输入完整的Code？)", Toast.LENGTH_SHORT).show();
                                    searchView.clearFocus();
                                } else {
                                    initRecyclerView();
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

            }

            @Override
            public void onSearchViewClosed() {
//                recyclerUgoSearchResult.setVisibility(View.INVISIBLE);
//                searchHintTextview.setVisibility(View.VISIBLE);

            }
        });
    }

    private void initSuggestionList() {
        if (CacheUtil.hasUGOSug()) {
            sugCodes = CacheUtil.getUGOSug("UGO_Code");
//            Log.d("tag", "size=" + sugCodes.length);
            sugAddresses = CacheUtil.getUGOSug("UGO_Address");
        } else {
            loadingDialog.show();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String errorMsg[] = new String[1];
                    List<GreenObjectSug> res = WebServiceUtils.getUGOSug(errorMsg);
                    loadingDialog.dismiss();
                    if (res != null) {
                        CacheUtil.putUGOSug(res);
                        sugCodes = CacheUtil.getUGOSug("UGO_Code");
                        sugAddresses = CacheUtil.getUGOSug("UGO_Address");
                    } else if (errorMsg[0] != null && !errorMsg[0].equals("")) {
                        Looper.prepare();
                        Toast.makeText(SearchUgoActivity.this, errorMsg[0], Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    } else {
                        Looper.prepare();
                        Toast.makeText(SearchUgoActivity.this, "网络连接断开，请稍后再试", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }
                }
            }).start();
        }
    }

    private void initRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerUgoSearchResult.setLayoutManager(linearLayoutManager);
        adapter = new UgoListAdapter(searchResult);

        multipleAdapter = MultipleSelect.with(this)
                .adapter(adapter)
                .decorateFactory(new CheckBoxFactory(R.color.colorPrimary))
                .stateChangeListener(new StateChangeListener() {
                    @Override
                    public void onSelectMode() {
                    }

                    @Override
                    public void onSelect(int i, int i1) {
                    }

                    @Override
                    public void onUnSelect(int i, int i1) {
                    }

                    @Override
                    public void onDone(@NotNull ArrayList<Integer> arrayList) {
                        for (Integer i : arrayList) {
                            selectResult.add(searchResult.get(i));
                        }
                        Intent intent = new Intent();
                        intent.putExtra("selectUgo", (Serializable) selectResult);
                        setResult(RESULT_OK, intent);
                        finish();
                    }

                    @Override
                    public void onDelete(@NotNull ArrayList<Integer> arrayList) {
                    }

                    @Override
                    public void onCancel() {
                    }
                })
                .build();

        //列表分隔线
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerUgoSearchResult.getContext(),
                linearLayoutManager.getOrientation());
        recyclerUgoSearchResult.addItemDecoration(dividerItemDecoration);

        recyclerUgoSearchResult.setAdapter(multipleAdapter);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar_ugo_search, menu);
        MenuItem item = menu.findItem(R.id.menu_toolbar_item_search);
        MenuItem spinnerItem = menu.findItem(R.id.spinner);
        Spinner spinner = (Spinner) MenuItemCompat.getActionView(spinnerItem);

        String[] data = {"Code", "地址"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.my_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    searchView.setSuggestions(sugCodes);
                    flag = "code";
                } else {
                    searchView.setSuggestions(sugAddresses);
                    flag = "address";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        searchView.setMenuItem(item);
        return super.onCreateOptionsMenu(menu);
    }

}
