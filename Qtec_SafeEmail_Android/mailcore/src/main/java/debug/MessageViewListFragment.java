package debug;

import android.app.Activity;
import android.os.Bundle;
import android.app.ListFragment;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.util.Log;

import com.gongw.mailcore.account.Account;
import com.gongw.mailcore.account.AccountModel;
import com.gongw.mailcore.contact.Contact;
import com.gongw.mailcore.folder.FolderModel;
import com.gongw.mailcore.folder.LocalFolder;
import com.gongw.mailcore.message.LocalMessage;
import com.gongw.mailcore.message.MessageBuilder;
import com.gongw.mailcore.message.MessageContact;
import com.gongw.mailcore.message.MessageModel;
import com.gongw.mailcore.net.MessageSender;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


/**
 * A list fragment representing a list of MessagesViews. This fragment
 * also supports tablet devices by allowing list items to be given an
 * 'activated' state upon selection. This helps indicate which item is
 * currently being viewed in a {@link MessageViewDetailFragment}.
 * <p/>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class MessageViewListFragment extends ListFragment {

    /**
     * The serialization (saved instance state) Bundle key representing the
     * activated item position. Only used on tablets.
     */
    private static final String STATE_ACTIVATED_POSITION = "activated_position";

    /**
     * The fragment's current callback object, which is notified of list item
     * clicks.
     */
    private Callbacks mCallbacks = sDummyCallbacks;

    /**
     * The current activated item position. Only used on tablets.
     */
    private int mActivatedPosition = ListView.INVALID_POSITION;

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callbacks {
        /**
         * Callback for when an item has been selected.
         */
        void onItemSelected(LocalMessage msg);
    }

    /**
     * A dummy implementation of the {@link Callbacks} interface that does
     * nothing. Used only when this fragment is not attached to an activity.
     */
    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void onItemSelected(LocalMessage msg) {
        }
    };

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MessageViewListFragment() {
    }

    private ArrayAdapter<MessageAdapter> adapter;
    private java.util.List<LocalMessage> messages;
    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new Thread(){
            @Override
            public void run() {
                Account account =  AccountModel.singleInstance().getAccountsByEmail("gongw@qtec.cn").get(0);

                try {
                    List<LocalFolder> folderList = FolderModel.singleInstance().getFolders(account);
                    LocalFolder inboxFolder = new LocalFolder();
                    inboxFolder.setFullName("INBOX");
                    for(LocalFolder localFolder : folderList){
                        if(localFolder.getFullName().equals("INBOX")){
                            inboxFolder = localFolder;
                            break;
                        }
                    }
                    messages = MessageModel.singleInstance().getMessagesByPage(inboxFolder, 0);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            updateResult();
                        }
                    });


//                    Address[] to = new Address[]{new InternetAddress("gongw@qtec.cn", "龚伟")};
//                    MimeMessage message = new MessageBuilder().subject()
//                                                                .to(to)
//                                                                .content("测试邮件Text正文")
//                                                                .sender(to[0])
//                                                                .build();
//                    LocalMessage localMessage = new LocalMessage();
//                    localMessage.setSubject("测试javamail发送邮件");
//                    localMessage.setSubject("测试邮件Text正文");
//                    MessageContact messageContact = new MessageContact();
//                    messageContact.setLocalMessage(localMessage);
//                    messageContact.setContact(new Contact("gongw@qtec.cn", "gongw"));
//                    List<MessageContact> tos = new ArrayList<>();
//                    tos.add(messageContact);
//                    localMessage.setRecipientsTo(tos);
//                    MessageModel.singleInstance().sendMessage(localMessage);

                } catch (MessagingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Restore the previously serialized activated item position.
        if (savedInstanceState != null
                && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // Reset the active callbacks interface to the dummy implementation.
        mCallbacks = sDummyCallbacks;
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);

        // Notify the active callbacks interface (the activity, if the
        // fragment is attached to one) that an item has been selected.
//        mCallbacks.onItemSelected(DummyContent.ITEMS.get(position).id);
        Log.d("msglist", "row clicked: " + id);
        mCallbacks.onItemSelected(messages.get((int) id));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            // Serialize and persist the activated item position.
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
    }

    /**
     * Turns on activate-on-click mode. When this mode is on, list items will be
     * given the 'activated' state when touched.
     */
    public void setActivateOnItemClick(boolean activateOnItemClick) {
        // When setting CHOICE_MODE_SINGLE, ListView will automatically
        // give items the 'activated' state when touched.
        getListView().setChoiceMode(activateOnItemClick
                ? ListView.CHOICE_MODE_SINGLE
                : ListView.CHOICE_MODE_NONE);
    }

    private void setActivatedPosition(int position) {
        if (position == ListView.INVALID_POSITION) {
            getListView().setItemChecked(mActivatedPosition, false);
        } else {
            getListView().setItemChecked(position, true);
        }

        mActivatedPosition = position;
    }

    private void updateResult() {
        ArrayList<MessageAdapter> array = new ArrayList();
        for(LocalMessage msg : messages) {
            MessageAdapter msgAdapter = new MessageAdapter(msg);
            array.add(msgAdapter);
        }
        adapter = new ArrayAdapter<MessageAdapter>(
                getActivity(),
                android.R.layout.simple_list_item_activated_1,
                android.R.id.text1,
                array);

        setListAdapter(adapter);

    }
}
