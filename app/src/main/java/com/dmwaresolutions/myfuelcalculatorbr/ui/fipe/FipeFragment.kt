package com.dmwaresolutions.myfuelcalculatorbr.ui.fipe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.dmwaresolutions.myfuelcalculatorbr.databinding.FragmentFipeBinding
import com.dmwaresolutions.myfuelcalculatorbr.ui.MainActivity
import com.dmwaresolutions.myfuelcalculatorbr.ui.viewmodel.MainViewModel
import com.google.android.material.textfield.MaterialAutoCompleteTextView

class FipeFragment : Fragment() {

    private var _binding: FragmentFipeBinding? = null
    private val binding get() = _binding!!
    private lateinit var mainViewModel: MainViewModel
    private lateinit var mainActivity: MainActivity

    var vehicleType = ""
    var makeList = listOf<Pair<String, String>>()
    var modelsList = listOf<Pair<Int, String>>()
    var yearsList = listOf<Pair<String, String>>()
    var fipeVehicle = mutableListOf("", "", "", "")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFipeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainActivity = activity as MainActivity
        mainViewModel = mainActivity.mainViewModel

        setupViews()
        setupButtons()
        setupObservers()
    }

    private fun setupObservers() {
        mainViewModel.makeList.observe(viewLifecycleOwner){ marcas ->
            val makeArray = mutableListOf<String>()
            makeList = marcas.map { marca ->
                makeArray.add(marca.nome)
                marca.codigo to marca.nome
            }

            (binding.edtMakeLayout.editText as? MaterialAutoCompleteTextView)?.setSimpleItems(
                makeArray.toTypedArray()
            )
        }

        mainViewModel.modelsList.observe(viewLifecycleOwner) {
            val modelArray = mutableListOf<String>()
            modelsList = it.modelos.map { model ->
                modelArray.add(model.nome)
                model.codigo to model.nome
            }

            (binding.edtModelLayout.editText as? MaterialAutoCompleteTextView)?.setSimpleItems(
                modelArray.toTypedArray()
            )
        }

        mainViewModel.yearsList.observe(viewLifecycleOwner) {
            val yearArray = mutableListOf<String>()
            yearsList = it.map { year ->
                yearArray.add(year.nome)
                year.codigo to year.nome
            }

            (binding.edtYearLayout.editText as? MaterialAutoCompleteTextView)?.setSimpleItems(
                yearArray.toTypedArray()
            )
        }
    }

    private fun setupButtons() {
        binding.btnSearchFipe.setOnClickListener {
            if (!mainViewModel.verificarConexaoInternet(requireContext())) {
                Toast.makeText(context, "Sem conexão com a internet", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            mainViewModel.getFipeVehicle(
                vehicleType,
                fipeVehicle[1],
                fipeVehicle[2],
                fipeVehicle[3],
                requireContext()
            )
        }

        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                binding.rbCarro.id -> {
                    vehicleType = "carros"
                }
                binding.rbMoto.id -> {
                    vehicleType = "motos"
                }
                binding.rbCaminhao.id -> {
                    vehicleType = "caminhoes"
                }
            }

            fipeVehicle[0] = vehicleType
            mainViewModel.getAllMakes(vehicleType, requireContext())
        }
    }

    private fun setupViews() {
        binding.radioGroup.check(binding.rbCarro.id)

        vehicleType = "carros"
        fipeVehicle[0] = vehicleType
        mainViewModel.getAllMakes(vehicleType, requireContext())

        mainViewModel.fipeVehicle.observe(viewLifecycleOwner) { fipe ->
            binding.fipeValueTextView.text = "R$ ${fipe.Valor}"
            binding.fipeMakeTv.text = fipe.Marca
            binding.fipeModelTv.text = fipe.Modelo
            binding.fipeYearTv.text = fipe.AnoModelo.toString()
            binding.fipeFuelTv.text = fipe.Combustivel
            binding.fipeRefMonthTv.text = fipe.MesReferencia
        }

        binding.edtMake.setOnItemClickListener { _, _, position, _ ->
            if (!mainViewModel.verificarConexaoInternet(requireContext())) {
                Toast.makeText(context, "Sem conexão com a internet", Toast.LENGTH_SHORT).show()
                return@setOnItemClickListener
            }

            fipeVehicle[1] = makeList[position].first

            mainViewModel.getAllModels(vehicleType, makeList[position].first, requireContext())
        }

        binding.edtModel.setOnItemClickListener { _, _, position, _ ->
            if (!mainViewModel.verificarConexaoInternet(requireContext())) {
                Toast.makeText(context, "Sem conexão com a internet", Toast.LENGTH_SHORT).show()
                return@setOnItemClickListener
            }

            fipeVehicle[2] = modelsList[position].first.toString()

            mainViewModel.getAllYears(
                vehicleType,
                fipeVehicle[1],
                modelsList[position].first.toString(),
                requireContext()
            )
        }

        binding.edtYear.setOnItemClickListener { _, _, position, _ ->
            if (!mainViewModel.verificarConexaoInternet(requireContext())) {
                Toast.makeText(context, "Sem conexão com a internet", Toast.LENGTH_SHORT).show()
                return@setOnItemClickListener
            }

            fipeVehicle[3] = yearsList[position].first
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}