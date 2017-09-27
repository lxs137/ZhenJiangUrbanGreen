package com.nju.urbangreen.zhenjiangurbangreen.basisClass;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nju.urbangreen.zhenjiangurbangreen.R;
import com.nju.urbangreen.zhenjiangurbangreen.inspectRecord.Inspect;
import com.nju.urbangreen.zhenjiangurbangreen.inspectRecord.InspectRegisterActivity;
import com.nju.urbangreen.zhenjiangurbangreen.maintainRecord.Maintain;
import com.nju.urbangreen.zhenjiangurbangreen.maintainRecord.MaintainRegisterActivity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by tommy on 2017/9/23.
 */

public class BaseListAdapter extends RecyclerView.Adapter<BaseItemViewHolder>{
    private List<Object> list;
    private String type;

    public BaseListAdapter(List<Object> list, String type) {
        this.list = list;
        this.type = type;
    }

    @Override
    public BaseItemViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_list_item, parent, false);
        final BaseItemViewHolder viewHolder = new BaseItemViewHolder(view);
        viewHolder.itemTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resolveClick(viewHolder,view);
            }
        });
        viewHolder.itemContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resolveClick(viewHolder,view);
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(BaseItemViewHolder viewHolder, int i) {
        if(type.equals("maintain")) {
            Maintain maintain = (Maintain) list.get(i);
            viewHolder.itemTitle.setText(maintain.MR_Code + "/" + maintain.MR_MaintainType);
            viewHolder.itemContent.setText(maintain.MR_MaintainContent);
        }

        if(type.equals("inspect")){
            Inspect inspect = (Inspect)list.get(i);
            viewHolder.itemTitle.setText(inspect.getIR_Code() + "/" + inspect.getIR_Type());
            viewHolder.itemContent.setText(inspect.getIR_Content());
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private void resolveClick(BaseItemViewHolder viewHolder, View view) {
        int position = viewHolder.getAdapterPosition();
        if(type.equals("maintain")) {
            Maintain maintain = (Maintain) list.get(position);
            Intent intent = new Intent(view.getContext(), MaintainRegisterActivity.class);
            intent.putExtra("maintain_object", maintain);
            view.getContext().startActivity(intent);
        }
        if(type.equals("inspect")){
            Inspect inspect = (Inspect)list.get(position);
            Intent intent = new Intent(view.getContext(), InspectRegisterActivity.class);
            intent.putExtra("inspect_object", inspect);
            view.getContext().startActivity(intent);
        }
    }
}
