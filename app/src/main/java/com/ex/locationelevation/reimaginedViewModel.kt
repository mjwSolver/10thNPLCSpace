package com.ex.locationelevation

import android.Manifest
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.IBinder
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.*
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class reimaginedViewModel : ViewModel() {

    private val _theLatitude: MutableLiveData<Double> by lazy { MutableLiveData<Double>() }
    val theLatitude = _theLatitude

    private val _theLongitude: MutableLiveData<Double> by lazy { MutableLiveData<Double>() }
    val theLongitude = _theLongitude

    private val _theAltitude: MutableLiveData<Double> by lazy { MutableLiveData<Double>() }
    val theAltitude = _theAltitude

    private val _theAccuracy: MutableLiveData<Float> by lazy { MutableLiveData<Float>() }
    val theAccuracy = _theAccuracy

    private val _dummyData: MutableLiveData<Int> by lazy { MutableLiveData<Int>() }
    val theDummy get() = _dummyData

//    private var _dummyData = MutableLiveData<Int>().apply { this.value = 0 }
//    val theDummy get() = _dummyData

//    private lateinit var _dummyData: LiveData<Int>
//    val theDummy get() = _dummyData

//    private lateinit var _dummyData: LiveData<Int>
//    val theDummy get() = _dummyData

    fun generateLocations(contextual:Context) = viewModelScope.launch(SupervisorJob() + Dispatchers.Default) {
//        LocationService().shareLocationFlow(contextual).collectLatest{ newLocation ->
//            _theLatitude.postValue(newLocation.latitude)
//            _theLongitude.postValue(newLocation.longitude)
//            _theAltitude.postValue(newLocation.altitude)
//            _theAccuracy.postValue(newLocation.accuracy)
//
//        }
    }

    fun collectLocationFlow() = viewModelScope.launch {

    }

    fun dummyDataFlow() = viewModelScope.launch(Dispatchers.IO) {
        LocationService.dummyFlow.collect{
            Log.d("Grand Dummy", "Collected Dummy $it")
            _dummyData.postValue(it)
        }

//        _dummyData = LocationService.dummyFlow.asLiveData()

//        liveData<Int> {
//            LocationService.dummyFlow.collect{
//                Log.d("Grand Dummy", "Collected Dummy $it")
//                _dummyData.postValue(it)
//            }
//        }

    }

    val rangeArray get() = _rangeArray
    private var _rangeArray: LiveData<DoubleArray> = Transformations.map(_theAltitude) {
        when (it) {
            in 90.01..100.00 -> doubleArrayOf(90.01, 100.00)
            in 80.00..90.00 -> doubleArrayOf(80.0, 90.0)
            in 79.61..79.99 -> doubleArrayOf(79.61, 79.99)
            in 77.20..79.60 -> doubleArrayOf(77.2, 79.6)
            in 76.01..77.19 -> doubleArrayOf(76.01, 77.19)
            in 72.90..76.00 -> doubleArrayOf(72.9, 76.0)
            in 60.01..72.80 -> doubleArrayOf(60.01, 72.8)
            in 54.00..64.00 -> doubleArrayOf(54.00, 64.00)
            else -> doubleArrayOf(10.01, 40.00)
        }
    }

    val messageToDisplay get() = _messageToDisplay
//    private String word = "something";

    private val _messageToDisplay: LiveData<String> = Transformations.map(_theAltitude) {
        when (it) {
            in 90.01..100.00 -> "c u in hevannaa"
            in 80.00..90.00 -> "7th floor"
            in 79.61..79.99 -> "6 - 7 floor"
            in 77.20..79.60 -> "6th floor"
            in 76.01..77.19 -> "5 - 6 floor"
            in 72.90..76.00 -> "5th floor"
            in 60.01..72.80 -> "2 - 4 floor"
            in 54.00..64.00 -> "1st floor"
            else -> "r y andegrawun? \nI hef gud inggres"
        }
    }

    fun checkActivityForLocationPermission(activity: Activity){
        if(ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED
            && ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED){

            ActivityCompat.requestPermissions(activity, arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ), 0)

        }
    }


}