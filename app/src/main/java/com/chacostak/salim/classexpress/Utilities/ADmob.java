package com.chacostak.salim.classexpress.Utilities;

import android.content.Context;
import android.widget.LinearLayout;

import com.google.android.gms.ads.*;

/**
 * Created by Salim on 24/01/2015.
 */
public class ADmob {

    public AdView ad;

    public ADmob(Context activity, String myId){
        ad = new AdView(activity);
        ad.setAdUnitId(myId);
    }

    public void addBanner(LinearLayout layout){
        ad.setAdSize(AdSize.BANNER);
        layout.addView(ad);
        request();
    }

    public void addSmartBanner(LinearLayout layout){
        ad.setAdSize(AdSize.SMART_BANNER);
        layout.addView(ad);
        request();
    }

    private void request(){
        AdRequest request = new AdRequest.Builder().addTestDevice("50532991B70E417159423E75F29A6208").addTestDevice("02781130887CE55333D99B0A67FB5F26").build();
        ad.loadAd(request);
    }


}
