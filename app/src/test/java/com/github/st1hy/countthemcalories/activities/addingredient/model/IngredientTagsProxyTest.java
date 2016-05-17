package com.github.st1hy.countthemcalories.activities.addingredient.model;

import android.os.Bundle;
import android.os.Parcel;

import com.github.st1hy.countthemcalories.BuildConfig;
import com.github.st1hy.countthemcalories.activities.addingredient.model.IngredientTagsModel.ParcelableProxy;
import com.github.st1hy.countthemcalories.activities.tags.model.TagsModel;
import com.github.st1hy.countthemcalories.database.Tag;
import com.github.st1hy.countthemcalories.testutils.RobolectricConfig;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static com.github.st1hy.countthemcalories.activities.addingredient.model.IngredientTagsModel.ParcelableProxy.CREATOR;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricGradleTestRunner.class)
 @Config(constants = BuildConfig.class, sdk = RobolectricConfig.sdk, packageName = RobolectricConfig.packageName)
public class IngredientTagsProxyTest {

    TagsModel tagsModel;
    IngredientTagsModel model;

    @Before
    public void setUp() throws Exception {
        tagsModel = Mockito.mock(TagsModel.class);

        model = new IngredientTagsModel(tagsModel, null);

        model.addTag(1L, "Tag 1");
        model.addTag(2L, "Tag 2");
    }

    @Test
    public void testOnSavedState() throws Exception {
        Bundle bundle = new Bundle();

        model.onSaveState(bundle);

        IngredientTagsModel restoredModel = new IngredientTagsModel(tagsModel, bundle);

        assertThat(restoredModel.tags, hasSize(2));
        Tag tag0 = restoredModel.tags.get(0);
        Tag tag1 = restoredModel.tags.get(1);
        assertThat(tag0.getId(), equalTo(1L));
        assertThat(tag0.getName(), equalTo("Tag 1"));
        assertThat(tag1.getId(), equalTo(2L));
        assertThat(tag1.getName(), equalTo("Tag 2"));
    }

    @Test
    public void testToParcel() throws Exception {
        Parcel parcel = Parcel.obtain();
        model.parcelableProxy.snapshot(model).writeToParcel(parcel, 0);

        parcel.setDataPosition(0);
        ParcelableProxy fromParcel = CREATOR.createFromParcel(parcel);

        assertThat(fromParcel.tags, hasSize(2));
        Tag tag0 = fromParcel.tags.get(0);
        Tag tag1 = fromParcel.tags.get(1);
        assertThat(tag0.getId(), equalTo(1L));
        assertThat(tag0.getName(), equalTo("Tag 1"));
        assertThat(tag1.getId(), equalTo(2L));
        assertThat(tag1.getName(), equalTo("Tag 2"));
        parcel.recycle();
    }

    @Test
    public void testSnapshot() throws Exception {
        model.parcelableProxy.snapshot(model);

        assertThat(model.parcelableProxy.tags, hasSize(2));
        Tag tag0 = model.parcelableProxy.tags.get(0);
        Tag tag1 = model.parcelableProxy.tags.get(1);
        assertThat(tag0.getId(), equalTo(1L));
        assertThat(tag0.getName(), equalTo("Tag 1"));
        assertThat(tag1.getId(), equalTo(2L));
        assertThat(tag1.getName(), equalTo("Tag 2"));
    }

    @Test
    public void testProxyOther() throws Exception {
        MatcherAssert.assertThat(model.parcelableProxy.describeContents(), Matchers.equalTo(0));
        assertThat(IngredientTagsModel.ParcelableProxy.CREATOR.newArray(4),
                allOf(instanceOf(IngredientTagsModel.ParcelableProxy[].class), arrayWithSize(4)));
    }
}
