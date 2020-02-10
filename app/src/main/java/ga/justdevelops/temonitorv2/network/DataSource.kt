package ga.justdevelops.temonitorv2.network

import kotlinx.coroutines.Deferred

interface DataSource {

    suspend fun getSensorsData(ipAddr: String, ipPort: String): Deferred<List<String>>
}