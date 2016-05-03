package com.github.st1hy.countthemcalories.activities.tags.presenter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.tags.model.TagsActivityModel;
import com.github.st1hy.countthemcalories.activities.tags.model.TagsModel;
import com.github.st1hy.countthemcalories.activities.tags.view.TagsView;
import com.github.st1hy.countthemcalories.core.ui.Visibility;
import com.github.st1hy.countthemcalories.database.BuildConfig;
import com.github.st1hy.countthemcalories.database.Tag;
import com.google.common.base.Strings;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Notification;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.internal.operators.OnSubscribeRedo;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

import static com.github.st1hy.countthemcalories.activities.tags.model.TagsModel.DbProcessing.FINISHED;
import static com.github.st1hy.countthemcalories.activities.tags.model.TagsModel.DbProcessing.NOT_STARTED;
import static com.github.st1hy.countthemcalories.activities.tags.model.TagsModel.DbProcessing.STARTED;

public class TagsPresenterImp extends RecyclerView.Adapter<TagViewHolder> implements TagsPresenter,
        OnItemInteraction {
    final TagsView view;
    final TagsModel model;
    final TagsActivityModel activityModel;
    final CompositeSubscription subscriptions = new CompositeSubscription();

    @Inject
    public TagsPresenterImp(@NonNull TagsView view,
                            @NonNull TagsModel model,
                            @NonNull TagsActivityModel activityModel) {
        this.view = view;
        this.model = model;
        this.activityModel = activityModel;
    }

    @Override
    public void onAddTagClicked(@NonNull Observable<Void> clicks) {
        clicks.flatMap(showNewTagDialog())
                .map(trim())
                .filter(notEmpty())
                .flatMap(addTagRefresh())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (BuildConfig.DEBUG) Timber.e(e, "Error adding tag");
                    }

                    @Override
                    public void onNext(Integer position) {
                        view.scrollToPosition(position);
                    }
                });
    }

    @Override
    public void onStart() {
        subscriptions.add(model.getDbProcessingObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onDbProcessing()));
    }

    @Override
    public void onStop() {
        subscriptions.clear();
    }

    @Override
    public void onRefresh(@NonNull Observable<Void> refreshes) {
        refreshes.flatMap(refreshModel()).subscribe();
    }

    @Override
    public void onSearch(@NonNull Observable<CharSequence> observable) {
        Observable<List<Tag>> listObservable = observable
                .debounce(500, TimeUnit.MILLISECONDS)
                .doOnNext(new Action1<CharSequence>() {
                    @Override
                    public void call(CharSequence text) {
                        if (BuildConfig.DEBUG) Timber.v("Search notification: queryText='%s'", text);
                    }
                })
                .flatMap(queryDatabaseFiltered())
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable e) {
                        if (BuildConfig.DEBUG) Timber.e(e, "Search exploded");
                    }
                });
        OnSubscribeRedo.retry(listObservable, REDO_INFINITE, AndroidSchedulers.mainThread())
            .subscribe();
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.tags_item;
    }

    @Override
    public TagViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, null);
        view.setLayoutParams(parent.getLayoutParams());
        return new TagViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(TagViewHolder holder, int position) {
        holder.setName(model.getItemAt(position).getName());
        holder.setPosition(position);
    }

    @Override
    public int getItemCount() {
        return model.getItemCount();
    }

    @Override
    public void onItemLongClicked(final int position) {
        final Tag tag = model.getItemAt(position);
        view.showRemoveTagDialog().subscribe(deleteTag(tag));
    }

    @Override
    public void onItemClicked(int position) {
        if (activityModel.isInSelectMode()) {
            view.setResultAndReturn(model.getItemAt(position).getId());
        }
    }

    @NonNull
    Func1<String, Observable<Integer>> addTagRefresh() {
        return new Func1<String, Observable<Integer>>() {
            @Override
            public Observable<Integer> call(String tagName) {
                return model.addTagAndRefresh(tagName);
            }
        };
    }

    @NonNull
    Action1<TagsModel.DbProcessing> onDbProcessing() {
        return new Action1<TagsModel.DbProcessing>() {
            @Override
            public void call(TagsModel.DbProcessing dbProcessing) {
                if (BuildConfig.DEBUG) Timber.v("Db processing %s", dbProcessing);
                view.setNoTagsButtonVisibility(Visibility.of(
                        dbProcessing == FINISHED && model.getItemCount() == 0
                ));
                view.setDataRefreshing(dbProcessing == STARTED);
                if (dbProcessing == NOT_STARTED) model.getTags().subscribe();
                if (dbProcessing == FINISHED) notifyDataSetChanged();
            }
        };
    }

    @NonNull
    Func1<Void, Observable<String>> showNewTagDialog() {
        return new Func1<Void, Observable<String>>() {
            @Override
            public Observable<String> call(Void aVoid) {
                return view.showEditTextDialog(model.getNewTagDialogTitle());
            }
        };
    }

    @NonNull
    Func1<Void, Observable<List<Tag>>> refreshModel() {
        return new Func1<Void, Observable<List<Tag>>>() {
            @Override
            public Observable<List<Tag>> call(Void aVoid) {
                return model.getTags();
            }
        };
    }

    @NonNull
    Func1<String, Boolean> notEmpty() {
        return new Func1<String, Boolean>() {
            @Override
            public Boolean call(String s) {
                return !Strings.isNullOrEmpty(s);
            }
        };
    }

    @NonNull
    Func1<String, String> trim() {
        return new Func1<String, String>() {
            @Override
            public String call(String s) {
                return s.trim();
            }
        };
    }

    @NonNull
    Action1<Void> deleteTag(final Tag tag) {
        return new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                model.removeTagAndRefresh(tag).subscribe();
            }
        };
    }

    @NonNull
    Func1<? super CharSequence, ? extends Observable<List<Tag>>> queryDatabaseFiltered() {
        return new Func1<CharSequence, Observable<List<Tag>>>() {
            @Override
            public Observable<List<Tag>> call(CharSequence text) {
                return model.getTagsFiltered(text.toString());
            }
        };
    }

    static final Func1<Observable<? extends Notification<?>>, Observable<?>> REDO_INFINITE = new Func1<Observable<? extends Notification<?>>, Observable<?>>() {
        @Override
        public Observable<?> call(Observable<? extends Notification<?>> ts) {
            return ts.map(new Func1<Notification<?>, Notification<?>>() {
                @Override
                public Notification<?> call(Notification<?> terminal) {
                    return Notification.createOnNext(null);
                }
            });
        }
    };
}
