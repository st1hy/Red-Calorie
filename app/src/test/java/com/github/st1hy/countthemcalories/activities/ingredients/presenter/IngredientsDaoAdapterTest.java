package com.github.st1hy.countthemcalories.activities.ingredients.presenter;

import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.github.st1hy.countthemcalories.BuildConfig;
import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.ingredients.model.IngredientsModel;
import com.github.st1hy.countthemcalories.activities.ingredients.model.RxIngredientsDatabaseModel;
import com.github.st1hy.countthemcalories.activities.ingredients.model.commands.IngredientsDatabaseCommands;
import com.github.st1hy.countthemcalories.activities.ingredients.presenter.viewholder.EmptySpaceViewHolder;
import com.github.st1hy.countthemcalories.activities.ingredients.presenter.viewholder.IngredientItemViewHolder;
import com.github.st1hy.countthemcalories.activities.ingredients.view.IngredientsView;
import com.github.st1hy.countthemcalories.core.adapter.RecyclerEvent;
import com.github.st1hy.countthemcalories.core.command.CommandResponse;
import com.github.st1hy.countthemcalories.core.command.InsertResult;
import com.github.st1hy.countthemcalories.core.rx.Schedulers;
import com.github.st1hy.countthemcalories.core.state.Visibility;
import com.github.st1hy.countthemcalories.database.Ingredient;
import com.github.st1hy.countthemcalories.database.IngredientTemplate;
import com.github.st1hy.countthemcalories.database.parcel.IngredientTypeParcel;
import com.github.st1hy.countthemcalories.database.unit.AmountUnitType;
import com.github.st1hy.countthemcalories.database.unit.EnergyDensity;
import com.github.st1hy.countthemcalories.testutils.RobolectricConfig;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;

import rx.Observable;
import rx.Scheduler;
import rx.functions.Action1;
import rx.plugins.TestRxPlugins;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

import static com.github.st1hy.countthemcalories.testutils.RecyclerEventMatchers.hasPosition;
import static com.github.st1hy.countthemcalories.testutils.RecyclerEventMatchers.hasType;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = RobolectricConfig.sdk, packageName = RobolectricConfig.packageName)
public class IngredientsDaoAdapterTest {

    @Mock
    IngredientsView view;
    @Mock
    IngredientsModel model;
    @Mock
    RxIngredientsDatabaseModel daoModel;
    @Mock
    Picasso picasso;
    @Mock
    IngredientsDatabaseCommands commands;
    @Mock
    Cursor cursor;
    IngredientsDaoAdapter adapter;

    @Mock
    CommandResponse deleteResponse, insertResponse;
    @Mock
    InsertResult insertResult;


    @Before
    public void setup() {
        TestRxPlugins.registerImmediateMainThreadHook();
        MockitoAnnotations.initMocks(this);
        adapter = new IngredientDaoAdapterProxy(view, model, daoModel, commands, picasso);

        when(deleteResponse.undoAvailability()).thenReturn(Observable.just(true));
        when(deleteResponse.undo()).thenReturn(Observable.just(insertResponse));
        when(deleteResponse.getResponse()).thenReturn(cursor);
        when(insertResponse.getResponse()).thenReturn(insertResult);
    }

    class IngredientDaoAdapterProxy extends IngredientsDaoAdapter {
        public IngredientDaoAdapterProxy(@NonNull IngredientsView view,
                                         @NonNull IngredientsModel model,
                                         @NonNull RxIngredientsDatabaseModel databaseModel,
                                         @NonNull IngredientsDatabaseCommands commands,
                                         @NonNull Picasso picasso) {
            super(view, model, databaseModel, commands, picasso);
        }

        @Override
        protected Cursor getCursor() {
            return cursor;
        }

        @Override
        protected void notifyItemRemovedRx(int position) {
            super.notifyItemRemovedRx(position);
        }
    }

    @Test
    public void testStart() throws Exception {
        adapter.onStart();

        verifyNoMoreInteractions(view, model, daoModel, commands, cursor, picasso);
    }

    @Test
    public void testGetItemViewType() throws Exception {
        when(cursor.getCount()).thenReturn(1);

        assertThat(adapter.getItemViewType(0), equalTo(R.layout.ingredients_item_scrolling));
        assertThat(adapter.getItemViewType(1), equalTo(R.layout.ingredients_item_bottom_space));

        verify(cursor, times(2)).getCount();

        verifyNoMoreInteractions(view, model, daoModel, commands, cursor, picasso);
    }

    @Test
    public void testOnCreateViewHolder() throws Exception {
        ViewGroup viewGroup = new LinearLayout(RuntimeEnvironment.application.getApplicationContext());
        assertThat(adapter.onCreateViewHolder(viewGroup, IngredientsDaoAdapter.item_layout),
                instanceOf(IngredientItemViewHolder.class));
        assertThat(adapter.onCreateViewHolder(viewGroup, IngredientsDaoAdapter.item_empty_space_layout),
                instanceOf(EmptySpaceViewHolder.class));

        verifyNoMoreInteractions(view, model, daoModel, commands, cursor, picasso);
    }

    @Test
    public void testOnBindViewHolder() throws Exception {
        IngredientItemViewHolder holder = mock(IngredientItemViewHolder.class);
        IngredientTemplate ingredient = mock(IngredientTemplate.class);
        when(holder.getReusableIngredient()).thenReturn(ingredient);
        when(ingredient.getAmountType()).thenReturn(AmountUnitType.MASS);
        when(ingredient.getEnergyDensityAmount()).thenReturn(BigDecimal.TEN);
        when(model.getReadableEnergyDensity(any(EnergyDensity.class))).thenReturn("10 kcal / g");
        ImageView imageView = mock(ImageView.class);
        when(holder.getImage()).thenReturn(imageView);
        Uri uri = Uri.parse("http://eat.me/ham.png");
        when(ingredient.getImageUri()).thenReturn(uri);
        RequestCreator requestCreator = mock(RequestCreator.class);
        when(picasso.load(uri)).thenReturn(requestCreator);
        when(requestCreator.centerCrop()).thenReturn(requestCreator);
        when(requestCreator.fit()).thenReturn(requestCreator);

        adapter.onBindViewHolder(holder, 0);

        verify(cursor).moveToPosition(0);
        verify(holder).getReusableIngredient();
        verify(daoModel).performReadEntity(cursor, ingredient);
        verify(holder).setPosition(0);
        verify(ingredient).getName();
        verify(holder).setName(anyString());
        verify(ingredient).getAmountType();
        verify(ingredient).getEnergyDensityAmount();
        verify(model).getReadableEnergyDensity(any(EnergyDensity.class));
        verify(holder).setEnergyDensity(anyString());
        verify(picasso).cancelRequest(imageView);
        verify(holder).getImage();
        verify(ingredient).getImageUri();
        verify(picasso).load(uri);

        verifyNoMoreInteractions(view, model, daoModel, commands, cursor, picasso, holder, imageView,
                ingredient);
    }

    @Test
    public void testBindImageNoPicture() throws Exception {
        IngredientTemplate ingredient = mock(IngredientTemplate.class);
        when(ingredient.getAmountType()).thenReturn(AmountUnitType.MASS);
        IngredientItemViewHolder holder = mock(IngredientItemViewHolder.class);
        ImageView imageView = mock(ImageView.class);
        when(holder.getImage()).thenReturn(imageView);
        when(ingredient.getImageUri()).thenReturn(Uri.EMPTY);

        adapter.onBindImage(ingredient, holder);
        verify(holder).getImage();
        verify(picasso).cancelRequest(imageView);
        verify(ingredient).getImageUri();
        verify(ingredient).getAmountType();
        verify(imageView).setImageResource(R.drawable.ic_fork_and_knife_wide);

        verifyNoMoreInteractions(view, model, daoModel, commands, cursor, picasso, holder, imageView,
                ingredient);
    }

    @Test
    public void testGetItemCount() throws Exception {
        when(cursor.getCount()).thenReturn(1);

        assertThat(adapter.getItemCount(), equalTo(2));

        verify(cursor).getCount();

        verifyNoMoreInteractions(view, model, daoModel, commands, cursor, picasso);
    }

    @Test
    public void testOnAttachedToWindow() throws Exception {
        IngredientItemViewHolder holder = mock(IngredientItemViewHolder.class);

        adapter.onViewAttachedToWindow(holder);

        verify(holder).onAttached();

        verifyNoMoreInteractions(view, model, daoModel, commands, cursor, picasso, holder);
    }

    @Test
    public void testOnDetachedFromWindow() throws Exception {
        IngredientItemViewHolder holder = mock(IngredientItemViewHolder.class);
        ImageView imageView = mock(ImageView.class);
        when(holder.getImage()).thenReturn(imageView);

        adapter.onViewDetachedFromWindow(holder);

        verify(holder).getImage();
        verify(picasso).cancelRequest(imageView);
        verify(holder).onDetached();

        verifyNoMoreInteractions(view, model, daoModel, commands, cursor, picasso, holder, imageView);
    }

    @Test
    public void testOnDeleteClicked() throws Exception {
        IngredientTemplate ingredient = prepareDelete();

        adapter.onDeleteClicked(ingredient, 0);

        verifyDelete(ingredient);

        verifyNoMoreInteractions(view, model, daoModel, commands, cursor, picasso, ingredient,
                deleteResponse, insertResponse, insertResult);
    }

    @NonNull
    private IngredientTemplate prepareDelete() {
        IngredientTemplate ingredient = mock(IngredientTemplate.class);
        when(daoModel.getById(anyLong())).thenReturn(Observable.just(ingredient));
        when(ingredient.getChildIngredients()).thenReturn(Collections.<Ingredient>emptyList());
        when(commands.delete(ingredient)).thenReturn(Observable.<CommandResponse<Cursor, InsertResult>>just(deleteResponse));
        when(view.showUndoMessage(anyInt())).thenReturn(Observable.<Void>empty());
        when(cursor.getCount()).thenReturn(2);
        return ingredient;
    }

    private void verifyDelete(IngredientTemplate ingredient) {
        verify(ingredient).getId();
        verify(daoModel).getById(anyLong());
        verify(ingredient).getChildIngredients();
        verify(commands).delete(ingredient);
        verify(deleteResponse).undoAvailability();
        verify(model).getUndoDeleteMessage();
        verify(view).showUndoMessage(anyInt());
        verify(deleteResponse).getResponse();
        verify(cursor).getCount();
        verify(view).setNoIngredientsVisibility(Visibility.GONE);
        verify(model).getNoIngredientsMessage();
        verify(view).setNoIngredientsMessage(anyInt());
    }

    @Test
    public void testDeleteShowConfirmation() throws Exception {
        IngredientTemplate ingredient = prepareDelete();

        when(ingredient.getChildIngredients()).thenReturn(Collections.singletonList(new Ingredient()));
        when(view.showUsedIngredientRemoveConfirmationDialog()).thenReturn(Observable.<Void>just(null));

        adapter.onDeleteClicked(ingredient, 0);

        verifyDelete(ingredient);
        verify(view).showUsedIngredientRemoveConfirmationDialog();
        verify(model).getNoIngredientsMessage();
        verify(view).setNoIngredientsMessage(anyInt());

        verifyNoMoreInteractions(view, model, daoModel, commands, cursor, picasso, ingredient,
                deleteResponse, insertResponse, insertResult);
    }

    @Test
    public void testDeleteUndo() throws Exception {
        IngredientTemplate ingredient = prepareDelete();

        Subject<Void, Void> undoSubject = PublishSubject.create();
        when(view.showUndoMessage(anyInt())).thenReturn(undoSubject);

        adapter.onDeleteClicked(ingredient, 0);

        verifyDelete(ingredient);
        verify(view).showUndoMessage(anyInt());

        when(insertResult.getNewItemPositionInCursor()).thenReturn(10);
        when(insertResult.getCursor()).thenReturn(cursor);

        //Emit undo request
        undoSubject.onNext(null);
        verify(deleteResponse).undo();
        verify(cursor).close();
        verify(insertResponse).getResponse();
        verify(insertResult).getNewItemPositionInCursor();
        verify(insertResult).getCursor();
        verify(view).scrollToPosition(10);
        verify(cursor, times(2)).getCount();
        verify(view, times(2)).setNoIngredientsVisibility(Visibility.GONE);
        verify(model, times(2)).getNoIngredientsMessage();
        verify(view, times(2)).setNoIngredientsMessage(anyInt());

        verifyNoMoreInteractions(view, model, daoModel, commands, cursor, picasso, ingredient,
                deleteResponse, insertResponse, insertResult);
    }


    @Test
    public void testScrollToNewItemWhenSearchFinishes() throws Exception {
        adapter.onIngredientAdded(10L);
        verifyNoMoreInteractions(view, model, daoModel, commands, cursor, picasso);
        Schedulers.registerHook(new Schedulers.HookImp() {
            @Override
            public Scheduler computation() {
                return immediate();
            }
        });

        when(daoModel.findInCursor(cursor, 10L)).thenReturn(500);

        adapter.onSearchFinished();

        verify(daoModel).findInCursor(cursor, 10L);
        verify(view).scrollToPosition(500);

        verifyNoMoreInteractions(view, model, daoModel, commands, cursor, picasso);
        Schedulers.reset();
    }

    @Test
    public void testOnIngredientClicked() throws Exception {
        final IngredientTemplate ingredient = mock(IngredientTemplate.class);

        when(model.isInSelectMode()).thenReturn(true);

        adapter.onIngredientClicked(ingredient, 10);

        verify(model).isInSelectMode();
        verify(ingredient).getId();
        verify(view).setResultAndReturn(argThat(hasIngredient(ingredient)));

        verifyNoMoreInteractions(view, model, daoModel, commands, cursor, picasso, ingredient);
    }

    @NonNull
    private TypeSafeMatcher<IngredientTypeParcel> hasIngredient(final IngredientTemplate ingredient) {
        return new TypeSafeMatcher<IngredientTypeParcel>() {
            @Override
            protected boolean matchesSafely(IngredientTypeParcel item) {
                return item.getWhenReady().getOrNull() == ingredient;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Has the same ingredient");
            }
        };
    }

    @Test
    public void testOnEditClicked() throws Exception {
        final IngredientTemplate ingredient = mock(IngredientTemplate.class);

        adapter.onEditClicked(ingredient, 10);

        verify(ingredient).getId();
        verify(view).openEditIngredientScreen(eq(10L), argThat(hasIngredient(ingredient)));

        verifyNoMoreInteractions(view, model, daoModel, commands, cursor, picasso, ingredient);
    }

    @Test
    public void testEvents() throws Exception {
        final AtomicReference<RecyclerEvent> event = new AtomicReference<>();
        adapter.getEvents().subscribe(new Action1<RecyclerEvent>() {
            @Override
            public void call(RecyclerEvent recyclerEvent) {
                event.set(recyclerEvent);
            }
        });

        final RecyclerEvent.Type type = RecyclerEvent.Type.REMOVED;
        ((IngredientDaoAdapterProxy) adapter).notifyItemRemovedRx(1);

        assertThat(event.get(), allOf(hasPosition(1), hasType(type)));

        verifyNoMoreInteractions(view, model, daoModel, commands, cursor, picasso);
    }

    @Test
    public void testHideUndoMessage() throws Exception {
        adapter.showUndoMessage(R.string.ingredients_undo_remove).call(false);

        verify(view).hideUndoMessage();

        verifyNoMoreInteractions(view, model, daoModel, commands, cursor, picasso);
    }

    @Test
    public void testOnAddedIngredientNotPresentInSearch() throws Exception {
        when(insertResult.getNewItemPositionInCursor()).thenReturn(-1);
        when(insertResult.getCursor()).thenReturn(cursor);

        adapter.onIngredientAdded().onNext(insertResult);

        verify(insertResult).getNewItemPositionInCursor();
        verify(insertResult).getCursor();
        verify(cursor).getCount();
        verify(view).setNoIngredientsVisibility(Visibility.VISIBLE);
        verify(model).getNoIngredientsMessage();
        verify(view).setNoIngredientsMessage(anyInt());

        verifyNoMoreInteractions(view, model, daoModel, commands, cursor, picasso);
    }
}