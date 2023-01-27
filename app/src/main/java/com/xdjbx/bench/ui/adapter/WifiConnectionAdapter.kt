package com.xdjbx.bench.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.xdjbx.bench.ui.data.WifiConnection
import com.xdjbx.bench.databinding.FragmentWifiBinding


class WifiConnectionAdapter(private val wifiList: ArrayList<WifiConnection>) : RecyclerView.Adapter<WifiConnectionAdapter.ViewHolder>() {

    inner class ViewHolder(binding: FragmentWifiBinding) : RecyclerView.ViewHolder(binding.root) {
        val wifiName: TextView = binding.wifiName
        val wifiCheckBox: CheckBox = binding.wifiSelected

        override fun toString(): String {
            return "${super.toString()} - ${wifiName.text} - ${wifiCheckBox.isChecked}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            FragmentWifiBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return wifiList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.wifiName.text = wifiList[position].name
        holder.wifiCheckBox.isChecked = wifiList[position].checked
    }
}