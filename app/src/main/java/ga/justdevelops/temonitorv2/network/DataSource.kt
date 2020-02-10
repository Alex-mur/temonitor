package ga.justdevelops.temonitorv2.network

import ga.justdevelops.temonitorv2.entity.Sensor
import kotlinx.coroutines.Deferred

interface DataSource {

    suspend fun getSensorsData(address: String): Deferred<List<Sensor>>
}