package com.cg.autoshell;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.cg.autoshell.event.BackEvent;
import com.cg.autoshell.event.ClickViewByClassNameEvent;
import com.cg.autoshell.event.ClickViewByIdEvent;
import com.cg.autoshell.event.ClickViewByTextEvent;
import com.cg.autoshell.event.ForSearchAllViewEvent;
import com.cg.autoshell.event.ScrollViewByClassNameEvent;
import com.cg.autoshell.event.ScrollViewByIdEvent;
import com.cg.autoshell.event.SetViewTextEvent;
import com.cg.autoshell.rx.RxBus;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chengang on 2017/11/13.
 */

public class PushHandler {
    private static final String TAG = "PushHandler";


    private PushHandler() {
    }


    private int maxCount = 100;


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                if (getPushData().size() > 0) {

                    String now = getPushData().get(0);
                    String time = getJson(now, "time");

                    if (time.length() <= 0) {
                        delayTime = 1500;
                    } else {
                        delayTime = Long.parseLong(time);
                    }
                    handData(now);

                    getPushData().remove(0);
                    if (getPushData().size() > 0) {
                        handler.sendEmptyMessageDelayed(1, delayTime);
                    }


                }

            }
        }
    };

    private long delayTime = 1500;

    private String getJson(String json, String key) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (jsonObject != null) {
            return jsonObject.optString(key);
        }
        return "";
    }

    private ArrayList<String> pushData = new ArrayList<>();

    public void start() {
        handler.sendEmptyMessage(1);
    }


    public static PushHandler getManager() {
        return SingleJumpPageManager.instance;
    }

    private static class SingleJumpPageManager {
        static PushHandler instance = new PushHandler();
    }

    private void handData(String json) {

        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(json);
            String type = jsonObject.optString("type");
            if (TextUtils.isEmpty(type)) {
                type = jsonObject.optString("page_type");
            }
            Log.e(TAG, "type" + type);
            String data = jsonObject.optString("data");
            Log.e(TAG, "data" + data);

            switch (type) {
                default:
                    break;
                case "click_by_text":
                    Log.e(TAG, "post click_by_text");

                    RxBus.getInstance().post(new ClickViewByTextEvent(data));
                    break;
                case "scroll_by_id":
                    Log.e(TAG, "post scroll_by_id");

                    String dir = jsonObject.optString("dir");
                    RxBus.getInstance().post(new ScrollViewByIdEvent(data, dir));
                    break;
                case "click_by_id":
                    Log.e(TAG, "post click_by_id");

                    RxBus.getInstance().post(new ClickViewByIdEvent(data));
                    break;
                case "set_view_text":
                    Log.e(TAG, "post set_view_text");

                    String viewText = jsonObject.optString("view_text");
                    RxBus.getInstance().post(new SetViewTextEvent(data, viewText));
                    break;
                case "forsearch":
                    RxBus.getInstance().post(new ForSearchAllViewEvent());
                    break;
                case "click_by_classname":
                    RxBus.getInstance().post(new ClickViewByClassNameEvent(data));
                    break;
                case "scroll_by_classname":
                    String scrollByClassnameDir = jsonObject.optString("dir");
                    RxBus.getInstance().post(new ScrollViewByClassNameEvent(data, scrollByClassnameDir));
                    break;
                case "back":
                    RxBus.getInstance().post(new BackEvent());
                    break;
                case "startapp":
                    doStartApplicationWithPackageName(data);
                    break;


            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private ArrayList<String> getPushData() {
        if (pushData == null) {
            pushData = new ArrayList<>();
        }
        return pushData;
    }

    public void handReceiveData(String receive) {
        boolean needPush = false;
        if (getPushData().size() == 0) {
            needPush = true;
        }
        if (receive.contains("clear_shell_array")) {
            getPushData().clear();
            return;
        }
        if (receive.contains("run_count")) {
            try {
                JSONObject jsonObject = new JSONObject(receive);
                maxCount = jsonObject.optInt("run_count", 0);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return;
        }

        for (int i = 0; i < maxCount; i++) {
            String[] jsonArray = receive.split(";");
            for (String aJsonArray : jsonArray) {
                getPushData().add(aJsonArray);
            }
        }
        if (needPush) {
            start();
        }
    }

    private void doStartApplicationWithPackageName(String packagename) {

        // 通过包名获取此APP详细信息，包括Activities、services、versioncode、name等等
        PackageInfo packageinfo = null;
        try {
            packageinfo = BaseApp.getAppContext().getPackageManager().getPackageInfo(packagename, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageinfo == null) {
            return;
        }

        // 创建一个类别为CATEGORY_LAUNCHER的该包名的Intent
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(packageinfo.packageName);

        // 通过getPackageManager()的queryIntentActivities方法遍历
        List<ResolveInfo> resolveinfoList = BaseApp.getAppContext().getPackageManager()
                .queryIntentActivities(resolveIntent, 0);

        ResolveInfo resolveinfo = resolveinfoList.iterator().next();
        if (resolveinfo != null) {
            // packagename = 参数packname
            String packageName = resolveinfo.activityInfo.packageName;
            // 这个就是我们要找的该APP的LAUNCHER的Activity[组织形式：packagename.mainActivityname]
            String className = resolveinfo.activityInfo.name;
            // LAUNCHER Intent
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // 设置ComponentName参数1:packagename参数2:MainActivity路径
            ComponentName cn = new ComponentName(packageName, className);

            intent.setComponent(cn);
            BaseApp.getAppContext().startActivity(intent);
        }
    }

}
