package com.github.st1hy.countthemcalories.core.permissions;

import rx.Observable;

public interface RequestRationale {
    /**
     * Show rationale why app needs a permission. It may be a question to user.
     * @return observable that provides user answer to the rationale. If answer is true proceed
     * with permission request.
     */
    Observable<UserResponseForRationale> showRationale();

}
