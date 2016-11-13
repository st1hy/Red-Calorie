package com.github.st1hy.countthemcalories.activities.settings.fragment.presenter;

import com.github.st1hy.countthemcalories.activities.settings.fragment.view.SelectUnitViewHolder;
import com.github.st1hy.countthemcalories.activities.settings.fragment.view.SettingsView;
import com.github.st1hy.countthemcalories.activities.settings.model.SettingsChangedEvent;
import com.github.st1hy.countthemcalories.activities.settings.model.SettingsModel;
import com.github.st1hy.countthemcalories.activities.settings.model.UnitChangedEvent;
import com.github.st1hy.countthemcalories.database.unit.EnergyUnit;
import com.github.st1hy.countthemcalories.database.unit.MassUnit;
import com.github.st1hy.countthemcalories.database.unit.VolumeUnit;
import com.github.st1hy.countthemcalories.testrunner.RxMockitoJUnitRunner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import rx.Observable;
import rx.subjects.PublishSubject;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(RxMockitoJUnitRunner.class)
public class SettingsPresenterImplTest {

    @Mock
    private SettingsView view;
    @Mock
    private SettingsModel preferencesModel;
    @Mock
    private SelectUnitViewHolder placeHolder;
    @Mock
    private SelectUnitViewHolder activeHolder;
    private SettingsPresenterImpl presenter;

    @Before
    public void setup() {
        presenter = new SettingsPresenterImpl(view, preferencesModel, dialogView);
        when(placeHolder.clickObservable()).thenReturn(Observable.<Void>empty());
        when(activeHolder.clickObservable()).thenReturn(Observable.<Void>just(null));
        when(view.getEnergyHolder()).thenReturn(placeHolder);
        when(view.getMassHolder()).thenReturn(placeHolder);
        when(view.getVolumeHolder()).thenReturn(placeHolder);
        when(preferencesModel.toObservable()).thenReturn(Observable.<SettingsChangedEvent>empty());
        when(preferencesModel.getEnergyUnit()).thenReturn(EnergyUnit.KCAL);
        when(preferencesModel.getMassUnit()).thenReturn(MassUnit.G100);
        when(preferencesModel.getVolumeUnit()).thenReturn(VolumeUnit.ML100);
    }

    @Test
    public void testLiquidUnitSettingsClicked() throws Exception {
        final int which = 0;
        final VolumeUnit expected = VolumeUnit.values()[which];
        when(view.getVolumeHolder()).thenReturn(activeHolder);
        when(view.showAlertDialog(anyInt(), any(String[].class))).thenReturn(Observable.just(which));

        presenter.onStart();

        verify(preferencesModel, times(VolumeUnit.values().length + 1)).getUnitName(isA(VolumeUnit.class));
        verify(view).showAlertDialog(anyInt(), any(String[].class));
        verify(preferencesModel).setVolumeUnit(expected);
    }

    @Test
    public void testSolidUnitSettingsClicked() throws Exception {
        final int which = 0;
        final MassUnit expected = MassUnit.values()[which];
        when(view.getMassHolder()).thenReturn(activeHolder);
        when(view.showAlertDialog(anyInt(), any(String[].class))).thenReturn(Observable.just(which));

        presenter.onStart();

        verify(preferencesModel, times(MassUnit.values().length + 1)).getUnitName(isA(MassUnit.class));
        verify(view).showAlertDialog(anyInt(), any(String[].class));
        verify(preferencesModel).setMassUnit(expected);
    }

    @Test
    public void testEnergyUnitSettingsClicked() throws Exception {
        final int which = 0;
        final EnergyUnit expected = EnergyUnit.values()[which];
        when(view.getEnergyHolder()).thenReturn(activeHolder);
        when(view.showAlertDialog(anyInt(), any(String[].class))).thenReturn(Observable.just(which));

        presenter.onStart();
        verify(preferencesModel, times(EnergyUnit.values().length + 1)).getUnitName(isA(EnergyUnit.class));
        verify(view).showAlertDialog(anyInt(), any(String[].class));
        verify(preferencesModel).setEnergyUnit(expected);

    }

    @Test
    public void testNoViewChangesBeforeStart() throws Exception {
        final PublishSubject<SettingsChangedEvent> subject = PublishSubject.create();
        when(preferencesModel.toObservable()).thenReturn(subject);
        subject.onNext(new UnitChangedEvent.Mass(MassUnit.G100));
        verifyZeroInteractions(view);
    }

    @Test
    public void testNoViewChangesAfterStop() throws Exception {
        final PublishSubject<SettingsChangedEvent> subject = PublishSubject.create();
        when(preferencesModel.toObservable()).thenReturn(subject);
        when(activeHolder.clickObservable()).thenReturn(Observable.<Void>empty());
        when(view.getVolumeHolder()).thenReturn(activeHolder);
        presenter.onStart();
        verify(view).getEnergyHolder();
        verify(view).getMassHolder();
        verify(view).getVolumeHolder();
        verify(activeHolder).clickObservable();
        verify(activeHolder).setTitle(anyInt());
        verify(activeHolder).setUnit(anyString());
        presenter.onStop();
        subject.onNext(new UnitChangedEvent.Mass(MassUnit.G100));
        verifyNoMoreInteractions(view, activeHolder);
    }

    @Test
    public void testLiquidTextChangesAfterStartOnPreferencesChanges() throws Exception {
        final PublishSubject<SettingsChangedEvent> subject = PublishSubject.create();
        when(preferencesModel.toObservable()).thenReturn(subject);
        when(view.getVolumeHolder()).thenReturn(activeHolder);
        when(activeHolder.clickObservable()).thenReturn(Observable.<Void>empty());
        presenter.onStart();
        verify(view).getEnergyHolder();
        verify(view).getMassHolder();
        verify(view).getVolumeHolder();
        verify(activeHolder).clickObservable();
        verify(activeHolder).setTitle(anyInt());
        verify(activeHolder, times(1)).setUnit(anyString());
        subject.onNext(new UnitChangedEvent.Volume(VolumeUnit.ML));
        verify(view, times(2)).getVolumeHolder();
        verify(activeHolder, times(2)).setUnit(anyString());
        presenter.onStop();
        verifyNoMoreInteractions(view, activeHolder);
    }


    @Test
    public void testOnStop() throws Exception {
        presenter.onStop();

        assertThat(presenter.subscriptions.isUnsubscribed(), equalTo(false));
        assertThat(presenter.subscriptions.hasSubscriptions(), equalTo(false));
    }
}