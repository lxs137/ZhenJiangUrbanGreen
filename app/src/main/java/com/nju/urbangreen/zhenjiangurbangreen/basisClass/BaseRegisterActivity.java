package com.nju.urbangreen.zhenjiangurbangreen.basisClass;

import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.nju.urbangreen.zhenjiangurbangreen.R;

/**
 * Created by tommy on 2017/9/26.
 */

public class BaseRegisterActivity extends AppCompatActivity{
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar_maintain, menu);
        return super.onCreateOptionsMenu(menu);
    }

}
