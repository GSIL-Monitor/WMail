package debug;

import com.gongw.common.base.BaseApplication;
import com.gongw.mailcore.imap.ImapService;
import com.gongw.mailcore.setting.ConnectInfo;
import com.gongw.mailcore.setting.ConnectType;
import com.gongw.mailcore.setting.PortType;
import com.libmailcore.IMAPFetchMessagesOperation;
import com.libmailcore.IMAPMessage;
import com.libmailcore.IMAPMessagesRequestKind;
import com.libmailcore.IndexSet;
import com.libmailcore.MailException;
import com.libmailcore.OperationCallback;
import com.libmailcore.Range;

import org.litepal.LitePal;

import java.util.List;

/**
 * Created by gongw on 2018/7/10.
 */

public class Application extends BaseApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        LitePal.initialize(this);
        ConnectInfo connectInfo = new ConnectInfo();
        connectInfo.setConnectType(ConnectType.SSL);
        connectInfo.setEmail("gongw@qtec.cn");
        connectInfo.setPwd("Gg147258");
        connectInfo.setServerHost("imap.exmail.qq.com");
        connectInfo.setServerPort(PortType.IMAP.defaultTLSPort);
        ImapService imapService = new ImapService(connectInfo);
        IndexSet indexSet = new IndexSet();
        Range range = new Range(1, 10);
        indexSet.addRange(range);
        final IMAPFetchMessagesOperation operation = imapService.imapSession.fetchMessagesByNumberOperation("INBOX", IMAPMessagesRequestKind.IMAPMessagesRequestKindFullHeaders, indexSet);
        operation.start(new OperationCallback() {
            @Override
            public void succeeded() {
                List<IMAPMessage> messageList = operation.messages();
                for(IMAPMessage message : messageList){
//                    message.save
                }
            }

            @Override
            public void failed(MailException e) {

            }
        });
    }
}
