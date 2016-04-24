package com.github.st1hy.countthemcalories.database;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;

public class DatabaseGenerator {

    public static void main(String[] args) throws Exception {
        Schema schema = new Schema(1001, "com.github.st1hy.countthemcalories.database");
        schema.setDefaultJavaPackageDao("com.github.st1hy.countthemcalories.database");
        schema.setDefaultJavaPackageDao("com.github.st1hy.countthemcalories.database");

        Entity ingredientTemplate = schema.addEntity("IngredientTemplate");
        ingredientTemplate.setTableName("INGREDIENTS_TEMPLATE");
        Property idPropertyOfIngredientTemplate = ingredientTemplate.addIdProperty()
                .index().unique().autoincrement().getProperty();
        ingredientTemplate.addStringProperty("name").notNull();
        ingredientTemplate.addStringProperty("imageUri");
        ingredientTemplate.addDateProperty("creationDate").notNull();
        ingredientTemplate.addIntProperty("caloriesUnit").notNull(); //i.e kcal / 100g
        ingredientTemplate.addIntProperty("amountUnit").notNull(); //i.e gram

        Entity ingredientComponent = schema.addEntity("Ingredient");
        ingredientComponent.setTableName("INGREDIENTS");
        Property idPropertyOfIngredient = ingredientComponent.addIdProperty()
                .index().unique().autoincrement().getProperty();
        ingredientComponent.addIntProperty("amount").notNull();

        Entity meal = schema.addEntity("Meal");
        meal.setTableName("MEALS");
        Property idPropertyOfMeal = meal.addIdProperty()
                .index().unique().autoincrement().getProperty();
        meal.addStringProperty("name");
        meal.addDateProperty("creationDate").notNull();

        Entity day = schema.addEntity("Day");
        day.setTableName("DAYS");
        day.addDateProperty("date");
        Property idOfTheDay = day.addIdProperty()
                .index().unique().autoincrement().getProperty();

        //what meal is composed of
        meal.addToMany(ingredientComponent, idPropertyOfIngredient);
        //what type this ingredient is
        ingredientComponent.addToOne(ingredientTemplate, idPropertyOfIngredientTemplate);
        //what meals contain this ingredient type
        ingredientTemplate.addToMany(meal, idPropertyOfMeal);
        //what was eaten this day
        day.addToMany(meal, idPropertyOfMeal);
        //when this meal was eaten
        meal.addToOne(day, idOfTheDay);

//        ContentProvider ingredientsContentProvider = ingredientTemplate.addContentProvider();
//        ingredientsContentProvider.setBasePath("ingredients");
//        ContentProvider mealsContentProvider = meal.addContentProvider();
//        mealsContentProvider.setBasePath("meals");
//        ContentProvider dayContentProvider = day.addContentProvider();
//        dayContentProvider.setBasePath("days");

        new DaoGenerator().generateAll(schema, "database/src/main/java", "database/src/main/java", "database/src/test/java");
    }

}
