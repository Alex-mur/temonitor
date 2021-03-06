package ga.justdevelops.temonitorv2.ui.main

import android.graphics.Point
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import ga.justdevelops.temonitorv2.R
import kotlinx.android.synthetic.main.edit_address_dialog_fragment.*

class EditAddressDialogFragment: DialogFragment() {

    companion object {
        private const val DEVICE_ADDRESS = "DEVICE_ADDRESS"

        fun getInstance(address: String?): EditAddressDialogFragment {
            val dialog = EditAddressDialogFragment()
            if (!address.isNullOrEmpty()) {
                Bundle().let {
                    it.putString(DEVICE_ADDRESS, address)
                    dialog.arguments = it
                }
            }
            return dialog
        }
    }

    interface EditAddressListener {
        fun onAddressEdited(address: String)
    }

    private lateinit var editAddressListener: EditAddressListener

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.edit_address_dialog_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.getString(DEVICE_ADDRESS)?.let {
            et_address.setText(it)
        }
        initListeners()
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.let {
            val size = Point()
            it.windowManager?.defaultDisplay?.getSize(size)
            it.setLayout(size.x, WindowManager.LayoutParams.WRAP_CONTENT)
            it.setGravity(Gravity.CENTER)
        }
    }

    private fun initListeners() {
        editAddressListener = parentFragment as EditAddressListener
        btn_set_address.setOnClickListener {
            validateInput(et_address.text.toString())?.let {
                editAddressListener.onAddressEdited(it)
                dismiss()
            } ?: showInputWarning()
        }
    }

    private fun showInputWarning() {
        et_address.error = getString(R.string.address_input_error_hint)
    }

    private fun validateInput(text: String?): String? {
        if(text != null && text.isNotEmpty() && text.matches(Regex("""[\w\d-.]+:\d{2,5}"""))) {
            return text

        } else return null
    }
}