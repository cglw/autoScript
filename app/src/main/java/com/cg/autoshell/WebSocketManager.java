package com.cg.autoshell;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.cg.autoshell.event.MessageEvent;
import com.cg.autoshell.rx.RxBus;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

/**
 * Created by chengang on 2017/11/15.
 */

public class WebSocketManager {

    private WebSocketClient client;// 连接客户端
    private boolean CONNECT_SUCCESS = false;
    private int CONNECT_COUNT = 0;
    private boolean isConnecting = false;

    public WebSocketManager() {
    }

    public static WebSocketManager getManager() {
        return SingleJumpPageManager.instance;
    }

    private static class SingleJumpPageManager {
        static WebSocketManager instance = new WebSocketManager();
    }

    public void reconnect() {
        if (!isConnecting && !CONNECT_SUCCESS) {
            handler.sendEmptyMessageDelayed(1, 1000);
            isConnecting = true;
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                if (CONNECT_COUNT <= 5 && !CONNECT_SUCCESS) {
                    CONNECT_COUNT++;
                    connect();

                }
            }
        }
    };

    public WebSocketClient getConnect() {
        if (client == null)
            return connect();
        else
            return client;
    }


    public WebSocketClient connect() {
        try {
            client = new WebSocketClient(new URI(AppConfig.SOCKET_ADDRESS), new Draft_17()) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {

                    CONNECT_COUNT = 0;
                    CONNECT_SUCCESS = true;
                    isConnecting = false;
                    postLogin();
                    RxBus.getInstance().post(new MessageEvent("connect success", "socket"));


                }


                @Override
                public void onMessage(final String message) {

                    Log.e(TAG, "handlerReceiveMessage" + message);
                    try {
                        JSONObject jsonObject = new JSONObject(message);

                        if (!jsonObject.optString("type").equals("ping"))
                            PushHandler.getManager().handReceiveData(message);
                        RxBus.getInstance().post(new MessageEvent(message, "socket"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }

                @Override
                public void onClose(final int code, final String reason, boolean remote) {

                    isConnecting = false;
                    CONNECT_SUCCESS = false;
                    //1000是自己主动断开
                    if (code != 1000) {
                        reconnect();
                    }
                    Log.e(TAG, "--?onClose" + reason + "-->" + code);

                }

                @Override
                public void onError(final Exception ex) {
                    isConnecting = false;
                    CONNECT_SUCCESS = false;
                    Log.e(TAG, "onError" + ex.getMessage());


                }
            };
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        client.connect();
        return client;
    }

    private void postLogin() {
        Map<String, String> a = new HashMap();
        a.put("type", "login_shell");
        a.put("uuid", DeviceUuidFactory.getDeviceUuid() + "");
        JSONObject js = new JSONObject(a);
        try {
            Log.e(TAG, "postLogin" + js);
            client.send(js.toString());
        } catch (Exception ee) {

        }
    }

    public void postNormal(String msg) {
        Map<String, String> a = new HashMap();
        a.put("type", "view");
        a.put("content", msg);
        a.put("uuid", DeviceUuidFactory.getDeviceUuid() + "");
        JSONObject js = new JSONObject(a);
        Log.e(TAG, "postNormal" + js);

        try {
            client.send(js.toString());
        } catch (Exception ee) {

        }
    }


}
