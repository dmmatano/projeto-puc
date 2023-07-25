package com.dmwaresolutions.myfuelcalculatorbr.ui.adapter

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.dmwaresolutions.myfuelcalculatorbr.databinding.VehicleItemBinding
import com.dmwaresolutions.myfuelcalculatorbr.model.Vehicle
import com.dmwaresolutions.myfuelcalculatorbr.ui.MainActivity
import com.dmwaresolutions.myfuelcalculatorbr.ui.home.HomeFragmentDirections

class VehicleAdapter(private val activity: MainActivity) :
    RecyclerView.Adapter<VehicleAdapter.VehicleViewHolder>() {

    class VehicleViewHolder(val itemBinding: VehicleItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root)

    private val differCallback =
        object : DiffUtil.ItemCallback<Vehicle>() {
            override fun areItemsTheSame(oldItem: Vehicle, newItem: Vehicle): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Vehicle, newItem: Vehicle): Boolean {
                return oldItem.name == newItem.name &&
                        oldItem.autonomy == newItem.autonomy &&
                        oldItem.image == newItem.image &&
                        oldItem.fuel == newItem.fuel
            }
        }
    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VehicleViewHolder {
        return VehicleAdapter.VehicleViewHolder(
            VehicleItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: VehicleViewHolder, position: Int) {
        val currentVehicle = differ.currentList[position]

        holder.itemBinding.carNameTextView.text = currentVehicle.name
        holder.itemBinding.autonomyTextView.text = autonomyToString(currentVehicle.autonomy)
        holder.itemBinding.fuelTextView.text = currentVehicle.fuel

        loadUriImage(currentVehicle.image).let {
            if (it != null) holder.itemBinding.vehicleImageView.setImageBitmap(it)
        }

        holder.itemView.setOnClickListener {
            it.findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToVehiclesFragment(currentVehicle)
            )
        }
    }

    private fun loadUriImage(uriString: String?): Bitmap? {
        if (uriString == null) return null
        return BitmapFactory.decodeStream(
            activity.contentResolver.openInputStream(Uri.parse(uriString))
        )
    }

    private fun autonomyToString(autonomy: Double): CharSequence {
        return "Autonomia: $autonomy km/litro"
    }

}