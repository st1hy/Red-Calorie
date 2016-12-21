package com.github.st1hy.countthemcalories.core.rx;

import android.support.annotation.NonNull;

import rx.Observable;
import rx.subjects.PublishSubject;


public final class Transformers {

    private Transformers() {
    }

    /**
     * Creates observable transformer that replicates information from source observable onto target
     * subject. Source observable still needs active subscription for this to happen.
     */
    @NonNull
    public static <T> Observable.Transformer<T, T> channel(@NonNull PublishSubject<T> target) {
        return new ChannelTransformer<>(target);
    }


    private static class ChannelTransformer<T> implements Observable.Transformer<T, T> {
        @NonNull
        private final PublishSubject<T> target;

        private ChannelTransformer(@NonNull PublishSubject<T> target) {
            this.target = target;
        }

        @Override
        public Observable<T> call(Observable<T> source) {
            return source.doOnEach(notification -> {
                switch (notification.getKind()) {
                    case OnNext:
                        target.onNext((T) notification.getValue());
                        break;
                    case OnError:
                        target.onError(notification.getThrowable());
                        break;
                    case OnCompleted:
                        target.onCompleted();
                        break;
                }
            });
        }
    }

}
