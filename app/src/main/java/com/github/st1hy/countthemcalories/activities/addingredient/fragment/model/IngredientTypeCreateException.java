package com.github.st1hy.countthemcalories.activities.addingredient.fragment.model;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import com.github.st1hy.countthemcalories.R;

import java.util.List;

public class IngredientTypeCreateException extends IllegalStateException {
    private final List<ErrorType> errors;

    public IngredientTypeCreateException(@NonNull List<ErrorType> errors) {
        this.errors = errors;
    }

    @NonNull
    public List<ErrorType> getErrors() {
        return errors;
    }

    public enum ErrorType {
        NO_NAME(R.string.add_ingredient_name_error_empty, InputType.NAME),
        NO_VALUE(R.string.add_ingredient_energy_density_error_empty, InputType.VALUE),
        ZERO_VALUE(R.string.add_ingredient_energy_density_error_zero, InputType.VALUE);

        private final int errorResId;
        @NonNull
        private final InputType inputType;

        ErrorType(@StringRes int errorResId, @NonNull InputType inputType) {
            this.errorResId = errorResId;
            this.inputType = inputType;
        }

        @StringRes
        public int getErrorResId() {
            return errorResId;
        }

        @NonNull
        public InputType getInputType() {
            return inputType;
        }

    }
}
