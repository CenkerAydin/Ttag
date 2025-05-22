package com.cenkeraydin.ttagmobil

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.cenkeraydin.ttagmobil.ui.reservation.UserReservationScreen
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserReservationScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private lateinit var vm: FakeReservationViewModel

    @Before
    fun setUp() {
        val app = composeRule.activity.application

        vm = FakeReservationViewModel(app)
        composeRule.setContent {
            UserReservationScreen(
                navHostController = rememberNavController(),
                viewModel = vm
            )
        }
    }
    @Test
    fun searchButton_enabled_afterValidInput() {
        val btnText   = composeRule.activity.getString(R.string.search_reservations)
        val dateHint  = composeRule.activity.getString(R.string.date)
        val hourHint  = composeRule.activity.getString(R.string.hour)
        val fromHint  = composeRule.activity.getString(R.string.from)
        val toHint    = composeRule.activity.getString(R.string.to)

        composeRule
            .onNodeWithText(btnText)
            .assertIsNotEnabled()

        composeRule.onNodeWithText(dateHint).performTextInput("2025-05-20")
        composeRule.onNodeWithText(hourHint).performTextInput("10:00")
        composeRule.onNodeWithText(fromHint).performTextInput("Antalya Havalimanı")
        composeRule.onNodeWithText(toHint).performTextInput("Konyaaltı")

        composeRule.waitForIdle()

        composeRule
            .onNodeWithText(btnText)
            .assertIsEnabled()
            .performClick()

    }
}
