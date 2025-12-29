package com.example.dataagrin.app.data.connectivity

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import com.example.dataagrin.app.KoinTestRule
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ConnectivityCheckerTest {
    @get:Rule
    val koinTestRule = KoinTestRule()

    @Test
    fun `isConnectedToInternet returns true when connected via WiFi`() {
        // Given
        val context = mockk<Context>()
        val connectivityManager = mockk<ConnectivityManager>()
        val network = mockk<Network>()
        val capabilities = mockk<NetworkCapabilities>()

        every { context.getSystemService(Context.CONNECTIVITY_SERVICE) } returns connectivityManager
        every { connectivityManager.activeNetwork } returns network
        every { connectivityManager.getNetworkCapabilities(network) } returns capabilities
        every { capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) } returns true

        val connectivityChecker = ConnectivityChecker(context)

        // When
        val result = connectivityChecker.isConnectedToInternet()

        // Then
        assertTrue(result)
    }

    @Test
    fun `isConnectedToInternet returns false when no active network`() {
        // Given
        val context = mockk<Context>()
        val connectivityManager = mockk<ConnectivityManager>()

        every { context.getSystemService(Context.CONNECTIVITY_SERVICE) } returns connectivityManager
        every { connectivityManager.activeNetwork } returns null

        val connectivityChecker = ConnectivityChecker(context)

        // When
        val result = connectivityChecker.isConnectedToInternet()

        // Then
        assertFalse(result)
    }

    @Test
    fun `isConnectedToInternet returns true when connected via cellular`() {
        // Given
        val context = mockk<Context>()
        val connectivityManager = mockk<ConnectivityManager>()
        val network = mockk<Network>()
        val capabilities = mockk<NetworkCapabilities>()

        every { context.getSystemService(Context.CONNECTIVITY_SERVICE) } returns connectivityManager
        every { connectivityManager.activeNetwork } returns network
        every { connectivityManager.getNetworkCapabilities(network) } returns capabilities
        every { capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) } returns false
        every { capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) } returns true
        every { capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) } returns false

        val connectivityChecker = ConnectivityChecker(context)

        // When
        val result = connectivityChecker.isConnectedToInternet()

        // Then
        assertTrue(result)
    }

    @Test
    fun `isConnectedToInternet returns false when no valid transport`() {
        // Given
        val context = mockk<Context>()
        val connectivityManager = mockk<ConnectivityManager>()
        val network = mockk<Network>()
        val capabilities = mockk<NetworkCapabilities>()

        every { context.getSystemService(Context.CONNECTIVITY_SERVICE) } returns connectivityManager
        every { connectivityManager.activeNetwork } returns network
        every { connectivityManager.getNetworkCapabilities(network) } returns capabilities
        every { capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) } returns false
        every { capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) } returns false
        every { capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) } returns false

        val connectivityChecker = ConnectivityChecker(context)

        // When
        val result = connectivityChecker.isConnectedToInternet()

        // Then
        assertFalse(result)
    }
}
