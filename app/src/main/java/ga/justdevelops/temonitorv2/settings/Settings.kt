package ga.justdevelops.temonitorv2.settings

interface Settings {

    fun setSensorsNames(names: List<String>)

    fun getSensorsNames(): List<String>?
}