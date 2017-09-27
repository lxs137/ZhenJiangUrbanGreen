package com.nju.urbangreen.zhenjiangurbangreen.message;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.nju.urbangreen.zhenjiangurbangreen.R;
import com.nju.urbangreen.zhenjiangurbangreen.basisClass.BaseActivity;
import com.nju.urbangreen.zhenjiangurbangreen.util.WebServiceUtils;
import com.nju.urbangreen.zhenjiangurbangreen.widget.TitleBarLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Kyle on 2017/8/29.
 */

public class MessageListActivity extends BaseActivity {
    @BindView(R.id.Toolbar_simple)
    public Toolbar toolbar;
    public TitleBarLayout titleBarLayout;//标题栏
    @BindView(R.id.list)
    ListView listView;

    private List<Message> messageList = new ArrayList<>();
    AlertDialog viewMessageDialog;
    AlertDialog deleteDialog;
    MessageAdapter adapter;
    Spinner spinner;
    boolean readflag;
    private SwipeRefreshLayout swipeRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_main_activity);
        ButterKnife.bind(this);
        // setTitleBarLayout();
        initToolbar();
        spinner = (Spinner) findViewById(R.id.message_spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {

                TextView tv = (TextView) view;
                tv.setGravity(Gravity.CENTER_HORIZONTAL);
                String[] languages = getResources().getStringArray(R.array.messageTypeDropList);
                if (pos == 0) {
                    readflag = true;
                    getMessageList(readflag);
                } else {
                    readflag = false;
                    getMessageList(readflag);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });


    }

    public void setTitleBarLayout() {
        //初始化TitleBarLayout
        titleBarLayout.setTitleText("xiaoxi");
        titleBarLayout.setBtnBackClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //右侧的自定义按钮此时为搜索按钮，点击是会显示出TitleSearchView
        titleBarLayout.setBtnSelfDefBkg(R.drawable.ic_btn_self_def_search);
        titleBarLayout.setBtnSelfDefClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //显示出TitleSearchView
                titleBarLayout.setTsvSearchAvailable();
            }
        });
    }

    private void initToolbar() {
        ButterKnife.bind(this);
        toolbar.setTitle("消息");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    public void getMessageList(final boolean readflag) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String[] errMsg = new String[1];
                messageList = WebServiceUtils.getAllMessages(errMsg, "xk", readflag);
                //messageList = WebServiceUtils.getAllMessages(errMsg,SPUtils.getString("username", ""),true);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (messageList == null) {
                            initEmptyListView();
                        } else {
                            initListView(readflag);
                        }
                    }
                });
            }
        }).start();
    }

    private void initListView(final boolean readflag) {
        MessageAdapter adapter = new MessageAdapter(MessageListActivity.this, R.layout.message_list_item, messageList);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeColors(2);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            public void onRefresh() {
                refreshMessage(readflag);
            }
        });
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        listView.setOnItemClickListener(new MessageListOnItemClickListener());
        listView.setOnItemLongClickListener(new MessageListOnItemLongClickListener());
    }

    private void initEmptyListView() {

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,new String[]{"没有内容"});
        listView.setAdapter(adapter);
    }


    private void refreshMessage(final boolean readflag) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                    ;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getMessageList(readflag);
                        swipeRefresh.setRefreshing(false);
//                        Looper.prepare();
//                        Toast.makeText(MessageListActivity.this,"正在刷新",Toast.LENGTH_SHORT).show();

                    }
                });
            }
        }).start();
    }

    class MessageListOnItemClickListener implements AdapterView.OnItemClickListener {

        @SuppressWarnings("unchecked")
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(MessageListActivity.this);
            final View tmp = LayoutInflater.from(MessageListActivity.this).inflate(R.layout.message_detail, null);
            ((TextView) tmp.findViewById(R.id.MessageSectionTitle)).setText("查看消息");
            tmp.findViewById(R.id.title_back).setVisibility(View.GONE);
            tmp.findViewById(R.id.title_delete).setVisibility(View.GONE);

            final Message message = messageList.get(position);
            String time = message.getQM_CreateTime().substring(0, 19).replace("T", " ");
            TextView textView1 = (TextView) tmp.findViewById(R.id.Time);
            textView1.setText(time);
            TextView textView2 = (TextView) tmp.findViewById(R.id.Sender);
            textView2.setText(message.getPFullName_From());
            TextView textView3 = (TextView) tmp.findViewById(R.id.Msg);
            textView3.setText(message.getQuickMessage());
            builder.setView(tmp);
            builder.setCancelable(false);
            tmp.findViewById(R.id.btnDelete).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String[] errMsg = new String[1];
                            //boolean isDelete=WebServiceUtils.DeleteMessage(errMsg,"xk",true);
                            final boolean isDelete = WebServiceUtils.DeleteMessage(errMsg, message.getQM_ID());
                            Looper.prepare();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    getMessageList(readflag);
                                    //initListView(readflag);
                                    if (isDelete == true) {
                                        Toast.makeText(MessageListActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                                        // getMessageList(readflag);
                                    } else {
                                        Toast.makeText(MessageListActivity.this, "删除失败！", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }).start();

                    viewMessageDialog.dismiss();
                }
            });

            tmp.findViewById(R.id.btnClose).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (message.getQM_IsShown() == false) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                String[] errMsg = new String[1];
                                //boolean isDelete=WebServiceUtils.DeleteMessage(errMsg,"xk",true);
                                final boolean changeReadState = WebServiceUtils.UpdateMessage(errMsg, message.getQM_ID());
                                Looper.prepare();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        getMessageList(readflag);
                                        //initListView(readflag);
                                    }
                                });
                            }
                        }).start();
                    }
                    viewMessageDialog.dismiss();


                }
            });
            viewMessageDialog = builder.create();
            viewMessageDialog.show();
        }
    }

    class MessageListOnItemLongClickListener implements AdapterView.OnItemLongClickListener {

        @SuppressWarnings("unchecked")
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position,
                                       long id) {
            final AlertDialog.Builder dialog = new AlertDialog.Builder(MessageListActivity.this);
            final Message message = messageList.get(position);
            dialog.setMessage("确定删除此条通知吗？");
            dialog.setPositiveButton("删除", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
//
                    //      new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            String[] errMsg = new String[1];
//                            //boolean isDelete=WebServiceUtils.DeleteMessage(errMsg,"xk",true);
//                            boolean isDelete=WebServiceUtils.DeleteMessage(errMsg,message.getQM_ID());
//                            if(isDelete==true){
//                                Toast.makeText(MessageListActivity.this,"删除成功",Toast.LENGTH_SHORT).show();
//                                // getMessageList(readflag);
//                            }
//                            else{
//                                Toast.makeText(MessageListActivity.this,"删除失败！",Toast.LENGTH_SHORT).show();
//                            }
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    getMessageList(readflag);
//
//                                }
//                            });
//                        }
//                    }).start();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String[] errMsg = new String[1];
                            //boolean isDelete=WebServiceUtils.DeleteMessage(errMsg,"xk",true);
                            final boolean isDelete = WebServiceUtils.DeleteMessage(errMsg, message.getQM_ID());
                            Looper.prepare();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    getMessageList(readflag);
                                    //initListView(readflag);
                                    if (isDelete == true) {
                                        Toast.makeText(MessageListActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                                        // getMessageList(readflag);
                                    } else {
                                        Toast.makeText(MessageListActivity.this, "删除失败！", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }).start();

                    deleteDialog.dismiss();

                }
            });
            dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    deleteDialog.dismiss();
                }
            });
            deleteDialog = dialog.create();
            deleteDialog.show();
            return true;
        }
    }
}
