package com.dmwaresolutions.myfuelcalculatorbr.ui.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.widget.Toast
import androidx.lifecycle.*
import com.dmwaresolutions.myfuelcalculatorbr.model.*
import com.dmwaresolutions.myfuelcalculatorbr.repository.AppRepository
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel(private val repository: AppRepository) : ViewModel() {
    private var _vehicleImage = MutableLiveData<Bitmap>()
    val vehicleImage: LiveData<Bitmap>
        get() = _vehicleImage

    private var _bsVehicle = MutableLiveData<Vehicle>()
    val bsVehicle: LiveData<Vehicle>
        get() = _bsVehicle

    private var _permissionGranted = MutableLiveData<Boolean>()
    val permissionGranted: LiveData<Boolean>
        get() = _permissionGranted

    var currentCostList: List<FuelCost>? = null
    var fragmentsAlreadyShown = mutableListOf(false, false, false) //home, vehicles, calculator

    val makeList = MutableLiveData<List<Make>>()
    val modelsList = MutableLiveData<Models>()
    val yearsList = MutableLiveData<List<Year>>()
    val fipeVehicle = MutableLiveData<FipeVehicle>()

    fun setPermissionGranted(permission: Boolean) {
        _permissionGranted.postValue(permission)
    }

    fun setBsVehicle(v: Vehicle) {
        _bsVehicle.postValue(v)
    }

    fun setVehicleImage(b: Bitmap) {
        _vehicleImage.postValue(b)
    }

    fun insertUser(user: User) = viewModelScope.launch {
        repository.insertUser(user)
    }

    fun updateUser(user: User) = viewModelScope.launch {
        repository.updateUser(user)
    }

    fun getUser() = repository.getUser()

    fun addVehicle(v: Vehicle) = viewModelScope.launch {
        repository.insertVehicle(v)
    }

    fun updateVehicle(v: Vehicle) = viewModelScope.launch {
        repository.updateVehicle(v)
    }

    fun deleteVehicle(v: Vehicle) = viewModelScope.launch {
        repository.deleteVehicle(v)
    }

    fun getAllVehicles() = repository.getAllVehicles()

    fun addCost(cost: FuelCost) = viewModelScope.launch {
        repository.insertCost(cost)
    }

    fun deleteCost(cost: FuelCost) = viewModelScope.launch {
        repository.deleteCost(cost)
    }

    fun getAllCosts() = repository.getAllCosts()

    fun getAllMakes(veiculo: String, context: Context) {
        val request = repository.getAllMakes(veiculo)

        request.enqueue(object : Callback<List<Make>> {
            override fun onResponse(call: Call<List<Make>>, response: Response<List<Make>>) {
                if (response.code() == 200) {
                    makeList.postValue(response.body())
                }
            }

            override fun onFailure(call: Call<List<Make>>, t: Throwable) {
                Toast.makeText(
                    context,
                    "ERRO: ${t.message}. Tente novamente",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    fun getAllModels(veiculo: String, codMarca: String, context: Context) {
        val request = repository.getAllModels(veiculo, codMarca)

        request.enqueue(object : Callback<Models> {
            override fun onResponse(call: Call<Models>, response: Response<Models>) {
                if (response.code() == 200) {
                    modelsList.postValue(response.body())
                }
            }

            override fun onFailure(call: Call<Models>, t: Throwable) {
                Toast.makeText(
                    context,
                    "ERRO: ${t.message}. Tente novamente",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    fun getAllYears(veiculo: String, codMarca: String, codModelo: String,  context: Context) {
        val request = repository.getAllYears(veiculo, codMarca, codModelo)

        request.enqueue(object : Callback<List<Year>> {
            override fun onResponse(call: Call<List<Year>>, response: Response<List<Year>>) {
                if (response.code() == 200) {
                    yearsList.postValue(response.body())
                }
            }

            override fun onFailure(call: Call<List<Year>>, t: Throwable) {
                Toast.makeText(
                    context,
                    "ERRO: ${t.message}. Tente novamente",
                    Toast.LENGTH_LONG
                ).show()
            }

        })
    }

    fun getFipeVehicle(
        veiculo: String,
        codMarca: String,
        codModelo: String,
        codAno: String,
        context: Context
    ) {
        val request = repository.getFipeVehicle(veiculo, codMarca, codModelo, codAno)

        request.enqueue(object : Callback<FipeVehicle> {
            override fun onResponse(call: Call<FipeVehicle>, response: Response<FipeVehicle>) {
                if (response.code() == 200) {
                    fipeVehicle.postValue(response.body())
                }
            }

            override fun onFailure(call: Call<FipeVehicle>, t: Throwable) {
                Toast.makeText(
                    context,
                    "ERRO: ${t.message}. Tente novamente",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    fun verificarConexaoInternet(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val networkCapabilities = connectivityManager.activeNetwork ?: return false
        val activeNetwork =
            connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false

        when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                // Conexão via Wi-Fi
                return true
            }
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                // Conexão via dados móveis
                return true
            }
            else -> {
                // Sem conexão com a internet
                return false
            }
        }
    }

    fun populateVehicleDb(){
        addVehicle(
            Vehicle(
                vid = 0,
                name = "FOX 1.0 2014",
                fuel = "Etanol",
                autonomy = 7.5,
                image = null
            )
        )
    }

    fun populateCostDb(){
        addCost(
            FuelCost(
                cid = 0,
                name = "Casa - trabalho (mes)",
                totalCost = 263.34,
                vehicle = "Fox 1.0 2014",
                fuel = "Etanol",
                totalDistance = 440.0
            )
        )
    }

}


class MainViewModelFactory(private val repository: AppRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}