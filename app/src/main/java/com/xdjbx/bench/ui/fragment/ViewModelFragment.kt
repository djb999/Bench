package com.xdjbx.bench.ui.fragment

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.xdjbx.bench.R

class ViewModelFragment : Fragment() {

    companion object {
        fun newInstance() = ViewModelFragment()
    }

    private lateinit var viewModel: ViewModelViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_view_model, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ViewModelViewModel::class.java)
        // TODO: Use the ViewModel
    }

}