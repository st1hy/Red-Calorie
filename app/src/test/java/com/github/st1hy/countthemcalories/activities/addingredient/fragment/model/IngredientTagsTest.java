package com.github.st1hy.countthemcalories.activities.addingredient.fragment.model;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.database.Tag;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class IngredientTagsTest {

    private IngredientTagsModel model;

    @Before
    public void setUp() throws Exception {

        ArrayList<Tag> tags = new ArrayList<>(2);
        tags.add(new Tag(1L, "Tag 1"));
        tags.add(new Tag(2L, "Tag 2"));

        model = new IngredientTagsModel(tags);
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
