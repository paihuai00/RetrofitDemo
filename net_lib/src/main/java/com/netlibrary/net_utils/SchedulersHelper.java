package com.netlibrary.net_utils;

import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import org.reactivestreams.Publisher;

/**
 * create by cuishuxiang
 *
 * @date : 2019/1/25
 * @description: 切换线程
 */
public class SchedulersHelper {
    /**
     * 线程统一处理
     *
     * @param <T>
     * @return
     */
    public static <T> ObservableTransformer<T, T> changeSchedulerObservable() {

        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(Observable<T> upstream) {
                return upstream.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    /**
     * 线程统一处理(背压)
     *
     * @param <T>
     * @return
     */
    public static <T> FlowableTransformer<T, T> changeSchedulerFlowable() {
        return new FlowableTransformer<T, T>() {
            @Override
            public Publisher<T> apply(Flowable<T> upstream) {
                return upstream.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .unsubscribeOn(Schedulers.io());
            }
        };
    }

}
