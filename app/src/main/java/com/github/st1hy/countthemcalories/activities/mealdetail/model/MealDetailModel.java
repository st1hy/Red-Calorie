package com.github.st1hy.countthemcalories.activities.mealdetail.model;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.st1hy.countthemcalories.activities.mealdetail.view.MealDetailActivity;
import com.github.st1hy.countthemcalories.activities.overview.model.RxMealsDatabaseModel;
import com.github.st1hy.countthemcalories.database.Meal;
import com.github.st1hy.countthemcalories.database.parcel.MealParcel;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import timber.log.Timber;

public class MealDetailModel {

    final RxMealsDatabaseModel databaseModel;
    final ParcelableProxy parcelableProxy;

    final boolean isDataValid;
    Meal meal;
    final MealParcel mealParcel;
    final Observable<Meal> mealObservable;

    public MealDetailModel(@NonNull RxMealsDatabaseModel databaseModel,
                           @Nullable Intent intent,
                           @Nullable Bundle savedState) {
        this.databaseModel = databaseModel;
        ParcelableProxy parcelableProxy = null;
        if (savedState != null) {
            parcelableProxy = savedState.getParcelable(ParcelableProxy.STATE_MODEL);
        }
        if (parcelableProxy != null) {
            mealParcel = parcelableProxy.mealParcel;
            isDataValid = true;
            meal = mealParcel.getWhenReady().getOrNull();
            if (meal == null) {
                mealObservable = loadFromParcel(mealParcel);
            } else {
                mealObservable = Observable.just(meal);
            }
        } else {
            parcelableProxy = new ParcelableProxy();
            mealParcel = getValidMealParcel(intent);
            isDataValid = mealParcel != null;
            if (isDataValid) {
                meal = mealParcel.getWhenReady().getOrNull();
                if (meal == null) {
                    mealObservable = loadFromParcel(mealParcel);
                } else {
                    mealObservable = Observable.just(meal);
                }
            } else {
                Timber.e("Incorrect starting intent: %s", intent);
                meal = null;
                mealObservable = Observable.empty();
            }
        }
        this.parcelableProxy = parcelableProxy;
    }

    /**
     * not null and valid only when #isDataValid is true, after #getMealObservable returns any value
     */
    public Meal getMeal() {
        return meal;
    }

    public boolean isDataValid() {
        return isDataValid;
    }

    @NonNull
    public Observable<Meal> getMealObservable() {
        return mealObservable;
    }

    public void onSaveState(@NonNull Bundle savedState) {
        savedState.putParcelable(ParcelableProxy.STATE_MODEL, parcelableProxy.snapshot(this));
    }

    Observable<Meal> loadFromParcel(@NonNull MealParcel mealParcel) {
        Observable<Meal> observable = databaseModel.unParcel(mealParcel)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Action1<Meal>() {
                    @Override
                    public void call(Meal meal) {
                        MealDetailModel.this.meal = meal;
                    }
                }).replay().autoConnect().share();
        observable.subscribe(new MealLoader());
        return observable;
    }

    private MealParcel getValidMealParcel(@Nullable Intent intent) {
        if (intent == null) return null;
        return intent.getParcelableExtra(MealDetailActivity.EXTRA_MEAL_PARCEL);
    }

    static class ParcelableProxy implements Parcelable {
        static String STATE_MODEL = "meal details model";
        private MealParcel mealParcel;

        ParcelableProxy() {
        }

        ParcelableProxy snapshot(@NonNull MealDetailModel model) {
            this.mealParcel = model.mealParcel;
            return this;
        }

        public static final Creator<ParcelableProxy> CREATOR = new Creator<ParcelableProxy>() {
            @Override
            public ParcelableProxy createFromParcel(Parcel source) {
                ParcelableProxy parcelableProxy = new ParcelableProxy();
                parcelableProxy.mealParcel = source.readParcelable(getClass().getClassLoader());
                return parcelableProxy;
            }

            @Override
            public ParcelableProxy[] newArray(int size) {
                return new ParcelableProxy[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeParcelable(mealParcel, 0);
        }
    }

    public static class MealLoader extends Subscriber<Meal> {
        @Override
        public void onCompleted() {
        }

        @Override
        public void onError(Throwable e) {
            Timber.e(e, "Error loading meal");
        }

        @Override
        public void onNext(Meal meal) {

        }
    }
}
