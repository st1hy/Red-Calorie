package com.github.st1hy.countthemcalories.core.rx;

import android.support.annotation.NonNull;

import rx.Observable;
import rx.subjects.Subject;


public final class Transformers {

    private Transformers() {
    }

    /**
     * Creates observable transformer that replicates information from source observable onto target
     * subject. Source observable still needs active subscription for this to happen.
     */
    @NonNull
    public static <T> Observable.Transformer<T, T> channel(@NonNull Subject<T, T> target) {
        return new ChannelTransformer<>(target);
    }


    private static class ChannelTransformer<T> implements Observable.Transformer<T, T> {
        @NonNull
        private final Subject<T, T> target;

        private ChannelTransformer(@NonNull Subject<T, T> target) {
            this.target = target;
        }

        @SuppressWarnings("unchecked")
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
