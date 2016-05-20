package com.github.st1hy.countthemcalories.activities.overview.view.viewholder;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.st1hy.countthemcalories.R;
import com.jakewharton.rxbinding.view.RxView;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.functions.Func1;

public class MealItemHolder extends AbstractMealItemHolder {

    @BindView(R.id.overview_item_image)
    ImageView image;
    @BindView(R.id.overview_item_name)
    TextView name;
    @BindView(R.id.overview_item_energy)
    TextView totalEnergy;
    @BindView(R.id.overview_item_ingredients)
    TextView ingredients;
    @BindView(R.id.overview_item_date)
    TextView date;
    @BindView(R.id.overview_item_root)
    View root;

    public MealItemHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @NonNull
    public ImageView getImage() {
        return image;
    }

    public void setName(@NonNull String name) {
        this.name.setText(name);
    }

    public void setTotalEnergy(@NonNull String totalEnergy) {
        this.totalEnergy.setText(totalEnergy);
    }

    public void setIngredients(@NonNull String ingredients) {
        this.ingredients.setText(ingredients);
    }

    public void setDate(@NonNull String date) {
        this.date.setText(date);
    }

    @NonNull
    public Observable<View> onClick() {
        return RxView.clicks(root).map(new Func1<Void, View>() {
            @Override
            public View call(Void aVoid) {
                return image;
            }
        });
    }
}
