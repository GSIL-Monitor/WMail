package com.gongw.common.base;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by gongw on 2018/7/10.
 */

public abstract class BaseFragment<T extends ViewDataBinding> extends Fragment {

    protected T binding;
    protected Context context;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, getContentViewLayout(), container, false);
        init();
        return binding.getRoot();
    }

    public abstract int getContentViewLayout();

    public abstract void init();

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.context = null;
    }

    public void addFragment(int containerViewId, Fragment fragment, boolean addToBackStack){
        FragmentManager manager = getChildFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(containerViewId, fragment, fragment.toString());
        if(addToBackStack){
            transaction.addToBackStack(fragment.toString());
        }
        transaction.commitAllowingStateLoss();
    }

    public void replaceFragment(int containerViewId, Fragment fragment, boolean addToBackStack){
        FragmentManager manager = getChildFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(containerViewId, fragment, fragment.toString());
        if(addToBackStack){
            transaction.addToBackStack(fragment.toString());
        }
        transaction.commitAllowingStateLoss();
    }

    public void removeFragment(Fragment fragment){
        FragmentManager manager = getChildFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.remove(fragment);
        transaction.commitAllowingStateLoss();
    }
}
