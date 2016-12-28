package com.github.st1hy.countthemcalories.database.production;

import org.greenrobot.greendao.database.Database;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;

@RunWith(MockitoJUnitRunner.class)
public class DatabaseMigrationHelperTest {
    private final DatabaseMigrationHelper migrationHelper = new DatabaseMigrationHelper();
    @Mock
    private Database db;

    @Test
    public void testUpgrade() throws Exception {
        assertFalse(migrationHelper.onUpgrade(db, 1, 2));
    }

    @Test
    public void testDowngrade() throws Exception {
        assertFalse(migrationHelper.onDowngrade(db, 2, 1));
    }
}