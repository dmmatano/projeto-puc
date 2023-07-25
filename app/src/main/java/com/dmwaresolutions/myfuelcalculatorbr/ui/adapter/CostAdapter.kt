package com.dmwaresolutions.myfuelcalculatorbr.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.dmwaresolutions.myfuelcalculatorbr.databinding.FuelCostItemBinding
import com.dmwaresolutions.myfuelcalculatorbr.model.FuelCost
import com.dmwaresolutions.myfuelcalculatorbr.ui.viewmodel.MainViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class CostAdapter(private val mainViewModel: MainViewModel, val context: Context) :
    RecyclerView.Adapter<CostAdapter.CostViewHolder>() {

    class CostViewHolder(val itemBinding: FuelCostItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root)

    private val differCallback =
        object : DiffUtil.ItemCallback<FuelCost>() {
            override fun areItemsTheSame(oldItem: FuelCost, newItem: FuelCost): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: FuelCost, newItem: FuelCost): Boolean {
                return oldItem.name == newItem.name &&
                        oldItem.totalCost == newItem.totalCost &&
                        oldItem.vehicle == newItem.vehicle &&
                        oldItem.fuel == newItem.fuel &&
                        oldItem.totalDistance == newItem.totalDistance
            }
        }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CostViewHolder {
        return CostAdapter.CostViewHolder(
            FuelCostItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: CostViewHolder, position: Int) {
        val currentCost = differ.currentList[position]

        holder.itemBinding.nameTextView.text = currentCost.name
        holder.itemBinding.costTextView.text =
            "R$ ${roundToDecimalPlaces(currentCost.totalCost, 2)}"
        holder.itemBinding.carTextView.text = currentCost.vehicle
        holder.itemBinding.fuelTextView.text = currentCost.fuel
        holder.itemBinding.distanceTextView.text =
            "Distância: ${roundToDecimalPlaces(currentCost.totalDistance, 1)} Km"
        holder.itemBinding.deleteCostBtn.setOnClickListener {
            //delete dialog
            setupDeleteAlertDialog(it, currentCost, position)
        }
    }

    private fun roundToDecimalPlaces(number: Double, places: Int): String {
        return when (places) {
            1 -> String.format("%.1f", number)
            2 -> String.format("%.2f", number)
            else -> String.format("%.2f", number)
        }
    }

    private fun removeItem(position: Int) {
        val updatedList = mutableListOf<FuelCost>()
        val deleteList = mutableListOf<Int>()

        deleteList.add(differ.currentList[position].cid)

        differ.currentList.forEachIndexed { index, item ->
            if (index != position) updatedList.add(item)
        }
        differ.submitList(updatedList)
    }

    private fun setupDeleteAlertDialog(view: View, currentCost: FuelCost, position: Int) {
        val messageBox = MaterialAlertDialogBuilder(context)

        messageBox
            .setTitle("Deseja apagar o registro?")
            .setPositiveButton("Sim") { _, _ ->
                removeItem(position)

                mainViewModel.deleteCost(currentCost)

                Toast.makeText(
                    context,
                    "Registro apagado com sucesso",
                    Toast.LENGTH_LONG
                ).show()

            }
            .setNegativeButton("Cancelar") { _, _ -> }
            .create()
            .show()
    }

}