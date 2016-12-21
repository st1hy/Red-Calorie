package com.github.st1hy.countthemcalories.activities.addmeal.fragment.view;

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
import com.github.st1hy.countthemcalories.inject.PerFragment;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;

import javax.inject.Inject;
import javax.inject.Named;

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
    @BindView(R.id.add_meal_button_add_ingredient)
    Button addIngredientButton;
    @BindView(R.id.add_meal_total_calories)
    TextView totalCalories;

    @Inject
    public AddMealViewController(@NonNull @Named("fragmentRootView") View rootView,
                                 @NonNull AddMealScreen delegate) {
        super(delegate);
        ButterKnife.bind(this, rootView);
    }

    @NonNull
    @Override
    public Observable<Void> getAddIngredientButtonObservable() {
        return super.getAddIngredientButtonObservable()
                .mergeWith(RxView.clicks(addIngredientButton));
    }

    @Override
    public void setName(@NonNull String name) {
        this.name.setText(name);
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
    public void showNameError(@NonNull String error) {
        name.setError(error);
    }

    @Override
    public void hideNameError() {
        name.setError(null);
    }
}
