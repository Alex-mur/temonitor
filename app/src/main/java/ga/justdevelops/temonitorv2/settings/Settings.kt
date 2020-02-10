package ga.justdevelops.temonitorv2.settings

import ga.justdevelops.temonitorv2.entity.Sensor

interface Settings {

    fun saveSensors(sensors: List<Sensor>)

    fun saveDeviceAddress(address: String)

    fun getDeviceAddress(): String?

    fun getSensors(): List<Sensor>?
}