package com.github.st1hy.countthemcalories.ui.activities.settings.model.unit

import android.support.annotation.StringRes
import com.github.st1hy.countthemcalories.ui.R

enum class AmountUnitType { MASS, VOLUME }

enum class VolumeUnit constructor(
        val id: Int,
        override val base: Double,
        @get:StringRes override val nameRes: Int
) : AmountUnit {

    ML(1, 1.0, R.string.unit_milliliter) {
        override fun getBaseUnit() = this
    },
    ML100(2, 100.0, R.string.unit_100_milliliter) {
        override fun getBaseUnit() = ML
    },
    FL_OZ(3, 29.5735295625, R.string.unit_fl_oz) {
        override fun getBaseUnit() = FL_OZ
    };

    override fun getType(): AmountUnitType = AmountUnitType.VOLUME

    companion object {

        @JvmStatic
        fun fromId(id: Int): VolumeUnit? {
            when (id) {
                1 -> return ML
                2 -> return ML100
                3 -> return FL_OZ
            }
            return null
        }
    }
}

enum class MassUnit constructor(
        val id: Int,
        override val base: Double,
        @get:StringRes override val nameRes: Int
) : AmountUnit {

    G(1, 1.0, R.string.unit_gram) {
        override fun getBaseUnit() = this
    },
    G100(2, 100.0, R.string.unit_100_gram) {
        override fun getBaseUnit() = G
    },
    OZ(3, 28.349523125, R.string.unit_oz) {
        override fun getBaseUnit() = OZ
    };

    override fun getType(): AmountUnitType = AmountUnitType.MASS

    companion object {

        @JvmStatic
        fun fromId(id: Int): MassUnit? {
            when (id) {
                1 -> return G
                2 -> return G100
                3 -> return OZ
            }
            return null
        }
    }
}

enum class EnergyUnit constructor(
        val id: Int,
        val base: Double,
        @get:StringRes override val nameRes: Int
) : NamedUnit {

    KJ(1, 1.0, R.string.unit_kj),
    KCAL(2, 4.184, R.string.unit_kcal);

    companion object {

        @JvmStatic
        fun fromId(id: Int): EnergyUnit? {
            when (id) {
                1 -> return KJ
                2 -> return KCAL
            }
            return null
        }
    }
}

interface NamedUnit {

    @get:StringRes
    val nameRes: Int
}

interface AmountUnit : NamedUnit {

    fun getType(): AmountUnitType

    /**
     * Sometimes units are not singular (ie 100g) and what makes sense in energy density (kcal/100g)
     * doesn't when typing specific amount: 2.5 100 g should be 250 g, we must convert those units back
     * to singular when typing mass. This value describes this relation.
     *
     * @return base singular unit or this if unit is already singular
     */
    fun getBaseUnit(): AmountUnit

    /**
     * Base for conversion between database format and this unit format.
     */
    val base: Double

}
