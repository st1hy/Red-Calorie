package com.github.st1hy.countthemcalories.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.net.Uri;

import com.github.st1hy.countthemcalories.database.property.AmountUnitTypePropertyConverter;
import com.github.st1hy.countthemcalories.database.property.BigDecimalPropertyConverter;
import com.github.st1hy.countthemcalories.database.property.JodaTimePropertyConverter;
import com.github.st1hy.countthemcalories.database.property.UriPropertyConverter;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "INGREDIENTS_TEMPLATE".
*/
public class IngredientTemplateDao extends AbstractDao<IngredientTemplate, Long> {

    public static final String TABLENAME = "INGREDIENTS_TEMPLATE";

    /**
     * Properties of entity IngredientTemplate.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Name = new Property(1, String.class, "name", false, "NAME");
        public final static Property ImageUri = new Property(2, String.class, "imageUri", false, "IMAGE_URI");
        public final static Property CreationDate = new Property(3, long.class, "creationDate", false, "CREATION_DATE");
        public final static Property AmountType = new Property(4, int.class, "amountType", false, "AMOUNT_TYPE");
        public final static Property EnergyDensityAmount = new Property(5, String.class, "energyDensityAmount", false, "ENERGY_DENSITY_AMOUNT");
    };

    private DaoSession daoSession;

    private final UriPropertyConverter imageUriConverter = new UriPropertyConverter();
    private final JodaTimePropertyConverter creationDateConverter = new JodaTimePropertyConverter();
    private final AmountUnitTypePropertyConverter amountTypeConverter = new AmountUnitTypePropertyConverter();
    private final BigDecimalPropertyConverter energyDensityAmountConverter = new BigDecimalPropertyConverter();

    public IngredientTemplateDao(DaoConfig config) {
        super(config);
    }
    
    public IngredientTemplateDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"INGREDIENTS_TEMPLATE\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE ," + // 0: id
                "\"NAME\" TEXT NOT NULL ," + // 1: name
                "\"IMAGE_URI\" TEXT," + // 2: imageUri
                "\"CREATION_DATE\" INTEGER NOT NULL ," + // 3: creationDate
                "\"AMOUNT_TYPE\" INTEGER NOT NULL ," + // 4: amountType
                "\"ENERGY_DENSITY_AMOUNT\" TEXT NOT NULL );"); // 5: energyDensityAmount
        // Add Indexes
        db.execSQL("CREATE INDEX " + constraint + "IDX_INGREDIENTS_TEMPLATE__id ON INGREDIENTS_TEMPLATE" +
                " (\"_id\");");
        db.execSQL("CREATE INDEX " + constraint + "IDX_INGREDIENTS_TEMPLATE_NAME ON INGREDIENTS_TEMPLATE" +
                " (\"NAME\");");
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"INGREDIENTS_TEMPLATE\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, IngredientTemplate entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindString(2, entity.getName());
 
        Uri imageUri = entity.getImageUri();
        if (imageUri != null) {
            stmt.bindString(3, imageUriConverter.convertToDatabaseValue(imageUri));
        }
        stmt.bindLong(4, creationDateConverter.convertToDatabaseValue(entity.getCreationDate()));
        stmt.bindLong(5, amountTypeConverter.convertToDatabaseValue(entity.getAmountType()));
        stmt.bindString(6, energyDensityAmountConverter.convertToDatabaseValue(entity.getEnergyDensityAmount()));
    }

    @Override
    protected void attachEntity(IngredientTemplate entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(daoSession);
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public IngredientTemplate readEntity(Cursor cursor, int offset) {
        IngredientTemplate entity = new IngredientTemplate( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getString(offset + 1), // name
            cursor.isNull(offset + 2) ? null : imageUriConverter.convertToEntityProperty(cursor.getString(offset + 2)), // imageUri
            creationDateConverter.convertToEntityProperty(cursor.getLong(offset + 3)), // creationDate
            amountTypeConverter.convertToEntityProperty(cursor.getInt(offset + 4)), // amountType
            energyDensityAmountConverter.convertToEntityProperty(cursor.getString(offset + 5)) // energyDensityAmount
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, IngredientTemplate entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setName(cursor.getString(offset + 1));
        entity.setImageUri(cursor.isNull(offset + 2) ? null : imageUriConverter.convertToEntityProperty(cursor.getString(offset + 2)));
        entity.setCreationDate(creationDateConverter.convertToEntityProperty(cursor.getLong(offset + 3)));
        entity.setAmountType(amountTypeConverter.convertToEntityProperty(cursor.getInt(offset + 4)));
        entity.setEnergyDensityAmount(energyDensityAmountConverter.convertToEntityProperty(cursor.getString(offset + 5)));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(IngredientTemplate entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(IngredientTemplate entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
