package br.com.ceccon.andre.utils

import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import br.com.ceccon.andre.R

object NavigationExtension {
    private val slideLeftOptions = NavOptions.Builder()
        .setEnterAnim(R.anim.slide_in_right)
        .setExitAnim(R.anim.slide_out_left)
        .setPopEnterAnim(R.anim.slide_in_left)
        .setPopExitAnim(R.anim.slide_out_right)
        .setLaunchSingleTop(true)
        .build()

    fun NavController.navigateWithAnimations(
        directions: NavDirections,
        animation: NavOptions = slideLeftOptions
    ) {
        this.navigate(directions, animation)
    }
}
