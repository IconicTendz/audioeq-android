package com.audioeq.audio

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioDeviceInfo
import android.media.AudioManager
import com.audioeq.data.db.entity.HeadphoneProfileEntity
import com.audioeq.data.db.dao.HeadphoneProfileDao
import com.audioeq.data.model.DeviceType
import com.audioeq.data.model.HeadphoneProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HeadphoneProfileManager(
    private val context: Context,
    private val profileDao: HeadphoneProfileDao
) {

    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

    private val _currentDeviceType = MutableStateFlow(DeviceType.BUILT_IN_SPEAKER)
    val currentDeviceType: StateFlow<DeviceType> = _currentDeviceType

    private val _currentDeviceName = MutableStateFlow("Built-in Speaker")
    val currentDeviceName: StateFlow<String> = _currentDeviceName

    private val _matchingProfile = MutableStateFlow<HeadphoneProfile?>(null)
    val matchingProfile: StateFlow<HeadphoneProfile?> = _matchingProfile

    private val audioDeviceReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                BluetoothDevice.ACTION_ACL_CONNECTED,
                BluetoothDevice.ACTION_ACL_DISCONNECTED -> {
                    detectCurrentDevice()
                }
                AudioManager.ACTION_AUDIO_BECOMING_NOISY -> {
                    detectCurrentDevice()
                }
            }
        }
    }

    fun startMonitoring() {
        val filter = IntentFilter().apply {
            addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
            addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
            addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
        }
        context.registerReceiver(audioDeviceReceiver, filter)
        detectCurrentDevice()
    }

    fun stopMonitoring() {
        try {
            context.unregisterReceiver(audioDeviceReceiver)
        } catch (_: Exception) {}
    }

    fun detectCurrentDevice() {
        val devices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
        val activeDevice = devices.firstOrNull { it.isSource != true } ?: devices.firstOrNull()

        if (activeDevice != null) {
            val type = when (activeDevice.type) {
                AudioDeviceInfo.TYPE_BUILTIN_SPEAKER -> DeviceType.BUILT_IN_SPEAKER
                AudioDeviceInfo.TYPE_WIRED_HEADPHONES -> DeviceType.WIRED_HEADPHONES
                AudioDeviceInfo.TYPE_WIRED_HEADSET -> DeviceType.WIRED_HEADPHONES
                AudioDeviceInfo.TYPE_BLUETOOTH_A2DP -> DeviceType.BLUETOOTH
                AudioDeviceInfo.TYPE_BLUETOOTH_SCO -> DeviceType.BLUETOOTH
                AudioDeviceInfo.TYPE_USB_DEVICE -> DeviceType.USB_DAC
                AudioDeviceInfo.TYPE_USB_HEADSET -> DeviceType.USB_DAC
                AudioDeviceInfo.TYPE_HDMI -> DeviceType.HDMI
                AudioDeviceInfo.TYPE_HDMI_ARC -> DeviceType.HDMI
                AudioDeviceInfo.TYPE_DOCK -> DeviceType.USB_DAC
                else -> DeviceType.WIRED_HEADPHONES
            }

            val deviceName = activeDevice.productName?.toString() ?: "Unknown Device"
            val bluetoothName = if (type == DeviceType.BLUETOOTH) {
                getConnectedBluetoothName()
            } else ""

            _currentDeviceType.value = type
            _currentDeviceName.value = deviceName

            // Find matching profile
            findMatchingProfile(type, deviceName, bluetoothName)
        }
    }

    private fun getConnectedBluetoothName(): String {
        return bluetoothAdapter?.bondedDevices?.firstOrNull { device ->
            device.type == BluetoothDevice.DEVICE_TYPE_CLASSIC || device.type == BluetoothDevice.DEVICE_TYPE_DUAL
        }?.name ?: ""
    }

    private fun findMatchingProfile(type: DeviceType, name: String, bluetoothName: String) {
        // Run async lookup
        kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
            val profile = profileDao.findProfileByBluetooth(bluetoothName, "")
            _matchingProfile.value = profile?.let { HeadphoneProfileEntity.toDomain(it) }
        }
    }

    fun createProfileForCurrentDevice(presetId: String? = null): HeadphoneProfile {
        val profile = HeadphoneProfile(
            name = _currentDeviceName.value,
            bluetoothName = _currentDeviceName.value,
            presetId = presetId,
            deviceType = _currentDeviceType.value,
            autoDetect = true
        )
        kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
            profileDao.insertProfile(HeadphoneProfileEntity.fromDomain(profile))
        }
        return profile
    }
}
