package ga.justdevelops.temonitorv2.ui.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ga.justdevelops.temonitorv2.network.DataSource
import ga.justdevelops.temonitorv2.network.TeMonitorWebDataSource
import ga.justdevelops.temonitorv2.settings.Settings
import ga.justdevelops.temonitorv2.settings.SharedPrefSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val dataSource: DataSource = TeMonitorWebDataSource()
    private val settings: Settings = SharedPrefSettings(application)
    private val sensorsData: MutableLiveData<List<String>> = MutableLiveData()
    private val sensorsNames: MutableLiveData<List<String>> = MutableLiveData()

    private val updatingTimer: Timer = Timer()
    private val updatingTask: TimerTask = object: TimerTask() {
        override fun run() {
            updateSensorsData()
        }
    }


    fun startSensorsDataUpdating() {
        updatingTimer.schedule(updatingTask, 2000, 60000)
    }

    private fun updateSensorsData() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                sensorsData.value = dataSource.getSensorsData("91.215.88.136", "65002").await()

            } catch (e: Exception) {
                Log.d("MYTAG", e.message)
            }
        }
    }

    fun updateSensorsNames() {
        settings.getSensorsNames()?.let { names ->
            sensorsNames.value = names

        } ?: run {

            val result = ArrayList<String>()
            for (i in 0..7) {
                result.add("Sensor_$i")
            }
            sensorsNames.value = result
        }
    }

    fun changeSensorName(id: Int, newName: String) {
        val result: ArrayList<String> = ArrayList<String>()
        result.addAll (sensorsNames.value!!)
        result[id] = newName
        sensorsNames.value = result
        settings.setSensorsNames(result)
    }

    fun getSensorsData(): LiveData<List<String>> = sensorsData

    fun getSensorsNames(): LiveData<List<String>> = sensorsNames
}
