package com.dmwaresolutions.myfuelcalculatorbr.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.dmwaresolutions.myfuelcalculatorbr.databinding.ItemVehicleBottomSheetBinding
import com.dmwaresolutions.myfuelcalculatorbr.model.Vehicle
import com.dmwaresolutions.myfuelcalculatorbr.ui.viewmodel.MainViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog

class BottomSheetAdapter(
    private val viewModel: MainViewModel,
    private val dialog: BottomSheetDialog
) :
    RecyclerView.Adapter<BottomSheetAdapter.BottomSheetViewHolder>() {

    class BottomSheetViewHolder(val itemBinding: ItemVehicleBottomSheetBinding) :
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BottomSheetViewHolder {
        return BottomSheetAdapter.BottomSheetViewHolder(
            ItemVehicleBottomSheetBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount() = differ.currentList.size

    override fun onBindViewHolder(holder: BottomSheetViewHolder, position: Int) {
        val currentItem = differ.currentList[position]

        holder.itemBinding.tvName.text = currentItem.name
        holder.itemBinding.tvFuel.text = currentItem.fuel
        holder.itemBinding.tvAuthonomy.text = currentItem.autonomy.toString() + " km/l"
        holder.itemView.setOnClickListener {
            viewModel.setBsVehicle(currentItem)
            dialog.dismiss()
        }
    }

}