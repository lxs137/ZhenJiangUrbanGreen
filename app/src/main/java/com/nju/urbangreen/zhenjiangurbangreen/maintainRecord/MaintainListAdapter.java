package com.nju.urbangreen.zhenjiangurbangreen.maintainRecord;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.nju.urbangreen.zhenjiangurbangreen.R;
import com.nju.urbangreen.zhenjiangurbangreen.basisClass.BaseItemViewHolder;

import java.util.List;

/**
 * Created by tommy on 2017/8/17.
 */

public class MaintainListAdapter extends RecyclerView.Adapter<BaseItemViewHolder>{
    private List<Maintain> maintainList;
//    private BaseItemViewHolder viewHolder;

    public MaintainListAdapter(List<Maintain> maintainList) {
        this.maintainList = maintainList;
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
        Maintain maintain = maintainList.get(i);
        viewHolder.itemTitle.setText(maintain.MR_Code + "/" + maintain.MR_MaintainType);
        viewHolder.itemContent.setText(maintain.MR_MaintainContent);

    }

    @Override
    public int getItemCount() {
        return maintainList.size();
    }

    private void resolveClick(BaseItemViewHolder viewHolder, View view) {
        int position = viewHolder.getAdapterPosition();
//        Toast.makeText(view.getContext(), position + "", Toast.LENGTH_SHORT).show();
        Maintain maintain = maintainList.get(position);

        Intent intent = new Intent(view.getContext(), MaintainRegisterActivity.class);
        intent.putExtra("maintain_object", maintain);

        view.getContext().startActivity(intent);
    }
}
