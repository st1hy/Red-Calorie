package com.github.st1hy.countthemcalories.activities.settings.presenter;

import com.github.st1hy.countthemcalories.activities.settings.model.EnergyUnit;
import com.github.st1hy.countthemcalories.activities.settings.model.SettingsChangedEvent;
import com.github.st1hy.countthemcalories.activities.settings.model.SettingsModel;
import com.github.st1hy.countthemcalories.activities.settings.view.SettingsView;
import com.github.st1hy.countthemcalories.database.unit.AmountUnitType;
import com.github.st1hy.countthemcalories.database.unit.GravimetricEnergyDensityUnit;
import com.github.st1hy.countthemcalories.database.unit.VolumetricEnergyDensityUnit;
import com.github.st1hy.countthemcalories.testrunner.RxMockitoJUnitRunner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import rx.subjects.PublishSubject;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(RxMockitoJUnitRunner.class)
public class SettingsPresenterImpTest {

    @Mock
    private SettingsView view;
    @Mock
    private SettingsModel preferencesModel;
    private SettingsPresenterImp presenter;

    @Before
    public void setup() {
        presenter = new SettingsPresenterImp(view, preferencesModel);
    }

    @Test
    public void testLiquidUnitSettingsClicked() throws Exception {
        final int which = 0;
        final VolumetricEnergyDensityUnit expected = VolumetricEnergyDensityUnit.values()[which];
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                AmountUnitType unit = (AmountUnitType) invocation.getArguments()[0];
                presenter.onSelectedUnitType(unit, which);
                return null;
            }
        }).when(view).showUnitSettingsDialog(any(AmountUnitType.class), any(String[].class));

        presenter.onLiquidUnitSettingsClicked();

        verify(preferencesModel, times(VolumetricEnergyDensityUnit.values().length))
                .getUnitPlural(any(VolumetricEnergyDensityUnit.class), eq(1));
        verify(view, times(1)).showUnitSettingsDialog(eq(AmountUnitType.VOLUME), Matchers.<String[]>any());
        verify(preferencesModel).setPreferredVolumetricUnit(expected);
        verifyNoMoreInteractions(view);
    }

    @Test
    public void testSolidUnitSettingsClicked() throws Exception {
        final int which = 0;
        final GravimetricEnergyDensityUnit expected = GravimetricEnergyDensityUnit.values()[which];
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                AmountUnitType unit = (AmountUnitType) invocation.getArguments()[0];
                presenter.onSelectedUnitType(unit, which);
                return null;
            }
        }).when(view).showUnitSettingsDialog(any(AmountUnitType.class), any(String[].class));

        presenter.onSolidUnitSettingsClicked();

        verify(view).showUnitSettingsDialog(eq(AmountUnitType.MASS), Matchers.<String[]>any());
        verify(preferencesModel, times(GravimetricEnergyDensityUnit.values().length))
                .getUnitPlural(any(GravimetricEnergyDensityUnit.class), eq(1));
        verify(preferencesModel).setPreferredGravimetricUnit(expected);
        verifyNoMoreInteractions(view);
    }

    @Test
    public void testNoViewChangesBeforeStart() throws Exception {
        final PublishSubject<SettingsChangedEvent> subject = PublishSubject.create();
        when(preferencesModel.toObservable()).thenReturn(subject);
        subject.onNext(new EnergyUnit.Mass(GravimetricEnergyDensityUnit.KCAL_AT_100G));
        verifyZeroInteractions(view);
    }

    @Test
    public void testNoViewChangesAfterStop() throws Exception {
        final PublishSubject<SettingsChangedEvent> subject = PublishSubject.create();
        when(preferencesModel.toObservable()).thenReturn(subject);
        presenter.onStart();
        presenter.onStop();
        subject.onNext(new EnergyUnit.Mass(GravimetricEnergyDensityUnit.KCAL_AT_100G));
        verifyZeroInteractions(view);
    }

    @Test
    public void testLiquidTextChangesAfterStartOnPreferencesChanges() throws Exception {
        final PublishSubject<SettingsChangedEvent> subject = PublishSubject.create();
        when(preferencesModel.toObservable()).thenReturn(subject);
        presenter.onStart();
        subject.onNext(new EnergyUnit.Volume(VolumetricEnergyDensityUnit.KCAL_AT_100ML));
        presenter.onStop();
        verify(view, only()).setLiquidUnit(anyString());
    }

    @Test
    public void testSolidTextChangesAfterStartOnPreferencesChanges() throws Exception {
        final PublishSubject<SettingsChangedEvent> subject = PublishSubject.create();
        when(preferencesModel.toObservable()).thenReturn(subject);
        presenter.onStart();
        subject.onNext(new EnergyUnit.Mass(GravimetricEnergyDensityUnit.KJ_AT_G));
        presenter.onStop();
        verify(view, only()).setSolidUnit(anyString());
    }
}