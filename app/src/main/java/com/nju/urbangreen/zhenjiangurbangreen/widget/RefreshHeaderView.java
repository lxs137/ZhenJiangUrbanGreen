package com.nju.urbangreen.zhenjiangurbangreen.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.aspsine.swipetoloadlayout.SwipeRefreshTrigger;
import com.aspsine.swipetoloadlayout.SwipeTrigger;

/**
 * Created by tommy on 2017/8/30.
 */

public class RefreshHeaderView extends android.support.v7.widget.AppCompatTextView implements SwipeRefreshTrigger,SwipeTrigger {
    public RefreshHeaderView(Context context) {
        super(context);
    }

    public RefreshHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RefreshHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onRefresh() {
        setText("正在刷新");
    }

    @Override
    public void onPrepare() {

    }

    @Override
    public void onMove(int yScrolled, boolean isComplete, boolean automatic) {
//        if(isComplete){
//            setText("正在刷新");
//        }
    }

    @Override
    public void onRelease() {

    }

    @Override
    public void onComplete() {
//        setText("刷新完成");
    }

    @Override
    public void onReset() {

    }
}
