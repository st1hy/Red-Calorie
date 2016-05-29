package com.github.st1hy.countthemcalories.activities.addingredient.model;

import android.os.Bundle;
import android.os.Parcel;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.BuildConfig;
import com.github.st1hy.countthemcalories.activities.addingredient.model.IngredientTagsModel.ParcelableProxy;
import com.github.st1hy.countthemcalories.activities.tags.model.TagsModel;
import com.github.st1hy.countthemcalories.database.Tag;
import com.github.st1hy.countthemcalories.testutils.RobolectricConfig;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.Arrays;

import static com.github.st1hy.countthemcalories.activities.addingredient.model.IngredientTagsModel.ParcelableProxy.CREATOR;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = RobolectricConfig.sdk, packageName = RobolectricConfig.packageName)
public class IngredientTagsTest {

    @Mock
    TagsModel tagsModel;
    IngredientTagsModel model;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

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


    @Test
    public void testGetTag() throws Exception {
        assertThat(model.getTagAt(0), hasName("Tag 1"));
        assertThat(model.getTagAt(1), hasName("Tag 2"));
    }

    @Test
    public void testGetSize() throws Exception {
        assertThat(model.getSize(), equalTo(2));
    }

    @Test
    public void testReplaceTags() throws Exception {
        model.replaceTags(Arrays.asList(new Tag(1L, "Tag replace 1"), new Tag(223L, "Tag replace 2")));
        assertThat(model.getSize(), equalTo(2));
        assertThat(model.getTagAt(0), hasValues(1L, "Tag replace 1"));
        assertThat(model.getTagAt(1), hasValues(223L, "Tag replace 2"));
    }

    @Test
    public void testRemove() throws Exception {
        model.remove(model.getTagAt(0));
        assertThat(model.getSize(), equalTo(1));
        assertThat(model.getTagAt(0), hasName("Tag 2"));
    }

    @Test
    public void testGetTagIds() throws Exception {
        assertThat(model.getTagIds(), hasSize(2));
        assertThat(model.getTagIds(), contains(1L, 2L));
    }

    @NonNull
    private static Matcher<Tag> hasName(@NonNull final String name) {
        return new TypeSafeMatcher<Tag>() {
            @Override
            protected boolean matchesSafely(Tag item) {
                return name.equals(item.getName());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("has name: " + name);
            }
        };
    }


    @NonNull
    private static Matcher<Tag> hasValues(@NonNull final Long id, @NonNull final String name) {
        return new TypeSafeMatcher<Tag>() {
            @Override
            protected boolean matchesSafely(Tag item) {
                return item.getId().equals(id) && name.equals(item.getName());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("has id: " + id + " and has name: " + name);
            }
        };
    }
}
