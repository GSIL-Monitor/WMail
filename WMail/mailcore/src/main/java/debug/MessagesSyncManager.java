package debug;


import com.gongw.mailcore.account.Account;
import com.gongw.mailcore.account.AccountModel;
import com.gongw.mailcore.message.LocalMessage;

import java.util.List;

import javax.mail.URLName;

/**
 * Created by hoa on 1/8/15.
 */
public class MessagesSyncManager {
    public LocalMessage currentMessage;

    static private MessagesSyncManager theSingleton;

    public static MessagesSyncManager singleton() {
        if (theSingleton == null) {
            theSingleton = new MessagesSyncManager();
        }
        return theSingleton;
    }

    private MessagesSyncManager() {

    }


}
