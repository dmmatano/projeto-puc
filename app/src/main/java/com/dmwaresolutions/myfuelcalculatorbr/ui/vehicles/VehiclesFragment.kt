package com.dmwaresolutions.myfuelcalculatorbr.ui.vehicles
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.dmwaresolutions.myfuelcalculatorbr.databinding.FragmentVehiclesBinding
import com.dmwaresolutions.myfuelcalculatorbr.model.Vehicle
import com.dmwaresolutions.myfuelcalculatorbr.ui.MainActivity
import com.dmwaresolutions.myfuelcalculatorbr.ui.viewmodel.MainViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class VehiclesFragment : Fragment() {

    private lateinit var mainActivity: MainActivity
    private lateinit var mainViewModel: MainViewModel
    private val args: VehiclesFragmentArgs by navArgs()
    private var _binding: FragmentVehiclesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVehiclesBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainActivity = activity as MainActivity
        mainViewModel = mainActivity.mainViewModel

        mainActivity.selectedImageUri = null

        setupViews()
        setupButtons()
    }

    private fun setupButtons() {
        binding.btnUpdateVehicle.setOnClickListener {
            saveInputsData(args.vehicle)
            Toast.makeText(
                requireContext(),
                "Veículo atualizado com sucesso!",
                Toast.LENGTH_LONG
            ).show()

            it.findNavController().navigate(
                VehiclesFragmentDirections.actionVehiclesFragmentToHomeFragment()
            )
        }

        binding.btnDeleteVehicle.setOnClickListener {
            setupDeleteAlertDialog(it)
        }

        binding.btnEditImage.setOnClickListener {
            mainActivity.openImageGallery()
            setVehiclePhoto()
        }

        binding.vehicleImageView.setOnClickListener {
            mainActivity.openImageGallery()
            setVehiclePhoto()
        }

    }

    private fun setVehiclePhoto() {
        mainViewModel.vehicleImage.observe(viewLifecycleOwner) { bitmap ->
            binding.vehicleImageView.setImageBitmap(bitmap)
        }
    }

    private fun setupDeleteAlertDialog(view: View) {
        val messageBox = MaterialAlertDialogBuilder(requireContext())

        messageBox
            .setTitle("Deseja apagar o veículo?")
            .setPositiveButton("Sim") { _, _ ->
                mainViewModel.deleteVehicle(args.vehicle!!)
                view.findNavController().navigate(
                    VehiclesFragmentDirections.actionVehiclesFragmentToHomeFragment()
                )
                Toast.makeText(
                    requireContext(),
                    "Veículo apagado com sucesso",
                    Toast.LENGTH_LONG
                ).show()
            }
            .setNegativeButton("Cancelar") { _, _ -> }
            .create()
            .show()
    }

    private fun saveInputsData(vehicle: Vehicle?) {
        val newVehicle: Vehicle?

        if (vehicle != null) {
            newVehicle = Vehicle(
                vid = vehicle.vid,
                name = binding.edtNome.text.toString(),
                fuel = binding.edtFuel.text.toString(),
                autonomy = binding.edtAutonomy.text.toString().toDouble(),
                image = vehicle.image
            )
            mainActivity.saveOrUpdateVehicle(newVehicle, false)
        } else {
            newVehicle = Vehicle(
                vid = 0,
                name = binding.edtNome.text.toString(),
                fuel = binding.edtFuel.text.toString(),
                autonomy = binding.edtAutonomy.text.toString().toDouble(),
                image = null
            )
            mainActivity.saveOrUpdateVehicle(newVehicle, true)
        }
    }

    private fun setupViews() {
        if (args.vehicle != null) {
            binding.edtNome.setText(args.vehicle?.name)
            binding.edtAutonomy.setText(args.vehicle?.autonomy.toString())
            binding.edtFuel.setText(args.vehicle?.fuel)
            binding.btnDeleteVehicle.visibility = View.VISIBLE
            mainActivity.loadUriImage(args.vehicle!!.image).let {
                if (it != null) binding.vehicleImageView.setImageBitmap(it)
            }
        } else {
            binding.btnDeleteVehicle.visibility = View.GONE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}