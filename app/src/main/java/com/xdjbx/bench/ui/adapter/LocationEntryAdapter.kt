package com.xdjbx.bench.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.xdjbx.bench.ui.data.GeofenceEntry
import com.xdjbx.sensors.databinding.FragmentLocationEntryBinding
import com.xdjbx.sensors.databinding.FragmentWifiBinding


class LocationEntryAdapter(private val locationList: ArrayList<GeofenceEntry>) : RecyclerView.Adapter<LocationEntryAdapter.ViewHolder>() {

    inner class ViewHolder(binding: FragmentLocationEntryBinding) : RecyclerView.ViewHolder(binding.root) {
        val locationName: TextView = binding.locationName
        val locationEntered: CheckBox = binding.locationEntered

        override fun toString(): String {
            return "${super.toString()} - ${locationName.text} - ${locationEntered.isChecked}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            FragmentLocationEntryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return locationList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.locationName.text = locationList[position].id
        holder.locationEntered.isChecked = locationList[position].entered
    }
}