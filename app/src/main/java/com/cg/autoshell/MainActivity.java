package com.cg.autoshell;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cg.autoshell.event.MessageEvent;
import com.cg.autoshell.rx.RxBus;
import com.cg.autoshell.rx.RxCallback;
import com.cg.autoshell.utils.AppManager;
import com.igexin.sdk.PushManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscription;

public class MainActivity extends AppCompatActivity {
    private WindowManager wm = null;
    private WindowManager.LayoutParams wmParams = null;
    private ViewGroup inflate = null;
    private int statusBarHeight1;

    @BindView(R.id.edit_1)
    EditText editText;
    @BindView(R.id.edit_2)
    EditText editText2;
    @BindView(R.id.edit_3)
    EditText editText3;
    private static final String TAG = "MainActivity";
    private Subscription app;

    public void getcid(View view) {
        editText.setText(PushManager.getInstance().getClientid(this));
//        WebSocketManager.getManager().connect();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PushHandler.getManager().start();
        AppManager.getAppManager().addActivity(this);
        WebSocketManager.getManager().connect();
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        NotificationHelper.create().notifyNoCancel("AutoShell", "Running",
                PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0),
                1);

        String packageNamez = "com.test.aa,com.test.bb";
        if (packageNamez != null) {
            String[] packageNames = packageNamez.split("(\\s)*,(\\s)*");

            for (String a : packageNames) {
                Log.e(TAG, a + "");
            }
        }
        Log.e(TAG, PushManager.getInstance().getClientid(this) + "  client");

        PushManager.getInstance().initialize(this.getApplicationContext(), DemoPushService.class);
        PushManager.getInstance().registerPushIntentService(this.getApplicationContext(), DemoIntentService.class);

        if (!PushManager.getInstance().isPushTurnedOn(BaseApp.getAppContext())) {
            PushManager.getInstance().turnOnPush(BaseApp.getAppContext());
        }
        editText.setText(PushManager.getInstance().getClientid(this));


        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusBarHeight1 = getResources().getDimensionPixelSize(resourceId);
        }
        initFloatView();
        app = RxBus.getInstance().subscribeEvent(new RxCallback<MessageEvent>() {
            @Override
            public void back(MessageEvent obj) {
                if (obj.type.equals("app")) {
                    editText2.setText(obj.msg);
                } else {
                    editText3.setText(obj.msg);
                }

            }
        });
    }


    public void open_setting() {
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivity(intent);

    }

    public void cid(View view) {
        Log.e(TAG, "get cid" + PushManager.getInstance().getClientid(this));
        editText.setText(PushManager.getInstance().getClientid(this));

    }

    public void open(View view) {
        open_setting();
    }


    private void initFloatView() {
        wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        //设置LayoutParams(全局变量）相关参数
        wmParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_TOAST,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT);
        wmParams.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
        //以屏幕左上角为原点，设置x、y初始值
        wmParams.x = 0;
        wmParams.y = 0;
        //设置悬浮窗口长宽数据

        createLeftFloatView();
        inflate.invalidate();
    }

    private void createLeftFloatView() {

        inflate = new LinearLayout(this);
        inflate.setBackgroundColor(Color.BLUE);
        TextView child = new TextView(this);
        child.setClickable(true);
        child.setTextColor(Color.WHITE);
        inflate.addView(child);
        child.setTextSize(10);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) child.getLayoutParams();
        layoutParams.setMargins(dip2px(this, 30), dip2px(this, 30), dip2px(this, 30), dip2px(this, 30));

        child.setText("运行中");
//        View open = inflate.findViewById(R.id.open);
//        EditText editText = (EditText) inflate.findViewById(R.id.edit_1);
//        editTet.setText(PushManager.getInstance().getClientid(this));
//
        child.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Context context=null;
//                if(MainActivity.this!=null)
//                    context=MainActivity.this;
//                else
//                    context=BaseApp.getAppContext();


                Activity activity = AppManager.getAppManager().currentActivity();

                if (activity != null) {
                    Intent intent = new Intent(activity, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    PendingIntent pendingIntent =
                            PendingIntent.getActivity(activity, 0, intent, 0);
                    try {
                        pendingIntent.send();
                    } catch (PendingIntent.CanceledException e) {
                        e.printStackTrace();
                    }
                }

            }
        });

        wm.addView(inflate, wmParams);


        inflate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                // 当前值以屏幕左上角为原点
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mStartX = event.getRawX();
                        mStartY = event.getRawY();
                        break;

                    case MotionEvent.ACTION_MOVE:
                        wmParams.x += event.getRawX() - mStartX;
                        wmParams.y += event.getRawY() - mStartY;
                        wm.updateViewLayout(inflate, wmParams);
                        mStartX = event.getRawX();
                        mStartY = event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        inflate.setAlpha(0.5f);
                        return false;

                }

                // 消耗触摸事件
                return false;

            }
        });
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, double dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private float mStartX, mStartY;


    // To check if service is enabled
    private boolean isAccessibilitySettingsOn(Context mContext) {
        int accessibilityEnabled = 0;
        final String service = "com.cg.autoshell/com.cg.autoshell.AutoService";
        boolean accessibilityFound = false;
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                    mContext.getApplicationContext().getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
            Log.v(TAG, "accessibilityEnabled = " + accessibilityEnabled);
        } catch (Settings.SettingNotFoundException e) {
            Log.e(TAG, "Error finding setting, default accessibility to not found: "
                    + e.getMessage());
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            Log.v(TAG, "***ACCESSIBILIY IS ENABLED*** -----------------");
            String settingValue = Settings.Secure.getString(
                    mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                TextUtils.SimpleStringSplitter splitter = mStringColonSplitter;
                splitter.setString(settingValue);
                while (splitter.hasNext()) {
                    String accessabilityService = splitter.next();

                    Log.v(TAG, "-------------- > accessabilityService :: " + accessabilityService);
                    if (accessabilityService.equalsIgnoreCase(service)) {
                        Log.v(TAG, "We've found the correct setting - accessibility is switched on!");
                        return true;
                    }
                }
            }
        } else {
            Log.v(TAG, "***ACCESSIBILIY IS DISABLED***");
        }

        return accessibilityFound;
    }

    public void checkOpen() {
        if (!isAccessibilitySettingsOn(BaseApp.getAppContext())) {

            Toast.makeText(BaseApp.getAppContext(), "请打开开机辅助!", Toast.LENGTH_LONG).show();

            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(intent);

        } else
            Toast.makeText(BaseApp.getAppContext(), "已经启动了!", Toast.LENGTH_LONG).show();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.cancelSubscription(app);
    }

    public void check(View view) {
        checkOpen();
    }
}
