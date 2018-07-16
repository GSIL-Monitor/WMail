//package com.gongw.common.base;
//
//import android.content.Context;
//import android.support.v7.widget.RecyclerView;
//import android.view.View;
//import android.view.ViewGroup;
//
//import java.util.List;
//
///**
// * Created by gongw on 2018/7/11.
// */
//
//public abstract class BaseBindingAdapter<T> extends RecyclerView.Adapter {
//
//    Context context;
//    List<T> items;
//
//    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        return null;
//    }
//
//    @Override
//    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
//        onBindItem(binding, this.items.get(position));
//    }
//
//    @Override
//    public int getItemCount() {
//        return items.size();
//    }
//
//    public class BaseBindingViewHolder extends RecyclerView.ViewHolder
//    {
//        public BaseBindingViewHolder(View itemView)
//        {
//            super(itemView);
//        }
//    }
//
//    public abstract void onBindItem();
//}
