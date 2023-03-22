package com.jew.tech.bindview;

import android.app.Activity;

public class BinderView {

    public static void inject(Activity activity) {
        String className = activity.getClass().getName() + "_ViewBinding";
        try {
            IBindView iBindView = (IBindView) Class.forName(className).newInstance();
            iBindView.inject(activity);
        } catch (Exception e) {

        }
    }

}
