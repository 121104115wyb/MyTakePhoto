package com.btzh.mytakephoto.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.btzh.androidphpfiles.listener.ItemClickListener;
import com.btzh.androidphpfiles.moudle.MyTImage;
import com.btzh.androidphpfiles.R;
import com.bumptech.glide.Glide;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wyb on 2018/3/13.
 * <p>
 * ## 图片adapter
 */

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.MyViewHolder> {

    private List<MyTImage> tImageList;
    private Context context;
    private ItemClickListener itemClickListener;

    public PhotoAdapter(Context context, List<MyTImage> tImageList) {
        this.context = context;
        this.tImageList = tImageList;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = null;
        View view = null;
        if (holder == null) {
            view = LayoutInflater.from(context).inflate(R.layout.photo_item, parent, false);
            holder = new MyViewHolder(view);
        } else {
            holder = (MyViewHolder) view.getTag();
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        if (tImageList != null && tImageList.size()>0) {
//            for (int imgitem = 0; imgitem < tImageList.size(); imgitem++) {

                Glide.with(context).load(new File(tImageList.get(position).getLocalPath())).into(holder.imageItem);
//            }
        }
    }


    @Override
    public int getItemCount() {
        return tImageList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.image_item)
        ImageView imageItem;

        public MyViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void onClick(View v) {
            if (itemClickListener != null) {
                itemClickListener.onClick(v, getAdapterPosition());
            }
        }
    }
}
