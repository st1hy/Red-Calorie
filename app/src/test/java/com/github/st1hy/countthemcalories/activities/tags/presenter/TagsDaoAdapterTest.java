package com.github.st1hy.countthemcalories.activities.tags.presenter;


import android.database.Cursor;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.BuildConfig;
import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.tags.model.RxTagsDatabaseModel;
import com.github.st1hy.countthemcalories.activities.tags.model.TagsActivityModel;
import com.github.st1hy.countthemcalories.activities.tags.model.TagsViewModel;
import com.github.st1hy.countthemcalories.core.command.InsertResult;
import com.github.st1hy.countthemcalories.activities.tags.model.commands.TagsDatabaseCommands;
import com.github.st1hy.countthemcalories.activities.tags.presenter.viewholder.TagItemViewHolder;
import com.github.st1hy.countthemcalories.activities.tags.view.TagsView;
import com.github.st1hy.countthemcalories.core.command.CommandResponse;
import com.github.st1hy.countthemcalories.core.state.Visibility;
import com.github.st1hy.countthemcalories.database.JointIngredientTag;
import com.github.st1hy.countthemcalories.database.Tag;
import com.github.st1hy.countthemcalories.testutils.RobolectricConfig;
import com.github.st1hy.countthemcalories.testutils.TestError;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.Collections;

import rx.Observable;
import rx.plugins.TestRxPlugins;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = RobolectricConfig.sdk, packageName = RobolectricConfig.packageName)
public class TagsDaoAdapterTest {

    @Mock
    private TagsView view;
    @Mock
    private RxTagsDatabaseModel model;
    @Mock
    private TagsActivityModel activityModel;
    @Mock
    private Cursor cursor;
    @Mock
    private TagsViewModel viewModel;
    @Mock
    private TagsDatabaseCommands commands;

    private TagsDaoAdapter presenter;

    @Mock
    private CommandResponse commandResponse, undoResponse;
    @Mock
    private InsertResult result;

    @Before
    public void setUp() {
        TestRxPlugins.registerImmediateMainThreadHook();
        MockitoAnnotations.initMocks(this);
        presenter = new TestTagsPresenter(view, model, activityModel, viewModel, commands);

        when(commandResponse.undoAvailability()).thenReturn(Observable.just(true));
        when(commandResponse.undo()).thenReturn(Observable.just(undoResponse));
        when(commandResponse.getResponse()).thenReturn(result);
        when(result.getCursor()).thenReturn(cursor);
    }

    class TestTagsPresenter extends TagsDaoAdapter {
        public TestTagsPresenter(@NonNull TagsView view,
                                 @NonNull RxTagsDatabaseModel model,
                                 @NonNull TagsActivityModel activityModel,
                                 @NonNull TagsViewModel viewModel,
                                 @NonNull TagsDatabaseCommands commands) {
            super(view, model, activityModel, viewModel, commands);
        }

        @Override
        protected Cursor getCursor() {
            return cursor;
        }
    }

    @Test
    public void testOnStart() throws Exception {
        when(cursor.getCount()).thenReturn(2);
        when(view.getOnAddTagClickedObservable()).thenReturn(Observable.<Void>empty());

        presenter.onStart();

        verify(view).getOnAddTagClickedObservable();
        verifyNoMoreInteractions(model, view, cursor, activityModel, commands, commandResponse,
                undoResponse, result);
    }

    @Test
    public void testAddTagClick() throws Exception {
        final String tagName = "Tag name";
        final int position = 230;
        final int message = 0x21;

        when(view.showEditTextDialog(anyInt(), anyString())).thenReturn(Observable.just(tagName));
        when(cursor.getCount()).thenReturn(500);
        when(result.getNewItemPositionInCursor()).thenReturn(position);
        when(commands.insert(any(Tag.class))).thenReturn(Observable.<CommandResponse<InsertResult, Cursor>>just(commandResponse));
        when(viewModel.getUndoAddMessage()).thenReturn(message);

        presenter.onAddTagClicked(Observable.<Void>just(null));

        verify(viewModel).getNewTagDialogTitle();
        verify(view).showEditTextDialog(anyInt(), anyString());
        verify(commands).insert(any(Tag.class));
        //noinspection ResourceType
        verify(view).showUndoMessage(message);
        verify(view).scrollToPosition(position);
        verify(cursor).getCount();
        verify(view).setNoTagsButtonVisibility(Visibility.GONE);
        verify(commandResponse, times(2)).getResponse();
        verify(commandResponse).undoAvailability();
        verify(result).getCursor();
        verify(result, times(2)).getNewItemPositionInCursor();
        verifyNoMoreInteractions(model, view, cursor, activityModel, commands, commandResponse,
                undoResponse, result);
    }

    @Test
    public void testAddTagError() throws Exception {
        final String tagName = "Tag name";
        when(view.showEditTextDialog(anyInt(), anyString())).thenReturn(Observable.just(tagName));
        when(commands.insert(any(Tag.class))).thenReturn(Observable.<CommandResponse<InsertResult, Cursor>>error(new TestError()));

        Subject<Void, Void> subject = PublishSubject.create(); //Subject here to prevent from infinite loop when retry kicks in
        presenter.onAddTagClicked(subject);
        subject.onNext(null);

        verify(viewModel).getNewTagDialogTitle();
        verify(view).showEditTextDialog(anyInt(), anyString());
        verify(commands).insert(any(Tag.class));

        subject.onNext(null);
        verify(viewModel, times(2)).getNewTagDialogTitle();
        verify(view, times(2)).showEditTextDialog(anyInt(), anyString());
        verify(commands, times(2)).insert(any(Tag.class));

        verifyNoMoreInteractions(model, view, cursor, activityModel, commands, commandResponse,
                undoResponse, result);
    }

    @Test
    public void testGetItemType() throws Exception {
        when(cursor.getCount()).thenReturn(1);

        assertEquals(R.layout.tags_item_scrolling, presenter.getItemViewType(0));
        assertEquals(R.layout.tags_item_bottom_space, presenter.getItemViewType(1));

        verify(cursor, times(2)).getCount();

        verifyNoMoreInteractions(model, view, cursor, activityModel, commands, commandResponse,
                undoResponse, result);
    }

    @Test
    public void testOnBindViewHolder() throws Exception {
        TagItemViewHolder mockViewHolder = Mockito.mock(TagItemViewHolder.class);
        Tag tag = Mockito.mock(Tag.class);
        when(cursor.getCount()).thenReturn(1);
        when(mockViewHolder.getReusableTag()).thenReturn(tag);

        presenter.onBindViewHolder(mockViewHolder, 0);

        verify(cursor).moveToPosition(0);
        verify(model).performReadEntity(cursor, tag);
        verify(mockViewHolder).bind(anyInt(), eq(tag));
        verifyNoMoreInteractions(model, view, cursor, activityModel, commands, commandResponse,
                undoResponse, result);
    }

    @Test
    public void testRemoveItem() throws Exception {
        when(view.showRemoveTagDialog()).thenReturn(Observable.<Void>just(null));
        final Tag tag = Mockito.mock(Tag.class);
        when(commands.delete(any(Tag.class))).thenReturn(Observable.<CommandResponse<InsertResult, Cursor>>just(commandResponse));
        when(commandResponse.getResponse()).thenReturn(cursor);
        when(cursor.getCount()).thenReturn(1);
        when(model.getById(anyLong())).thenReturn(Observable.just(tag));
        when(tag.getIngredientTypes()).thenReturn(Collections.singletonList(new JointIngredientTag()));

        presenter.onDeleteClicked(1, tag);

        verify(tag).getId();
        verify(model).getById(anyLong());
        verify(tag).getIngredientTypes();
        verify(view).showRemoveTagDialog();
        verify(commands).delete(tag);
        verify(view).showUndoMessage(anyInt());
        verify(view).setNoTagsButtonVisibility(Visibility.GONE);
        verify(cursor).getCount();
        verify(commandResponse).getResponse();
        verify(commandResponse).undoAvailability();
        verifyNoMoreInteractions(model, view, cursor, activityModel, commands, commandResponse,
                undoResponse, result, tag);
    }


    @Test
    public void testRemoveItemTagNotUsed() throws Exception {
        when(view.showRemoveTagDialog()).thenReturn(Observable.<Void>just(null));
        final Tag tag = Mockito.mock(Tag.class);
        when(commands.delete(any(Tag.class))).thenReturn(Observable.<CommandResponse<InsertResult, Cursor>>just(commandResponse));
        when(commandResponse.getResponse()).thenReturn(cursor);
        when(cursor.getCount()).thenReturn(1);
        when(model.getById(anyLong())).thenReturn(Observable.just(tag));
        when(tag.getIngredientTypes()).thenReturn(Collections.<JointIngredientTag>emptyList());

        presenter.onDeleteClicked(1, tag);

        //Same as testRemoveItem, only confirmation dialog is not shown
        verify(tag).getId();
        verify(model).getById(anyLong());
        verify(tag).getIngredientTypes();
        verify(commands).delete(tag);
        verify(view).showUndoMessage(anyInt());
        verify(view).setNoTagsButtonVisibility(Visibility.GONE);
        verify(cursor).getCount();
        verify(commandResponse).getResponse();
        verify(commandResponse).undoAvailability();
        verifyNoMoreInteractions(model, view, cursor, activityModel, commands, commandResponse,
                undoResponse, result, tag);
    }

    @Test
    public void testOnItemClicked() throws Exception {
        when(activityModel.isInSelectMode()).thenReturn(true);
        final Tag tag = new Tag(0x231L, "Name");

        presenter.onTagClicked(1, tag);

        verify(activityModel).isInSelectMode();
        verify(view).setResultAndReturn(tag.getId(), tag.getName());
        verifyNoMoreInteractions(model, view, cursor, activityModel, commands, commandResponse,
                undoResponse, result);
    }


    @Test
    public void testOnSearch() throws Exception {
        when(cursor.getCount()).thenReturn(0);
        when(model.getAllFiltered("test", Collections.<Long>emptyList())).thenReturn(Observable.just(cursor));

        presenter.onSearch(Observable.<CharSequence>just("t", "te", "tes", "test"));

        verify(activityModel, times(2)).getExcludedTagIds();
        verify(model).getAllFiltered("t", Collections.<Long>emptyList());
        verify(model).getAllFiltered("test", Collections.<Long>emptyList());
        verify(view).setNoTagsButtonVisibility(Visibility.VISIBLE);
        verify(cursor).getCount();
        verifyNoMoreInteractions(model, view, cursor, activityModel, commands, commandResponse,
                undoResponse, result);
    }

    @Test
    public void testAddThenUndo() throws Exception {
        final String tagName = "Tag name";
        final int position = 230;

        when(view.showEditTextDialog(anyInt(), anyString())).thenReturn(Observable.just(tagName));
        when(cursor.getCount()).thenReturn(500);
        when(result.getNewItemPositionInCursor()).thenReturn(position);
        when(commands.insert(any(Tag.class))).thenReturn(Observable.<CommandResponse<InsertResult, Cursor>>just(commandResponse));
        when(view.showUndoMessage(anyInt())).thenReturn(Observable.<Void>just(null));
        when(undoResponse.getResponse()).thenReturn(cursor);

        presenter.onAddTagClicked(Observable.<Void>just(null));

        verify(viewModel).getNewTagDialogTitle();
        verify(view).showEditTextDialog(anyInt(), anyString());
        verify(commands).insert(any(Tag.class));
        verify(view).showUndoMessage(anyInt());
        verify(view).scrollToPosition(position);
        verify(cursor, times(2)).getCount();
        verify(view, times(2)).setNoTagsButtonVisibility(Visibility.GONE);
        verify(commandResponse, times(2)).getResponse();
        verify(commandResponse).undoAvailability();
        verify(result).getCursor();
        verify(result, times(2)).getNewItemPositionInCursor();

        verify(commandResponse).undo();
        verify(cursor).close();
        verify(undoResponse).getResponse();

        verifyNoMoreInteractions(model, view, cursor, activityModel, commands, commandResponse,
                undoResponse, result);
    }

    @Test
    public void testEditTag() throws Exception {
        Tag tag = Mockito.mock(Tag.class);
        when(tag.getName()).thenReturn("Tag name");
        when(view.showEditTextDialog(anyInt(), anyString())).thenReturn(Observable.just("New name"));
        when(model.updateRefresh(tag)).thenReturn(Observable.just(cursor));
        when(cursor.getCount()).thenReturn(500);
        when(model.findInCursor(cursor, tag)).thenReturn(1);

        presenter.onEditClicked(1, tag);

        verify(tag).getName();
        verify(view).showEditTextDialog(anyInt(), eq("Tag name"));
        verify(tag).setName("New name");
        verify(model).updateRefresh(tag);
        verify(model).findInCursor(cursor, tag);
        verify(cursor).getCount();
        verify(view).setNoTagsButtonVisibility(Visibility.GONE);

        verifyNoMoreInteractions(model, view, cursor, activityModel, commands, commandResponse,
                undoResponse, result, tag);
    }
}