package com.dmwaresolutions.myfuelcalculatorbr.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dmwaresolutions.myfuelcalculatorbr.R
import com.dmwaresolutions.myfuelcalculatorbr.databinding.DialogEditProfileBinding
import com.dmwaresolutions.myfuelcalculatorbr.databinding.FragmentHomeBinding
import com.dmwaresolutions.myfuelcalculatorbr.model.User
import com.dmwaresolutions.myfuelcalculatorbr.model.Vehicle
import com.dmwaresolutions.myfuelcalculatorbr.ui.MainActivity
import com.dmwaresolutions.myfuelcalculatorbr.ui.adapter.CostAdapter
import com.dmwaresolutions.myfuelcalculatorbr.ui.adapter.VehicleAdapter
import com.dmwaresolutions.myfuelcalculatorbr.ui.viewmodel.MainViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class HomeFragment : Fragment() {
    private lateinit var vehicleAdapter: VehicleAdapter
    private lateinit var costAdapter: CostAdapter
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var mainViewModel: MainViewModel
    private lateinit var mainActivity: MainActivity
    private lateinit var currentUserList: List<User>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainActivity = activity as MainActivity
        mainViewModel = mainActivity.mainViewModel

        setupVehicleRecyclerView()
        setupCostRecyclerView()
        setupViews()
        setupButtons()
    }

    private fun setupViews() {
        mainViewModel.getUser().observe(viewLifecycleOwner) { user ->
            currentUserList = user

            if (user.isNotEmpty()) {
                binding.userNameTextView.text = currentUserList.first().name
            }
        }
    }

    private fun setupButtons() {

        binding.btnEditProfile.setOnClickListener {
            showEditProfileDialog()
        }

        binding.shareBtn.setOnClickListener {
            val costList = mainViewModel.currentCostList
            var shareText = "--------- Gastos de Combustível ---------\n"

            costList?.forEach {
                shareText += "${it.name}\nCombustível: ${it.fuel}\n" +
                        "Distância percorrida: ${String.format("%.1f", it.totalDistance)} km\n" +
                        "Gasto total: R$ ${String.format("%.2f", it.totalCost)}\n" +
                        "----------------------------------------\n"
            }
            buildShareIntent(shareText)
        }
    }

    private fun buildShareIntent(allCostsTxt: String) {
        val intent = Intent()
        intent.action = Intent.ACTION_SEND
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, allCostsTxt)
        startActivity(Intent.createChooser(intent, "Exportar custos"))
    }

    private fun setupVehicleRecyclerView() {
        mainViewModel.permissionGranted.observe(viewLifecycleOwner) { permissionOk ->
            if (!permissionOk) return@observe

            vehicleAdapter = VehicleAdapter(mainActivity)

            mainViewModel.getAllVehicles().observe(viewLifecycleOwner) { vList ->
                binding.vehicleRecyclerView.apply {
                    setHasFixedSize(true)
                    adapter = vehicleAdapter
                }
                vehicleAdapter.differ.submitList(vList)
            }

            binding.vehicleRecyclerView.addOnScrollListener(object :
                RecyclerView.OnScrollListener() {

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val totalPositions = recyclerView.adapter?.itemCount ?: 0

                    // Obter a quantidade de rolagem horizontal
                    val scrollOffset = recyclerView.computeHorizontalScrollOffset()

                    // Calcular a posição do item visível
                    val itemSize =
                        recyclerView.getChildAt(0).width  // Supondo que todos os itens têm o mesmo tamanho
                    val posicaoVisivel = scrollOffset / itemSize

                    // Atualizar o indicador de posição
                    if (totalPositions > 1) {
                        when (posicaoVisivel) {
                            0 -> {
                                binding.btnLeftRv.visibility = View.GONE
                                binding.btnRightRv.visibility = View.VISIBLE
                            }
                            else -> {
                                if (posicaoVisivel < (totalPositions.minus(1))) {
                                    binding.btnLeftRv.visibility = View.VISIBLE
                                    binding.btnRightRv.visibility = View.VISIBLE
                                } else {
                                    binding.btnLeftRv.visibility = View.VISIBLE
                                    binding.btnRightRv.visibility = View.GONE
                                }
                            }
                        }
                    }
                }
            })
        }
    }

    private fun setupCostRecyclerView() {
        costAdapter = CostAdapter(mainViewModel, requireContext())

        mainViewModel.getAllCosts().observe(viewLifecycleOwner){ cList ->
            binding.recyclerView.apply {
                layoutManager = LinearLayoutManager(this.context)
                setHasFixedSize(true)
                adapter = costAdapter
            }
            costAdapter.differ.submitList(cList)

            mainViewModel.currentCostList = cList
        }
    }


    private fun showEditProfileDialog() {

        val messageBox = MaterialAlertDialogBuilder(requireContext())
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_profile, null)
        val bindingDialog = DialogEditProfileBinding.bind(dialogView)

        messageBox.setView(dialogView).setTitle("Digite seu nome")
            .setPositiveButton("Salvar") { _, _ ->
                updateUser(bindingDialog.edtNewTraining.text.toString())
            }
            .setNegativeButton("Cancelar") { _, _ ->
                //NOTHING
            }
            .create().show()
    }

    private fun updateUser(name: String) {
        if (currentUserList.isNotEmpty()) {
            mainViewModel.updateUser(
                User(
                    uid = currentUserList.first().uid,
                    name = name
                )
            )
        } else {
            mainViewModel.insertUser(
                User(
                    uid = 0,
                    name = name
                )
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}