package com.cg.autoshell.rx;

import android.os.Handler;
import android.util.Log;

import java.util.Map;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * Created by Eric on 2017/1/20.
 */

public class RxBus {

    private static final String TAG = "RxBus";

    public static void cancelSubscription(Subscription subscription) {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }

    private static volatile RxBus sRxBus;
    // 主题
    private final Subject<Object, Object> mBus;

    // PublishSubject只会把在订阅发生的时间点之后来自原始Observable的数据发射给观察者
    public RxBus() {
        mBus = new SerializedSubject<>(PublishSubject.create());
    }

    // 单例RxBus
    public static RxBus getInstance() {
        if (sRxBus == null) {
            synchronized (RxBus.class) {
                if (sRxBus == null) {
                    sRxBus = new RxBus();
                }
            }
        }
        return sRxBus;
    }

    // 提供了一个新的事件
    public void post(Object o) {
        mBus.onNext(o);

    } // 提供了一个新的事件

    public void postDely(final Object o) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mBus.onNext(o);
            }
        }, 1000);

    }
    // 提供了一个新的事件


    // 根据传递的 eventType 类型返回特定类型(eventType)的 被观察者
    public <T> Observable<T> toObservable(Class<T> eventType) {
        return mBus.ofType(eventType);
//        ofType = filter + cast
//        return mBus.filter(new Func1<Object, Boolean>() {
//            @Override
//            public Boolean call(Object o) {
//                return eventType.isInstance(o);
//            }
//        }) .cast(eventType);
    }


    public <T> Observable<T> toObservableMuti(Map<Class<T>, RxAction1<T>> datas) {

        for (Class<T> key : datas.keySet()) {
            Subscription subscribe = getInstance().toObservable(key).subscribe(datas.get(key));
        }
        return null;
    }


    /**
     * 一个默认的订阅方法(不指定类型)
     * 一般来说 如果一个页面有多种消息那么就可以用该方法来订阅而不是指定类型
     *
     * @param next
     * @param error
     * @return
     */
    public Subscription doSubscribe(Action1<Object> next, Action1<Throwable> error) {
        return tObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(next, error);
    }

    public Observable<Object> tObservable() {

        return mBus;
    }

    public Subscription subscribeEvent(final RxCallback... callback) {
        Subscription mSubscription = null;

        for (int i = 0; i < callback.length; i++) {
            final int j = i;
            Log.e(TAG, String.valueOf(callback[i].getT()));
            mSubscription = RxBus.getInstance().toObservable((Class<Object>) callback[i].getT()).subscribe(new RxAction1<Object>() {

                @Override
                public void callback(Object o) {
                    callback[j].back(o);
                }

            });
        }
        return mSubscription;
    }


}
