package com.github.st1hy.countthemcalories.activities.overview.mealpager;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.widget.TextView;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addmeal.model.PhysicalQuantitiesModel;
import com.github.st1hy.countthemcalories.activities.overview.model.DayData;
import com.github.st1hy.countthemcalories.inject.PerActivity;
import com.jakewharton.rxbinding.support.v4.view.RxViewPager;

import org.joda.time.format.DateTimeFormat;

import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;

@PerActivity
public class MealPagerView {

    @Inject
    @Named("activityContext")
    Context context;
    @Inject
    ViewPager viewPager;
    @BindView(R.id.overview_toolbar_date)
    TextView date;
    @BindView(R.id.overview_toolbar_total_energy)
    TextView totalEnergy;

    @Inject
    PhysicalQuantitiesModel quantitiesModel;

    @Inject
    public MealPagerView(Activity activity) {
        ButterKnife.bind(this, activity);
    }

    public void setCurrentItem(int dayPositionInModel, boolean smoothScroll) {
        viewPager.setCurrentItem(dayPositionInModel, smoothScroll);
    }

    @NonNull
    @CheckResult
    public Observable<Integer> onPageSelected() {
        return RxViewPager.pageSelections(viewPager);
    }

    public void setTitle(@NonNull DayData day) {
        if (day.isToday()) {
            date.setText(context.getString(R.string.overview_toolbar_title_today));
        } else {
            String dateString = DateTimeFormat.shortDate().print(day.getDateTime());
            date.setText(String.format(Locale.getDefault(), "%s:", dateString));
        }
        totalEnergy.setText(quantitiesModel.formatAsEnergy(day.getValue()));
    }
}
