package ga.justdevelops.temonitorv2.ui.main

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import ga.justdevelops.temonitorv2.R
import kotlinx.android.synthetic.main.main_fragment.*

class MainFragment : Fragment(), EditAddressDialogFragment.EditAddressListener {

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
    }

    override fun onResume() {
        super.onResume()
        viewModel.startSensorsDataUpdating()
    }

    override fun onPause() {
        super.onPause()
        viewModel.stopSensorsUpdating()
    }

    private fun subscribeLiveData() {
        viewModel.getSensorsList().observe(this, Observer { list ->
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

        viewModel.getIsShowEditAddressDialog().observe(this, Observer {
            if (it) showChangeAddressDialog()
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

    private fun setSensorDate(id: Int, date: String) {
        sensorsViewList[id].findViewById<TextView>(R.id.sensor_date_value).text = date
    }

    private fun showRenameSensorDialog(id: Int) {
        val oldName = sensorsViewList.get(id).findViewById<TextView>(R.id.sensor_name).text
        val input = EditText(context)

        AlertDialog.Builder(context)
            .setTitle("Rename Sensor_$id: $oldName")
            .setView(input)
            .setPositiveButton(getString(R.string.btn_save)) { dialog, _ ->
                viewModel.renameSensor(id, input.text.toString())
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

        iv_settings.setOnClickListener {
            viewModel.onSettingsBtnPressed()
        }
    }

    private fun showChangeAddressDialog() {
        EditAddressDialogFragment().let {
            it.setStyle(DialogFragment.STYLE_NORMAL, 0)
            it.show(childFragmentManager, "")
        }
    }

    override fun onAddressEdited(address: String) {
        viewModel.setDeviceAddress(address)
    }

    override fun onEditAddressDialogDismissed() {
        viewModel.onEditAddressDialogDismissed()
    }
}
