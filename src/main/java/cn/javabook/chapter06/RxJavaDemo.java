package cn.javabook.chapter06;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.*;
import io.reactivex.rxjava3.disposables.Disposable;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class RxJavaDemo {
    public static void main(String[] args) {
        // 第一种：RxJava 1.x不支持背压
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> emitter) {
                emitter.onNext("Hello");
                emitter.onNext("RxJava");
                emitter.onNext("Observable");
                emitter.onComplete();
            }
        }).subscribe(
                new Observer<String>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable disposable) {
                        // 停止产生数据的信号，为遵循Reactive Streams，避免名字冲突
                        // 将rx.Subscription改名为io.reactivex.disposables.Disposable
                        // disposable.dispose();
                        System.out.println("Disposable: " + disposable.toString());
                    }

                    @Override
                    public void onNext(@NonNull String value) {
                        System.out.println("onNext: " + value);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        System.out.println("onError: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        System.out.println("onComplete");
                    }
                });
        // 第二种：RxJava 2.x及以上支持背压
        Flowable.create(new FlowableOnSubscribe<String>() {
                    @Override
                    public void subscribe(@NonNull FlowableEmitter<String> emitter) {
                        emitter.onNext("Hello");
                        emitter.onNext("RxJava");
                        emitter.onNext("Flowable");
                        emitter.onComplete();
                    }
                   // 背压策略
                }, BackpressureStrategy.ERROR)
                // 背压操作符
                //.onBackpressureBuffer()
                // 使被观察者在独立线程执行
                //.subscribeOn(Schedulers.newThread())
                // 使观察者在独立线程执行
                //.observeOn(Schedulers.newThread())
                .subscribe(
                        new Subscriber<String>() {
                            // 观察者设置接收事件的数量，如果不设置接收不到事件
                            @Override
                            public void onSubscribe(@NonNull Subscription subscription) {
                                // 订阅后也可取消订阅，和disposable.dispose()一样
                                // subscription.cancel();
                                subscription.request(2);
                                System.out.println("Subscription: " + subscription.toString());
                            }

                            @Override
                            public void onNext(String value) {
                                System.out.println("onNext: " + value);
                            }

                            @Override
                            public void onError(Throwable throwable) {
                                System.out.println("onError: " + throwable.getMessage());
                            }

                            @Override
                            public void onComplete() {
                                System.out.println("onComplete");
                            }
                        }
                );
    }
}
