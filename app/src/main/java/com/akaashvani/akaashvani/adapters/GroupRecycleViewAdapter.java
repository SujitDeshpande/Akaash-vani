package com.akaashvani.akaashvani.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.akaashvani.akaashvani.R;
import com.akaashvani.akaashvani.tabs.TabActivity;
import java.security.acl.Group;
import java.util.List;

public class GroupRecycleViewAdapter extends RecyclerView.Adapter<GroupRecycleViewAdapter.GroupNameViewHolder> {
    List<Group> data;
    Context mContext;

    public GroupRecycleViewAdapter(Context context, List<Group> groups) {
        data = groups;
        mContext = context;
    }

    @Override
    public GroupNameViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_grp_layout, parent, false);
        GroupNameViewHolder holder = new GroupNameViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(GroupNameViewHolder holder, int position) {
        final Group currentGrp = data.get(position);

        if (!TextUtils.isEmpty(currentGrp.getName())) {
            holder.mGrpNameTextView.setText(currentGrp.getName());
        }

        holder.mGrpNameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, TabActivity.class);
                //intent.putExtra("groupObjId", currentGrp.getObjectId());
                //intent.putExtra("groupName", currentGrp.getName());

                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    class GroupNameViewHolder extends RecyclerView.ViewHolder {

        TextView mGrpNameTextView;
//        ImageView image;

        public GroupNameViewHolder(View itemView) {
            super(itemView);
            mGrpNameTextView = (TextView) itemView.findViewById(R.id.groupName_textview);
//            image = (ImageView) itemView.findViewById(R.id.list_img);

        }

    }


}
