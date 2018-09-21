package debug;

import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.util.Log;

import com.gongw.mailcore.R;
import com.gongw.mailcore.message.LocalMessage;
import com.gongw.mailcore.part.LocalPart;
import com.gongw.mailcore.part.PartModel;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import javax.mail.MessagingException;


/**
 * A fragment representing a single MessageView detail screen.
 * This fragment is either contained in a {@link MessageViewListActivity}
 * in two-pane mode (on tablets) or a {@link MessageViewDetailActivity}
 * on handsets.
 */
public class MessageViewDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    //private DummyContent.DummyItem mItem;
    private LocalMessage message;

    private Handler handler = new Handler(Looper.getMainLooper());

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MessageViewDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = DummyContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));
        }
        */
        message = MessagesSyncManager.singleton().currentMessage;
    }

    private WebView webView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_messageview_detail, container, false);
        webView = (WebView) rootView.findViewById(R.id.messageview_detail);

        if (message != null) {
            Log.d("detail", "message: " + message);
            new Thread(){
                @Override
                public void run() {
                    try {
                        List<LocalPart> contentParts = PartModel.singleInstance().getContentParts(message);
                        LocalPart htmlPart = contentParts.get(0);
                        FileInputStream fis = new FileInputStream(htmlPart.getLocalPath());
                        byte[] datas = new byte[fis.available()];
                        fis.read(datas);
                        final String html = new String(datas);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                webView.loadData(html, "text/html", "utf-8");
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (MessagingException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }

        return rootView;
    }


}
