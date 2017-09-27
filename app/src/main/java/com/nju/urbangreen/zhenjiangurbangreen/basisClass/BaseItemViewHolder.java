package com.nju.urbangreen.zhenjiangurbangreen.basisClass;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;

import com.nju.urbangreen.zhenjiangurbangreen.R;

/**
 * Created by tommy on 2017/9/2.
 */

/**
 * recyclerView item公用viewholder，分为标题和内容两部分，都是可左右滑动的
 */

public class BaseItemViewHolder extends RecyclerView.ViewHolder {
    public View itemView;
    public EditText itemTitle;
    public EditText itemContent;

    public BaseItemViewHolder(View view) {
        super(view);
        itemView = view;
        itemTitle = (EditText) itemView.findViewById(R.id.recycler_title);
        itemContent = (EditText) itemView.findViewById(R.id.recycler_content);
    }
}
