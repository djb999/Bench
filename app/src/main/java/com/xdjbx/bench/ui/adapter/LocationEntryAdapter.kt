package com.xdjbx.bench.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.xdjbx.bench.ui.data.GeofenceEntry
import com.xdjbx.bench.databinding.FragmentLocationEntryBinding
import com.xdjbx.bench.databinding.FragmentWifiBinding


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

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val location = locationList[position]
        holder.locationName.text = "${location.id} - (${String.format("%.4g", location.lat)},${String.format("%.4g", location.long)})"
        holder.locationEntered.isChecked = location.entered
    }
}