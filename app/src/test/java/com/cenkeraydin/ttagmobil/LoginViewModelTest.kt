package com.cenkeraydin.ttagmobil

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.cenkeraydin.ttagmobil.data.retrofit.ApiService
import com.cenkeraydin.ttagmobil.ui.logins.LoginViewModel
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class LoginViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    private val testDispatcher = StandardTestDispatcher()
    private val app: Application = mockk(relaxed = true)
    private var api = mockk<ApiService>()
    private val viewModel = LoginViewModel(app)

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        api = mockk(relaxed = true)
        val apiField = LoginViewModel::class.java.getDeclaredField("api")
        apiField.isAccessible = true
        apiField.set(viewModel, api)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `clearLoginState sets state to null`() {
        val viewModel = LoginViewModel(app)
        viewModel.loginState = "Önce dolu"

        viewModel.clearLoginState()

        assertNull(viewModel.loginState)
    }


}
