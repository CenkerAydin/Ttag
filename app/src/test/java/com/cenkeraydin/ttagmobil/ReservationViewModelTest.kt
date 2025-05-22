package com.cenkeraydin.ttagmobil

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.cenkeraydin.ttagmobil.data.model.account.AvailableDriver
import com.cenkeraydin.ttagmobil.data.model.car.Car
import com.cenkeraydin.ttagmobil.data.retrofit.ApiService
import com.cenkeraydin.ttagmobil.ui.reservation.ReservationViewModel
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import retrofit2.Response

class ReservationViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: ReservationViewModel
    private lateinit var api: ApiService

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        val mockPrefs = mock(SharedPreferences::class.java)
        val mockContext = mock(Context::class.java)
        val mockApplication = mock(Application::class.java)
        whenever(mockApplication.applicationContext).thenReturn(mockContext)
        whenever(mockContext.getSharedPreferences(any(), any())).thenReturn(mockPrefs)
        viewModel = ReservationViewModel(mockApplication)

        api = mockk(relaxed = true)
        val apiField = ReservationViewModel::class.java.getDeclaredField("api")
        apiField.isAccessible = true
        apiField.set(viewModel, api)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `selectDriver updates selectedDriver and cars`() = runTest {
        val driver = AvailableDriver(
            driverId = "1",
            firstName = "Cenker",
            lastName = "Aydın",
            pictureUrl = "url",
            experienceYears = 3,
            cars = listOf(
                Car(id = "car1","2", carBrand = "Toyota", carModel = "Corolla", 3,2,100,
                    emptyList()
                ),
            )
        )
        viewModel.selectDriver(driver)

        assertEquals(driver, viewModel.selectedDriver.value)
        assertEquals(driver.cars, viewModel.cars.value)


    }


}