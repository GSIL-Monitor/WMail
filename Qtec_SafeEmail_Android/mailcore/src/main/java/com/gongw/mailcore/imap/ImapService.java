package com.gongw.mailcore.imap;

import com.gongw.mailcore.setting.ConnectInfo;
import com.libmailcore.ConnectionLogType;
import com.libmailcore.ConnectionLogger;
import com.libmailcore.IMAPConnectOperation;
import com.libmailcore.IMAPFetchFoldersOperation;
import com.libmailcore.IMAPFetchMessagesOperation;
import com.libmailcore.IMAPFolder;
import com.libmailcore.IMAPFolderInfo;
import com.libmailcore.IMAPFolderInfoOperation;
import com.libmailcore.IMAPMessage;
import com.libmailcore.IMAPSession;
import com.libmailcore.IndexSet;
import com.libmailcore.Log;
import com.libmailcore.MailException;
import com.libmailcore.OperationCallback;

import java.util.List;

/**
 * Created by gongw on 2018/7/16.
 */

public class ImapService {
    private IMAPSession imapSession;
    private ConnectionLogger connectionLogger;
    private IMAPConnectOperation connectOperation;
    private IMAPFetchFoldersOperation fetchFoldersOperation;
    private IMAPFolderInfoOperation folderInfoOperation;
    private IMAPFetchMessagesOperation fetchMessagesByNumberOperation;
    private IMAPFetchMessagesOperation fetchMessagesByUidOperation;

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
        connectOperation = (IMAPConnectOperation) imapSession.connectOperation();
        connectOperation.start(new OperationCallback() {
            @Override
            public void succeeded() {

            }

            @Override
            public void failed(MailException e) {

            }
        });
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
    public void folderInfo(String path){
        if(folderInfoOperation !=null){
            folderInfoOperation.cancel();
        }
        folderInfoOperation = imapSession.folderInfoOperation(path);
        folderInfoOperation.start(new OperationCallback() {
            @Override
            public void succeeded() {
                IMAPFolderInfo folderInfo = folderInfoOperation.info();
            }

            @Override
            public void failed(MailException e) {

            }
        });
    }

    public void fetchMessagesByNumber(String path, int kindFlag, IndexSet indexSet, List<String> extraHeaders){
        if(fetchMessagesByNumberOperation!=null){
            fetchMessagesByNumberOperation.cancel();
        }
        fetchMessagesByNumberOperation = imapSession.fetchMessagesByNumberOperation(path, kindFlag, indexSet);
        fetchMessagesByNumberOperation.setExtraHeaders(extraHeaders);
        fetchMessagesByNumberOperation.start(new OperationCallback() {
            @Override
            public void succeeded() {
                List<IMAPMessage> imapMessages = fetchMessagesByNumberOperation.messages();
            }

            @Override
            public void failed(MailException e) {

            }
        });
    }

    public void fetchMessagesByUid(String path, int kindFlag, IndexSet indexSet, List<String> extraHeaders){
        if(fetchMessagesByUidOperation!=null){
            fetchMessagesByUidOperation.cancel();
        }
        fetchMessagesByUidOperation = imapSession.fetchMessagesByUIDOperation(path, kindFlag, indexSet);
        fetchMessagesByUidOperation.setExtraHeaders(extraHeaders);
        fetchMessagesByUidOperation.start(new OperationCallback() {
            @Override
            public void succeeded() {
                List<IMAPMessage> imapMessages = fetchMessagesByUidOperation.messages();
            }

            @Override
            public void failed(MailException e) {

            }
        });
    }

}
