package com.github.st1hy.countthemcalories.database.commands.ingredients;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.database.IngredientTagJoint;
import com.github.st1hy.countthemcalories.database.rx.IngredientRemovalEffect;
import com.github.st1hy.countthemcalories.database.rx.RxIngredientsDatabaseModel;
import com.github.st1hy.countthemcalories.database.commands.AbstractCommandResponse;
import com.github.st1hy.countthemcalories.database.commands.Command;
import com.github.st1hy.countthemcalories.database.commands.CommandResponse;
import com.github.st1hy.countthemcalories.database.commands.InsertResult;
import com.github.st1hy.countthemcalories.database.DaoSession;
import com.github.st1hy.countthemcalories.database.Ingredient;
import com.github.st1hy.countthemcalories.database.IngredientTemplate;
import com.github.st1hy.countthemcalories.database.IngredientTemplateDao;
import com.github.st1hy.countthemcalories.database.Meal;

import rx.Observable;

class InsertCommand implements Command<InsertResult, Cursor> {
    @NonNull
    private final RxIngredientsDatabaseModel databaseModel;
    @NonNull
    private final IngredientsDatabaseCommands commands;
    @NonNull
    private final IngredientRemovalEffect whatsRemoved;

    InsertCommand(@NonNull RxIngredientsDatabaseModel databaseModel,
                  @NonNull IngredientsDatabaseCommands commands,
                  @NonNull IngredientRemovalEffect whatsRemoved) {
        this.databaseModel = databaseModel;
        this.commands = commands;
        this.whatsRemoved = whatsRemoved;
    }

    @Override
    public Observable<CommandResponse<InsertResult, Cursor>> executeInTx() {
        return databaseModel.fromDatabaseTask(this);
    }

    @Override
    public CommandResponse<InsertResult, Cursor> call() throws Exception {
        DaoSession session = databaseModel.session();
        IngredientTemplateDao ingredientTemplateDao = session.getIngredientTemplateDao();
        IngredientTemplate template = whatsRemoved.getTemplate();
        ingredientTemplateDao.insertOrReplace(template);
        session.getIngredientTagJointDao().insertOrReplaceInTx(whatsRemoved.getJointedTags());
        template.resetTags();
        template.getTags();
        session.getMealDao().insertOrReplaceInTx(whatsRemoved.getMeals());
        session.getIngredientDao().insertOrReplaceInTx(whatsRemoved.getChildIngredients());
        for (IngredientTagJoint jTag : whatsRemoved.getJointedTags()) {
            jTag.refresh();
        }
        for (Ingredient ingredient : whatsRemoved.getChildIngredients()) {
            ingredient.refresh();
        }
        for (Meal meal : whatsRemoved.getMeals()) {
            meal.resetIngredients();
            meal.refresh();
            meal.getIngredients();
        }
        session.clear();
        Cursor cursor = databaseModel.refresh().call();
        InsertResult result = getResult(template, cursor);
        return new InsertResponse(result, template);
    }

    @NonNull
    private InsertResult getResult(IngredientTemplate template, Cursor cursor) {
        int position = databaseModel.findInCursor(cursor, template);
        return new InsertResult(cursor, position);
    }

    private class InsertResponse extends AbstractCommandResponse<InsertResult, Cursor> {
        final IngredientTemplate ingredient;

        public InsertResponse(@NonNull InsertResult result, @NonNull IngredientTemplate ingredient) {
            super(result, false);
            this.ingredient = ingredient;
        }

        @NonNull
        @Override
        protected Observable<CommandResponse<Cursor, InsertResult>> reverseCommand() {
            return commands.delete(ingredient);
        }
    }
}
