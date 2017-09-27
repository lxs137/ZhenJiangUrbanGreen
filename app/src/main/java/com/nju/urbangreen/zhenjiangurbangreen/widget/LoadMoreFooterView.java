package com.nju.urbangreen.zhenjiangurbangreen.widget;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.aspsine.swipetoloadlayout.SwipeLoadMoreTrigger;
import com.aspsine.swipetoloadlayout.SwipeTrigger;

/**
 * Created by tommy on 2017/8/30.
 */

public class LoadMoreFooterView extends AppCompatTextView implements SwipeLoadMoreTrigger,SwipeTrigger {
    public LoadMoreFooterView(Context context) {
        super(context);
    }

    public LoadMoreFooterView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LoadMoreFooterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onLoadMore() {
        setText("正在加载...");
    }

    @Override
    public void onPrepare() {

    }

    @Override
    public void onMove(int i, boolean b, boolean b1) {

    }

    @Override
    public void onRelease() {

    }

    @Override
    public void onComplete() {

    }

    @Override
    public void onReset() {

    }
}
