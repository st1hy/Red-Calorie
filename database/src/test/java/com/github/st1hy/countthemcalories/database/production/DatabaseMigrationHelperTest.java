package com.github.st1hy.countthemcalories.database.production;

import android.database.sqlite.SQLiteDatabase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;

@RunWith(MockitoJUnitRunner.class)
public class DatabaseMigrationHelperTest {
    final DatabaseMigrationHelper migrationHelper = new DatabaseMigrationHelper();
    @Mock
    private SQLiteDatabase db;

    @Test
    public void testUpgrade() throws Exception {
        assertFalse(migrationHelper.onUpgrade(db, 1, 2));
    }

    @Test
    public void testDowngrade() throws Exception {
        assertFalse(migrationHelper.onDowngrade(db, 2, 1));
    }
}