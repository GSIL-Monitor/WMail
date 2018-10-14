package com.gongw.login.view;

import android.view.View;
import com.gongw.common.base.BaseActivity;
import com.gongw.common.base.BaseFragment;
import com.gongw.login.R;
import com.gongw.login.databinding.FragmentMailLoginBinding;

/**
 * Created by gongw on 2018/7/15.
 */

public class LoginFragment extends BaseFragment<FragmentMailLoginBinding> {

    @Override
    public int getContentViewLayout() {
        return R.layout.fragment_mail_login;
    }

    @Override
    public void init() {
        binding.tvSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((BaseActivity)context).replaceFragment(R.id.fl_container, new MailServerFragment(), true);
            }
        });
    }
}
