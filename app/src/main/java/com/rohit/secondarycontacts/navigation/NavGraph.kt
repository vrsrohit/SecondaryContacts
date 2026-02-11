package com.rohit.secondarycontacts.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.rohit.secondarycontacts.ui.screens.AddContactScreen
import com.rohit.secondarycontacts.ui.screens.EditContactScreen
import com.rohit.secondarycontacts.ui.screens.HomeScreen
import com.rohit.secondarycontacts.viewmodel.ContactViewModel

object Routes {
    const val HOME = "home"
    const val ADD_CONTACT = "add_contact"
    const val EDIT_CONTACT = "edit_contact/{contactId}"

    fun editContact(contactId: Int) = "edit_contact/$contactId"
}

@Composable
fun NavGraph(
    navController: NavHostController,
    viewModel: ContactViewModel
) {
    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            HomeScreen(
                viewModel = viewModel,
                onAddContact = { navController.navigate(Routes.ADD_CONTACT) },
                onEditContact = { id -> navController.navigate(Routes.editContact(id)) }
            )
        }

        composable(Routes.ADD_CONTACT) {
            AddContactScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.EDIT_CONTACT,
            arguments = listOf(navArgument("contactId") { type = NavType.IntType })
        ) { backStackEntry ->
            val contactId = backStackEntry.arguments?.getInt("contactId") ?: return@composable
            EditContactScreen(
                contactId = contactId,
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
