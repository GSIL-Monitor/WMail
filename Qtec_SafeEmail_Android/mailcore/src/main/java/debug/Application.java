package debug;

import com.gongw.common.base.BaseApplication;

import org.litepal.LitePal;

/**
 * Created by gongw on 2018/7/10.
 */

public class Application extends BaseApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        LitePal.initialize(getApplicationContext());
    }
}
