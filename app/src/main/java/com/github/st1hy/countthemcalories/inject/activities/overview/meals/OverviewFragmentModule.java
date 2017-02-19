package com.github.st1hy.countthemcalories.inject.activities.overview.meals;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.overview.meals.MealsFragment;
import com.github.st1hy.countthemcalories.activities.overview.meals.model.CurrentDayModel;
import com.github.st1hy.countthemcalories.activities.overview.meals.presenter.MealsStateSaver;
import com.github.st1hy.countthemcalories.core.adapter.delegate.RecyclerViewAdapterDelegate;
import com.github.st1hy.countthemcalories.inject.PerFragment;
import com.github.st1hy.countthemcalories.inject.activities.overview.quantifier.datetime.MealListDate;

import org.joda.time.DateTime;
import org.parceler.Parcels;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

import static com.github.st1hy.countthemcalories.activities.overview.meals.MealsFragment.ARG_CURRENT_DATE;

@Module(includes = OverviewFragmentBindings.class)
public class OverviewFragmentModule {

    @NonNull
    private final MealsFragment fragment;
    @Nullable
    private final Bundle savedState;

    public OverviewFragmentModule(@NonNull MealsFragment fragment, @Nullable Bundle savedState) {
        this.fragment = fragment;
        this.savedState = savedState;
    }

    @Provides
    @Named("fragmentRoot")
    public View rootView() {
        return fragment.getView();
    }

    @Provides
    public static RecyclerView recyclerView(@Named("fragmentRoot") View view,
                                            RecyclerViewAdapterDelegate adapter,
                                            @Named("activityContext") Context context) {
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.overview_recycler_view);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        return recyclerView;
    }

    @Provides
    @Nullable
    @Named("savedState")
    public Bundle savedState() {
        return savedState;
    }

    @Provides
    @Named("arguments")
    @Nullable
    public Bundle arguments() {
        return fragment.getArguments();
    }

    @Provides
    @MealListDate
    public DateTime currentDate(@Nullable @Named("arguments") Bundle arguments) {
        DateTime dateTime = null;
        if (arguments != null) {
            dateTime = (DateTime) arguments.getSerializable(ARG_CURRENT_DATE);
        }
        if (dateTime == null) {
            dateTime = DateTime.now();
        }
        return dateTime;
    }

    @Provides
    @PerFragment
    public static CurrentDayModel currentDayModel(@Nullable @Named("savedState") Bundle savedState,
                                                  @MealListDate DateTime currentDate) {
        if (savedState != null ) {
            return Parcels.unwrap(savedState.getParcelable(MealsStateSaver.CURRENT_DAY_STATE));
        } else {
            return new CurrentDayModel(currentDate);
        }
    }

}
