package ga.justdevelops.temonitorv2.network

import ga.justdevelops.temonitorv2.entity.Sensor
import ga.justdevelops.temonitorv2.fromOffsetDateTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import org.threeten.bp.OffsetDateTime
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.URL
import java.net.URLConnection


class TeMonitorWebDataSource: DataSource {

    override suspend fun getSensorsData(address: String): Deferred<List<Sensor>> {
        return CoroutineScope(Dispatchers.IO).async {
            var pageData = ""
            val targetURL = "http://" + address + "/tiny.htm"
            val url = URL(targetURL)
            val con: URLConnection = url.openConnection()
            val input: InputStream = con.getInputStream()
            val br = BufferedReader(InputStreamReader(input))
            var line: String? = null
            while (br.readLine().also({ line = it }) != null) {
                pageData += line
            }
            br.close()
            input.close()

            val result = ArrayList<Sensor>()

            var temp = pageData
                .replace(" ", "")
                .replace(Regex("""\d#"""), "")
                .split("</BR>")

            for (i in 0..7) {
                result.add(Sensor(i, "Sensor_$i", temp[i], fromOffsetDateTime(OffsetDateTime.now())))
            }

            result
        }
    }
}