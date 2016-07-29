package com.github.st1hy.countthemcalories.activities.overview.fragment.presenter;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.github.st1hy.countthemcalories.BuildConfig;
import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addmeal.model.PhysicalQuantitiesModel;
import com.github.st1hy.countthemcalories.activities.overview.fragment.model.MealsViewModel;
import com.github.st1hy.countthemcalories.activities.overview.fragment.model.RxMealsDatabaseModel;
import com.github.st1hy.countthemcalories.activities.overview.fragment.model.commands.MealsDatabaseCommands;
import com.github.st1hy.countthemcalories.activities.overview.fragment.view.OverviewView;
import com.github.st1hy.countthemcalories.activities.overview.fragment.viewholder.EmptyMealItemHolder;
import com.github.st1hy.countthemcalories.activities.overview.fragment.viewholder.MealItemHolder;
import com.github.st1hy.countthemcalories.core.command.CommandResponse;
import com.github.st1hy.countthemcalories.core.permissions.PermissionsHelper;
import com.github.st1hy.countthemcalories.core.rx.Functions;
import com.github.st1hy.countthemcalories.core.rx.Schedulers;
import com.github.st1hy.countthemcalories.core.state.Visibility;
import com.github.st1hy.countthemcalories.database.Ingredient;
import com.github.st1hy.countthemcalories.database.IngredientTemplate;
import com.github.st1hy.countthemcalories.database.Meal;
import com.github.st1hy.countthemcalories.database.parcel.MealParcel;
import com.github.st1hy.countthemcalories.testutils.OptionalMatchers;
import com.github.st1hy.countthemcalories.testutils.RobolectricConfig;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rx.Observable;
import rx.Scheduler;
import rx.functions.Func1;
import rx.plugins.TestRxPlugins;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = RobolectricConfig.sdk, packageName = RobolectricConfig.packageName)
public class MealsAdapterTest {

    @Mock
    OverviewView view;
    @Mock
    RxMealsDatabaseModel databaseModel;
    @Mock
    Picasso picasso;
    @Mock
    PhysicalQuantitiesModel quantityModel;
    @Mock
    MealsDatabaseCommands commands;
    @Mock
    MealsViewModel viewModel;
    @Mock
    PermissionsHelper permissionsHelper;

    MealsAdapter adapter;

    @Before
    public void setUp() throws Exception {
        TestRxPlugins.registerImmediateMainThreadHook();
        Schedulers.registerHook(new Schedulers.HookImp() {
            @Override
            public Scheduler computation() {
                return immediate();
            }
        });
        MockitoAnnotations.initMocks(this);
        adapter = new MealsAdapter(view, databaseModel, picasso, quantityModel, commands, viewModel, permissionsHelper);

        Func1<?, BigDecimal> anyDecimal = Functions.into(BigDecimal.TEN);
        when(quantityModel.mapToEnergy()).thenReturn((Func1<Ingredient, BigDecimal>) anyDecimal);
        when(quantityModel.setScale(anyInt())).thenReturn((Func1<BigDecimal, BigDecimal>) anyDecimal);
        when(quantityModel.sumAll()).thenReturn((Func1<BigDecimal, BigDecimal>) anyDecimal);
        Func1<?, String> anyString = Functions.into("energy");
        when(quantityModel.energyAsString()).thenReturn((Func1<BigDecimal, String>) anyString);
    }

    @After
    public void tearDown() throws Exception {
        TestRxPlugins.reset();
    }

    @Test
    public void testOnStart() throws Exception {
        List<Meal> meals = prepareEmptyStart();

        adapter.onStart();

        assertThat(adapter.list, equalTo(meals));
        verify(databaseModel).getAllFilteredSortedDate(any(DateTime.class), any(DateTime.class));
        onVerifyShowTotal();
        verify(view).setEmptyListVisibility(Visibility.VISIBLE);

        testVerifyNoMoreInteractions();
    }

    @NonNull
    private List<Meal> prepareEmptyStart() {
        List<Meal> meals = Collections.emptyList();
        when(databaseModel.getAllFilteredSortedDate(any(DateTime.class), any(DateTime.class)))
                .thenReturn(Observable.just(meals));
        return meals;
    }

    private void onVerifyShowTotal(int count) {
        verify(quantityModel, times(count)).mapToEnergy();
        verify(quantityModel, times(count)).sumAll();
        verify(quantityModel, times(count)).setScale(0);
        verify(quantityModel, times(count)).energyAsString();
        verify(view, times(count)).setTotalEnergy("energy");
    }

    private void onVerifyShowTotal() {
        onVerifyShowTotal(1);
    }


    @Test
    public void testOnStop() throws Exception {
        adapter.onStop();

        assertThat(adapter.subscriptions.hasSubscriptions(), equalTo(false));
        testVerifyNoMoreInteractions();
    }

    @Test
    public void testGetItemViewType() throws Exception {
        adapter.list = Collections.emptyList();
        assertThat(adapter.getItemViewType(0), equalTo(MealsAdapter.bottomSpaceLayout));
        adapter.list = Collections.singletonList(new Meal());
        assertThat(adapter.getItemViewType(0), equalTo(MealsAdapter.mealItemLayout));
        assertThat(adapter.getItemViewType(1), equalTo(MealsAdapter.bottomSpaceLayout));

        testVerifyNoMoreInteractions();
    }

    @Test
    public void testGetItemCount() throws Exception {
        adapter.list = Collections.emptyList();
        assertThat(adapter.getItemCount(), equalTo(1));
        adapter.list = Collections.singletonList(new Meal());
        assertThat(adapter.getItemCount(), equalTo(2));

        testVerifyNoMoreInteractions();
    }

    @Test
    public void testOnCreateViewHolder() throws Exception {
        ViewGroup parent = new LinearLayout(RuntimeEnvironment.application.getApplicationContext());
        assertThat(adapter.onCreateViewHolder(parent, MealsAdapter.bottomSpaceLayout),
                instanceOf(EmptyMealItemHolder.class));
        assertThat(adapter.onCreateViewHolder(parent, MealsAdapter.mealItemLayout),
                instanceOf(MealItemHolder.class));
        testVerifyNoMoreInteractions();
    }

    @Test
    public void testOnBindViewHolder() throws Exception {
        MealItemHolder holder = mock(MealItemHolder.class);
        Meal meal = mock(Meal.class);
        when(meal.getName()).thenReturn("Name");
        Ingredient ingredient = mock(Ingredient.class);
        when(meal.getIngredients()).thenReturn(Collections.singletonList(ingredient));
        IngredientTemplate template = mock(IngredientTemplate.class);
        when(ingredient.getIngredientType()).thenReturn(template);
        when(template.getName()).thenReturn("Ingredient");
        ImageView imageView = mock(ImageView.class);
        when(holder.getImage()).thenReturn(imageView);
        when(meal.getImageUri()).thenReturn(Uri.EMPTY);

        adapter.list = Collections.singletonList(meal);

        adapter.onBindViewHolder(holder, 0);

        verify(meal).getName();
        verify(holder).setName("Name");
        verify(holder).setMeal(meal);
        verify(meal).getCreationDate();
        verify(holder).setDate(anyString());

        //onBindIngredients
        verify(meal).getIngredients();
        verify(quantityModel).mapToEnergy();
        verify(ingredient).getIngredientType();
        verify(template).getName();
        verify(holder).setIngredients("Ingredient");
        verify(quantityModel).formatTime(any(DateTime.class));
        verify(quantityModel).sumAll();
        verify(quantityModel).setScale(0);
        verify(quantityModel).energyAsString();
        verify(holder).setTotalEnergy("energy");

        //onBindImage
        verify(meal).getImageUri();
        verify(holder).setImageUri(argThat(OptionalMatchers.<Uri>isAbsent()));

        testVerifyNoMoreInteractions();
        verifyNoMoreInteractions(meal, holder, ingredient, template, imageView);
    }

    @Test
    public void testOnViewAttached() throws Exception {
        MealItemHolder holder = mock(MealItemHolder.class);

        adapter.onViewAttachedToWindow(holder);

        verify(holder).onAttached();

        testVerifyNoMoreInteractions();
        verifyNoMoreInteractions(holder);
    }

    @Test
    public void testOnViewDetached() throws Exception {
        MealItemHolder holder = mock(MealItemHolder.class);

        adapter.onViewDetachedFromWindow(holder);

        verify(holder).onDetached();

        testVerifyNoMoreInteractions();
        verifyNoMoreInteractions(holder);
    }

    @Test
    public void testOnMealClicked() throws Exception {
        final Meal meal = mock(Meal.class);

        adapter.onMealClicked(meal, null);

        verify(meal).getId();
        verify(view).openMealDetails(argThat(hasMeal(meal)), Matchers.<View>eq(null));

        testVerifyNoMoreInteractions();
        verifyNoMoreInteractions(meal);
    }

    @NonNull
    private TypeSafeMatcher<MealParcel> hasMeal(final Meal meal) {
        return new TypeSafeMatcher<MealParcel>() {
            @Override
            protected boolean matchesSafely(MealParcel item) {
                return item.getWhenReady().getOrNull() == meal;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("has meal "+ meal);

            }
        };
    }

    @Test
    public void testOnDeleteClicked() throws Exception {
        PrepareDelete prepareDelete = new PrepareDelete().invoke();

        Meal meal = prepareDelete.getMeal();
        CommandResponse response = prepareDelete.getResponse();

        adapter.onDeleteClicked(meal);

        verifyDelete(meal, response);

        testVerifyNoMoreInteractions();
        verifyNoMoreInteractions(meal, response);
    }

    private void verifyDelete(Meal meal, CommandResponse response) {
        verify(meal, times(2)).getId();
        verify(commands).delete(meal);
        verify(response).undoAvailability();
        verify(viewModel).getUndoRemoveMealMessage();
        verify(view).showUndoMessage(R.string.overview_meal_removed_undo_message);
        verify(response).getResponse();
        verify(meal).getIngredients();
        onVerifyShowTotal();
        verify(view).setEmptyListVisibility(Visibility.GONE);
    }

    @Test
    public void testEditMealWithId() throws Exception {
        final Meal meal = mock(Meal.class);
        when(meal.getId()).thenReturn(1L);
        adapter.list = Collections.singletonList(meal);

        adapter.editMealWithId(1L);

        verify(meal, times(2)).getId();
        verify(view).openEditMealScreen(argThat(hasMeal(meal)));

        testVerifyNoMoreInteractions();
        verifyNoMoreInteractions(meal);
    }

    @Test
    public void testEditMealWithNoId() throws Exception {
        final Meal meal = mock(Meal.class);
        when(meal.getId()).thenReturn(1L);
        adapter.list = Collections.emptyList();

        adapter.editMealWithId(1L);

        testVerifyNoMoreInteractions();
        verifyNoMoreInteractions(meal);
    }

    @Test
    public void testDeleteWithNoId() throws Exception {
        final Meal meal = mock(Meal.class);
        when(meal.getId()).thenReturn(1L);
        adapter.list = Collections.emptyList();

        adapter.deleteMealWithId(1L);

        testVerifyNoMoreInteractions();
        verifyNoMoreInteractions(meal);
    }

    @Test
    public void testOnBindImageWithUri() throws Exception {
        final Meal meal = mock(Meal.class);
        final MealItemHolder holder = mock(MealItemHolder.class);
        final ImageView imageView = mock(ImageView.class);
        when(holder.getImage()).thenReturn(imageView);
        Uri uri = Uri.parse("http://example.com");
        when(meal.getImageUri()).thenReturn(uri);
        RequestCreator requestCreator = mock(RequestCreator.class);
        when(picasso.load(uri)).thenReturn(requestCreator);
        when(requestCreator.centerCrop()).thenReturn(requestCreator);
        when(requestCreator.fit()).thenReturn(requestCreator);

        adapter.onBindImage(meal, holder);

        verify(meal).getImageUri();
        verify(holder).setImageUri(argThat(OptionalMatchers.equalTo(uri)));

        testVerifyNoMoreInteractions();
        verifyNoMoreInteractions(meal, holder, imageView);
    }

    @Test
    public void testUndoDelete() throws Exception {
        PrepareDelete prepareDelete = new PrepareDelete().invoke();
        Meal meal = prepareDelete.getMeal();
        CommandResponse response = prepareDelete.getResponse();

        Subject<Void, Void> undoSubject = PublishSubject.create();
        when(view.showUndoMessage(anyInt())).thenReturn(undoSubject);

        assertThat(adapter.list, hasSize(2));

        adapter.onDeleteClicked(meal);

        verifyDelete(meal, response);
        assertThat(adapter.list, hasSize(1));

        CommandResponse undoResponse = mock(CommandResponse.class);
        when(response.undo()).thenReturn(Observable.just(undoResponse));
        when(undoResponse.getResponse()).thenReturn(meal);

        //Emit undo
        undoSubject.onNext(null);
        verify(response).undo();
        assertThat(adapter.list, hasSize(2));
        verify(undoResponse).getResponse();
        verify(meal, times(3)).getIngredients();
        onVerifyShowTotal(2);
        verify(view, times(2)).setEmptyListVisibility(Visibility.GONE);

        testVerifyNoMoreInteractions();
        verifyNoMoreInteractions(meal, response, undoResponse);
    }

    //Auto-generated method class
    private class PrepareDelete {
        private Meal meal;
        private CommandResponse response;

        public Meal getMeal() {
            return meal;
        }

        public CommandResponse getResponse() {
            return response;
        }

        public PrepareDelete invoke() {
            meal = mock(Meal.class);
            adapter.list = new ArrayList<>();
            adapter.list.add(meal);
            adapter.list.add(meal);
            response = mock(CommandResponse.class);
            when(response.undoAvailability()).thenReturn(Observable.just(true));
            when(viewModel.getUndoRemoveMealMessage()).thenReturn(R.string.overview_meal_removed_undo_message);
            when(view.showUndoMessage(anyInt())).thenReturn(Observable.<Void>empty());

            when(commands.delete(meal)).thenReturn(Observable.<CommandResponse<Void, Meal>>just(response));
            when(meal.getIngredients()).thenReturn(Collections.<Ingredient>emptyList());
            when(response.getResponse()).thenReturn(null);
            return this;
        }
    }

    @Test
    public void testHideUndoMessage() throws Exception {
        adapter.showUndoMessage(R.string.undo).call(false);

        verify(view).hideUndoMessage();

        testVerifyNoMoreInteractions();
    }

    private void testVerifyNoMoreInteractions() {
        verifyNoMoreInteractions(view, databaseModel, picasso, quantityModel, commands, viewModel,
                permissionsHelper);
    }
}