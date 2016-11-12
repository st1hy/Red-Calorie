package com.github.st1hy.countthemcalories.activities.ingredients.fragment.presenter;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.ingredients.fragment.model.IngredientsFragmentModel;
import com.github.st1hy.countthemcalories.activities.ingredients.fragment.view.IngredientsView;
import com.github.st1hy.countthemcalories.activities.ingredients.fragment.viewholder.EmptySpaceViewHolder;
import com.github.st1hy.countthemcalories.activities.ingredients.fragment.viewholder.IngredientItemViewHolder;
import com.github.st1hy.countthemcalories.activities.ingredients.model.RxIngredientsDatabaseModel;
import com.github.st1hy.countthemcalories.activities.ingredients.model.commands.IngredientsDatabaseCommands;
import com.github.st1hy.countthemcalories.core.adapter.RecyclerEvent;
import com.github.st1hy.countthemcalories.core.command.CommandResponse;
import com.github.st1hy.countthemcalories.core.command.InsertResult;
import com.github.st1hy.countthemcalories.core.permissions.PermissionsHelper;
import com.github.st1hy.countthemcalories.core.rx.Schedulers;
import com.github.st1hy.countthemcalories.core.state.Visibility;
import com.github.st1hy.countthemcalories.core.tokensearch.LastSearchResult;
import com.github.st1hy.countthemcalories.core.tokensearch.SearchResult;
import com.github.st1hy.countthemcalories.database.Ingredient;
import com.github.st1hy.countthemcalories.database.IngredientTemplate;
import com.github.st1hy.countthemcalories.database.parcel.IngredientTypeParcel;
import com.github.st1hy.countthemcalories.database.unit.AmountUnitType;
import com.github.st1hy.countthemcalories.database.unit.EnergyDensity;
import com.github.st1hy.countthemcalories.testutils.OptionalMatchers;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
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

@RunWith(MockitoJUnitRunner.class)
public class IngredientsDaoAdapterTest {

    @Mock
    private IngredientsView view;
    @Mock
    private IngredientsFragmentModel model;
    @Mock
    private RxIngredientsDatabaseModel daoModel;
    @Mock
    private Picasso picasso;
    @Mock
    private IngredientsDatabaseCommands commands;
    @Mock
    private Cursor cursor;
    @Mock
    private Context context;

    private IngredientsDaoAdapter adapter;

    @Mock
    private CommandResponse deleteResponse, insertResponse;
    @Mock
    private InsertResult insertResult;
    @Mock
    private PermissionsHelper permissionsHelper;
    @Mock
    private LastSearchResult lastSearchResult;


    @Before
    public void setup() {
        TestRxPlugins.registerImmediateMainThreadHook();
        MockitoAnnotations.initMocks(this);
        when(lastSearchResult.get()).thenReturn(SearchResult.EMPTY);
        adapter = new IngredientDaoAdapterProxy(view, model, daoModel, commands, picasso, permissionsHelper, lastSearchResult);

        when(deleteResponse.undoAvailability()).thenReturn(Observable.just(true));
        when(deleteResponse.undo()).thenReturn(Observable.just(insertResponse));
        when(deleteResponse.getResponse()).thenReturn(cursor);
        when(insertResponse.getResponse()).thenReturn(insertResult);
    }

    class IngredientDaoAdapterProxy extends IngredientsDaoAdapter {
        public IngredientDaoAdapterProxy(@NonNull IngredientsView view,
                                         @NonNull IngredientsFragmentModel model,
                                         @NonNull RxIngredientsDatabaseModel databaseModel,
                                         @NonNull IngredientsDatabaseCommands commands,
                                         @NonNull Picasso picasso,
                                         @NonNull PermissionsHelper permissionsHelper,
                                         @NonNull LastSearchResult recentSearchResult) {
            super(view, model, databaseModel, commands, picasso, permissionsHelper, recentSearchResult, searchResultObservable, dialogView);
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
        final String query = "query";
        final List<String> tokens = Collections.singletonList("token");
        final SearchResult searchResult = new SearchResult(query, tokens);
        when(view.getSearchObservable()).thenReturn(Observable.just(searchResult));
        when(daoModel.getAllFilteredBy(query, tokens)).thenReturn(Observable.just(cursor));
        when(cursor.getCount()).thenReturn(0);

        adapter.onStart();

        verify(view).getSearchObservable();
        verify(daoModel).getAllFilteredBy(query, tokens);
        verify(model).getSearchEmptyMessage();
        verify(view).setNoIngredientsMessage(anyInt());
        verify(view).setNoIngredientsVisibility(Visibility.VISIBLE);
        verify(cursor).getCount();
        verify(lastSearchResult).set(searchResult);

        testVerifyNoMoreInteraction();
    }

    @Test
    public void testGetItemViewType() throws Exception {
        when(cursor.getCount()).thenReturn(1);

        assertThat(adapter.getItemViewType(0), equalTo(R.layout.ingredients_item_scrolling));
        assertThat(adapter.getItemViewType(1), equalTo(R.layout.ingredients_item_bottom_space));

        verify(cursor, times(2)).getCount();


        testVerifyNoMoreInteraction();
    }

    private void testVerifyNoMoreInteraction(Object... objects) {
        verifyNoMoreInteractions(view, model, daoModel, commands, cursor, picasso,
                permissionsHelper, lastSearchResult);
        if (objects.length > 0) verifyNoMoreInteractions(objects);
    }

    @Test
    public void testOnCreateViewHolder() throws Exception {
        ViewGroup viewGroup = mock(ViewGroup.class);
        assertThat(adapter.onCreateViewHolder(viewGroup, IngredientsDaoAdapter.item_layout),
                instanceOf(IngredientItemViewHolder.class));
        assertThat(adapter.onCreateViewHolder(viewGroup, IngredientsDaoAdapter.item_empty_space_layout),
                instanceOf(EmptySpaceViewHolder.class));

        testVerifyNoMoreInteraction(viewGroup);
    }

    @Test
    public void testOnBindViewHolder() throws Exception {
        IngredientItemViewHolder holder = mock(IngredientItemViewHolder.class);
        IngredientTemplate ingredient = mock(IngredientTemplate.class);
        when(holder.getReusableIngredient()).thenReturn(ingredient);
        when(ingredient.getAmountType()).thenReturn(AmountUnitType.MASS);
        when(ingredient.getEnergyDensityAmount()).thenReturn(BigDecimal.TEN);
        when(model.getReadableEnergyDensity(any(EnergyDensity.class))).thenReturn("10 kcal / g");
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
        verify(ingredient, times(2)).getAmountType();
        verify(ingredient).getEnergyDensityAmount();
        verify(model).getReadableEnergyDensity(any(EnergyDensity.class));
        verify(holder).setEnergyDensity(anyString());
        verify(ingredient).getImageUri();
        verify(holder).setImagePlaceholder(R.drawable.ic_fork_and_knife_wide);
        verify(holder).setImageUri(argThat(OptionalMatchers.equalTo(uri)));

        testVerifyNoMoreInteraction();
        verifyNoMoreInteractions(holder, ingredient);
    }

    @Test
    public void testBindImageNoPicture() throws Exception {
        IngredientTemplate ingredient = mock(IngredientTemplate.class);
        when(ingredient.getAmountType()).thenReturn(AmountUnitType.MASS);
        IngredientItemViewHolder holder = mock(IngredientItemViewHolder.class);
        ImageView imageView = mock(ImageView.class);
        when(ingredient.getImageUri()).thenReturn(Uri.EMPTY);

        adapter.onBindImage(ingredient, holder);
        verify(ingredient).getImageUri();
        verify(ingredient).getAmountType();
        verify(holder).setImagePlaceholder(R.drawable.ic_fork_and_knife_wide);
        verify(holder).setImageUri(argThat(OptionalMatchers.<Uri>isAbsent()));

        verifyNoMoreInteractions(view, model, daoModel, commands, cursor, picasso,
                holder, imageView, ingredient);
    }

    @Test
    public void testGetItemCount() throws Exception {
        when(cursor.getCount()).thenReturn(1);

        assertThat(adapter.getItemCount(), equalTo(2));

        verify(cursor).getCount();

        testVerifyNoMoreInteraction();
    }

    @Test
    public void testOnAttachedToWindow() throws Exception {
        IngredientItemViewHolder holder = mock(IngredientItemViewHolder.class);

        adapter.onViewAttachedToWindow(holder);

        verify(holder).onAttached();

        verifyNoMoreInteractions(view, model, daoModel, commands, cursor, picasso,
                holder);
    }

    @Test
    public void testOnDetachedFromWindow() throws Exception {
        IngredientItemViewHolder holder = mock(IngredientItemViewHolder.class);
        ImageView imageView = mock(ImageView.class);

        adapter.onViewDetachedFromWindow(holder);

        verify(holder).onDetached();

        verifyNoMoreInteractions(view, model, daoModel, commands, cursor, picasso,
                holder, imageView);
    }

    @Test
    public void testOnDeleteClicked() throws Exception {
        IngredientTemplate ingredient = prepareDelete();

        adapter.onDeleteClicked(ingredient, 0);

        verifyDelete(ingredient);

        verifyNoMoreInteractions(view, model, daoModel, commands, cursor, picasso,
                ingredient, deleteResponse, insertResponse, insertResult);
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

        verifyNoMoreInteractions(view, model, daoModel, commands, cursor, picasso,
                ingredient, deleteResponse, insertResponse, insertResult);
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

        verifyNoMoreInteractions(view, model, daoModel, commands, cursor, picasso,
                ingredient, deleteResponse, insertResponse, insertResult);
    }


    @Test
    public void testScrollToNewItemWhenSearchFinishes() throws Exception {
        adapter.onIngredientAdded(10L);
        testVerifyNoMoreInteraction();
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

        testVerifyNoMoreInteraction();
        Schedulers.reset();
    }

    @Test
    public void testOnIngredientClicked() throws Exception {
        final IngredientTemplate ingredient = mock(IngredientTemplate.class);

        when(model.isInSelectMode()).thenReturn(true);

        adapter.onIngredientClicked(ingredient, 10);

        verify(model).isInSelectMode();
        verify(ingredient).getId();
        verify(view).onIngredientSelected(argThat(hasIngredient(ingredient)));

        verifyNoMoreInteractions(view, model, daoModel, commands, cursor, picasso,
                ingredient);
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
                description.appendText("Has the same ingredient " + ingredient);
            }
        };
    }

    @Test
    public void testOnEditClicked() throws Exception {
        final IngredientTemplate ingredient = mock(IngredientTemplate.class);

        adapter.onEditClicked(ingredient, 10);

        verify(ingredient).getId();
        verify(view).editIngredientTemplate(eq(10L), argThat(hasIngredient(ingredient)));

        verifyNoMoreInteractions(view, model, daoModel, commands, cursor, picasso,
                ingredient);
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

        testVerifyNoMoreInteraction();
    }

    @Test
    public void testHideUndoMessage() throws Exception {
        adapter.showUndoMessage(R.string.ingredients_undo_remove).call(false);

        verify(view).hideUndoMessage();

        testVerifyNoMoreInteraction();
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
        verify(lastSearchResult).get();

        testVerifyNoMoreInteraction();
    }

    @Test
    public void testAddToNewMeal() throws Exception {
        when(model.isInSelectMode()).thenReturn(false);
        final IngredientTemplate ingredient = mock(IngredientTemplate.class);
        when(view.showAlertDialog(anyInt(), anyInt())).thenReturn(Observable.just(0));

        adapter.onIngredientClicked(ingredient, 0);

        testVerifyOpenOptions();
        verify(view).addToNewMeal(argThat(hasIngredient(ingredient)));

        testVerifyNoMoreInteraction();
    }

    @Test
    public void testEditIngredientFromOptions() throws Exception {
        when(model.isInSelectMode()).thenReturn(false);
        final IngredientTemplate ingredient = mock(IngredientTemplate.class);
        when(view.showAlertDialog(anyInt(), anyInt())).thenReturn(Observable.just(1));

        adapter.onIngredientClicked(ingredient, 0);

        testVerifyOpenOptions();
        verify(view).editIngredientTemplate(eq(0L), argThat(hasIngredient(ingredient)));

        testVerifyNoMoreInteraction();
    }

    @Test
    public void testDeleteIngredientFromOptions() throws Exception {
        when(model.isInSelectMode()).thenReturn(false);
        final IngredientTemplate ingredient = prepareDelete();
        when(view.showAlertDialog(anyInt(), anyInt())).thenReturn(Observable.just(2));

        adapter.onIngredientClicked(ingredient, 0);

        testVerifyOpenOptions();
        verifyDelete(ingredient);
        verify(lastSearchResult).get();

        testVerifyNoMoreInteraction();
    }

    private void testVerifyOpenOptions() {
        verify(model).isInSelectMode();
        verify(model).getIngredientOptions();
        verify(model).getIngredientOptionsTitle();
        verify(view).showAlertDialog(anyInt(), anyInt());
    }
}