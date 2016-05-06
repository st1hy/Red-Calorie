package com.github.st1hy.countthemcalories.core.presenter;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.github.st1hy.countthemcalories.core.rx.RxDatabaseModel;
import com.github.st1hy.countthemcalories.testrunner.RxMockitoJUnitRunner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import rx.Observable;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(RxMockitoJUnitRunner.class)
public class RxDaoRecyclerAdapterTest {

    @Mock
    RxDatabaseModel<Void> model;
    @Mock
    Cursor cursor;

    TestAdapter adapter;

    @Before
    public void setUp() throws Exception {
        adapter = new TestAdapter(model);
    }

    @Test
    public void testOnStop() throws Exception {
        when(model.getAllObservable()).thenReturn(Observable.just(cursor));
        when(cursor.getCount()).thenReturn(0);

        adapter.onStart();
        adapter.onStop();

        assertThat(adapter.subscriptions.isUnsubscribed(), equalTo(false));
        assertThat(adapter.subscriptions.hasSubscriptions(), equalTo(false));
        verifyNoMoreInteractions(model, cursor);
    }


    public static class TestViewHolder extends RecyclerView.ViewHolder {
        public TestViewHolder(View itemView) {
            super(itemView);
        }
    }

    public static class TestAdapter extends RxDaoRecyclerAdapter<TestViewHolder, Void> {

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