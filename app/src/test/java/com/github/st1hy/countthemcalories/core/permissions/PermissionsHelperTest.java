package com.github.st1hy.countthemcalories.core.permissions;

import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.st1hy.countthemcalories.core.rx.SimpleObserver;
import com.github.st1hy.countthemcalories.testrunner.RxMockitoJUnitRunner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.util.ReflectionHelpers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.schedulers.Schedulers;

import static com.github.st1hy.countthemcalories.core.permissions.RequestRationale.UserResponseForRationale.ABORT_REQUEST;
import static com.github.st1hy.countthemcalories.core.permissions.RequestRationale.UserResponseForRationale.CONTINUE_WITH_REQUEST;
import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(RxMockitoJUnitRunner.class)
public class PermissionsHelperTest {

    private static final Permission ANY_PERMISSION = null;

    @Mock
    private PermissionSubject subject;
    @Mock
    private RequestRationale requestRationale;
    private PermissionsHelper permissionsHelper;

    @Before
    public void setup() {
        permissionsHelper = new PermissionsHelper(subject);
    }

    @Test
    public void testAcceptsRationaleRequestGranted() {
        ReflectionHelpers.setStaticField(Build.VERSION.class, "SDK_INT", 23);

        final String testPermission = "testPermission";
        when(subject.checkSelfPermission(testPermission)).thenReturn(PackageManager.PERMISSION_DENIED);
        when(subject.shouldShowRequestPermissionRationale(testPermission)).thenReturn(true);
        when(requestRationale.showRationale()).thenReturn(Observable.just(CONTINUE_WITH_REQUEST));
        when(subject.requestPermission(any(String[].class)))
                .thenAnswer(permissionAnswer(testPermission, Permission.GRANTED));

        permissionsHelper.checkPermissionAndAskIfNecessary(testPermission, requestRationale)
                .observeOn(Schedulers.immediate())
                .subscribe(expectPermission(Permission.GRANTED));

        verify(subject, times(1)).checkSelfPermission(testPermission);
        verify(subject, times(1)).shouldShowRequestPermissionRationale(testPermission);
        verify(requestRationale, times(1)).showRationale();
        verify(subject, times(1)).requestPermission(any(String[].class));
        verifyNoMoreInteractions(subject, requestRationale);
    }

    @Test
    public void testNoRationaleRequestDenied() {
        ReflectionHelpers.setStaticField(Build.VERSION.class, "SDK_INT", 23);

        final String testPermission = "testPermission";
        when(subject.checkSelfPermission(testPermission)).thenReturn(PackageManager.PERMISSION_DENIED);
        when(subject.requestPermission(any(String[].class)))
                .thenAnswer(permissionAnswer(testPermission, Permission.DENIED));

        permissionsHelper.checkPermissionAndAskIfNecessary(testPermission, null)
                .observeOn(Schedulers.immediate())
                .subscribe(expectPermission(Permission.DENIED));

        verify(subject, times(1)).checkSelfPermission(testPermission);
        verify(subject, times(1)).requestPermission(any(String[].class));
        verifyNoMoreInteractions(subject, requestRationale);
    }

    @Test
    public void testPermissionIsGranted() {
        ReflectionHelpers.setStaticField(Build.VERSION.class, "SDK_INT", 23);

        final String testPermission = "testPermission";
        when(subject.checkSelfPermission(testPermission)).thenReturn(PackageManager.PERMISSION_GRANTED);

        permissionsHelper.checkPermissionAndAskIfNecessary(testPermission, requestRationale)
                .observeOn(Schedulers.immediate())
                .subscribe(expectPermission(Permission.GRANTED));

        verify(subject, times(1)).checkSelfPermission(testPermission);
        verifyNoMoreInteractions(subject, requestRationale);
    }

    @Test
    public void testUserIgnoresRationale() {
        ReflectionHelpers.setStaticField(Build.VERSION.class, "SDK_INT", 23);

        final String testPermission = "testPermission";
        when(subject.checkSelfPermission(testPermission)).thenReturn(PackageManager.PERMISSION_DENIED);
        when(subject.shouldShowRequestPermissionRationale(testPermission)).thenReturn(true);
        when(requestRationale.showRationale()).thenReturn(Observable.just(ABORT_REQUEST));
        when(subject.requestPermission(any(String[].class)))
                .thenAnswer(permissionAnswer(testPermission, Permission.GRANTED));

        permissionsHelper.checkPermissionAndAskIfNecessary(testPermission, requestRationale)
                .observeOn(Schedulers.immediate())
                .subscribe(expectPermission(Permission.REQUEST_CANCELED));

        verify(subject, times(1)).checkSelfPermission(testPermission);
        verify(subject, times(1)).shouldShowRequestPermissionRationale(testPermission);
        verify(requestRationale, times(1)).showRationale();
        verifyNoMoreInteractions(subject, requestRationale);
    }

    @Test
    public void testPermissionGrantedForOlderApi() {
        ReflectionHelpers.setStaticField(Build.VERSION.class, "SDK_INT", 22);

        final String testPermission = "testPermission";

        permissionsHelper.checkPermissionAndAskIfNecessary(testPermission, requestRationale)
                .observeOn(Schedulers.immediate())
                .subscribe(expectPermission(Permission.GRANTED));

        verifyZeroInteractions(subject, requestRationale);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFailsOnUnknownPermission() {
        ReflectionHelpers.setStaticField(Build.VERSION.class, "SDK_INT", 23);

        final String testPermission = "testPermission";
        when(subject.checkSelfPermission(testPermission)).thenReturn(Integer.MAX_VALUE);

        permissionsHelper.checkPermissionAndAskIfNecessary(testPermission, requestRationale)
                .observeOn(Schedulers.immediate())
                .subscribe(expectPermission(ANY_PERMISSION));
    }

    @Test
    public void testIllegalAnswerFromPermission() {
        ReflectionHelpers.setStaticField(Build.VERSION.class, "SDK_INT", 23);

        final String testPermission = "testPermission";
        when(subject.checkSelfPermission(testPermission)).thenReturn(PackageManager.PERMISSION_DENIED);
        when(subject.shouldShowRequestPermissionRationale(testPermission)).thenReturn(false);
        when(subject.requestPermission(any(String[].class)))
                .thenAnswer(new Answer<Observable<Permission[]>>() {
                    @Override
                    public Observable<Permission[]> answer(InvocationOnMock invocation) throws Throwable {
                        return Observable.just(new Permission[]{});
                    }
                });
        final AtomicReference<IllegalAnswerError> error = new AtomicReference<>();
        permissionsHelper.checkPermissionAndAskIfNecessary(testPermission, requestRationale)
                .observeOn(Schedulers.immediate())
                .subscribe(new SimpleObserver<Permission>() {
                    @Override
                    public void onError(Throwable e) {
                        if (e instanceof IllegalArgumentException) {
                            if (error.getAndSet(new IllegalAnswerError()) != null)
                                throw new Error("More errors was thrown", e);
                        } else {
                            throw new Error("Unknown error was thrown", e);
                        }
                    }
                });
        assertThat(error.get(), instanceOf(IllegalAnswerError.class));
    }

    private static class IllegalAnswerError extends Error {
    }

    private static Answer<Observable<Permission[]>> permissionAnswer(@NonNull final String request,
                                                                     @NonNull final Permission permissions) {
        return new Answer<Observable<Permission[]>>() {
            @Override
            public Observable<Permission[]> answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                assertThat(arguments, arrayWithSize(1));
                Object argument = arguments[0];
                assertThat(argument, instanceOf(String[].class));
                String[] actualArgument = (String[]) argument;
                assertEquals(request, actualArgument[0]);
                return Observable.just(new Permission[]{permissions});
            }
        };
    }

    private static Observer<Permission> expectPermission(@Nullable final Permission expected) {
        return new Subscriber<Permission>() {
            List<Permission> output = new ArrayList<>(1);

            @Override
            public void onCompleted() {
                unsubscribe();
            }

            @Override
            public void onError(Throwable e) {
                unsubscribe();
                throw new Error(e);
            }

            @Override
            public void onNext(Permission permission) {
                if (expected != ANY_PERMISSION) assertEquals(expected, permission);
                output.add(permission);
                assertThat(output, hasSize(1));
            }
        };
    }
}