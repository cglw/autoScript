package com.cg.autoshell.rx;

import rx.functions.Action1;

/**
 * Created by chengang on 2017/8/4.
 */

public abstract class RxAction1<T> implements Action1<T> {
    private static final String TAG = "RxAction1";

    @Override
    public void call(T t) {
        try {
            callback(t);
        } catch (Exception ee) {
        }
    }

    public abstract void callback(T t);
}
