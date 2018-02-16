package com.github.st1hy.countthemcalories.ui.activities.settings.view

import android.view.View
import com.github.st1hy.countthemcalories.ui.inject.app.PerFragment
import com.github.st1hy.countthemcalories.ui.inject.quantifier.view.FragmentRootView
import kotlinx.android.synthetic.main.settings_content.view.*
import javax.inject.Inject


@PerFragment internal class UnitsViewHolder @Inject constructor(
        @FragmentRootView private val root: View) {
    val energy: View = root.settings_unit_energy
    val mass: View = root.settings_unit_mass
    val volume: View = root.settings_unit_volume
    val bodyMass: View = root.settings_unit_body_mass
}
