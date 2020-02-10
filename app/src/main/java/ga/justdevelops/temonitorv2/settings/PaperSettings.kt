package ga.justdevelops.temonitorv2.settings

import android.content.Context
import ga.justdevelops.temonitorv2.entity.Sensor
import io.paperdb.Paper

class PaperSettings(context: Context): Settings {

    companion object {
        private const val BOOK_NAME = "BOOK_NAME"
        private const val KEY_SENSORS = "KEY_SENSORS"
        private const val KEY_DEVICE_ADDRESS = "KEY_DEVICE_ADDRESS"
    }

    init {
        Paper.init(context)
    }

    private val book = Paper.book(BOOK_NAME)

    override fun saveSensors(sensors: List<Sensor>) {
        book.write(KEY_SENSORS, sensors)
    }

    override fun saveDeviceAddress(address: String) {
        book.write(KEY_DEVICE_ADDRESS, address)
    }

    override fun getDeviceAddress(): String? {
        return book.read(KEY_DEVICE_ADDRESS, null)
    }

    override fun getSensors(): List<Sensor>? {
        return book.read(KEY_SENSORS, null)
    }
}