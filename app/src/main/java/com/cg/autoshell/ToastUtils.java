package com.cg.autoshell;

import android.widget.Toast;

/**
 * Created by chengang on 2017/11/9.
 */

public class ToastUtils {
    public static void showToastShort(String content) {
        Toast.makeText(BaseApp.getAppContext(), content, Toast.LENGTH_SHORT).show();
    }


    public static void showToastShort(int res) {
        Toast.makeText(BaseApp.getAppContext(), BaseApp.getAppContext().getString(res), Toast.LENGTH_SHORT).show();
    }

}
