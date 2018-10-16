package com.gongw.login.view;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.gongw.common.adapter.SimpleAdapter;
import com.gongw.common.base.BaseActivity;
import com.gongw.common.base.BaseFragment;
import com.gongw.login.BR;
import com.gongw.login.R;
import com.gongw.login.databinding.FragmentMailTypeBinding;
import com.gongw.login.model.MailType;
import com.gongw.login.view.widget.RecyclerViewDivider;
import com.gongw.login.viewmodel.MailTypeModel;

/**
 * Created by gongw on 2018/7/10.
 */

public class MailTypeFragment extends BaseFragment<FragmentMailTypeBinding> {

    @Override
    public int getContentViewLayout() {
        return R.layout.fragment_mail_type;
    }

    @Override
    public void init() {
        RecyclerView recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new RecyclerViewDivider(recyclerView.getContext(), LinearLayoutManager.VERTICAL));

        SimpleAdapter<MailType> adapter = new SimpleAdapter<MailType>(MailTypeModel.getInstance().getDatas(), R.layout.item_mail_type, BR.mailType){
            @Override
            public void addListener(View root, MailType mailType, final int position) {
                root.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((BaseActivity)context).replaceFragment(R.id.fl_container, new LoginFragment(), true);
                    }
                });
            }
        };

        recyclerView.setAdapter(adapter);
    }

}
