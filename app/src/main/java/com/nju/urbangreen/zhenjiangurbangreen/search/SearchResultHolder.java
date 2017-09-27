package com.nju.urbangreen.zhenjiangurbangreen.search;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.nju.urbangreen.zhenjiangurbangreen.R;
import com.nju.urbangreen.zhenjiangurbangreen.basisClass.GreenObject;
import com.nju.urbangreen.zhenjiangurbangreen.inspectRecord.InspectListActivity;
import com.nju.urbangreen.zhenjiangurbangreen.maintainRecord.MaintainListActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by lxs on 2016/9/30.
 */
public class SearchResultHolder extends RecyclerView.ViewHolder{
    private GreenObject obj;
    private CardView mCardView;

    @BindView(R.id.tv_search_item_id)
    public TextView tvCode;

    @BindView(R.id.tv_search_item_type)
    public TextView tvType;

    @BindView(R.id.tv_search_item_name)
    public TextView tvName;

    @BindView(R.id.tv_search_item_address)
    public TextView tvAddress;

    @BindView(R.id.btn_search_item_MR)
    public AppCompatButton btnMaintainRecord;

    @BindView(R.id.btn_search_item_Event)
    public AppCompatButton btnEvent;

    @BindView(R.id.btn_search_item_IR)
    public AppCompatButton btnInspectRecord;

    @BindView(R.id.iv_search_item_detail)
    public AppCompatImageView ivDetail;

    public SearchResultHolder(View itemView) {
        super(itemView);
        mCardView=(CardView) itemView.findViewById(R.id.cardView_searchResult);
        ButterKnife.bind(this, itemView);
        ivDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), UGOInfoActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("UGO", obj);
                intent.putExtras(bundle);
                view.getContext().startActivity(intent);
            }
        });
        btnMaintainRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MaintainListActivity.class);
//                intent.putExtra("UGO_Ucode", obj.getID());
                v.getContext().startActivity(intent);
            }
        });
        btnEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), InspectListActivity.class);
//                intent.putExtra("UGO_Ucode", obj.getID());
                v.getContext().startActivity(intent);
            }
        });
        btnInspectRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), InspectListActivity.class);
//                intent.putExtra("UGO_Ucode", obj.getID());
                v.getContext().startActivity(intent);
            }
        });
    }

    public void bindObj(GreenObject object) {
        this.obj = object;
        if(obj.UGO_Address != null) {
            tvAddress.setText("地址: " + obj.UGO_Address);
        }
        String type = "类型: 行道树";
        if(obj.UGO_ClassType_ID.equals("000"))
            type = "类型: 绿地";
        else if(obj.UGO_ClassType_ID.equals("001"))
            type = "类型: 古树名木";
        tvType.setText(type);
        tvCode.setText(obj.UGO_Ucode);
        tvName.setText("名字: " + obj.UGO_Name);
    }


}
