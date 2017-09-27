package com.nju.urbangreen.zhenjiangurbangreen.map;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISLocalTiledLayer;
import com.esri.android.map.event.OnStatusChangedListener;
import com.esri.android.runtime.ArcGISRuntime;
import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.Geometry;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.PictureMarkerSymbol;
import com.esri.core.symbol.SimpleFillSymbol;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.core.symbol.Symbol;
import com.nju.urbangreen.zhenjiangurbangreen.R;
import com.nju.urbangreen.zhenjiangurbangreen.basisClass.BaseActivity;
import com.nju.urbangreen.zhenjiangurbangreen.util.FileUtil;
import com.nju.urbangreen.zhenjiangurbangreen.util.GeoJsonUtil;
import com.nju.urbangreen.zhenjiangurbangreen.util.WebServiceUtils;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SimpleMapActivity extends BaseActivity {

    private ArcGISLocalTiledLayer localTPKLayer;
    GraphicsLayer curUGOLayer;
    Envelope fullExtent = new Envelope(491067.0,3558797.0,501540.0,3566679.0);

    @BindView(R.id.map_simple)
    public MapView map;

    @BindView(R.id.Toolbar_simple)
    public Toolbar toolbar;

    private String type, name, location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = this.getIntent();
        type = intent.getStringExtra("type");
        name = intent.getStringExtra("name");
        location = intent.getStringExtra("location");

        setContentView(R.layout.activity_simple_map);
        ButterKnife.bind(this);
        initToolbar();
        setMap();
    }

    private void initToolbar() {
        String typeName = "行道树";
        if(type.equals("000"))
            typeName = "绿地";
        else if(type.equals("001"))
            typeName = "古树名木";
        toolbar.setTitle(typeName + " " + name);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void setMap() {
        ArcGISRuntime.setClientId("1eFHW78avlnRUPHm");
        String tpkFileName = FileUtil.getBaseMapTPKDir() + WebServiceUtils.BaseMapFileNames[0];
        localTPKLayer = new ArcGISLocalTiledLayer(tpkFileName);
        map.addLayer(localTPKLayer);
        curUGOLayer = new GraphicsLayer();
        map.addLayer(curUGOLayer);
        map.setExtent(fullExtent);


        final Geometry geometry = GeoJsonUtil.String2Geometry(location);
        if(geometry != null) {
            Symbol symbol;
            if (type.equals("000")) {
                symbol = new SimpleFillSymbol(
                        ResourcesCompat.getColor(getResources(), R.color.green_land, null), SimpleFillSymbol.STYLE.SOLID);
                ((SimpleFillSymbol) symbol).setOutline(new SimpleLineSymbol(
                        ResourcesCompat.getColor(getResources(), R.color.green_land_border, null), 1.0f, SimpleLineSymbol.STYLE.SOLID));

            } else if (type.equals("001")) {
                symbol = new PictureMarkerSymbol(new BitmapDrawable(
                        BitmapFactory.decodeResource(getResources(), R.drawable.ancient_tree)));
            } else {
                symbol = new PictureMarkerSymbol(new BitmapDrawable(
                        BitmapFactory.decodeResource(getResources(), R.drawable.street_tree)));
            }
            Graphic graphic = new Graphic(geometry, symbol, null);
            curUGOLayer.addGraphic(graphic);
            curUGOLayer.setSelectedGraphics(new int[]{(int) graphic.getId()}, true);


            map.setOnStatusChangedListener(new OnStatusChangedListener() {
                @Override
                public void onStatusChanged(Object o, STATUS status) {
                    if (status == STATUS.INITIALIZED && o == map) {
                        zoomTo(geometry);
                    }
                }
            });
        }else{
            Toast.makeText(this, "没有坐标", Toast.LENGTH_SHORT).show();
        }

    }

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


}
