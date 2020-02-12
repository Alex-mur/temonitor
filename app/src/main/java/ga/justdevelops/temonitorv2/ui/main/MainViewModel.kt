package ga.justdevelops.temonitorv2.ui.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ga.justdevelops.temonitorv2.R
import ga.justdevelops.temonitorv2.entity.Sensor
import ga.justdevelops.temonitorv2.fromOffsetDateTime
import ga.justdevelops.temonitorv2.network.DataSource
import ga.justdevelops.temonitorv2.network.TeMonitorWebDataSource
import ga.justdevelops.temonitorv2.settings.PaperSettings
import ga.justdevelops.temonitorv2.settings.Settings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.threeten.bp.OffsetDateTime
import java.lang.Exception
import java.util.*

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val dataSource: DataSource = TeMonitorWebDataSource()
    private val settings: Settings = PaperSettings(application)
    private val sensorsList = MutableLiveData<List<Sensor>>()
    private val isShowEditAddressDialog = MutableLiveData<Boolean>()
    private val isShowConnectionAlert = MutableLiveData<Boolean>()
    private lateinit var updatingTimer: Timer
    private lateinit var updatingTask: TimerTask


    fun startSensorsDataUpdating() {
        getSavedSensorsData()
        updatingTimer = Timer()
        updatingTask = object: TimerTask() {
            override fun run() {
                updateSensorsData()
            }
        }
        updatingTimer.schedule(updatingTask, 2000, 60000)
    }

    fun stopSensorsUpdating() {
        updatingTimer.cancel()
    }

    fun renameSensor(id: Int, newName: String) {
        val sensors = sensorsList.value
        sensors?.find { it.id == id }?.name = newName
        sensorsList.postValue(sensors)
        settings.saveSensors(sensors!!)
    }

    fun getSensorsList(): LiveData<List<Sensor>> = sensorsList

    fun setDeviceAddress(address:String) {
        stopSensorsUpdating()
        isShowEditAddressDialog.postValue(false)
        isShowConnectionAlert.postValue(false)
        settings.saveDeviceAddress(address)
        startSensorsDataUpdating()
    }

    fun onSettingsBtnPressed() {
        isShowEditAddressDialog.postValue(true)
    }

    fun getIsShowEditAddressDialog(): LiveData<Boolean> = isShowEditAddressDialog

    fun getIsShowConnectionAlert(): LiveData<Boolean> = isShowConnectionAlert

    private fun updateSensorsData() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                settings.getDeviceAddress()?.let { address ->
                    dataSource.getSensorsData(address).await().let { values ->
                        sensorsList.value?.let { sensors ->
                            sensorsList.postValue(updateSensorsValues(sensors, values))

                        } ?: sensorsList.postValue(createNewSensorsList(values))

                        settings.saveSensors(sensorsList.value!!)
                        isShowConnectionAlert.postValue(false)
                    }
                } ?: run {
                    isShowEditAddressDialog.value?.let {
                        if (!it) isShowEditAddressDialog.postValue(true)
                    } ?: isShowEditAddressDialog.postValue(true)
                }

            } catch (e: Exception) {
                isShowConnectionAlert.postValue(true)
            }
        }
    }

    private fun getSavedSensorsData() {
        settings.getSensors()?.let {
            sensorsList.postValue(it)
        }
    }

    private fun createNewSensorsList(values: List<String>): List<Sensor> {
        val newSensorsList = ArrayList<Sensor>()
        for (i in 0..7) {
            newSensorsList.add(
                Sensor(
                    i,
                    "${getApplication<Application>()
                        .applicationContext.getString(
                        R.string.sensor_name_default_text
                    )}_$i",
                    values[i],
                    fromOffsetDateTime(OffsetDateTime.now())
                )
            )
        }
        return newSensorsList
    }

    private fun updateSensorsValues(sensors: List<Sensor>, values: List<String>): List<Sensor> {
        sensors.forEach {
            it.value = values[it.id]
            it.date = fromOffsetDateTime(OffsetDateTime.now())
        }
        return sensors
    }
}
