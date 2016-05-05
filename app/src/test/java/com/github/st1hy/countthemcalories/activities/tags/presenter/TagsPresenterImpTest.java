package com.github.st1hy.countthemcalories.activities.tags.presenter;


import android.database.Cursor;

import com.github.st1hy.countthemcalories.BuildConfig;
import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.tags.model.TagsActivityModel;
import com.github.st1hy.countthemcalories.activities.tags.model.TagsModel;
import com.github.st1hy.countthemcalories.activities.tags.presenter.viewholder.TagItemViewHolder;
import com.github.st1hy.countthemcalories.activities.tags.view.TagsView;
import com.github.st1hy.countthemcalories.core.ui.Visibility;
import com.github.st1hy.countthemcalories.database.Tag;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import rx.Observable;
import rx.subjects.PublishSubject;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class TagsPresenterImpTest {

    private TagsView view;
    private TagsModel model;
    private TagsActivityModel activityModel;
    private TagsPresenterImp presenter;
    private Cursor cursor;

    @Before
    public void setup() {
        cursor = Mockito.mock(Cursor.class);
        view = Mockito.mock(TagsView.class);
        model = Mockito.mock(TagsModel.class);
        activityModel = Mockito.mock(TagsActivityModel.class);
        presenter = new TagsPresenterImp(view, model, activityModel);
    }

    @Test
    public void testOnStart() throws Exception {
        when(cursor.getCount()).thenReturn(2);
        when(view.getOnAddTagClickedObservable()).thenReturn(Observable.<Void>empty());

        presenter.onStart();

        verify(view).getOnAddTagClickedObservable();
        verifyNoMoreInteractions(model, view, cursor, activityModel);
    }

    @Test
    public void testAddTagClick() throws Exception {
        final String tagName = "Tag name";
        final int position = 230;
        when(view.showEditTextDialog(anyInt())).thenReturn(Observable.just(tagName));
        when(model.addNewAndRefresh(any(Tag.class))).thenReturn(Observable.just(cursor));
        when(cursor.getCount()).thenReturn(500);
        when(cursor.getPosition()).thenReturn(position);

        presenter.onAddTagClicked(Observable.<Void>just(null));

        verify(model).getNewTagDialogTitle();
        verify(view).showEditTextDialog(anyInt());
        verify(model).addNewAndRefresh(any(Tag.class));
        verify(cursor).getPosition();
        verify(view).scrollToPosition(position);
        verify(cursor).getCount();
        verify(view).setNoTagsButtonVisibility(Visibility.GONE);
        verifyNoMoreInteractions(model, view, cursor, activityModel);
    }

    @Test
    public void testAddTagError() throws Exception {
        final String tagName = "Tag name";
        when(view.showEditTextDialog(anyInt())).thenReturn(Observable.just(tagName));
        when(model.addNewAndRefresh(new Tag(null, tagName))).thenThrow(new Error());

        PublishSubject<Void> subject = PublishSubject.create();
        presenter.onAddTagClicked(subject);
        subject.onNext(null);

        //Doesn't crash is good
        verify(model).getNewTagDialogTitle();
        verify(view).showEditTextDialog(anyInt());
        verify(model).addNewAndRefresh(any(Tag.class));

        verifyNoMoreInteractions(model, view, cursor, activityModel);
    }

    @Test
    public void testGetItemType() throws Exception {
        presenter.cursor = cursor;
        when(cursor.getCount()).thenReturn(1);

        assertEquals(R.layout.tags_item, presenter.getItemViewType(0));
        assertEquals(R.layout.tags_item_bottom_space, presenter.getItemViewType(1));
    }

    @Test
    public void testOnBindViewHolder() throws Exception {
        TagItemViewHolder mockViewHolder = Mockito.mock(TagItemViewHolder.class);
        Tag tag = Mockito.mock(Tag.class);
        presenter.cursor = cursor;
        when(cursor.getCount()).thenReturn(1);
        when(mockViewHolder.getReusableTag()).thenReturn(tag);

        presenter.onBindViewHolder(mockViewHolder, 0);

        verify(cursor).moveToPosition(0);
        verify(model).performReadEntity(cursor, tag);
        verify(mockViewHolder).bind(tag);
        verifyNoMoreInteractions(model, view, cursor, activityModel);
    }

    @Test
    public void testLongPressItem() throws Exception {
        when(view.showRemoveTagDialog()).thenReturn(Observable.<Void>just(null));
        final Tag tag = Mockito.mock(Tag.class);
        when(model.removeAndRefresh(anyLong())).thenReturn(Observable.just(cursor));
        when(cursor.getCount()).thenReturn(1);

        presenter.onItemLongClicked(tag);

        verify(view).showRemoveTagDialog();
        verify(model).removeAndRefresh(tag.getId());
        verify(view).setNoTagsButtonVisibility(Visibility.GONE);
        verify(cursor).getCount();
        verifyNoMoreInteractions(model, view, cursor, activityModel);
    }
    @Test
    public void testOnItemClicked() throws Exception {
        when(activityModel.isInSelectMode()).thenReturn(true);
        final Tag tag = new Tag(0x231L, "Name");

        presenter.onItemClicked(tag);

        verify(activityModel).isInSelectMode();
        verify(view).setResultAndReturn(tag.getId(), tag.getName());
        verifyNoMoreInteractions(model, view, cursor, activityModel);
    }

//
    @Test
    public void testOnStop() throws Exception {
        when(model.getAllObservable()).thenReturn(Observable.just(cursor));
        when(cursor.getCount()).thenReturn(0);
        when(view.getOnAddTagClickedObservable()).thenReturn(Observable.<Void>empty());

        presenter.onStart();
        presenter.onStop();

        assertThat(presenter.subscriptions.isUnsubscribed(), equalTo(false));
        assertThat(presenter.subscriptions.hasSubscriptions(), equalTo(false));
    }

    @Test
    public void testOnSearch() throws Exception {
        when(model.getAllFiltered("test")).thenReturn(Observable.just(cursor));

        presenter.onSearch(Observable.<CharSequence>just("t","te","tes","test"));

        verify(model).getAllFiltered("test");
        verify(view).setNoTagsButtonVisibility(Visibility.VISIBLE);
        verify(cursor).getCount();
        verifyNoMoreInteractions(view, model);
    }

}