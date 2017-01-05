package com.github.st1hy.countthemcalories.core.activityresult;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

import rx.Observable;

/**
 * Interface to delegate the launch of an activity and return the result as an {@link Observable}
 */
public interface ActivityLauncher {

    /**
     * Launch an activity for which you would like a result when it finished.
     * When this activity exits, the {@link Observable} will be called with the given
     * {@link ActivityResult}. Using a negative requestCode is the same as calling
     * {@link Activity#startActivity} (the activity is not launched as a sub-activity),
     * and hence will result in a {@link IllegalArgumentException}.
     * <p>
     * <p>Note that this method should only be used with Intent protocols
     * that are defined to return a result.  In other protocols (such as
     * {@link Intent#ACTION_MAIN} or {@link Intent#ACTION_VIEW}), you may
     * not get the result when you expect.  For example, if the activity you
     * are launching uses the singleTask launch mode, it will not run in your
     * task and thus you will never receive a result.
     * <p>
     * <p>This method throws {@link IllegalArgumentException}
     * if there was no Activity found to run the given Intent.
     *
     * @param requestCode If >= 0, this code will be returned in
     *                    onActivityResult() when the activity exits.
     *                    Context.startActivity(Intent, Bundle)} for more details.
     * @throws IllegalArgumentException when requestCode is less than zero
     */
    @NonNull
    @CheckResult
    Observable.Transformer<StartParams, ActivityResult> startActivityForResult(int requestCode);

}
