package com.github.st1hy.countthemcalories.activities.addingredient.model;

import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.test.espresso.core.deps.guava.base.Function;
import android.support.test.espresso.core.deps.guava.collect.Lists;

import com.github.st1hy.countthemcalories.BuildConfig;
import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addingredient.model.AddIngredientModel.IngredientTypeCreateException;
import com.github.st1hy.countthemcalories.activities.addingredient.model.AddIngredientModel.IngredientTypeCreateException.ErrorType;
import com.github.st1hy.countthemcalories.activities.addingredient.view.AddIngredientActivity;
import com.github.st1hy.countthemcalories.activities.addingredient.view.AddIngredientActivityRoboTest;
import com.github.st1hy.countthemcalories.activities.ingredients.model.RxIngredientsDatabaseModel;
import com.github.st1hy.countthemcalories.activities.ingredients.view.IngredientsActivityRoboTest;
import com.github.st1hy.countthemcalories.activities.settings.model.SettingsModel;
import com.github.st1hy.countthemcalories.core.rx.SimpleSubscriber;
import com.github.st1hy.countthemcalories.database.IngredientTemplate;
import com.github.st1hy.countthemcalories.database.JointIngredientTag;
import com.github.st1hy.countthemcalories.database.Tag;
import com.github.st1hy.countthemcalories.database.parcel.IngredientTypeParcel;
import com.github.st1hy.countthemcalories.database.unit.AmountUnitType;
import com.github.st1hy.countthemcalories.database.unit.EnergyUnit;
import com.github.st1hy.countthemcalories.database.unit.MassUnit;
import com.github.st1hy.countthemcalories.database.unit.VolumeUnit;
import com.github.st1hy.countthemcalories.testutils.RobolectricConfig;
import com.github.st1hy.countthemcalories.testutils.TestError;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.Nullable;

import rx.Observable;
import rx.functions.Func1;

import static com.github.st1hy.countthemcalories.activities.addingredient.model.AddIngredientModel.IngredientTypeCreateException.ErrorType.NO_NAME;
import static com.github.st1hy.countthemcalories.activities.addingredient.model.AddIngredientModel.IngredientTypeCreateException.ErrorType.NO_VALUE;
import static com.github.st1hy.countthemcalories.activities.addingredient.model.AddIngredientModel.IngredientTypeCreateException.ErrorType.ZERO_VALUE;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollection;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = RobolectricConfig.sdk, packageName = RobolectricConfig.packageName)
public class AddIngredientModelRoboTest {

    final Uri testUri = Uri.parse("http://test.org/");
    final EnergyUnit expectedEnergy = EnergyUnit.KCAL;
    final MassUnit expectedMass = MassUnit.G100;
    final VolumeUnit expectedVolume = VolumeUnit.ML100;

    @Mock
    IngredientTagsModel tagsModel;
    @Mock
    RxIngredientsDatabaseModel typesModel;
    @Mock
    SettingsModel settingsModel;
    private final Resources resources = RuntimeEnvironment.application.getResources();
    private AddIngredientModel model;
    private IngredientTemplate example;
    private List<Tag> tags;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(settingsModel.getEnergyUnit()).thenReturn(expectedEnergy);
        when(settingsModel.getMassUnit()).thenReturn(expectedMass);
        when(settingsModel.getVolumeUnit()).thenReturn(expectedVolume);
        when(settingsModel.getAmountUnitFrom(AmountUnitType.MASS)).thenReturn(expectedMass);
    }

    private void setUpEmptyIngredient() {
        model = new AddIngredientModel(settingsModel, tagsModel, typesModel, resources, null, null);

        verify(settingsModel).getEnergyUnit();
        verify(settingsModel).getAmountUnitFrom(AmountUnitType.MASS);
        verifyNoMoreInteractions(typesModel, settingsModel, tagsModel);
    }

    private void setUpEditIngredient() {
        example = Mockito.spy(IngredientsActivityRoboTest.exampleIngredients[0]);
        tags = Arrays.asList(AddIngredientActivityRoboTest.exampleTags);
        List<JointIngredientTag> jTags = Lists.transform(tags, intoJTags(example));
        doReturn(jTags).when(example).getTags();

        Intent intent = new Intent();
        intent.setAction(AddIngredientActivity.ACTION_EDIT);
        IngredientTypeParcel parcel = new IngredientTypeParcel(example);
        intent.putExtra(AddIngredientActivity.EXTRA_EDIT_INGREDIENT_PARCEL, parcel);
        when(typesModel.unParcel(argThat(hasIngredientId(example)))).thenReturn(Observable.just(example));
        model = new AddIngredientModel(settingsModel, tagsModel, typesModel, resources, null, intent);

        model.setName("testName");
        model.setName("testEnergy");
        model.setImageUri(testUri);

        verify(typesModel).unParcel(argThat(hasIngredientId(example)));
        verify(settingsModel).getEnergyUnit();
        verify(settingsModel).getAmountUnitFrom(example.getAmountType());
        verify(tagsModel).replaceTags(argThat(contains(tags)));
        verifyNoMoreInteractions(typesModel, settingsModel, tagsModel);
    }

    @Test
    public void testInsertToDatabase() throws Exception {
        setUpEmptyIngredient();

        model.name = "Test";
        model.energyValue = "100";
        final List<Long> tagIds = Collections.singletonList(33L);
        Cursor cursor = Mockito.mock(Cursor.class);

        when(tagsModel.getTagIds()).thenReturn(tagIds);
        doReturn(Observable.just(cursor)).when(typesModel).addNew(any(IngredientTemplate.class), eq(tagIds));

        model.saveIntoDatabase();

        verify(tagsModel).getTagIds();
        verify(typesModel).addNew(any(IngredientTemplate.class), eq(tagIds));

        verifyNoMoreInteractions(tagsModel, typesModel, settingsModel);
    }

    @Test
    public void testInsertToDatabaseError() throws Exception {
        setUpEmptyIngredient();

        model.name = "";
        model.energyValue = "0";

        final AtomicReference<List<ErrorType>> errorsRef = new AtomicReference<>();
        model.saveIntoDatabase().subscribe(new SimpleSubscriber<IngredientTemplate>() {

            @Override
            public void onError(Throwable e) {
                if (e instanceof IngredientTypeCreateException) {
                    errorsRef.set(((IngredientTypeCreateException) e).getErrors());
                } else
                    throw new TestError(e);
            }
        });
        verifyZeroInteractions(typesModel, tagsModel, settingsModel);
        assertThat(errorsRef.get(), hasItems(ZERO_VALUE,
                NO_NAME));
    }

    @Test
    public void testOnSaveState() throws Exception {
        setUpEditIngredient();

        Bundle bundle = new Bundle();

        model.onSaveState(bundle);
        AddIngredientModel restoredModel = new AddIngredientModel(settingsModel, tagsModel, typesModel, resources, bundle, null);
        verify(settingsModel, times(2)).getEnergyUnit();
        verify(settingsModel, times(2)).getAmountUnitFrom(AmountUnitType.MASS);

        Assert.assertThat(restoredModel.source, equalTo(model.source));
        Assert.assertThat(restoredModel.amountType, equalTo(model.amountType));
        Assert.assertThat(restoredModel.amountUnit, equalTo(model.amountUnit));
        Assert.assertThat(restoredModel.name, equalTo(model.getName()));
        Assert.assertThat(restoredModel.energyValue, equalTo(model.getEnergyValue()));
        Assert.assertThat(restoredModel.imageUri, equalTo(model.getImageUri()));

        verifyNoMoreInteractions(tagsModel, typesModel, settingsModel);
    }


    @Test
    public void testToParcel() throws Exception {
        setUpEditIngredient();

        Parcel parcel = Parcel.obtain();
        model.parcelableProxy.snapshot(model);

        model.parcelableProxy.writeToParcel(parcel, 0);

        parcel.setDataPosition(0);
        AddIngredientModel.ParcelableProxy fromParcel = AddIngredientModel.ParcelableProxy.CREATOR.createFromParcel(parcel);

        IngredientTemplate templateFromParcel = typesModel.unParcel(fromParcel.source).toBlocking().single();
        verify(typesModel).unParcel(fromParcel.source);

        Assert.assertThat(templateFromParcel, equalTo(model.source.getWhenReady().getOrNull()));
        Assert.assertThat(fromParcel.amountType, equalTo(model.amountType));
        Assert.assertThat(fromParcel.name, equalTo(model.name));
        Assert.assertThat(fromParcel.energyValue, equalTo(model.energyValue));
        Assert.assertThat(fromParcel.imageUri, equalTo(model.imageUri));
        parcel.recycle();

        verifyNoMoreInteractions(tagsModel, typesModel, settingsModel);
    }

    @Test
    public void testProxyOther() throws Exception {
        setUpEmptyIngredient();
        MatcherAssert.assertThat(model.parcelableProxy.describeContents(), Matchers.equalTo(0));
        Assert.assertThat(AddIngredientModel.ParcelableProxy.CREATOR.newArray(4),
                allOf(instanceOf(AddIngredientModel.ParcelableProxy[].class), arrayWithSize(4)));
        verifyNoMoreInteractions(tagsModel, typesModel, settingsModel);
    }

    @Test
    public void testGeImageSourceDialogTitle() throws Exception {
        setUpEmptyIngredient();
        int title = model.getImageSourceDialogTitleResId();
        assertEquals(R.string.add_ingredient_image_select_title, title);;
        verifyNoMoreInteractions(tagsModel, typesModel, settingsModel);
    }

    @Test
    public void testGeImageSourceOptions() throws Exception {
        setUpEmptyIngredient();
        int array = model.getImageSourceOptionArrayResId();
        assertEquals(R.array.add_ingredient_image_select_options, array);;
        verifyNoMoreInteractions(tagsModel, typesModel, settingsModel);
    }

    @Test
    public void testCanCreateIngredient() throws Exception {
        setUpEmptyIngredient();
        model.name = "";
        model.energyValue = "";
        assertThat(model.canCreateIngredient(), Matchers.hasItems(NO_NAME, NO_VALUE));
        model.name = "s";
        model.energyValue = "";
        assertThat(model.canCreateIngredient(), Matchers.hasItems(NO_VALUE));
        model.name = "";
        model.energyValue = "0";
        assertThat(model.canCreateIngredient(), Matchers.hasItems(NO_NAME, ZERO_VALUE));
        model.name = "";
        model.energyValue = "1";
        assertThat(model.canCreateIngredient(), Matchers.hasItems(NO_NAME));
        model.name = "s";
        model.energyValue = "1s";
        assertThat(model.canCreateIngredient(), Matchers.hasItems(ZERO_VALUE));
        model.name = "s";
        model.energyValue = "0.0000";
        assertThat(model.canCreateIngredient(), Matchers.hasItems(ZERO_VALUE));
        model.name = "s";
        model.energyValue = "100";
        assertThat(model.canCreateIngredient(), hasSize(0));;
        verifyNoMoreInteractions(tagsModel, typesModel, settingsModel);
    }

    @Test
    public void testGetUnitType() throws Exception {
        setUpEmptyIngredient();

        assertThat(model.getUnitTypeFrom(null), equalTo(AmountUnitType.MASS));
        Intent intent = new Intent();
        assertThat(model.getUnitTypeFrom(intent), equalTo(AmountUnitType.MASS));
        intent.setAction(AddIngredientActivity.ACTION_EDIT);
        assertThat(model.getUnitTypeFrom(intent), equalTo(AmountUnitType.MASS));
        intent.setAction(AddIngredientActivity.ACTION_CREATE_MEAL);
        assertThat(model.getUnitTypeFrom(intent), equalTo(AmountUnitType.MASS));
        intent.setAction(AddIngredientActivity.ACTION_CREATE_DRINK);
        assertThat(model.getUnitTypeFrom(intent), equalTo(AmountUnitType.VOLUME));;
        verifyNoMoreInteractions(tagsModel, typesModel, settingsModel);
    }

    @Test
    public void testLoadingFinishes() throws Exception {
        setUpEditIngredient();

        Object single = model.getLoading()
                .map(new Func1<Void, Object>() {
                    @Override
                    public Object call(Void aVoid) {
                        return new Object();
                    }
                }).toBlocking()
                .single();
        assertThat(single, notNullValue());;
        verifyNoMoreInteractions(tagsModel, typesModel, settingsModel);
    }

    @Test
    public void testGetEnergyDensityUnit() throws Exception {
        setUpEmptyIngredient();

        when(settingsModel.getUnitName(expectedEnergy)).thenReturn("kcal");
        when(settingsModel.getUnitName(expectedMass)).thenReturn("100 g");

        assertThat(model.getEnergyDensityUnit(), equalTo("kcal / 100 g"));

        verify(settingsModel).getUnitName(expectedEnergy);
        verify(settingsModel).getUnitName(expectedMass);
        verifyNoMoreInteractions(tagsModel, typesModel, settingsModel);
    }

    @Test
    public void testSetGetValues() throws Exception {
        setUpEmptyIngredient();

        model.setName("test name");
        assertThat(model.getName(), equalTo("test name"));
        Uri uri = Uri.parse("http://test2.org");
        model.setImageUri(uri);
        assertThat(model.getImageUri(), equalTo(uri));
        model.setEnergyValue("test energy");
        assertThat(model.getEnergyValue(), equalTo("test energy"));

        verifyNoMoreInteractions(tagsModel, typesModel, settingsModel);
    }

    @Test
    public void testUpdateIngredient() throws Exception {
        setUpEditIngredient();

        final DateTime creationDate = example.getCreationDate();
        final Long id = example.getId();
        final Uri uri = Uri.parse("http://test-update.org");
        model.setImageUri(uri);
        model.setName("Test update name");
        model.setEnergyValue("23.55");

        final List<Long> tagIds = Collections.singletonList(33L);
        when(tagsModel.getTagIds()).thenReturn(tagIds);
        when(typesModel.update(any(IngredientTemplate.class), anyCollection())).thenReturn(Observable.just(null));

        model.saveIntoDatabase().toBlocking().single();

        verify(typesModel).update(argThat(new TypeSafeMatcher<IngredientTemplate>() {
            @Override
            protected boolean matchesSafely(IngredientTemplate item) {
                assertThat(item.getName(), equalTo("Test update name"));
                assertThat(item.getEnergyDensityAmount().toPlainString(), equalTo("0.985332"));
                assertThat(item.getId() , equalTo(id));
                assertThat(item.getCreationDate(), equalTo(creationDate));
                assertThat(item.getTags(), hasSize(tags.size())); //old tags
                assertThat(item.getImageUri(), equalTo(uri));
                return true;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("has updated fields");
            }
        }), argThat(contains(tagIds))); // new tags
    }

    @NonNull
    public static <T> Matcher<Collection<T>> contains(final Collection<T> items) {
        return new TypeSafeMatcher<Collection<T>>() {
            @Override
            protected boolean matchesSafely(Collection<T> item) {
                return item.containsAll(items);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("contains items: " + items);
            }
        };
    }

    @NonNull
    public static Matcher<IngredientTypeParcel> hasIngredientId(final IngredientTemplate example) {
        return new TypeSafeMatcher<IngredientTypeParcel>() {
            @Override
            protected boolean matchesSafely(IngredientTypeParcel item) {
                Parcel withId = Parcel.obtain();
                item.writeToParcel(withId, 0);
                withId.setDataPosition(0);
                long id = withId.readLong();
                withId.recycle();
                return example.getId() == id;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("matches ingredient type with id " + example.getId());
            }
        };
    }

    @NonNull
    private static Function<Tag, JointIngredientTag> intoJTags(final IngredientTemplate example) {
        return new Function<Tag, JointIngredientTag>() {
            @Nullable
            @Override
            public JointIngredientTag apply(@Nullable Tag tag) {
                JointIngredientTag spy = Mockito.spy(new JointIngredientTag(null, example.getId(), tag.getId()));
                doReturn(tag).when(spy).getTag();
                return spy;
            }
        };
    }

    @NonNull
    private static Function<Tag, Long> intoTagIds() {
        return new Function<Tag, Long>() {
            @Nullable
            @Override
            public Long apply(@Nullable Tag tag) {
                return tag.getId();
            }
        };
    }
}
