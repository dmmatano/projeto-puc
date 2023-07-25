package com.dmwaresolutions.myfuelcalculatorbr.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.dmwaresolutions.myfuelcalculatorbr.R
import com.dmwaresolutions.myfuelcalculatorbr.data.AppDatabase
import com.dmwaresolutions.myfuelcalculatorbr.databinding.ActivityMainBinding
import com.dmwaresolutions.myfuelcalculatorbr.model.Vehicle
import com.dmwaresolutions.myfuelcalculatorbr.repository.AppRepository
import com.dmwaresolutions.myfuelcalculatorbr.rest.RetrofitService
import com.dmwaresolutions.myfuelcalculatorbr.ui.viewmodel.MainViewModel
import com.dmwaresolutions.myfuelcalculatorbr.ui.viewmodel.MainViewModelFactory
import com.google.android.material.snackbar.Snackbar


class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    val binding get() = _binding!!
    lateinit var mainViewModel: MainViewModel

    lateinit var navController: NavController
    var selectedImageUri: Uri? = null

    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        imagePickerResult()
        setupViewModel()

        setupBottomNavigation()
        requestWritePermission()

        mainViewModel.apply {
            populateVehicleDb()
            populateCostDb()
        }

    }

    private fun imagePickerResult() {
        imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                selectedImageUri = result.data?.data
                val imageBitmap = selectedImageUri?.let { uri ->
                    contentResolver.openInputStream(uri)?.use { inputStream ->
                        BitmapFactory.decodeStream(inputStream)
                    }
                }
                imageBitmap?.let {
                    mainViewModel.setVehicleImage(it)
                }
            }
        }
    }

    private fun requestWritePermission() {
        val permission = Manifest.permission.READ_EXTERNAL_STORAGE
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(permission), 1)
        } else {
            mainViewModel.setPermissionGranted(true)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permissão concedida, agora você pode acessar a galeria de mídia externa.
            mainViewModel.setPermissionGranted(true)
        } else {
            // Permissão negada, você pode lidar com isso exibindo uma mensagem de erro ou solicitando a permissão novamente.
            mainViewModel.setPermissionGranted(false)
        }
    }


    private fun setupViewModel() {
        val repository =
            AppRepository(AppDatabase.getInstance(this), RetrofitService.getInstance())
        val viewModelProviderFactory = MainViewModelFactory(
            repository
        )
        mainViewModel = ViewModelProvider(
            this,
            viewModelProviderFactory
        )[MainViewModel::class.java]
    }

    private fun setupBottomNavigation() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        navController = navHostFragment.navController
        binding.bottomNavigation.setupWithNavController(navController)

        binding.bottomNavigation.setOnMenuItemClickListener { menuItem ->

            when (menuItem.itemId) {
                R.id.homeFragment -> {
                    navController.navigate(R.id.homeFragment)
                    true
                }
                R.id.vehiclesFragment -> {
                    navController.navigate(R.id.vehiclesFragment, null)
                    true
                }
                R.id.fipeFragment -> {
                    navController.navigate(R.id.fipeFragment, null)

                    true
                }
                else -> {
                    false
                }
            }
        }

        binding.fabCalculate.setOnClickListener {
            navController.navigate(R.id.calculatorFragment)
        }
    }

    fun showSuccessFeedbackSnackbar(success: Boolean, msg: String) {
        if (success) {
            Snackbar.make(
                binding.mainContainer,
                msg,
                Snackbar.LENGTH_SHORT
            ).setBackgroundTint(resources.getColor(R.color.main_yellow))
                .show()
        } else {
            Snackbar.make(
                binding.mainContainer,
                msg,
                Snackbar.LENGTH_SHORT
            ).setBackgroundTint(resources.getColor(R.color.main_red))
                .show()
        }
    }

    fun openImageGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        imagePickerLauncher.launch(intent)
    }


    fun saveOrUpdateVehicle(vehicle: Vehicle, isVehicleNew: Boolean) {
        if (isVehicleNew) {
            mainViewModel.addVehicle(
                Vehicle(
                    vid = vehicle.vid,
                    name = vehicle.name,
                    fuel = vehicle.fuel,
                    autonomy = vehicle.autonomy,
                    image = if (selectedImageUri == null) vehicle.image else selectedImageUri.toString()
                )
            )
        } else {
            mainViewModel.updateVehicle(
                Vehicle(
                    vid = vehicle.vid,
                    name = vehicle.name,
                    fuel = vehicle.fuel,
                    autonomy = vehicle.autonomy,
                    image = if (selectedImageUri == null) vehicle.image else selectedImageUri.toString()
                )
            )
        }
    }

    fun loadUriImage(uriString: String?): Bitmap? {
        if (uriString == null) return null
        return BitmapFactory.decodeStream(
            contentResolver.openInputStream(Uri.parse(uriString))
        )
    }

    fun abrirGoogleMaps() {
        val uri = Uri.parse("geo:0,0?q=")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.setPackage("com.google.android.apps.maps")
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}