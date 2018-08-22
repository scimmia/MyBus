package com.scimmia.mybus.utils.base;

import android.widget.Toast;
import com.scimmia.mybus.utils.eventbus.BusProvider;
import me.yokeyword.fragmentation.SupportFragment;

import java.util.HashMap;

/**
 * Created by lk on 2017/8/16.
 */
public class BaseFragment extends SupportFragment{
    @Override
    public void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }

    private Toast appMsg;
    public void showToast(String message){
        cancelToast();
        appMsg = Toast.makeText(_mActivity, message, Toast.LENGTH_SHORT);
        appMsg.show();

    }
    public void showToast(String message,int duration){
        cancelToast();
        appMsg = Toast.makeText(_mActivity, message, duration);
        appMsg.show();

    }
    public void cancelToast(){
        if (appMsg!=null){
            appMsg.cancel();
        }
    }
}
