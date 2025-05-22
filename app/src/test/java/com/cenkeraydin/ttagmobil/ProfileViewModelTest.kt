package com.cenkeraydin.ttagmobil

// ProfileViewModelTest.kt

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import com.cenkeraydin.ttagmobil.data.model.account.DeleteAccountResponse
import com.cenkeraydin.ttagmobil.data.model.account.Driver
import com.cenkeraydin.ttagmobil.data.model.account.User
import com.cenkeraydin.ttagmobil.data.retrofit.ApiService
import com.cenkeraydin.ttagmobil.util.UserPrefsHelper
import com.cenkeraydin.ttagmobil.ui.profile.ProfileViewModel
import com.cenkeraydin.ttagmobil.util.DriverPrefsHelper
import io.mockk.Called
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.lang.reflect.Field

class ProfileViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var api: ApiService
    private val context = mockk<Context>(relaxed = true)


    private lateinit var userPrefsHelper: UserPrefsHelper
    private lateinit var driverPrefs: DriverPrefsHelper
    private lateinit var viewModel: ProfileViewModel
    private val application = mockk<Application>(relaxed = true)
    private val navHostController = mockk<NavHostController>(relaxed = true)

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        userPrefsHelper = mockk(relaxed = true)
        viewModel = ProfileViewModel(application)
        viewModel = ProfileViewModel(application).apply {
            _user.value = User(
                id = "1",
                firstName = null,
                lastName  = null,
                email = "test@example.com",
                phoneNumber = null,
                userName = null,
                pictureUrl = null
            )
        }
        mockkStatic(Toast::class)
        every { Toast.makeText(any(), any<String>(), any()) } returns mockk(relaxed = true)
        mockkStatic(android.util.Log::class)
        every { android.util.Log.e(any(), any()) } returns 0
        api = mockk(relaxed = true)
        driverPrefs = mockk<DriverPrefsHelper>(relaxed = true)


        val fieldUser: Field = ProfileViewModel::class.java.getDeclaredField("userPrefs")
        fieldUser.isAccessible = true
        fieldUser.set(viewModel, userPrefsHelper)

        val fieldDriver = ProfileViewModel::class.java.getDeclaredField("driverPrefs")
        fieldDriver.isAccessible = true
        fieldDriver.set(viewModel, driverPrefs)

        val apiField = ProfileViewModel::class.java.getDeclaredField("api")
        apiField.isAccessible = true
        apiField.set(viewModel, api)
    }
    
    

    @Test
    fun `setUser updates user StateFlow and calls saveUser`() = runTest {
        val user = User(
            id = "123",
            email = "test@domain.com",
            firstName = "Ali",
            lastName = "Veli",
            phoneNumber = "5551234",
            userName = "testuser",
            pictureUrl = "url"
        )

        viewModel.setUser(user)

        // StateFlow güncellendi mi?
        assertEquals(user, viewModel.user.value)

        // saveUser çağrıldı mı?
        // Eğer UserPrefsHelper mock'u viewModel'a inject ettiysen kullanılabilir
        verify { userPrefsHelper.saveUser(user) }
    }

    @Test
    fun `setDriver updates driver StateFlow and calls saveDriver`() = runTest {
        val driver = Driver(
            email = "test@driver.com",
            firstName = "Ali",
            lastName = "Veli",
            identityNo = "12345678901",
            licenseUrl = "license-url",
            experienceYears = 5,
            phoneNumber = "5550001111",
            pictureUrl = "driver-pic",
            id = "driverid",
            userId = "userid",
            cars = emptyList()
        )


        viewModel.setDriver(driver)

        assertEquals(driver, viewModel.driver.value)
        verify { driverPrefs.saveDriver(driver) }
    }

    @Test
    fun `logout clears user and driver StateFlows and calls clear methods`() = runTest {
        // User ve driver için StateFlow'ları başlat (gerekirse)
        val user = User(
            id = "123",
            email = "test@domain.com",
            firstName = "Ali",
            lastName = "Veli",
            phoneNumber = "5551234",
            userName = "testuser",
            pictureUrl = "url"
        )
        viewModel.setUser(user)

        val driver = Driver(
            email = "test@driver.com",
            firstName = "Ali",
            lastName = "Veli",
            identityNo = "12345678901",
            licenseUrl = "license-url",
            experienceYears = 5,
            phoneNumber = "5550001111",
            pictureUrl = "driver-pic",
            id = "driverid",
            userId = "userid",
            cars = emptyList()
        )

        val driverField = ProfileViewModel::class.java.getDeclaredField("driverPrefs")
        driverField.isAccessible = true
        driverField.set(viewModel, driverPrefs)
        viewModel.setDriver(driver)

        viewModel.logout(navHostController)

        assertNull(viewModel.user.value)
        assertNull(viewModel.driver.value)

        verify { userPrefsHelper.clear() }
        verify { driverPrefs.clear() }
    }

    @Test
    fun `deleteAccount Passenger success shows toast and navigates`() = runTest {
        // given
        val responseBody = DeleteAccountResponse(
            succeeded = true,
            message = "Hesap silindi",
            errors = null,
            data = null
        )
        val successResponse = retrofit2.Response.success(responseBody)
        coEvery { api.deleteAccount("test@example.com") } returns successResponse

        // when
        viewModel.deleteAccount(context, navHostController, selectedRole = "Passenger")
        testDispatcher.scheduler.advanceUntilIdle()

        // then
        verify { Toast.makeText(context, "Hesap silindi", Toast.LENGTH_SHORT) }
        verify { navHostController.navigate("login", any<NavOptionsBuilder.() -> Unit>()) }
    }

    @Test
    fun `deleteAccount Driver failure shows error toast`() = runTest {
        // given
        val errorJson = "{\"message\":\"Silme işlemi başarısız\"}"
        val errorResponse = retrofit2.Response.error<DeleteAccountResponse>(
            400,
            errorJson.toResponseBody("application/json".toMediaTypeOrNull())
        )
        coEvery { api.deleteDriverAccount("test@example.com") } returns errorResponse

        // when
        viewModel.deleteAccount(context, navHostController, selectedRole = "Driver")
        testDispatcher.scheduler.advanceUntilIdle()

        // then
        verify { Toast.makeText(context, "Silme işlemi başarısız", Toast.LENGTH_SHORT) }
        verify(exactly = 0) { navHostController.navigate(any<String>(), any<NavOptionsBuilder.() -> Unit>()) }

    }

    @Test
    fun `deleteAccount null email shows email not found toast`() = runTest {
        // given
        viewModel._user.value = null

        // when
        viewModel.deleteAccount(context, navHostController, selectedRole = "Passenger")
        testDispatcher.scheduler.advanceUntilIdle()

        // then
        verify { Toast.makeText(context, "Email bulunamadı", Toast.LENGTH_SHORT) }
        verify { api wasNot Called }
        verify(exactly = 0) { navHostController.navigate(any<String>(), any<NavOptionsBuilder.() -> Unit>()) }

    }




    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }


}
