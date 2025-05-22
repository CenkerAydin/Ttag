package com.cenkeraydin.ttagmobil

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.cenkeraydin.ttagmobil.data.model.account.Driver
import com.cenkeraydin.ttagmobil.data.model.account.DriverInfoResponse
import com.cenkeraydin.ttagmobil.data.model.car.Car
import com.cenkeraydin.ttagmobil.data.model.car.CarCreateRequest
import com.cenkeraydin.ttagmobil.data.model.car.CarResponse
import com.cenkeraydin.ttagmobil.data.retrofit.ApiService
import com.cenkeraydin.ttagmobil.ui.car.CarViewModel
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.spyk
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response


class CarViewModelTest {

    private val standartTestDispatcher = StandardTestDispatcher()
    private lateinit var api: ApiService
    private lateinit var app: Application
    private lateinit var viewModel: CarViewModel
    private lateinit var prefs: SharedPreferences

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(standartTestDispatcher)
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0

        api   = mockk(relaxed = true)
        app   = mockk(relaxed = true)
        prefs = mockk(relaxed = true)

        every { app.getSharedPreferences("user_prefs", Context.MODE_PRIVATE) } returns prefs
        every { prefs.getString("email", null) } returns "driver@mail.com"

        // ViewModel
        viewModel = CarViewModel(app).apply { this.api = this@CarViewModelTest.api }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `getCarsForUser populates carsUsers on success`() = runTest {

        val fakeCars = listOf(
            Car(id = "1","1","A", carModel = "A",3,3,3, emptyList() ),
            Car(id = "1","2","b", carModel = "A",3,3,3, emptyList() ),
        )
        val fakeResponse = Response.success(
            CarResponse(
            true,
            "Success",
            errors = null,
            data = fakeCars)
        )

        coEvery { api.getCars() } returns fakeResponse

        // Act
        viewModel.getCarsForUser()
        advanceUntilIdle()

        // Assert
        assertEquals(fakeCars, viewModel.carsUsers.first())
        assertNull(viewModel.errorMessages.first())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `getCarsForDriver populates _cars on success`() = runTest(standartTestDispatcher) {
        // Arrange – fake API response
        val fakeCars = listOf(
            Car(id = "1","1","A", carModel = "A",3,3,3, emptyList() ),
            Car(id = "1","2","b", carModel = "A",3,3,3, emptyList() ),
        )

        val driverInfoResponse = DriverInfoResponse(
            succeeded = true, message = "ok", errors = null,
            data = Driver("1","2",cars = fakeCars ,"1","1",2,"1","2","test@gmail.com","1",
                "url"
            )
        )

        coEvery { api.getDriverInfo("driver@mail.com") } returns Response.success(driverInfoResponse)

        // Act
        viewModel.getCarsForDriver(app)        // context paramı app verdik
        advanceUntilIdle()

        // Assert
        assertEquals(fakeCars, viewModel.cars.value)
        assertNull(viewModel.errorMessages.first())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `getCarsForDriver sets errorMessages when api fails`() = runTest(standartTestDispatcher) {
        // Arrange
        coEvery { api.getDriverInfo("driver@mail.com") } returns
                Response.error(500, "Err".toResponseBody(null))

        // Act
        viewModel.getCarsForDriver(app)
        advanceUntilIdle()

        // Assert
        assertEquals("Error: 500", viewModel.errorMessages.first())
        assertTrue(viewModel.cars.value.isEmpty())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `addCar calls onSuccess and getCarsForDriver on successful API response`() = runTest {
        val carRequest = CarCreateRequest("1","234567890123456", "Toyota", 3,2,100)

        val response = Response.success(Unit) // ya da response body neyse ona göre

        coEvery { api.addCar(carRequest) } returns response

        var onSuccessCalled = false
        var onErrorCalled = false

        val spyViewModel = spyk(viewModel)

        coEvery { spyViewModel.getCarsForDriver(any()) } just Runs

        spyViewModel.addCar(
            carRequest,
            onSuccess = { onSuccessCalled = true },
            onError = { onErrorCalled = true },
            context = app
        )

        advanceUntilIdle()

        // Asserts
        assertTrue(onSuccessCalled)
        assertFalse(onErrorCalled)
        coVerify { spyViewModel.getCarsForDriver(app) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `addCar calls onError on failed API response`() = runTest {
        val carRequest = CarCreateRequest("1","234567890123456", "Toyota", 3,2,100)
        val response = Response.error<Unit>(400, "Bad Request".toResponseBody())

        coEvery { api.addCar(carRequest) } returns response

        var onSuccessCalled = false
        var onErrorCalled = false
        var errorMessage = ""

        viewModel.addCar(
            carRequest,
            onSuccess = { onSuccessCalled = true },
            onError = {
                onErrorCalled = true
                errorMessage = it
            },
            context = app
        )

        advanceUntilIdle()

        assertFalse(onSuccessCalled)
        assertTrue(onErrorCalled)
        assertTrue(errorMessage.contains("Hata kodu: 400"))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `addCar calls onError on exception`() = runTest {
        val carRequest = CarCreateRequest("1","234567890123456", "Toyota", 3,2,100)

        coEvery { api.addCar(carRequest) } throws RuntimeException("Network fail")

        var onSuccessCalled = false
        var onErrorCalled = false
        var errorMessage = ""

        viewModel.addCar(
            carRequest,
            onSuccess = { onSuccessCalled = true },
            onError = {
                onErrorCalled = true
                errorMessage = it
            },
            context = app
        )

        advanceUntilIdle()

        assertFalse(onSuccessCalled)
        assertTrue(onErrorCalled)
        assertTrue(errorMessage.contains("Network fail"))
    }

}
