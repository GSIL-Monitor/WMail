package debug;

import android.util.Log;

import com.gongw.common.base.BaseActivity;
import com.gongw.mailcore.account.Account;
import com.gongw.mailcore.account.AccountModel;
import com.gongw.mailcore.databinding.ActivityMainBinding;
import com.gongw.mailcore.R;
import com.gongw.mailcore.folder.FolderModel;
import com.gongw.mailcore.folder.LocalFolder;
import com.gongw.mailcore.message.LocalMessage;
import com.gongw.mailcore.message.MessageModel;

import java.util.List;

import javax.mail.MessagingException;
import javax.mail.URLName;

/**
 * Created by gongw on 2018/7/10.
 */

public class MainActivity extends BaseActivity<ActivityMainBinding> {

    @Override
    public int getContentViewLayout() {
        return R.layout.activity_main;
    }

    @Override
    public void init() {
            URLName urlName = new URLName("imaps", "imap.exmail.qq.com", 993, null, "gongw@qtec.cn", "Gg147258");
            final Account account = new Account();
            account.setStoreUrl(urlName.toString());
            account.setEmail("gongw@qtec.cn");
            account.setPassword("Gg147258");
            account.setAvailable(true);
            AccountModel.singleInstance().saveOrUpdateAccount(account);

//        new Thread(){
//            @Override
//            public void run() {
//                try {
//                List<LocalFolder> localFolders = FolderModel.singleInstance().getAllFolders(account);
//                LocalFolder inbox = null;
//                for(LocalFolder localFolder : localFolders){
//                    if(localFolder.getFullName().toLowerCase().equals("inbox")){
//                        inbox = localFolder;
//                        break;
//                    }
//                }
//                    List<LocalMessage> messages = MessageModel.singleInstance().getMessages(account, inbox, 0);
//                    for(LocalMessage message : messages){
//                        Log.i("message", message.toString());
//                    }
//                } catch (MessagingException e) {
//                    e.printStackTrace();
//                }
//            }
//        }.start();

    }

}
