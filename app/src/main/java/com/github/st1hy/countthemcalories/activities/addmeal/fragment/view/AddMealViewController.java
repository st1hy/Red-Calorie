package com.github.st1hy.countthemcalories.activities.addmeal.fragment.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addmeal.view.AddMealScreen;
import com.github.st1hy.countthemcalories.activities.addmeal.view.AddMealScreenDelegate;
import com.github.st1hy.countthemcalories.core.state.Visibility;
import com.github.st1hy.countthemcalories.core.time.RxTimePicker;
import com.github.st1hy.countthemcalories.inject.PerFragment;
import com.github.st1hy.countthemcalories.inject.quantifier.context.ActivityContext;
import com.github.st1hy.countthemcalories.inject.quantifier.view.FragmentRootView;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;

import org.joda.time.DateTime;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;

@PerFragment
public class AddMealViewController extends AddMealScreenDelegate implements AddMealView {

    @BindView(R.id.add_meal_ingredients_list)
    RecyclerView ingredientList;
    @BindView(R.id.add_meal_name)
    EditText name;
    @BindView(R.id.add_meal_empty_ingredients)
    View emptyIngredients;
    @BindView(R.id.add_meal_total_calories)
    TextView totalCalories;
    @BindView(R.id.add_meal_time_value)
    Button mealTime;
    @NonNull
    private final Context context;

    @Inject
    public AddMealViewController(@NonNull @FragmentRootView View rootView,
                                 @NonNull AddMealScreen delegate,
                                 @ActivityContext @NonNull Context context) {
        super(delegate);
        this.context = context;
        ButterKnife.bind(this, rootView);
    }

    @NonNull
    @Override
    public Observable<Void> getAddIngredientButtonObservable() {
        return super.getAddIngredientButtonObservable();
    }

    @Override
    public void setName(@NonNull String name) {
        this.name.setText(name);
        this.name.setSelection(name.length());
    }

    @NonNull
    @Override
    public Observable<CharSequence> getNameObservable() {
        return RxTextView.textChanges(name);
    }

    @Override
    public void setEmptyIngredientsVisibility(@NonNull Visibility visibility) {
        //noinspection WrongConstant
        emptyIngredients.setVisibility(visibility.getVisibility());
    }

    @Override
    public void scrollTo(int itemPosition) {
        ingredientList.scrollToPosition(itemPosition);
    }

    @Override
    public void setTotalEnergy(@NonNull String totalEnergy) {
        totalCalories.setText(totalEnergy);
    }

    @Override
    public void setHint(@NonNull String mealNameNow) {
        name.setHint(mealNameNow);
    }

    @NonNull
    @Override
    public Observable<Void> mealTimeClicked() {
        return RxView.clicks(mealTime);
    }

    @Override
    public void setMealTime(@NonNull String time) {
        mealTime.setText(time);
    }

    @NonNull
    @Override
    public Observable.Transformer<Void, DateTime> openTimePicker(DateTime currentTime) {
        return voidObservable -> voidObservable
                .flatMap(aVoid -> RxTimePicker.openPicker(context, currentTime));
    }

}
