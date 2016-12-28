package com.github.st1hy.countthemcalories.activities.addingredient.fragment.presenter;

import android.net.Uri;
import android.os.Bundle;
import android.support.test.espresso.core.deps.guava.collect.Lists;
import android.support.v4.util.Pair;
import android.widget.ImageView;

import com.github.st1hy.countthemcalories.BuildConfig;
import com.github.st1hy.countthemcalories.activities.addingredient.fragment.model.AddIngredientModel;
import com.github.st1hy.countthemcalories.activities.addingredient.fragment.model.AddIngredientModel.IngredientTypeCreateException;
import com.github.st1hy.countthemcalories.activities.addingredient.fragment.model.AddIngredientModel.IngredientTypeCreateException.ErrorType;
import com.github.st1hy.countthemcalories.activities.addingredient.fragment.view.AddIngredientView;
import com.github.st1hy.countthemcalories.activities.addingredient.view.AddIngredientActivity;
import com.github.st1hy.countthemcalories.core.permissions.Permission;
import com.github.st1hy.countthemcalories.core.permissions.PermissionsHelper;
import com.github.st1hy.countthemcalories.core.permissions.RequestRationale;
import com.github.st1hy.countthemcalories.core.withpicture.imageholder.ImageHolderDelegate;
import com.github.st1hy.countthemcalories.core.withpicture.imageholder.LoadedSource;
import com.github.st1hy.countthemcalories.database.IngredientTemplate;
import com.github.st1hy.countthemcalories.database.unit.AmountUnit;
import com.github.st1hy.countthemcalories.database.unit.MassUnit;
import com.github.st1hy.countthemcalories.database.unit.VolumeUnit;
import com.github.st1hy.countthemcalories.testutils.OptionalMatchers;
import com.github.st1hy.countthemcalories.testutils.RobolectricConfig;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Collections;
import java.util.List;

import rx.Observable;
import rx.plugins.TestRxPlugins;
import rx.subjects.PublishSubject;

import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static com.github.st1hy.countthemcalories.testutils.OptionalMatchers.valueMatches;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = RobolectricConfig.sdk, packageName = RobolectricConfig.packageName)
public class AddIngredientPresenterImpTest {
    final String testUnit = "test unit";
    final String testName = "test name";
    final String testEnergy = "test energy";

    @Mock
    private AddIngredientView view;
    @Mock
    private PermissionsHelper permissionsHelper;
    @Mock
    private AddIngredientModel model;
    @Mock
    private ImageHolderDelegate imageHolderDelegate;

    private AddIngredientPresenter presenter;

    @Before
    public void setUp() {
        TestRxPlugins.registerImmediateMainThreadHook();
        MockitoAnnotations.initMocks(this);

        when(permissionsHelper.checkPermissionAndAskIfNecessary(anyString(), any(RequestRationale.class)))
                .thenReturn(Observable.just(Permission.GRANTED));

        when(model.getName()).thenReturn(testName);
        when(model.getEnergyValue()).thenReturn(testEnergy);
        when(model.getImageUri()).thenReturn(Uri.EMPTY);
        when(model.getEnergyDensityUnit()).thenReturn(testUnit);

        when(model.getLoading()).thenReturn(Observable.<Void>just(null));

        when(view.getNameObservable()).thenReturn(Observable.<CharSequence>empty());
        when(view.getValueObservable()).thenReturn(Observable.<CharSequence>empty());
        when(view.getPictureSelectedObservable()).thenReturn(Observable.<Uri>empty());
        when(view.getSelectPictureObservable()).thenReturn(Observable.<Void>empty());
        when(view.getSaveObservable()).thenReturn(Observable.<Void>empty());
        when(model.canCreateIngredient(anyString(), anyString()))
                .thenReturn(Collections.<ErrorType>emptyList());
        when(view.getSearchObservable()).thenReturn(Observable.<Void>empty());
        when(view.getSelectTypeObservable()).thenReturn(Observable.<Void>empty());
        when(imageHolderDelegate.getLoadingObservable()).thenReturn(Observable.<LoadedSource>empty());

        presenter = new AddIngredientPresenterImp(view, permissionsHelper, model, imageHolderDelegate);
    }

    @After
    public void tearDown() throws Exception {
        TestRxPlugins.reset();
    }

    @Test
    public void testOnSaveState() throws Exception {
        Bundle bundle = Mockito.mock(Bundle.class);
        presenter.onSaveState(bundle);
        verify(model).onSaveState(bundle);

        testVerifyNoMoreInteraction();
    }

    private void testVerifyNoMoreInteraction() {
        verifyNoMoreInteractions(view, model, permissionsHelper, imageHolderDelegate);
    }

    @Test
    public void testOnStart() throws Exception {
        presenter.onStart();

        testVerifyStart();
        testVerifyNoMoreInteraction();
    }

    @Test
    public void testSave() {
        when(model.saveIntoDatabase()).thenReturn(Observable.just(new IngredientTemplate(1L)));
        when(view.getSaveObservable()).thenReturn(Observable.<Void>just(null));

        presenter.onStart();

        testVerifyStart();

        verify(model).saveIntoDatabase();
        verify(view).showNameError(Optional.<Integer>absent());
        verify(view).showValueError(Optional.<Integer>absent());
        verify(view).setResultAndFinish(argThat(hasExtra(AddIngredientActivity.RESULT_INGREDIENT_ID_LONG, 1L)));

        testVerifyNoMoreInteraction();
    }

    @Test
    public void testSaveError() throws Exception {
        when(model.saveIntoDatabase()).thenReturn(Observable.<IngredientTemplate>error(
                saveErrorOf(ImmutableList.of(ErrorType.NO_NAME)))
        );
        final PublishSubject<Void> saveSubject = PublishSubject.create();
        when(view.getSaveObservable()).thenReturn(saveSubject);

        presenter.onStart();
        saveSubject.onNext(null);

        testVerifyStart();

        verify(model).saveIntoDatabase();
        verify(view).showNameError(argThat(valueMatches(Matchers.any(Integer.class))));
        verify(view).showValueError(argThat(OptionalMatchers.<Integer>isAbsent()));
        verify(view).requestFocusToName();

        when(model.saveIntoDatabase()).thenReturn(Observable.<IngredientTemplate>error(
                saveErrorOf(ImmutableList.of(ErrorType.NO_VALUE)))
        );
        saveSubject.onNext(null);

        verify(model, times(2)).saveIntoDatabase();
        verify(view).showNameError(argThat(OptionalMatchers.<Integer>isAbsent()));
        verify(view).showValueError(argThat(valueMatches(Matchers.any(Integer.class))));
        verify(view).requestFocusToValue();

        testVerifyNoMoreInteraction();
    }

    private static IngredientTypeCreateException saveErrorOf(List<ErrorType> errors) {
        return new IngredientTypeCreateException(errors);
    }

    @Test
    public void testChangeName() throws Exception {
        when(view.getNameObservable()).thenReturn(Observable.<CharSequence>just("Name"));

        presenter.onStart();

        testVerifyStart();
        verify(model).setName("Name");

        testVerifyNoMoreInteraction();
    }

    @Test
    public void testChangeValue() throws Exception {
        when(view.getValueObservable()).thenReturn(Observable.<CharSequence>just("2.66"));

        presenter.onStart();
        testVerifyStart();
        verify(model).setEnergyValue("2.66");
        testVerifyNoMoreInteraction();
    }

    @Test
    public void testNameEmptyError() throws Exception {
        when(view.getNameObservable()).thenReturn(Observable.<CharSequence>just(""));
        when(view.getValueObservable()).thenReturn(Observable.<CharSequence>just("2.55"));
        when(model.canCreateIngredient(anyString(), anyString()))
                .thenReturn(Collections.singletonList(ErrorType.NO_NAME));

        presenter.onStart();
        testVerifyStart();

        verify(model).setName("");
        verify(model).setEnergyValue("2.55");
        verify(model).canCreateIngredient("", "2.55");
        verify(view).showNameError(eq(Optional.of(ErrorType.NO_NAME.getErrorResId())));
        verify(view).showValueError(eq(Optional.<Integer>absent()));
        verify(view).requestFocusToName();
        testVerifyNoMoreInteraction();
    }

    @Test
    public void testNoValueError() throws Exception {
        when(view.getNameObservable()).thenReturn(Observable.<CharSequence>just("Name"));
        when(view.getValueObservable()).thenReturn(Observable.<CharSequence>just(""));
        when(model.canCreateIngredient(anyString(), anyString()))
                .thenReturn(Collections.singletonList(ErrorType.NO_VALUE));

        presenter.onStart();
        testVerifyStart();

        verify(model).setName("Name");
        verify(model).setEnergyValue("");
        verify(model).canCreateIngredient("Name", "");
        verify(view).showNameError(eq(Optional.<Integer>absent()));
        verify(view).showValueError(eq(Optional.of(ErrorType.NO_VALUE.getErrorResId())));
        verify(view).requestFocusToValue();
        testVerifyNoMoreInteraction();
    }

    @Test
    public void testImageLoading() throws Exception {
        Uri uri = Mockito.mock(Uri.class);

        when(model.getImageUri()).thenReturn(uri);
        when(view.getImageView()).thenReturn(Mockito.mock(ImageView.class));
        when(imageHolderDelegate.getLoadingObservable())
                .thenReturn(Observable.just(LoadedSource.PICASSO));

        presenter.onStart();

        testVerifyStartNoImage();
        verify(imageHolderDelegate).setImageUri(argThat(OptionalMatchers.equalTo(uri)));
        verify(view).showImageOverlay();

        testVerifyNoMoreInteraction();
    }

    private void testVerifyStart() {
        testVerifyStartNoImage();
        verify(imageHolderDelegate).setImageUri(argThat(OptionalMatchers.<Uri>isAbsent()));
    }

    private void testVerifyStartNoImage() {
        verify(view).getSelectPictureObservable();
        verify(view).getPictureSelectedObservable();
        verify(model).getLoading();
        verify(model).getImageUri();
        verify(model).getName();
        verify(view).setName(testName);
        verify(model).getEnergyValue();
        verify(view).setEnergyDensityValue(testEnergy);
        verify(model).getEnergyDensityUnit();
        verify(view).setSelectedUnitName(testUnit);
        verify(view).getNameObservable();
        verify(view).getValueObservable();
        verify(view).getSaveObservable();
        verify(view).getSearchObservable();
        verify(view).getSelectTypeObservable();
        verify(imageHolderDelegate).onAttached();
        verify(imageHolderDelegate).getLoadingObservable();
    }

    @Test
    public void testSearchIngredientInTheInternet() throws Exception {
        PublishSubject<Void> search = PublishSubject.create();
        when(view.getSearchObservable()).thenReturn(search);
        PublishSubject<CharSequence> name = PublishSubject.create();
        when(view.getNameObservable()).thenReturn(name);

        presenter.onStart();
        testVerifyStart();

        name.onNext("Eggs");
        verify(model).setName("Eggs");

        search.onNext(null);
        verify(model, times(2)).getName();
        verify(model).getSearchIngredientQuery(anyString());
        verify(view).showInWebBrowser(any(Uri.class));

        testVerifyNoMoreInteraction();
    }

    @Test
    public void testSelectIngredientType() throws Exception {
        List<Pair<AmountUnit, CharSequence>> list = Lists.newLinkedList();
        list.add(Pair.<AmountUnit, CharSequence>create(MassUnit.G100,"option 1"));
        list.add(Pair.<AmountUnit, CharSequence>create(VolumeUnit.ML100,"option 2"));
        when(model.getSelectTypeDialogOptions()).thenReturn(list);
        PublishSubject<Void> selectType = PublishSubject.create();
        when(view.getSelectTypeObservable()).thenReturn(selectType);
        when(view.showAlertDialog(anyInt(), any(CharSequence[].class)))
                .thenReturn(Observable.just(1)); // option 2

        presenter.onStart();
        testVerifyStart();

        when(model.getEnergyDensityUnit()).thenReturn("option 2");

        selectType.onNext(null);
        verify(model).getSelectTypeDialogTitle();
        verify(model).getSelectTypeDialogOptions();
        verify(view).showAlertDialog(anyInt(), argThat(
                Matchers.<CharSequence>arrayContaining("option 1", "option 2")
        ));
        verify(model, times(2)).getEnergyDensityUnit();
        verify(model).setAmountType(VolumeUnit.ML100.getType());
        verify(view).setSelectedUnitName("option 2");

        testVerifyNoMoreInteraction();
    }
}