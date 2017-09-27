package com.nju.urbangreen.zhenjiangurbangreen.inspectRecord;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nju.urbangreen.zhenjiangurbangreen.R;
import com.nju.urbangreen.zhenjiangurbangreen.basisClass.BaseItemViewHolder;

import java.util.List;

/**
 * Created by tommy on 2017/9/20.
 */

public class InspectListAdapter extends RecyclerView.Adapter<BaseItemViewHolder>{
    private List<Inspect> inspectList;
//    private BaseItemViewHolder viewHolder;

    public InspectListAdapter(List<Inspect> inspectList) {
        this.inspectList = inspectList;
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
        Inspect inspect = inspectList.get(i);
        viewHolder.itemTitle.setText(inspect.getIR_Code() + "/" + inspect.getIR_Type());
        viewHolder.itemContent.setText(inspect.getIR_Content());
    }

    @Override
    public int getItemCount() {
        return inspectList.size();
    }

    private void resolveClick(BaseItemViewHolder viewHolder, View view) {
        int position = viewHolder.getAdapterPosition();
        Inspect inspect = inspectList.get(position);

        Intent intent = new Intent(view.getContext(), InspectRegisterActivity.class);
        intent.putExtra("inspect_object", inspect);

        view.getContext().startActivity(intent);
    }
}
