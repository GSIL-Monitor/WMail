package debug;

import com.gongw.common.base.BaseApplication;
import com.gongw.mailcore.account.Account;
import com.gongw.mailcore.account.AccountModel;

import org.litepal.LitePal;

import java.util.List;

import javax.mail.URLName;

/**
 * Created by gongw on 2018/7/10.
 */

public class Application extends BaseApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        LitePal.initialize(getApplicationContext());

        List<Account> accountList = AccountModel.singleInstance().getAccountsByEmail("gongw@qtec.cn");
        if(accountList.size() < 1){
            Account account = new Account();
            URLName urlName = new URLName("imaps", "imap.exmail.qq.com", 993, null, "gongw@qtec.cn", "Gg147258");
            account.setStoreUrl(urlName.toString());
            account.setEmail("gongw@qtec.cn");
            account.setPassword("Gg147258");
            account.setAvailable(true);
            AccountModel.singleInstance().saveOrUpdateAccount(account);
        }
    }
}
