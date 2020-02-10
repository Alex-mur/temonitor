package ga.justdevelops.temonitorv2.ui.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ga.justdevelops.temonitorv2.entity.Sensor
import ga.justdevelops.temonitorv2.network.DataSource
import ga.justdevelops.temonitorv2.network.TeMonitorWebDataSource
import ga.justdevelops.temonitorv2.settings.PaperSettings
import ga.justdevelops.temonitorv2.settings.Settings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.*

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val dataSource: DataSource = TeMonitorWebDataSource()
    private val settings: Settings = PaperSettings(application)
    private val sensorsList = MutableLiveData<List<Sensor>>()
    private val updatingTimer: Timer = Timer()
    private val updatingTask: TimerTask = object: TimerTask() {
        override fun run() {
            updateSensorsData()
        }
    }


    fun startSensorsDataUpdating() {

        //for test
        setDeviceAddress("91.215.88.136:65002")


        getSavedSensorsData()
        updatingTimer.schedule(updatingTask, 2000, 60000)
    }

    fun changeSensorName(id: Int, newName: String) {
        sensorsList.value?.find { it.id == id }?.name = newName
        settings.saveSensors(sensorsList.value!!)
    }

    fun getSensorsData(): LiveData<List<Sensor>> = sensorsList

    fun setDeviceAddress(address:String) {
        settings.saveDeviceAddress(address)
    }

    private fun updateSensorsData() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                settings.getDeviceAddress()?.let {
                    sensorsList.value = dataSource.getSensorsData(it).await()
                    settings.saveSensors(sensorsList.value!!)
                }

            } catch (e: Exception) {
                Log.d("MYTAG", e.message)
            }
        }
    }

    private fun getSavedSensorsData() {
        settings.getSensors()?.let {
            sensorsList.value = it
        }
    }
}
