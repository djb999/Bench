package com.xdjbx.bench.ui.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.xdjbx.bench.notification.LocalNotificationManager
import com.xdjbx.bench.ui.viewmodel.DeviceSharedViewModel
import com.xdjbx.bench.R
import kotlinx.android.synthetic.main.fragment_diagnostics.*
import kotlinx.android.synthetic.main.fragment_diagnostics.view.*
import kotlinx.android.synthetic.main.fragment_location_list.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DiagnosticsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DiagnosticsFragment : DeviceBaseFragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val layout = inflater.inflate(R.layout.fragment_diagnostics, container, false)
        val root = layout.rootView

        root.testNotificationButton.setOnClickListener {
            LocalNotificationManager(requireActivity()).notifyMe("Take your towel")
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        deviceSharedViewModel.eventList.observe(viewLifecycleOwner, Observer { events ->
            setAdapter()
        })

        buttonClearEvents.setOnClickListener {
            deviceSharedViewModel.clearEvents()
        }
    }

    private fun setAdapter() {
        val adapter = deviceSharedViewModel.eventList.value?.let {
            ArrayAdapter(
                requireActivity(),
                android.R.layout.simple_list_item_1,
                it.toTypedArray()
            )
        }
        event_list_view.adapter = adapter
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DiagnosticsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DiagnosticsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}