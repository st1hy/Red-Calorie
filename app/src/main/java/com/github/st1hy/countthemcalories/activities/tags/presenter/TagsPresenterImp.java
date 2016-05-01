package com.github.st1hy.countthemcalories.activities.tags.presenter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.tags.model.TagsModel;
import com.github.st1hy.countthemcalories.activities.tags.view.TagViewHolder;
import com.github.st1hy.countthemcalories.activities.tags.view.TagsView;
import com.github.st1hy.countthemcalories.core.ui.Visibility;
import com.github.st1hy.countthemcalories.database.Tag;
import com.google.common.base.Strings;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

import static com.github.st1hy.countthemcalories.activities.tags.model.TagsModel.DbProcessing.FINISHED;
import static com.github.st1hy.countthemcalories.activities.tags.model.TagsModel.DbProcessing.NOT_STARTED;
import static com.github.st1hy.countthemcalories.activities.tags.model.TagsModel.DbProcessing.STARTED;

public class TagsPresenterImp extends RecyclerView.Adapter<TagViewHolder> implements TagsPresenter,
        OnItemLongPressed {
    private final TagsView view;
    private final TagsModel model;
    private final CompositeSubscription subscriptions = new CompositeSubscription();

    @Inject
    public TagsPresenterImp(@NonNull TagsView view,
                            @NonNull TagsModel model) {
        this.view = view;
        this.model = model;
    }

    @Override
    public void onAddTagClicked(@NonNull Observable<Void> clicks) {
        clicks.flatMap(showNewTagDialog())
                .map(trim())
                .filter(notEmpty())
                .flatMap(addTagRefresh())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Tag>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e, "Error adding tag");
                    }

                    @Override
                    public void onNext(List<Tag> tags) {
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
        subscriptions.unsubscribe();
    }

    @Override
    public void onRefresh(@NonNull Observable<Void> refreshes) {
        refreshes.flatMap(refreshModel()).subscribe();
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
    public void onItemLongPressed(final int position) {
        final Tag tag = model.getItemAt(position);
        view.showRemoveTagDialog().subscribe(deleteTag(tag));
    }

    @NonNull
    Func1<String, Observable<List<Tag>>> addTagRefresh() {
        return new Func1<String, Observable<List<Tag>>>() {
            @Override
            public Observable<List<Tag>> call(String tagName) {
                return model.addTagAndRefresh(tagName);
            }
        };
    }

    @NonNull
    Action1<TagsModel.DbProcessing> onDbProcessing() {
        return new Action1<TagsModel.DbProcessing>() {
            @Override
            public void call(TagsModel.DbProcessing dbProcessing) {
                view.setNoTagsButtonVisibility(Visibility.of(
                        dbProcessing == FINISHED && model.getItemCount() == 0
                ));
                view.setDataRefreshing(dbProcessing == STARTED);
                if (dbProcessing == NOT_STARTED)  model.getTags().subscribe();
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
}
