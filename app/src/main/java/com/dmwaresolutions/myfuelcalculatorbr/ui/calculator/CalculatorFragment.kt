package com.dmwaresolutions.myfuelcalculatorbr.ui.calculator

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dmwaresolutions.myfuelcalculatorbr.R
import com.dmwaresolutions.myfuelcalculatorbr.databinding.FragmentCalculatorBinding
import com.dmwaresolutions.myfuelcalculatorbr.model.FuelCost
import com.dmwaresolutions.myfuelcalculatorbr.model.Vehicle
import com.dmwaresolutions.myfuelcalculatorbr.ui.MainActivity
import com.dmwaresolutions.myfuelcalculatorbr.ui.adapter.BottomSheetAdapter
import com.dmwaresolutions.myfuelcalculatorbr.ui.viewmodel.MainViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog

class CalculatorFragment : Fragment() {
    private lateinit var bottomSheetRecyclerView: RecyclerView
    private var _binding: FragmentCalculatorBinding? = null
    private val binding get() = _binding!!
    private lateinit var bottomSheetAdapter: BottomSheetAdapter
    private lateinit var mainViewModel: MainViewModel
    private lateinit var mainActivity: MainActivity
    private lateinit var dialog: BottomSheetDialog
    private var vehicle: Vehicle? = null
    private var firstObserver: Boolean = true


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentCalculatorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainActivity = activity as MainActivity
        mainViewModel = mainActivity.mainViewModel

        setupViews()
        setupButtons()
    }

    private fun setupViews() {
        dialog = BottomSheetDialog(requireContext(), R.style.Theme_BottomSheetDialog)
        dialog.setContentView(
            layoutInflater.inflate(R.layout.vehicles_bottom_sheet, null)
        )

        mainViewModel.bsVehicle.observe(viewLifecycleOwner) {
            if (!mainViewModel.fragmentsAlreadyShown[2]) {
                vehicle = it
                binding.autonomyInputText.setText(it.autonomy.toString())
                mainViewModel.fragmentsAlreadyShown[2] = true
            } else {
                if (!firstObserver) {
                    vehicle = it
                    binding.autonomyInputText.setText(it.autonomy.toString())
                } else {
                    firstObserver = false
                }
            }
        }
    }

    private fun setupButtons() {
        binding.autonomyBtn.setOnClickListener {
            showBottomSheet()
        }
        binding.distanceBtn.setOnClickListener {
            mainActivity.abrirGoogleMaps()
        }
        binding.calcBtn.setOnClickListener {
            calculateCost(it)
        }
    }

    private fun calculateCost(view: View) {
        val name = binding.nameInputText.text.toString()
        val authonomy = binding.autonomyInputText.text.toString().let {
            if (it.isEmpty()) 0.0 else it.toDouble()
        }
        val literCost = binding.fuelCostInputText.text.toString().let {
            if (it.isEmpty()) 0.0 else it.toDouble()
        }
        val distance = binding.distanceInputText.text.toString().let {
            if (it.isEmpty()) 0.0 else it.toDouble()
        }

        if (!inputsAccepted(name, authonomy, literCost)) {
            mainActivity.showSuccessFeedbackSnackbar(false, "Preencha todos os campos")
            return
        }

        val fuelCost = (distance / authonomy) * literCost

        mainViewModel.addCost(
            FuelCost(
                cid = 0,
                name = name,
                totalCost = fuelCost,
                vehicle = vehicle?.name ?: "Veículo desconhecido",
                fuel = vehicle?.fuel ?: "",
                totalDistance = binding.distanceInputText.text.toString().toDouble()
            )
        )

        mainActivity.showSuccessFeedbackSnackbar(true, "Atualizado com sucesso!")

        view.findNavController().navigate(
            CalculatorFragmentDirections.actionCalculatorFragmentToHomeFragment()
        )
    }

    private fun inputsAccepted(name: String, authonomy: Double, literCost: Double): Boolean {
        return !(name.isEmpty() || authonomy == 0.0 || literCost == 0.0)
    }

    private fun showBottomSheet() {
        val dialogView = layoutInflater.inflate(R.layout.vehicles_bottom_sheet, null)
        val dialog = BottomSheetDialog(requireContext(), R.style.Theme_BottomSheetDialog)
        dialog.setContentView(dialogView)

        bottomSheetAdapter = BottomSheetAdapter(mainViewModel, dialog)

        bottomSheetRecyclerView = dialogView.findViewById(R.id.rvVehicleBottomSheet)
        bottomSheetRecyclerView.layoutManager = LinearLayoutManager(this.context)
        bottomSheetRecyclerView.adapter = bottomSheetAdapter

        dialog.show()

        mainViewModel.getAllVehicles().observe(viewLifecycleOwner) { vList ->
            bottomSheetAdapter.differ.submitList(vList)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}