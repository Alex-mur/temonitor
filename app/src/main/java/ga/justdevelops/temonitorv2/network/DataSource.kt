package ga.justdevelops.temonitorv2.network

import kotlinx.coroutines.Deferred

interface DataSource {

    suspend fun getSensorsData(address: String): Deferred<List<String>>
}