package com.cg.autoshell;

import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.cg.autoshell.event.BackEvent;
import com.cg.autoshell.event.ClickViewByClassNameEvent;
import com.cg.autoshell.event.ClickViewByIdEvent;
import com.cg.autoshell.event.ClickViewByTextEvent;
import com.cg.autoshell.event.ForSearchAllViewEvent;
import com.cg.autoshell.event.ScrollViewByClassNameEvent;
import com.cg.autoshell.event.ScrollViewByIdEvent;
import com.cg.autoshell.event.SetViewTextEvent;
import com.cg.autoshell.rx.RxBus;
import com.cg.autoshell.rx.RxCallback;

import java.util.List;

/**
 * Created by chengang on 2017/11/9.
 */

public class AutoService extends AccessibilityService {

    private String prettyyes = "com.prettyyes.user";
    private String rongyun = "io.rong.imkit";
    private static final String TAG = "AutoService";
    public String control_text = "发送";
    private String send_msg;


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "AutoService onCreate");
        RxBus.getInstance().subscribeEvent(
                new RxCallback<ClickViewByClassNameEvent>() {
                    @Override
                    public void back(ClickViewByClassNameEvent obj) {
                        Log.e(TAG, "clickViewByClassName--->" + obj.className);
                        clickViewByClassName(getRootInActiveWindow(), obj.className);
                        getRootInActiveWindow().recycle();
                    }
                },
                new RxCallback<ClickViewByTextEvent>() {
                    @Override
                    public void back(ClickViewByTextEvent obj) {
                        clickViewByText(obj.text);
                        getRootInActiveWindow().recycle();

                    }
                }, new RxCallback<ScrollViewByIdEvent>() {
                    @Override
                    public void back(ScrollViewByIdEvent obj) {
                        scrollViewById(obj.id, obj.dir);

                    }
                }, new RxCallback<ClickViewByIdEvent>() {
                    @Override
                    public void back(ClickViewByIdEvent obj) {
                        operaViewById(obj.id);
                        getRootInActiveWindow().recycle();
                    }
                }, new RxCallback<SetViewTextEvent>() {
                    @Override
                    public void back(SetViewTextEvent obj) {
                        setViewText(obj.id, obj.text);
                    }
                }, new RxCallback<ForSearchAllViewEvent>() {
                    @Override
                    public void back(ForSearchAllViewEvent obj) {
                        if (getRootInActiveWindow() != null) {
                            Log.e(TAG, "start rootNode");
                            forsearch(getRootInActiveWindow());
                            getRootInActiveWindow().recycle();

                        }
                    }
                }, new RxCallback<ScrollViewByClassNameEvent>() {
                    @Override
                    public void back(ScrollViewByClassNameEvent obj) {
                        scorllviewByClassName(getRootInActiveWindow(), obj.className, obj.dir);
                    }
                }, new RxCallback<BackEvent>() {
                    @Override
                    public void back(BackEvent obj) {
                        Log.e(TAG, "back" + "performGlobalAction(GLOBAL_ACTION_BACK);\n");
                        performGlobalAction(GLOBAL_ACTION_BACK);
                        getRootInActiveWindow().recycle();
                    }
                });
    }


    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        int eventType = event.getEventType();
        String eventTypeName = "";

        switch (eventType) {
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                eventTypeName = "TYPE_WINDOW_CONTENT_CHANGED";
                break;
            case AccessibilityEvent.TYPE_VIEW_CLICKED:
                eventTypeName = "TYPE_VIEW_CLICKED";
                break;
            case AccessibilityEvent.TYPE_VIEW_FOCUSED:
                eventTypeName = "TYPE_VIEW_FOCUSED";
                break;
            case AccessibilityEvent.TYPE_VIEW_LONG_CLICKED:
                eventTypeName = "TYPE_VIEW_LONG_CLICKED";
                break;
            case AccessibilityEvent.TYPE_VIEW_SELECTED:
                eventTypeName = "TYPE_VIEW_SELECTED";
                break;
            case AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED:
                eventTypeName = "TYPE_VIEW_TEXT_CHANGED";
                break;
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                eventTypeName = "TYPE_WINDOW_STATE_CHANGED";
                break;
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                eventTypeName = "TYPE_NOTIFICATION_STATE_CHANGED";
                break;
            case AccessibilityEvent.TYPE_TOUCH_EXPLORATION_GESTURE_END:
                eventTypeName = "TYPE_TOUCH_EXPLORATION_GESTURE_END";
                break;
            case AccessibilityEvent.TYPE_ANNOUNCEMENT:
                eventTypeName = "TYPE_ANNOUNCEMENT";
                break;
            case AccessibilityEvent.TYPE_TOUCH_EXPLORATION_GESTURE_START:
                eventTypeName = "TYPE_TOUCH_EXPLORATION_GESTURE_START";
                break;
            case AccessibilityEvent.TYPE_VIEW_HOVER_ENTER:
                eventTypeName = "TYPE_VIEW_HOVER_ENTER";
                break;
            case AccessibilityEvent.TYPE_VIEW_HOVER_EXIT:
                eventTypeName = "TYPE_VIEW_HOVER_EXIT";
                break;
            case AccessibilityEvent.TYPE_VIEW_SCROLLED:
                eventTypeName = "TYPE_VIEW_SCROLLED";
                break;
            case AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED:
                eventTypeName = "TYPE_VIEW_TEXT_SELECTION_CHANGED";
                break;

        }
//        Log.i(TAG, "eventType:" + eventType);
//        Log.i(TAG, "eventTypeName:" + eventTypeName);

//        if (eventTypeName.equals("TYPE_WINDOW_STATE_CHANGED")) {
//            PushHandler.getManager().handler.sendEmptyMessageDelayed(1, PushHandler.getManager().delay_time);
//        }
//        send_msg = "";
//        forsearch(getRootInActiveWindow());
//        WebSocketManager.getManager().postNormal(send_msg);
//        RxBus.getInstance().post(new MessageEvent(send_msg, "app"));
//        send_msg = "";
    }


    /**
     * 查找到
     */
    @SuppressLint("NewApi")
    private void operaViewById(String id) {
        Log.e(TAG, "operaViewById" + id);
        if (getRootInActiveWindow() != null) {
            List<AccessibilityNodeInfo> list = getRootInActiveWindow()
                    .findAccessibilityNodeInfosByViewId(id);

            for (AccessibilityNodeInfo n : list) {
                if (n.isClickable()) {
                    n.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    break;
                }

            }
        }

    }

//    @Override
//    protected void onServiceConnected() {
//        AccessibilityServiceInfo info = getServiceInfo();
//        info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
//        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
//        info.flags = AccessibilityServiceInfo.DEFAULT;
//        info.notificationTimeout = 100;
//        setServiceInfo(info);
//        info.packageNames = new String[]{prettyyes, rongyun};
//        setServiceInfo(info);
//        super.onServiceConnected();
//    }

    @SuppressLint("NewApi")
    private void setViewText(String id, String text) {
        Log.e(TAG, "setViewText" + id + "-->" + text);
        if (getRootInActiveWindow() != null) {
            List<AccessibilityNodeInfo> list = getRootInActiveWindow()
                    .findAccessibilityNodeInfosByViewId(id);

            for (AccessibilityNodeInfo n : list) {
                Log.e(TAG, "find view");

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Bundle arguments = new Bundle();
                    arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
                            text);
                    n.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
                } else {
                    ClipboardManager clipboard = (ClipboardManager) getBaseContext().getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("text", text);
                    clipboard.setPrimaryClip(clip);
                    n.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
                    n.performAction(AccessibilityNodeInfo.ACTION_PASTE);
                }
                break;

            }
            List<AccessibilityNodeInfo> imlist = getRootInActiveWindow()
                    .findAccessibilityNodeInfosByViewId(id);

            for (AccessibilityNodeInfo n : imlist) {
                Log.e(TAG, "find view");
                n.setText(text);
                break;
            }

        }

    }

    @SuppressLint("NewApi")
    private void scrollViewById(String id, String dir) {
        Log.e(TAG, "scrollViewById" + id + "-->" + dir);

        if (getRootInActiveWindow() != null) {
            List<AccessibilityNodeInfo> list = getRootInActiveWindow()
                    .findAccessibilityNodeInfosByViewId(id);

            for (AccessibilityNodeInfo n : list) {
                if (n.isScrollable()) {
                    if ("backward".equals(dir)) {
                        n.performAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD);
                    } else {
                        n.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);

                    }
                    break;
                }


            }
        }

    }

    @SuppressLint("NewApi")
    private void clickViewByText(String txt) {
        if (getRootInActiveWindow() == null) {
            return;
        }
        Log.e(TAG, "clickViewByText" + getRootInActiveWindow() + "-->" + txt);

        clickViewByText(getRootInActiveWindow(), txt);
    }

    /**
     * 打印一个节点的结构
     *
     * @param info
     */
    @SuppressLint("NewApi")
    public void clickViewByText(AccessibilityNodeInfo info, String target) {
        if (info == null) {
            return;
        }
        Log.e(TAG, info.getClassName() + "" + target);
        if (info.getChildCount() == 0) {
            if (info.getText() != null) {
                try {
                    Log.e(TAG, "child" + info.getText().toString());
                    Log.e(TAG, "target" + target);
                    Log.e(TAG, info.getText().toString().equals(target) + "");


                    if (info.getText().toString().equals(target)) {
                        Log.e(TAG, info.getText().toString());
                        info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        AccessibilityNodeInfo parent = info.getParent();
                        while (parent != null) {
                            Log.i("demo", "parent isClick:" + parent.isClickable());
                            if (parent.isClickable()) {
                                parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                break;
                            }
                            parent = parent.getParent();
                        }

                    }

                } catch (Exception ee) {

                }

            }

        } else {
            for (int i = 0; i < info.getChildCount(); i++) {
                if (info.getChild(i) != null) {
                    clickViewByText(info.getChild(i), target);
                }
            }
        }
    }

    /**
     * 打印一个节点的结构
     *
     * @param info
     */
    @SuppressLint("NewApi")
    public void forsearch(AccessibilityNodeInfo info) {
        if (info == null) {
            return;
        }

        if (info.getChildCount() == 0) {

            send_msg += info.getClassName() + ";" + info.getViewIdResourceName();
            Log.e(TAG, "child" + info.getClassName() + "-->" +
                    info.getViewIdResourceName() + "-->"
            );
            if (info.getText() != null) {
                Log.e(TAG, info.getText().toString());
                send_msg += info.getClassName() + ";" + info.getViewIdResourceName() + info.getText().toString();

            }
            send_msg += "\n";

        } else {
            send_msg += info.getClassName() + info.getViewIdResourceName() + info.getChildCount() + "\n";
            Log.e(TAG, "parent" + info.getClassName() + "-->" + info.getViewIdResourceName() + "-->" + info.getChildCount() + "-->");
            for (int i = 0; i < info.getChildCount(); i++) {
                if (info.getChild(i) != null) {
                    forsearch(info.getChild(i));
                }
            }
        }
    }

    @SuppressLint("NewApi")
    public void scorllviewByClassName(AccessibilityNodeInfo info, String className, String dir) {
        if (info == null) {
            return;
        }

        if (info.getChildCount() == 0) {
            if (info.getClassName().toString().endsWith(className) && info.isScrollable()) {
                if ("backward".equals(dir)) {
                    info.performAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD);
                } else {
                    info.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);

                }
                return;

            }

        } else {
            if (info.getClassName().toString().endsWith(className) && info.isScrollable()) {
                if ("backward".equals(dir)) {
                    info.performAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD);
                } else {
                    info.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
                }
                return;

            }

            for (int i = 0; i < info.getChildCount(); i++) {
                if (info.getChild(i) != null) {
                    scorllviewByClassName(info.getChild(i), className, dir);
                }
            }
        }
    }

    @SuppressLint("NewApi")
    public void clickViewByClassName(AccessibilityNodeInfo info, String className) {
        if (info == null) {
            return;
        }
        if (info.getClassName().toString().endsWith(className) && info.isClickable()) {

            Log.e(TAG, "click" + info.getText() + info.getViewIdResourceName());
            info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            return;
        }
        if (info.getChildCount() == 0) {
            if (info.getClassName().toString().endsWith(className) && info.isClickable()) {
                Log.e(TAG, "click" + info.getText() + info.getViewIdResourceName());
                info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                return;
            }
        } else {
            for (int i = 0; i < info.getChildCount(); i++) {
                if (info.getClassName().toString().endsWith(className)) {
                    if (info.isClickable()) {
                        Log.e(TAG, "click" + info.getText() + info.getChild(i).getViewIdResourceName());
                        info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        return;
                    } else if (info.getChild(i).isClickable()) {
                        Log.e(TAG, "click" + info.getChild(i).getText() + info.getChild(i).getViewIdResourceName());
                        info.getChild(i).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        return;
                    }
                }


                if (info.getChild(i) != null) {
                    clickViewByClassName(info.getChild(i), className);
                }
            }
        }
    }

    @Override
    public void onInterrupt() {

    }


}
