package ga.justdevelops.temonitorv2.ui.main

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import ga.justdevelops.temonitorv2.R

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel
    private val sensorsViewList = ArrayList<FrameLayout>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.main_fragment, container, false)
        findAllViews(view)
        initListeners()
        return view
    }

    private fun findAllViews(view: View) {
        sensorsViewList.add(view.findViewById(R.id.sensor_0))
        sensorsViewList.add(view.findViewById(R.id.sensor_1))
        sensorsViewList.add(view.findViewById(R.id.sensor_2))
        sensorsViewList.add(view.findViewById(R.id.sensor_3))
        sensorsViewList.add(view.findViewById(R.id.sensor_4))
        sensorsViewList.add(view.findViewById(R.id.sensor_5))
        sensorsViewList.add(view.findViewById(R.id.sensor_6))
        sensorsViewList.add(view.findViewById(R.id.sensor_7))
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initViewModel()
        subscribeLiveData()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        viewModel.startSensorsDataUpdating()
    }

    private fun subscribeLiveData() {
        viewModel.getSensorsData().observe(this, Observer { list ->
            list.forEach {
                if (it.value.isNotEmpty()) {
                    setSensorName(it.id, it.name)
                    setSensorValue(it.id, it.value)
                    setSensorDate(it.id, it.date)
                    enableSensor(it.id)

                } else {
                    disableSensor(it.id)
                }
            }
        })
    }

    private fun enableSensor(id: Int) {
        sensorsViewList[id].visibility = View.VISIBLE
    }

    private fun disableSensor(id: Int) {
        sensorsViewList[id].visibility = View.GONE
    }

    private fun setSensorName(id: Int, name: String) {
        sensorsViewList[id].findViewById<TextView>(R.id.sensor_name).text = name
    }

    private fun setSensorValue(id: Int, value: String) {
        sensorsViewList[id].findViewById<TextView>(R.id.sensor_value).text = value
    }

    private fun setSensorDate(id: Int, date: String) {}

    private fun showRenameSensorDialog(id: Int) {
        val oldName = sensorsViewList.get(id).findViewById<TextView>(R.id.sensor_name).text
        val input = EditText(context)

        AlertDialog.Builder(context)
            .setTitle("Rename Sensor_$id: $oldName")
            .setView(input)
            .setPositiveButton(getString(R.string.btn_save)) { dialog, _ ->
                viewModel.changeSensorName(id, input.text.toString())
                dialog.dismiss()
            }
            .setNeutralButton(getString(R.string.btn_cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun initListeners() {
        for (i in 0..7) {
            sensorsViewList[i].findViewById<TextView>(R.id.sensor_name).setOnLongClickListener {
                showRenameSensorDialog(i)
                true
            }
        }
    }
}
