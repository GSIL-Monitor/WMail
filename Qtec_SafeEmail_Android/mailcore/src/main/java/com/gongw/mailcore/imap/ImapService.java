package com.gongw.mailcore.imap;


import com.gongw.mailcore.setting.ConnectInfo;
import com.libmailcore.ConnectionLogger;
import com.libmailcore.IMAPAppendMessageOperation;
import com.libmailcore.IMAPCopyMessagesOperation;
import com.libmailcore.IMAPFetchContentOperation;
import com.libmailcore.IMAPFetchFoldersOperation;
import com.libmailcore.IMAPFetchMessagesOperation;
import com.libmailcore.IMAPFolder;
import com.libmailcore.IMAPFolderInfoOperation;
import com.libmailcore.IMAPOperation;
import com.libmailcore.IMAPOperationProgressListener;
import com.libmailcore.IMAPSession;
import com.libmailcore.IMAPStoreFlagsRequestKind;
import com.libmailcore.IndexSet;
import com.libmailcore.Log;
import com.libmailcore.MailException;
import com.libmailcore.MessageFlag;
import com.libmailcore.OperationCallback;
import java.util.List;

/**
 * Created by gongw on 2018/7/16.
 */

public class ImapService {
    public IMAPSession imapSession;
    private ConnectionLogger connectionLogger;
    private IMAPOperation connectOperation;
    private IMAPOperation checkAccountOperation;
    private IMAPFetchFoldersOperation fetchFoldersOperation;
    private IMAPFolderInfoOperation folderInfoOperation;
    private IMAPFetchMessagesOperation fetchMessagesByNumberOperation;
    private IMAPOperation storeFlagsByUidOperation;
    private IMAPCopyMessagesOperation copyMessagesOperation;
    private IMAPOperation expungeOperation;
    private IMAPAppendMessageOperation appendMessageOperation;
    private IMAPFetchContentOperation fetchAttachmentByUIDOperation;

    public ImapService(ConnectInfo connectInfo){
        imapSession = new IMAPSession();
        imapSession.setUsername(connectInfo.getEmail());
        imapSession.setPassword(connectInfo.getPwd());
        imapSession.setHostname(connectInfo.getServerHost());
        imapSession.setPort(connectInfo.getServerPort());
        imapSession.setConnectionType(connectInfo.getConnectType().getValue());
        imapSession.setConnectionLogger(new ConnectionLogger() {
            @Override
            public void log(long l, int i, byte[] bytes) {
                //TODO:使用统一管理的LOG工具
            }
        });
        Log.setEnabled(true);
    }

    /**
     * 设置邮箱IMAP服务日志开关
     * @param enabled
     */
    public void setLogEnabled(boolean enabled){
        Log.setEnabled(enabled);
    }

    /**
     * 与邮箱IMAP服务器建立连接
     */
    public void connect(){
        if(connectOperation!=null){
            connectOperation.cancel();
        }
        disconnect();
        connectOperation = imapSession.connectOperation();
        connectOperation.start(new OperationCallback() {
            @Override
            public void succeeded() {

            }

            @Override
            public void failed(MailException e) {

            }
        });
    }

    public void checkAccount(OperationCallback callback){
        if(checkAccountOperation != null){
            checkAccountOperation.cancel();
        }
        checkAccountOperation = imapSession.checkAccountOperation();
        checkAccountOperation.start(callback);
    }

    /**
     * 与邮箱IMAP服务器断开连接
     */
    public void disconnect(){
        imapSession.cancelAllOperations();
    }

    /**
     * 获取邮箱的所有文件夹
     */
    public void fetchFolders(){
        if(fetchFoldersOperation !=null){
            fetchFoldersOperation.cancel();
        }
        fetchFoldersOperation = imapSession.fetchAllFoldersOperation();
        fetchFoldersOperation.start(new OperationCallback() {
            @Override
            public void succeeded() {
                List<IMAPFolder> imapFolders = fetchFoldersOperation.folders();
            }

            @Override
            public void failed(MailException e) {

            }
        });
    }

    /**
     * 获取邮箱指定文件夹的邮件数、未读数等信息
     * @param path 文件夹路径
     */
    public void folderInfo(String path, OperationCallback callback){
        if(folderInfoOperation !=null){
            folderInfoOperation.cancel();
        }
        folderInfoOperation = imapSession.folderInfoOperation(path);
        folderInfoOperation.start(callback);
    }

    /**
     * 按位置序号获取邮件头
     * @param path 文件夹路径
     * @param messagesRequestKind 邮件头包含的数据种类
     * @param indexSet 位置序号集合
     * @param extraHeaders 额外的邮件头
     * @param callback 回调接口
     */
    public void fetchMessagesByNumber(String path, int messagesRequestKind, IndexSet indexSet, List<String> extraHeaders, OperationCallback callback){
        if(fetchMessagesByNumberOperation!=null){
            fetchMessagesByNumberOperation.cancel();
        }
        fetchMessagesByNumberOperation = imapSession.fetchMessagesByNumberOperation(path, messagesRequestKind, indexSet);
        fetchMessagesByNumberOperation.setExtraHeaders(extraHeaders);
        fetchMessagesByNumberOperation.start(callback);
    }

    public void flagMessage(String path, IndexSet uids, int flagsRequestKind, int messageFlag, OperationCallback callback){
        if(storeFlagsByUidOperation != null){
            storeFlagsByUidOperation.cancel();
        }
        storeFlagsByUidOperation = imapSession.storeFlagsByUIDOperation(path, uids, flagsRequestKind, messageFlag);
        storeFlagsByUidOperation.start(callback);
    }

    public void copyMessages(String path, IndexSet uids, String destPath, OperationCallback callback){
        if(copyMessagesOperation != null){
            copyMessagesOperation.cancel();
        }
        copyMessagesOperation = imapSession.copyMessagesOperation(path, uids, destPath);
        copyMessagesOperation.start(callback);
    }

    public void deleteMessages(final String path, IndexSet uids, final OperationCallback callback){
        flagMessage(path, uids, IMAPStoreFlagsRequestKind.IMAPStoreFlagsRequestKindAdd, MessageFlag.MessageFlagDeleted, new OperationCallback() {
            @Override
            public void succeeded() {
                if(expungeOperation != null){
                    expungeOperation.cancel();
                }
                expungeOperation = imapSession.expungeOperation(path);
                expungeOperation.start(callback);
            }

            @Override
            public void failed(MailException e) {
                if(callback != null){
                    callback.failed(e);
                }
            }
        });

    }

    public void moveMessages(final String path, final IndexSet uids, String destPath, final OperationCallback callback){
        copyMessages(path, uids, destPath, new OperationCallback(){

            @Override
            public void succeeded() {
                deleteMessages(path, uids, callback);
            }

            @Override
            public void failed(MailException e) {
                if(callback != null){
                    callback.failed(e);
                }
            }
        });
    }

    public void appendMessage(String path, byte[] bytes, int messageFlags, OperationCallback callback){
        if(appendMessageOperation != null){
            appendMessageOperation.cancel();
        }
        appendMessageOperation = imapSession.appendMessageOperation(path, bytes, messageFlags);
        appendMessageOperation.start(callback);
    }

    public void fetchAttachmentByUIDOperation(String path, int uid, String partId, int encoding, OperationCallback callback){
        if(fetchAttachmentByUIDOperation != null){
            fetchAttachmentByUIDOperation.cancel();
        }
        fetchAttachmentByUIDOperation = imapSession.fetchMessageAttachmentByUIDOperation(path, uid, partId, encoding);
        fetchAttachmentByUIDOperation.setProgressListener(new IMAPOperationProgressListener() {
            @Override
            public void bodyProgress(long current, long maximum) {

            }
        });
        fetchAttachmentByUIDOperation.start(callback);
    }


}
