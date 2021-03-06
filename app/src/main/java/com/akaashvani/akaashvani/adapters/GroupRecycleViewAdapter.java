package com.akaashvani.akaashvani.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.akaashvani.akaashvani.R;
import com.akaashvani.akaashvani.tabs.TabActivity;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.List;

public class GroupRecycleViewAdapter extends RecyclerView.Adapter<GroupRecycleViewAdapter.GroupNameViewHolder> {
    List<String> data;
    List<ParseObject> parseData;
    Context mContext;
    private Bitmap bmp1, bmp2;
    private BitmapDrawable d, e;
    ImageView image;

    public GroupRecycleViewAdapter(Context context, ArrayList<ParseObject> groups) {
        parseData = groups;
        mContext = context;
    }

    @Override
    public GroupNameViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_grp_layout, parent, false);
        image = (ImageView) view.findViewById(R.id.imageview1);
        e = (BitmapDrawable) mContext.getResources().getDrawable(R.drawable.group_icon).getCurrent();
        bmp2 = Bitmap.createScaledBitmap(e.getBitmap(), (int) (e.getBitmap().getWidth() / 1), (int) (e.getBitmap().getHeight() / 1), false);
        bmp2 = getRoundedShape(bmp2);
        image.setImageBitmap(bmp2);
        GroupNameViewHolder holder = new GroupNameViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(GroupNameViewHolder holder, final int position) {
        if (!TextUtils.isEmpty(parseData.get(position).getString("name"))) {
            holder.mGrpNameTextView.setText(parseData.get(position).getString("name"));
        }

        holder.mGrpNameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, TabActivity.class);
                intent.putExtra("groupID", parseData.get(position).getObjectId().toString());
                intent.putExtra("groupName", parseData.get(position).getString("name"));
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return parseData.size();
    }

    public Bitmap getRoundedShape(Bitmap scaleBitmapImage) {

        int targetWidth = 200;
        int targetHeight = 200;
        Bitmap targetBitmap = Bitmap.createBitmap(targetWidth,
                targetHeight, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(targetBitmap);
        Path path = new Path();
        path.addCircle(((float) targetWidth - 1) / 2,
                ((float) targetHeight - 1) / 2,
                (Math.min(((float) targetWidth),
                        ((float) targetHeight)) / 2),
                Path.Direction.CCW);

        canvas.clipPath(path);
        Bitmap sourceBitmap = scaleBitmapImage;
        canvas.drawBitmap(sourceBitmap,
                new Rect(0, 0, sourceBitmap.getWidth(),
                        sourceBitmap.getHeight()),
                new Rect(0, 0, targetWidth,
                        targetHeight), null);
        return targetBitmap;
    }

    class GroupNameViewHolder extends RecyclerView.ViewHolder {

        TextView mGrpNameTextView;
        ImageView image;


        public GroupNameViewHolder(View itemView) {
            super(itemView);
            mGrpNameTextView = (TextView) itemView.findViewById(R.id.groupName_textview);
        }
    }
}
