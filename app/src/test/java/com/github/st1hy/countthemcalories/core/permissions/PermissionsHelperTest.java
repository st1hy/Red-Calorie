package com.github.st1hy.countthemcalories.core.permissions;

import android.content.pm.PackageManager;

import com.github.st1hy.countthemcalories.core.Utils;
import com.github.st1hy.countthemcalories.testrunner.RxMockitoJUnitRunner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;

import static com.github.st1hy.countthemcalories.core.permissions.UserResponseForRationale.ABORT_REQUEST;
import static com.github.st1hy.countthemcalories.core.permissions.UserResponseForRationale.CONTINUE_WITH_REQUEST;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@SuppressWarnings("CheckResult")
@RunWith(RxMockitoJUnitRunner.class)
public class PermissionsHelperTest {
    @Mock
    private PermissionSubject subject;
    @Mock
    private RequestRationale requestRationale;
    @Mock
    private Utils utils;
    @Mock
    private PersistentPermissionCache cache;
    private PermissionsHelper permissionsHelper;

    @Before
    public void setup() {
        permissionsHelper = new PermissionsHelper(subject, utils, cache);
    }

    @Test
    public void testAcceptsRationaleRequestGranted() {
        when(utils.hasMarshmallow()).thenReturn(true);

        final String testPermission = "testPermission";
        when(subject.checkSelfPermission(testPermission)).thenReturn(PackageManager.PERMISSION_DENIED);
        when(subject.shouldShowRequestPermissionRationale(testPermission)).thenReturn(true);
        when(requestRationale.showRationale()).thenReturn(Observable.just(CONTINUE_WITH_REQUEST));
        Permission[] permissions = {Permission.GRANTED};
        doReturn(Observable.just(permissions)).when(subject).requestPermission(any(String[].class));

        ReadPermission subscriber = new ReadPermission();
        permissionsHelper.checkPermissionAndAskIfNecessary(testPermission, requestRationale)
                .subscribe(subscriber);
        assertThat(subscriber.output, hasSize(1));
        assertThat(subscriber.output, hasItem(Permission.GRANTED));

        verify(subject).checkSelfPermission(testPermission);
        verify(subject).shouldShowRequestPermissionRationale(testPermission);
        verify(requestRationale).showRationale();
        verify(subject).requestPermission(any(String[].class));
        //noinspection ResultOfMethodCallIgnored
        verify(cache).put(testPermission, Permission.GRANTED);
        verifyNoMoreInteractions(subject, requestRationale, cache);
    }

    @Test
    public void testNoRationaleRequestDenied() {
        when(utils.hasMarshmallow()).thenReturn(true);

        final String testPermission = "testPermission";
        when(subject.checkSelfPermission(testPermission)).thenReturn(PackageManager.PERMISSION_DENIED);
        when(subject.requestPermission(any(String[].class)))
                .thenReturn(Observable.just(new Permission[]{Permission.DENIED}));

        ReadPermission subscriber = new ReadPermission();
        permissionsHelper.checkPermissionAndAskIfNecessary(testPermission, null)
                .subscribe(subscriber);
        assertThat(subscriber.output, hasSize(1));
        assertThat(subscriber.output, hasItem(Permission.DENIED));

        verify(subject).checkSelfPermission(testPermission);
        verify(subject).requestPermission(any(String[].class));
        //noinspection ResultOfMethodCallIgnored
        verify(cache).put(testPermission, Permission.DENIED);
        verifyNoMoreInteractions(subject, requestRationale, cache);
    }

    @Test
    public void testPermissionIsGranted() {
        when(utils.hasMarshmallow()).thenReturn(true);

        final String testPermission = "testPermission";
        when(subject.checkSelfPermission(testPermission)).thenReturn(PackageManager.PERMISSION_GRANTED);

        ReadPermission subscriber = new ReadPermission();
        permissionsHelper.checkPermissionAndAskIfNecessary(testPermission, requestRationale)
                .subscribe(subscriber);
        assertThat(subscriber.output, hasSize(1));
        assertThat(subscriber.output, hasItem(Permission.GRANTED));

        verify(subject).checkSelfPermission(testPermission);
        verifyNoMoreInteractions(subject, requestRationale, cache);
    }

    @Test
    public void testUserIgnoresRationale() {
        when(utils.hasMarshmallow()).thenReturn(true);

        final String testPermission = "testPermission";
        when(subject.checkSelfPermission(testPermission)).thenReturn(PackageManager.PERMISSION_DENIED);
        when(subject.shouldShowRequestPermissionRationale(testPermission)).thenReturn(true);
        when(requestRationale.showRationale()).thenReturn(Observable.just(ABORT_REQUEST));

        ReadPermission subscriber = new ReadPermission();
        permissionsHelper.checkPermissionAndAskIfNecessary(testPermission, requestRationale)
                .subscribe(subscriber);
        assertThat(subscriber.output, hasSize(1));
        assertThat(subscriber.output, hasItem(Permission.REQUEST_CANCELED));

        verify(subject).checkSelfPermission(testPermission);
        verify(subject).shouldShowRequestPermissionRationale(testPermission);
        verify(requestRationale).showRationale();
        verifyNoMoreInteractions(subject, requestRationale, cache);
    }

    @Test
    public void testPermissionGrantedForOlderApi() {
        when(utils.hasMarshmallow()).thenReturn(false);

        final String testPermission = "testPermission";

        ReadPermission subscriber = new ReadPermission();
        permissionsHelper.checkPermissionAndAskIfNecessary(testPermission, requestRationale)
                .subscribe(subscriber);
        assertThat(subscriber.output, hasSize(1));
        assertThat(subscriber.output, hasItem(Permission.GRANTED));

        verifyNoMoreInteractions(subject, requestRationale, cache);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFailsOnUnknownPermission() {
        when(utils.hasMarshmallow()).thenReturn(true);

        final String testPermission = "testPermission";
        when(subject.checkSelfPermission(testPermission)).thenReturn(Integer.MAX_VALUE);

        ReadPermission subscriber = new ReadPermission();
        permissionsHelper.checkPermissionAndAskIfNecessary(testPermission, requestRationale)
                .subscribe(subscriber);
    }

    @Test
    public void testIllegalAnswerFromPermission() {
        when(utils.hasMarshmallow()).thenReturn(true);

        final String testPermission = "testPermission";
        when(subject.checkSelfPermission(testPermission)).thenReturn(PackageManager.PERMISSION_DENIED);
        when(subject.shouldShowRequestPermissionRationale(testPermission)).thenReturn(false);
        when(subject.requestPermission(any(String[].class))).thenReturn(Observable.just(new Permission[]{}));

        ReadPermission subscriber = new ReadPermission();
        permissionsHelper.checkPermissionAndAskIfNecessary(testPermission, requestRationale)
                .subscribe(subscriber);
        assertThat(subscriber.error, nullValue()); //no response
        assertThat(subscriber.output, hasSize(0));

        verify(subject).checkSelfPermission(testPermission);
        verify(subject).shouldShowRequestPermissionRationale(testPermission);
        verify(subject).requestPermission(any(String[].class));
        verifyNoMoreInteractions(subject, requestRationale, cache);
    }


    private static class ReadPermission extends Subscriber<Permission> {
        Throwable error;
        List<Permission> output = new ArrayList<>(1);
        boolean isCompleted = false;


        @Override
        public void onCompleted() {
            isCompleted = true;
        }

        @Override
        public void onError(Throwable e) {
            error = e;
        }

        @Override
        public void onNext(Permission permission) {
            output.add(permission);
        }
    }
}