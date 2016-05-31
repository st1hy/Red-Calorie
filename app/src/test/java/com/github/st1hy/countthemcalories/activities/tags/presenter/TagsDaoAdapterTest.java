package com.github.st1hy.countthemcalories.activities.tags.presenter;


import android.database.Cursor;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.BuildConfig;
import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.tags.model.RxTagsDatabaseModel;
import com.github.st1hy.countthemcalories.activities.tags.model.TagsActivityModel;
import com.github.st1hy.countthemcalories.activities.tags.model.TagsViewModel;
import com.github.st1hy.countthemcalories.activities.tags.presenter.viewholder.TagItemViewHolder;
import com.github.st1hy.countthemcalories.activities.tags.view.TagsView;
import com.github.st1hy.countthemcalories.core.state.Visibility;
import com.github.st1hy.countthemcalories.database.Tag;
import com.github.st1hy.countthemcalories.testutils.RobolectricConfig;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import rx.Observable;
import rx.subjects.PublishSubject;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
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

    private TagsDaoAdapter presenter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        presenter = new TestTagsPresenter(view, model, activityModel, viewModel);
    }

    class TestTagsPresenter extends TagsDaoAdapter {
        public TestTagsPresenter(@NonNull TagsView view,
                                 @NonNull RxTagsDatabaseModel model,
                                 @NonNull TagsActivityModel activityModel,
                                 @NonNull TagsViewModel viewModel) {
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

        verify(viewModel).getNewTagDialogTitle();
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
        when(cursor.getCount()).thenReturn(1);

        assertEquals(R.layout.tags_item, presenter.getItemViewType(0));
        assertEquals(R.layout.tags_item_bottom_space, presenter.getItemViewType(1));
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
        verifyNoMoreInteractions(model, view, cursor, activityModel);
    }

    @Test
    public void testLongPressItem() throws Exception {
        when(view.showRemoveTagDialog()).thenReturn(Observable.<Void>just(null));
        final Tag tag = Mockito.mock(Tag.class);
        when(model.removeAndRefresh(anyLong())).thenReturn(Observable.just(cursor));
        when(cursor.getCount()).thenReturn(1);

        presenter.onTagLongClicked(1, tag);

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

        presenter.onTagClicked(1, tag);

        verify(activityModel).isInSelectMode();
        verify(view).setResultAndReturn(tag.getId(), tag.getName());
        verifyNoMoreInteractions(model, view, cursor, activityModel);
    }


    @Test
    public void testOnSearch() throws Exception {
        when(model.getAllFiltered("test")).thenReturn(Observable.just(cursor));

        presenter.onSearch(Observable.<CharSequence>just("t", "te", "tes", "test"));

        verify(model).getAllFiltered("t");
        verify(model).getAllFiltered("test");
        verify(view).setNoTagsButtonVisibility(Visibility.VISIBLE);
        verify(cursor).getCount();
        verifyNoMoreInteractions(view, model);
    }

}