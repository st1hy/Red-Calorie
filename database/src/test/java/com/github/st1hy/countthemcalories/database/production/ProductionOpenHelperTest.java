package com.github.st1hy.countthemcalories.database.production;

import android.content.Context;
import android.util.Log;

import org.greenrobot.greendao.database.Database;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.matches;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(PowerMockRunner.class)
@PrepareForTest({Log.class})
public class ProductionOpenHelperTest {
    final int from = 1;
    final int to = 2;

    @Mock
    private Database db;
    @Mock
    private Context context;
    @Mock
    private DatabaseMigrationHelper migrationHelper;

    private ProductionOpenHelper openHelper;

    @Before
    public void setUp() throws Exception {
        openHelper = new ProductionOpenHelper(context, "test", null, migrationHelper);
    }

    @Test
    public void testUpgrade() throws Exception {
        when(migrationHelper.onUpgrade(any(Database.class),anyInt(),anyInt())).thenReturn(true);

        openHelper.onUpgrade(db, from, to);

        verify(migrationHelper, only()).onUpgrade(db, from, to);
    }


    @Test
    public void testUpgradeFailed() throws Exception {
        PowerMockito.mockStatic(Log.class);
        when(migrationHelper.onUpgrade(any(Database.class),anyInt(),anyInt())).thenReturn(false);

        openHelper.onUpgrade(db, from, to);

        verify(migrationHelper).onUpgrade(db, from, to);
        verify(db, times(5)).execSQL(matches("DROP TABLE.*"));
        verify(db, times(5)).execSQL(matches("CREATE TABLE.*"));
    }
}