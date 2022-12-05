package com.ex.locationelevation

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class reimaginedViewModel : ViewModel() {

    private val _theLatitude: MutableLiveData<Double> by lazy { MutableLiveData<Double>() }
    val theLatitude = _theLatitude

    private val _theLongitude: MutableLiveData<Double> by lazy { MutableLiveData<Double>() }
    val theLongitude = _theLongitude

    private val _theAltitude: MutableLiveData<Double> by lazy { MutableLiveData<Double>() }
    val theAltitude = _theAltitude

    private val _theAccuracy: MutableLiveData<Float> by lazy { MutableLiveData<Float>() }
    val theAccuracy = _theAccuracy

    private val _theAltAcc: MutableLiveData<Float> by lazy { MutableLiveData<Float>() }
    val theAltAcc = _theAltAcc

// ===============================================================================================
// Jobs, Flows and Scopes

    private val _dummyDataState: MutableStateFlow<Int> by lazy { MutableStateFlow(0) }
    val theDummyState get() = _dummyDataState.asStateFlow()

    private val _dummyJob = LocationService.dummyFlow.cancellable()
    fun dummyDataStateFlow() = viewModelScope.launch(Dispatchers.IO) {
        _dummyJob.conflate().collect{
            Log.d("StateFlow Grand Dummy", "Collected Dummy is $it")
            _dummyDataState.value = it
        }
    }

    private lateinit var _latitudeFlow: Flow<Double>
    private lateinit var _latitudeJob: Job
    private suspend fun collectLatitudeJob() {
        _latitudeFlow = LocationService.latestLatitude.cancellable().conflate()
        _latitudeFlow.collectLatest{ _theLatitude.postValue(it) }
    }

    fun startLatitudeFlow() {
        _latitudeJob = viewModelScope.launch(Dispatchers.IO) { collectLatitudeJob() }
        _latitudeJob.start()
            .apply { Log.d("LATITUDE_FLOW_STARTED", "Latitude Flow is starting") }
    }
    fun cancelLatitudeFlow() {
        if(!::_latitudeJob.isInitialized) {
            Log.d("LATITUDE_FLOW_FAILED", "Latitude failed to STOP")
            return
        }
        _latitudeJob.cancel()
            .apply { Log.d("LATITUDE_FLOW_STOPPED", "Latitude Flow is canceled") }
    }

    private lateinit var _longitudeFlow: Flow<Double>
    private lateinit var _longitudeJob: Job
    private suspend fun collectLongitudeJob() {
        _longitudeFlow = LocationService.latestLongitude.cancellable().conflate()
        _longitudeFlow.collectLatest{ _theLongitude.postValue(it) }
    }

    fun startLongitudeFlow() {
        _longitudeJob = viewModelScope.launch(Dispatchers.IO) { collectLongitudeJob() }
        _longitudeJob.start()
            .apply { Log.d("LONGITUDE_FLOW_STARTED", "Longitude Flow is starting") }
    }
    fun cancelLongitudeFlow() {
        if(!::_longitudeJob.isInitialized) {
            Log.d("LONGITUDE_FLOW_FAILED", "Longitude failed to STOP")
            return
        }
        _longitudeJob.cancel()
            .apply { Log.d("LONGITUDE_FLOW_STOPPED", "Longitude Flow is canceled") }
    }



    private lateinit var _accuracyFlow: Flow<Float>
    private lateinit var _accuracyJob: Job
    private suspend fun collectAccuracyJob() {
        _accuracyFlow = LocationService.latestAccuracy.cancellable().conflate()
        _accuracyFlow.collectLatest{ _theAccuracy.postValue(it) }

    }

    fun startAccuracyFlow() {
        _accuracyJob = viewModelScope.launch(Dispatchers.IO) { collectAccuracyJob() }
        _accuracyJob.start()
            .apply { Log.d("ACCURACY_FLOW_STARTED", "Accuracy Flow is starting") }
    }
    fun cancelAccuracyFlow() {
        if(!::_accuracyJob.isInitialized) {
            Log.d("ACCURACY_FLOW_FAILED", "Accuracy failed to STOP")
            return
        }
        _accuracyJob.cancel()
            .apply { Log.d("ACCURACY_FLOW_STOPPED", "Accuracy Flow is canceled") }
    }

    private lateinit var _altitudeFlow: Flow<Double>
    private lateinit var _altitudeJob: Job
    private suspend fun collectAltitudeJob() {
        _altitudeFlow = LocationService.latestAltitude.cancellable().conflate()
        _altitudeFlow.collectLatest{ _theAltitude.postValue(it) }

    }
    fun startAltitudeFlow() {
        _altitudeJob = viewModelScope.launch(Dispatchers.IO) { collectAltitudeJob() }
        _altitudeJob.start()
            .apply { Log.d("ALTITUDE_FLOW_STARTED", "Altitude Flow is starting") }
    }
    fun cancelAltitudeFlow() {
        if(!::_altitudeJob.isInitialized) {
            Log.d("ALTITUDE_FLOW_FAILED", "Altitude failed to STOP")
            return
        }
        _altitudeJob.cancel()
        .apply { Log.d("ALTITUDE_FLOW_STOPPED", "Altitude Flow is canceled") }
    }

    private lateinit var _altAccFlow: Flow<Float>
    private lateinit var _altAccJob: Job
    private suspend fun collectAltAccJob() {
        _altAccFlow = LocationService.latestVerticalAccuracy.cancellable().conflate()
        _altAccFlow.collectLatest{ _theAltAcc.postValue(it) }

    }
    fun startAltAccFlow() {
        _altAccJob = viewModelScope.launch(Dispatchers.IO) { collectAltAccJob() }
        _altAccJob.start()
            .apply { Log.d("ALTITUDE_FLOW_STARTED", "AltAcc Flow is starting") }
    }
    fun cancelAltAccFlow() {
        if(!::_altAccJob.isInitialized) {
            Log.d("ALTITUDE_FLOW_FAILED", "AltAcc failed to STOP")
            return
        }
        _altAccJob.cancel()
            .apply { Log.d("ALTITUDE_FLOW_STOPPED", "AltAcc Flow is canceled") }
    }





// ===============================================================================================
// Data

    val rangeArray get() = _rangeArray
    private var _rangeArray: LiveData<DoubleArray> = Transformations.map(_theAltitude) {
        when (it) {
            in 90.01..100.00 -> doubleArrayOf(90.01, 100.00)
            in 80.00..90.00 -> doubleArrayOf(80.0, 90.0)
            in 79.61..79.99 -> doubleArrayOf(79.61, 79.99)
            in 77.20..79.60 -> doubleArrayOf(77.2, 79.6)
            in 76.01..77.19 -> doubleArrayOf(76.01, 77.19)
            in 72.90..76.00 -> doubleArrayOf(72.9, 76.0)
            // 2 - 4 floor
//            in 60.01..72.80 -> doubleArrayOf(60.01, 72.8)


            // 4th floor Experimental
            in 68.0..69.5 -> doubleArrayOf(68.0, 69.5)
            // 3rd floor Experimental
            in 65.0..65.5 -> doubleArrayOf(65.0, 65.5)
            // 2nd floor Experimental
            in 57.8..61.4 -> doubleArrayOf(57.8, 61.4)


            in 54.00..57.8 -> doubleArrayOf(54.00, 57.8)
//            in 54.00..64.00 -> doubleArrayOf(54.00, 64.00)
            else -> doubleArrayOf(10.01, 40.00)
        }
    }

    val messageToDisplay get() = _messageToDisplay

    private val _messageToDisplay: LiveData<String> = Transformations.map(_theAltitude) {

//        if (theLatitude.value != null && theLongitude.value != null) {
//        var certainLatitude = theLatitude.value!!.let { it }
//        val certainLongitude = theLongitude.value!!.let { it }
            when (it) {
                in 90.01..200.00 -> "c u in hevannaa"
                in 80.00..90.00 ->
                    if(theLatitude.value != null && theLongitude.value != null) {
                          if (theLatitude.value.toString().toDouble() in -7.2859..-7.2853 &&
                              theLongitude.value.toString().toDouble() in 112.6315..112.63185) {"Dian Auditorium"}
                        else if(theLatitude.value.toString().toDouble() in -7.286..-7.2857 &&
                              theLongitude.value.toString().toDouble() in 112.6314..112.6322) {"Metrodata"}
                        else "7th floor"
                        } else { "7thFloor" }
                in 79.61..79.99 -> "6 - 7 floor"
                in 77.20..79.60 -> "6th floor"
                in 76.01..77.19 -> "5 - 6 floor"
                in 72.90..76.00 -> "5th floor"


//                in 60.01..72.80 -> "2 - 4 floor"
                in 68.0..69.5 -> "4th floor"
                in 65.0..65.5 -> "3rd floor"
                in 57.8..61.4 -> "2nd floor"


                in 54.00..57.8 ->
                    if(theLatitude.value != null && theLongitude.value != null) {
                        if (theLatitude.value.toString().toDouble() in -7.2862..-7.2857 &&
                            theLongitude.value.toString().toDouble() in 112.6319..112.6322) {"Corepreneur"}
                        else "1st floor"
                    } else { "1stFloor" }
                // 1st floor past
                //  in 54.00..64.00 -> "1st floor"
                else -> "r y andegrawun? \nI hef gud inggres"
            }
//        }

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