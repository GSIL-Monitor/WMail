package debug;

import com.gongw.common.base.BaseApplication;
import com.gongw.mailcore.account.Account;
import com.gongw.mailcore.account.AccountModel;

import org.litepal.LitePal;


import javax.mail.URLName;

/**
 * Created by gongw on 2018/7/10.
 */

public class Application extends BaseApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        LitePal.initialize(getApplicationContext());

            Account account = new Account();
            URLName urlName = new URLName("imaps", "imap.exmail.qq.com", 993, null, "gongw@qtec.cn", "Gg147258");
            URLName urlName1 = new URLName("smtps", "smtp.exmail.qq.com", 465, null, "gongw@qtec.cn", "Gg147258");
            account.setStoreUrl(urlName.toString());
            account.setTransportUrl(urlName1.toString());
            account.setEmail("gongw@qtec.cn");
            account.setPassword("Gg147258");
            account.setAvailable(true);
            AccountModel.singleInstance().saveOrUpdateAccount(account);

//        Account account = new Account();
//        URLName urlName = new URLName("imaps", "imap.139.com", 993, null, "15079193126@139.com", "Gogowei1992");
//        URLName urlName1 = new URLName("smtps", "smtp.139.com", 465, null, "15079193126@139.com", "Gogowei1992");
//        account.setStoreUrl(urlName.toString());
//        account.setTransportUrl(urlName1.toString());
//        account.setEmail("15079193126@139.com");
//        account.setPassword("Gogowei1992");
//        account.setAvailable(true);
//        AccountModel.singleInstance().saveOrUpdateAccount(account);

    }
}
