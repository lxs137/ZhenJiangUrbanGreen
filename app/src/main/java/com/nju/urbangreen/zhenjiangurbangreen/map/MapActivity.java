package com.nju.urbangreen.zhenjiangurbangreen.map;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Looper;
import android.provider.Settings;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.LocationDisplayManager;
import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISLocalTiledLayer;
import com.esri.android.map.event.OnSingleTapListener;
import com.esri.android.runtime.ArcGISRuntime;
import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.Point;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.PictureMarkerSymbol;
import com.esri.core.symbol.SimpleFillSymbol;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol;
import com.esri.core.symbol.Symbol;
import com.nju.urbangreen.zhenjiangurbangreen.R;
import com.nju.urbangreen.zhenjiangurbangreen.basisClass.BaseActivity;
import com.nju.urbangreen.zhenjiangurbangreen.inspectRecord.InspectListActivity;
import com.nju.urbangreen.zhenjiangurbangreen.maintainRecord.MaintainListActivity;
import com.nju.urbangreen.zhenjiangurbangreen.settings.SystemFileActivity;
import com.nju.urbangreen.zhenjiangurbangreen.util.FileUtil;
import com.nju.urbangreen.zhenjiangurbangreen.util.GeoJsonUtil;
import com.nju.urbangreen.zhenjiangurbangreen.util.SPUtils;
import com.nju.urbangreen.zhenjiangurbangreen.util.WebServiceUtils;
import com.nju.urbangreen.zhenjiangurbangreen.basisClass.GreenObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapActivity extends BaseActivity {

    private static final String GreenLandType = "000", AncientTreeType = "001", StreetTreeType = "002";
    private static Map<String, Symbol> symbolMap;

    //TPK文件名称
    String tpkFileName = null;

    //当前行道树图层是否可见
    private boolean isGreenTreeLayerVisible;

    Envelope fullExtent=new Envelope(480594.6020435903,3550915.606576609,512013.935715591,3574562.78928764);
    Envelope fullExtent2=new Envelope(491067.0,3558797.0,501540.0,3566679.0);
    //用于定位按钮的LocationManager
    private LocationManager locationManager;
    private LocationDisplayManager locationDisplayManager;

    //正在定位对话框
    private ProgressDialog dlgLocating;

    private ProgressDialog loadingDialog;

    //本地TPK文件图层
    private ArcGISLocalTiledLayer localTPKLayer;

    //定位按钮
    @BindView(R.id.imgbtn_locate)
    public ImageButton imgBtnLocate;

    //地图底图图层切换
    @BindView(R.id.imgbtn_map_layer)
    public ImageButton imgBtnMapLayer;

    //图层控制按钮
    @BindView(R.id.imgbtn_layer_switch)
    public ImageButton imgBtnLayerSwitch;

    //全局显示按钮
    @BindView(R.id.imgbtn_global_view)
    public ImageButton imgBtnGlobalView;

    //显示周边开关
    @BindView(R.id.imgbtn_nearby)
    public ImageButton imgBtnNearby;

    //点击图层控制按钮后弹出的popup窗口
    LayerSwitchPopupWindow layerSwitchPopupWindow;

    @BindView(R.id.bottombar)
    public View bottomBar;

    @BindView(R.id.tv_map_UGO_info)
    public TextView tvUGOInfo;
    public String curUGOID;

    @BindView(R.id.btn_map_maintain_record)
    public Button btnMaintainRecord;

    @BindView(R.id.btn_map_inspect_record)
    public Button btnInspectRecord;

    @BindView(R.id.btn_map_event_record)
    public Button btnEventRecord;

    @BindView(R.id.btn_map_UGO_basicInfo)
    public Button btnUGOInfo;

    @BindView(R.id.map_main)
    public MapView map = null;

    GraphicsLayer streetTreeLayer;
    GraphicsLayer ancientTreeLayer;
    GraphicsLayer greenLandLayer;
    GraphicsLayer locationLayer;

    private ArrayList<GreenObject> greenLandList, ancientTreeList, streetTreeList;
    private final String[] tpkFileNames = WebServiceUtils.BaseMapFileNames;
    private final String[] tpkLayerNames = {"矢量图层", "影像图层"};
    private int curTpkFileNamesIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_map);

        ButterKnife.bind(this);

        loadingDialog = new ProgressDialog(MapActivity.this);

        ArcGISRuntime.setClientId("1eFHW78avlnRUPHm");

        //新建一个离线地图图层并添加到mapview中
        tpkFileName = FileUtil.getAppFileDir() + "tpk/" + tpkFileNames[curTpkFileNamesIndex];
        if(!new File(tpkFileName).exists()) {
            new MaterialDialog.Builder(this)
                    .title("地图图层文件未下载，是否前往下载界面")
                    .positiveText("确认")
                    .negativeText("取消")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            Intent intent = new Intent(MapActivity.this, SystemFileActivity.class);
                            startActivity(intent);
                        }
                    })
                    .show();
        }
        localTPKLayer = new ArcGISLocalTiledLayer(tpkFileName);
        map.addLayer(localTPKLayer);

        getUGOInfo();

        //设置定位按钮并添加定位符号图层
        setLocateButton();
        map.addLayer(locationLayer);

        //设置全局显示按钮
        setGlobalViewButton();

        //设置地图底图切换开关
        setMapLayerSwitchButton();

        //设置图层开关按钮
        setLayerSwitchPopupWindow();

        //设置显示周边按钮
        setNearTreeButton();

        //设置底部栏的按钮点击事件
        setBottomBar();

        //设置mapView单次tap监听
        map.setOnSingleTapListener(onSingleTapListener);

        //在第一次加载mapview的时候设置为全局显示
        map.setExtent(fullExtent2);

        openGPS();

    }

    private void setMapLayerSwitchButton() {
        imgBtnMapLayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialDialog.Builder(MapActivity.this)
                        .title("切换地图图层")
                        .items(tpkLayerNames)
                        .itemsCallbackSingleChoice(curTpkFileNamesIndex, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                curTpkFileNamesIndex = which;
                                map.removeLayer(0);
                                tpkFileName = FileUtil.getAppFileDir() + "tpk/" + tpkFileNames[curTpkFileNamesIndex];
                                localTPKLayer = new ArcGISLocalTiledLayer(tpkFileName);
                                map.addLayer(localTPKLayer, 0);
                                return true;
                            }
                        })
                        .positiveText("确认")
                        .show();
            }
        });
    }

    private void setNearTreeButton() {
        imgBtnNearby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadingDialog.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Location cur_loc = locationDisplayManager.getLocation();
                        String errorMsg[] = new String[1];
                        List<GreenObject> res = WebServiceUtils.getNearStreetTree(cur_loc.getLongitude(),
                                cur_loc.getLatitude(), SPUtils.getFloat("NearRadius", 500.f), errorMsg);
                        if(res != null) {
                            streetTreeList.clear();
                            streetTreeLayer.removeAll();
                            for(GreenObject obj : res) {
                                Geometry geometry = GeoJsonUtil.String2Geometry(obj.UGO_Geo_Location);
                                if(geometry != null) {
                                    Map<String,Object> greenObj = new HashMap<>();
                                    if (obj.UGO_ClassType_ID.equals(StreetTreeType)){
                                        streetTreeList.add(obj);
                                        greenObj.put("UGO_Ucode", obj.UGO_ID);
                                        greenObj.put("GraphicType", 2);
                                        Graphic graphic = new Graphic(geometry, symbolMap.get(StreetTreeType), greenObj);
                                        streetTreeLayer.addGraphic(graphic);
                                    }
                                }
                            }
                            loadingDialog.dismiss();
                        } else if(errorMsg[0] != null && !errorMsg[0].equals("")) {
                            loadingDialog.dismiss();
                            Looper.prepare();
                            Toast.makeText(MapActivity.this, errorMsg[0], Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        } else {
                            loadingDialog.dismiss();
                            Looper.prepare();
                            Toast.makeText(MapActivity.this, "无法获取周边行道树", Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                    }
                }).start();

            }
        });
    }

    /*
    * 设置全局显示按钮
    * */
    private void setGlobalViewButton(){
        imgBtnGlobalView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Envelope envelope = fullExtent;
                int vh = map.getMeasuredHeight();
                int vw = map.getMeasuredWidth();
                double eh = envelope.getHeight();
                double ew = envelope.getWidth();
                map.zoomToResolution(envelope.getCenter(),Math.max(eh/vh,ew/vw));
            }
        });
    }
    /*
    * 设置定位按钮
    * */
    private void setLocateButton(){
        locationDisplayManager= map.getLocationDisplayManager();
        locationDisplayManager.setShowLocation(true);
        locationDisplayManager.setAutoPanMode(LocationDisplayManager.AutoPanMode.LOCATION);//设置模式
        locationDisplayManager.setShowPings(true);
        locationDisplayManager.start();//开始定位
        imgBtnLocate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Point curP = locationDisplayManager.getPoint();
                if(curP.getX() >= fullExtent.getXMin() && curP.getX() <= fullExtent.getXMax()
                   && curP.getY() >= fullExtent.getYMin() && curP.getY() <= fullExtent.getYMax()) {
                    zoomTo(locationDisplayManager.getPoint());
                }
                else {
                    Toast.makeText(MapActivity.this, "当前位置不在地图范围内", Toast.LENGTH_SHORT).show();
                }
            }
        });
        locationLayer = new GraphicsLayer();
        locationManager = (LocationManager)MapActivity.this.getSystemService(Context.LOCATION_SERVICE);
    }

    /*
    * 设置图层开关按钮
    * */
    private void setLayerSwitchPopupWindow(){
        layerSwitchPopupWindow = new LayerSwitchPopupWindow(this, new ILayerSwitchListener() {
            @Override
            public boolean[] getLayerState() {
                return new boolean[] {greenLandLayer.isVisible(),ancientTreeLayer.isVisible(), streetTreeLayer.isVisible()};
            }

            @Override
            public void changeLayerState(boolean[] layerState) {
                GraphicsLayer[] layers = new GraphicsLayer[]{greenLandLayer,ancientTreeLayer, streetTreeLayer};
                for(int i = 0;i < layers.length;i++){
                    layers[i].setVisible(layerState[i]);
                    isGreenTreeLayerVisible = layerState[layers.length-1];
                }

            }
        });
        imgBtnLayerSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int[] location = new int[2];
                view.getLocationOnScreen(location);
                layerSwitchPopupWindow.show(view);
            }
        });
    }

    /*
    * 缩放至特定的位置来显示
    * */
    private void zoomTo(Geometry geometry){
        Envelope envelope = new Envelope();
        geometry.queryEnvelope(envelope);
        int vh = map.getMeasuredHeight();
        int vw = map.getMeasuredWidth();
        double eh = envelope.getHeight();
        double ew = envelope.getWidth();
        double res = Math.max(eh/vh,ew/vw);
        map.zoomToResolution(envelope.getCenter(),res);
    }

    private void highlightGraphic(Graphic graphic){
        int[] ids = new int[]{(int)graphic.getId()};
        int graphicType = (Integer)graphic.getAttributeValue("GraphicType");
        curUGOID = (String)graphic.getAttributeValue("UGO_Ucode");
        if(graphicType == 0){
            greenLandLayer.setSelectedGraphics(ids, true);
            tvUGOInfo.setText("绿地: " + curUGOID);
        }else if(graphicType == 1){
            ancientTreeLayer.setSelectedGraphics(ids, true);
            tvUGOInfo.setText("古树名木: " + curUGOID);
        }else if(graphicType == 2){
            streetTreeLayer.setSelectedGraphics(ids, true);
            tvUGOInfo.setText("行道树: " + curUGOID);
        }
    }

    /*
    * 实例化一个单次tap事件监听
    * */
    private OnSingleTapListener onSingleTapListener = new OnSingleTapListener() {
        @Override
        public void onSingleTap(float v, float v1) {
            if(!map.isLoaded()){
                return;
            }
            GraphicsLayer[] layers = new GraphicsLayer[]{greenLandLayer,ancientTreeLayer, streetTreeLayer};

            Integer graphicID = null;
            int layerIndex = 0;

            //在每个图层中搜索被点击的Graphics
            for(;layerIndex < layers.length;layerIndex++){
                GraphicsLayer graphicsLayer = layers[layerIndex];
                if(graphicsLayer == null){
                    continue;
                }
                graphicsLayer.clearSelection();
                if(!graphicsLayer.isVisible()){
                    continue;
                }
                int[] ids = graphicsLayer.getGraphicIDs(v,v1,25,1);
                if(ids != null && ids.length > 0){
                    graphicID = ids[0];
                    break;
                }
            }
            if(graphicID == null){
                bottomBar.setVisibility(View.GONE);
                return;
            }
            Graphic graphic = layers[layerIndex].getGraphic(graphicID);
            highlightGraphic(graphic);
            bottomBar.setVisibility(View.VISIBLE);
        }
    };


    /*
    *打开定位服务设置界面
    * */
    private void openGPS(){

        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("系统提示");
            builder.setMessage("是否现在打开GPS?");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);

                }
            });
            builder.setCancelable(true);
            AlertDialog dialog = builder.create();
            dialog.show();
        }

    }

    /**
     * 设置bottom bar中按钮的点击事件
     */
    private void setBottomBar(){
        btnMaintainRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MapActivity.this, MaintainListActivity.class);
                intent.putExtra("UGO_Ucode", curUGOID);
                startActivity(intent);
            }
        });

        btnInspectRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MapActivity.this, InspectListActivity.class);
                intent.putExtra("UGO_Ucode", curUGOID);
                startActivity(intent);
            }
        });

        btnEventRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MapActivity.this, InspectListActivity.class);
                intent.putExtra("UGO_Ucode", curUGOID);
                startActivity(intent);
            }
        });

        btnUGOInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void getUGOInfo() {
        greenLandLayer = new GraphicsLayer();
        ancientTreeLayer = new GraphicsLayer();
        streetTreeLayer = new GraphicsLayer();
        map.addLayer(greenLandLayer);
        map.addLayer(ancientTreeLayer);
        map.addLayer(streetTreeLayer);
        greenLandList = new ArrayList<>();
        ancientTreeList = new ArrayList<>();
        streetTreeList = new ArrayList<>();
        loadingDialog.setMessage("正在加载数据...");
        loadingDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                String errorMsg[] = new String[1];
                List<GreenObject> UGO_list = WebServiceUtils.getUGOInfoExceptST(errorMsg);
                createUGOLayers(UGO_list);
                loadingDialog.dismiss();
            }
        }).start();
    }

    private void createUGOLayers(List<GreenObject> list) {
        symbolMap = new HashMap<>();
        SimpleFillSymbol greenLandSymbol = new SimpleFillSymbol(
                ResourcesCompat.getColor(getResources(), R.color.green_land, null), SimpleFillSymbol.STYLE.SOLID);
        greenLandSymbol.setOutline(new SimpleLineSymbol(
                ResourcesCompat.getColor(getResources(), R.color.green_land_border, null), 1.0f, SimpleLineSymbol.STYLE.SOLID));
        symbolMap.put(GreenLandType, greenLandSymbol);
        PictureMarkerSymbol ancientTreeSymbol = new PictureMarkerSymbol(new BitmapDrawable(
                BitmapFactory.decodeResource(getResources(), R.drawable.ancient_tree)));
        ancientTreeSymbol.setOffsetY(16);
        symbolMap.put(AncientTreeType, ancientTreeSymbol);
        symbolMap.put(StreetTreeType, new SimpleMarkerSymbol(
                ResourcesCompat.getColor(getResources(), R.color.colorPrimaryDark, null), 5, SimpleMarkerSymbol.STYLE.CIRCLE));

        for(GreenObject obj : list) {
            Geometry geometry = GeoJsonUtil.String2Geometry(obj.UGO_Geo_Location);
            if(geometry != null) {
                Map<String,Object> greenObj = new HashMap<>();
                if (obj.UGO_ClassType_ID.equals(GreenLandType)) {
                    greenLandList.add(obj);
                    greenObj.put("UGO_Ucode", obj.UGO_ID);
                    greenObj.put("GraphicType", 0);
                    Graphic graphic = new Graphic(geometry, symbolMap.get(GreenLandType), greenObj);
                    greenLandLayer.addGraphic(graphic);
                }
                else if (obj.UGO_ClassType_ID.equals(AncientTreeType)) {
                    ancientTreeList.add(obj);
                    greenObj.put("UGO_Ucode", obj.UGO_ID);
                    greenObj.put("GraphicType", 1);
                    Graphic graphic = new Graphic(geometry, symbolMap.get(AncientTreeType), greenObj);
                    ancientTreeLayer.addGraphic(graphic);
                }
                else if (obj.UGO_ClassType_ID.equals(StreetTreeType)){
                    streetTreeList.add(obj);
                    greenObj.put("UGO_Ucode", obj.UGO_ID);
                    greenObj.put("GraphicType", 2);
                    Graphic graphic = new Graphic(geometry, symbolMap.get(StreetTreeType), greenObj);
                    streetTreeLayer.addGraphic(graphic);
                }
            }
        }
    }
}
