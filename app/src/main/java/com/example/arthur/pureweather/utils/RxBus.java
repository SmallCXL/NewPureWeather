package com.example.arthur.pureweather.utils;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * Created by Administrator on 2016/7/1.
 */
public class RxBus {

        private static volatile RxBus defaultInstance;
        // 主题
        private final Subject<Object,Object> BUS;
        // PublishSubject只会把在订阅发生的时间点之后来自原始Observable的数据发射给观察者
        private RxBus() {
            BUS = new SerializedSubject<>(PublishSubject.create());
        }
        // 单例RxBus
        public static RxBus getInstance() {
            RxBus rxBus = defaultInstance;
            if (rxBus == null) {
                synchronized (RxBus.class) {
                    rxBus = defaultInstance;
                    if (rxBus == null) {
                        rxBus = new RxBus();
                        defaultInstance = rxBus;
                    }
                }
            }
            return rxBus;
        }
        // 提供了一个新的事件
        public void post (Object o) {
            BUS.onNext(o);
        }

        // 根据传递的 eventType 类型返回特定类型(eventType)的 被观察者
        public <T> Observable<T> toObserverable (Class<T> eventType) {
            return BUS.ofType(eventType);
        }

}
