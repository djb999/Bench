package com.xdjbx.bench.ui.adapter

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.xdjbx.sensors.R
import kotlinx.android.synthetic.main.bluetooth_list_item.view.*

class BluetoothListAdapter (context: Context, private val bluetoothList: List<BluetoothDevice>) : BaseAdapter() {

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return bluetoothList.size
    }

    override fun getItem(position: Int): Any {
        return bluetoothList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @SuppressLint("MissingPermission")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        val holder: ViewHolder

        if (convertView == null) {
            view = inflater.inflate(R.layout.bluetooth_list_item, parent, false)
            val root = view.rootView
            holder = ViewHolder()
            holder.textBluetoothName = root.textBluetoothName
            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as ViewHolder
        }

        holder.textBluetoothName.text = bluetoothList[position].name
        return view
    }

    private class ViewHolder {
        internal lateinit var textBluetoothName: TextView
    }
}
