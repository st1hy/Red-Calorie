package com.github.st1hy.countthemcalories.core.headerpicture;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.application.CaloriesCounterApplication;
import com.github.st1hy.countthemcalories.core.baseview.BaseActivity;
import com.github.st1hy.countthemcalories.inject.ApplicationTestComponent;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import java.lang.ref.WeakReference;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

@Singleton
public class HeaderPicturePickerUtils {

    @Inject
    public HeaderPicturePickerUtils() {
    }

    //Singleton with temporary reference to picker in fragment scope may leak memory thus its a weak reference.
    private WeakReference<TestPicturePicker> injectedPicker = new WeakReference<>(null);

    public static void setTempUri(BaseActivity activity, Uri uri) {
        CaloriesCounterApplication application = (CaloriesCounterApplication) activity
                .getApplication();
        ApplicationTestComponent component = (ApplicationTestComponent) application.getComponent();
        HeaderPicturePickerUtils utils = component.testHeaderPicturePickerUtils();
        TestPicturePicker testPicturePicker = utils.injectedPicker.get();
        if (testPicturePicker != null) testPicturePicker.setTempImageUri(uri);
    }

    @NonNull
    public static TypeSafeMatcher<Intent> injectUriOnMatch(final BaseActivity activity,
                                                           final Uri uri) {
        return new TypeSafeMatcher<Intent>() {
            @Override
            protected boolean matchesSafely(Intent item) {
                HeaderPicturePickerUtils.setTempUri(activity, uri);
                return true;
            }

            @Override
            public void describeTo(Description description) {
            }
        };
    }

    @NonNull
    @CheckResult
    public Observable.Transformer<ImageSource, Uri> onPickImage(TestPicturePicker testPicturePicker) {
        return source -> source
                .doOnNext(ignore -> captureRefTo(testPicturePicker))
                .flatMap(is -> Observable.empty());
    }

    private void captureRefTo(TestPicturePicker testPicturePicker) {
        injectedPicker = new WeakReference<>(testPicturePicker);
    }
}