package debug;


import com.gongw.common.base.BaseActivity;
import com.gongw.mailcore.account.Account;
import com.gongw.mailcore.account.AccountModel;
import com.gongw.mailcore.databinding.ActivityMainBinding;
import com.gongw.mailcore.R;
import com.gongw.mailcore.folder.FolderModel;
import com.gongw.mailcore.folder.LocalFolder;
import com.gongw.mailcore.message.LocalMessage;
import com.gongw.mailcore.message.MessageModel;
import com.gongw.mailcore.part.PartModel;
import java.io.IOException;
import java.util.List;
import javax.mail.MessagingException;

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
//            URLName urlName = new URLName("imaps", "imap.exmail.qq.com", 993, null, "gongw@qtec.cn", "Gg147258");
//            final Account account = new Account();
//            account.setStoreUrl(urlName.toString());
//            account.setEmail("gongw@qtec.cn");
//            account.setPassword("Gg147258");
//            account.setAvailable(true);
//            AccountModel.singleInstance().saveOrUpdateAccount(account);



        new Thread(){
            @Override
            public void run() {
                try {
                    Account account = AccountModel.singleInstance().getAccountsById(1);
                List<LocalFolder> localFolders = FolderModel.singleInstance().getFolders(account);
                LocalFolder inbox = null;
                for(LocalFolder localFolder : localFolders){
                    if(localFolder.getFullName().toLowerCase().equals("inbox")){
                        inbox = localFolder;
                        break;
                    }
                }
                    List<LocalMessage> messages = MessageModel.singleInstance().getMessages(inbox, 0);
                    for(LocalMessage message : messages){
                        PartModel.singleInstance().getParts(message);
                    }
                } catch (MessagingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }

}
