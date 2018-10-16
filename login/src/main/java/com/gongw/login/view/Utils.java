package com.gongw.login.view;

import android.databinding.BindingAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import com.gongw.login.view.widget.RecyclerViewDivider;



/**
 * Created by gongw on 2018/7/11.
 */

public class Utils {

    @BindingAdapter({"drawable"})
    public static void setImage(ImageView imageView, String icon){
        int r_id = imageView.getResources().getIdentifier(icon, "drawable", imageView.getContext().getPackageName());
        imageView.setImageResource(r_id);
    }

}
