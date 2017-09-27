package com.nju.urbangreen.zhenjiangurbangreen.ugo;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nju.urbangreen.zhenjiangurbangreen.R;
import com.nju.urbangreen.zhenjiangurbangreen.basisClass.BaseItemViewHolder;
import com.nju.urbangreen.zhenjiangurbangreen.basisClass.GreenObject;
import com.nju.urbangreen.zhenjiangurbangreen.map.SimpleMapActivity;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by tommy on 2017/8/9.
 */

public class UgoListAdapter extends RecyclerView.Adapter<BaseItemViewHolder> {
    private List<GreenObject> mUgoList;

    @Override
    public BaseItemViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_list_item, parent, false);
        final BaseItemViewHolder holder = new BaseItemViewHolder(view);
        holder.itemTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resolveClickEvent(view,holder);
            }
        });
        holder.itemContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resolveClickEvent(view,holder);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(final BaseItemViewHolder holder, int position) {
        GreenObject ugObject = mUgoList.get(position);
        holder.itemTitle.setText(ugObject.UGO_Ucode + "/" + ugObject.UGO_Name);
        holder.itemContent.setText(ugObject.UGO_Address);
    }

    @Override
    public int getItemCount() {
        return mUgoList.size();
    }

    private void resolveClickEvent(View view,BaseItemViewHolder viewHolder) {
        int position = viewHolder.getAdapterPosition();
//        Toast.makeText(view.getContext(), position + "", Toast.LENGTH_SHORT).show();
        GreenObject object = mUgoList.get(position);
        Intent intent = new Intent(view.getContext(), SimpleMapActivity.class);
        intent.putExtra("type", object.UGO_ClassType_ID);
        intent.putExtra("name", object.UGO_Name);
        intent.putExtra("location", object.UGO_Geo_Location);
        try {
            view.getContext().startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(view.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public UgoListAdapter(List<GreenObject> ugObjectList) {
        mUgoList = ugObjectList;
    }
}
