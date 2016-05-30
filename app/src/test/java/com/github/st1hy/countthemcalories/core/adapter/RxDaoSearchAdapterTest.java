package com.github.st1hy.countthemcalories.core.adapter;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.github.st1hy.countthemcalories.BuildConfig;
import com.github.st1hy.countthemcalories.core.rx.RxDatabaseModel;
import com.github.st1hy.countthemcalories.testutils.RobolectricConfig;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import rx.Observable;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = RobolectricConfig.sdk, packageName = RobolectricConfig.packageName)
public class RxDaoSearchAdapterTest {

    @Mock
    RxDatabaseModel<Void> model;
    @Mock
    Cursor cursor;

    TestAdapter adapter;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        adapter = new TestAdapter(model);
        RecyclerView view = new RecyclerView(RuntimeEnvironment.application);
        view.setAdapter(adapter);
    }


    @Test
    public void testOnStop() throws Exception {
        when(model.getAllFiltered("test")).thenReturn(Observable.just(cursor));
        when(cursor.getCount()).thenReturn(0);

        adapter.onStart();
        adapter.onSearch(Observable.<CharSequence>just("test"));
        adapter.onStop();

        assertThat(adapter.subscriptions.isUnsubscribed(), equalTo(false));
        assertThat(adapter.subscriptions.hasSubscriptions(), equalTo(false));
        verify(cursor).close();
        verify(model).getAllFiltered("test");
        verifyNoMoreInteractions(model, cursor);
    }


    public static class TestViewHolder extends RecyclerView.ViewHolder {
        public TestViewHolder(View itemView) {
            super(itemView);
        }
    }

    public static class TestAdapter extends RxDaoSearchAdapter<TestViewHolder, Void> {

        public TestAdapter(@NonNull RxDatabaseModel<Void> databaseModel) {
            super(databaseModel);
        }

        @Override
        public TestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return null;
        }

        @Override
        public void onBindViewHolder(TestViewHolder holder, int position) {

        }
    }

}