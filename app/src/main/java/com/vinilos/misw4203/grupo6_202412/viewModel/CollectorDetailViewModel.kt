package com.vinilos.misw4203.grupo6_202412.viewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.vinilos.misw4203.grupo6_202412.VinilosApplication
import com.vinilos.misw4203.grupo6_202412.models.dto.AlbumDto
import com.vinilos.misw4203.grupo6_202412.models.dto.CollectorDto
import com.vinilos.misw4203.grupo6_202412.models.repository.VinilosRepository
import kotlinx.coroutines.launch


sealed class CollectorUiState {
    object Loading : CollectorUiState()
    data class Error(val message: String) : CollectorUiState()
    data class Success(val collector: CollectorDto) :
        CollectorUiState()
}

sealed class CollectorAlbumsUiState {
    object Loading : CollectorAlbumsUiState()
    data class Error(val message: String) : CollectorAlbumsUiState()
    data class Success(val collectorAlbums: ArrayList<AlbumDto>) :
        CollectorAlbumsUiState()
}


class CollectorDetailViewModel(
    private val vinilosRepository: VinilosRepository,
    private val collectorId: Int
) : ViewModel() {
    var collectorUiState: CollectorUiState by mutableStateOf(CollectorUiState.Loading)
    var collectoralbumsUiState: CollectorAlbumsUiState by mutableStateOf(CollectorAlbumsUiState.Loading)

    init {
        fetchData()
    }


    fun fetchData() {
        viewModelScope.launch {
            getCollector()
            getAlbums()
        }

    }


    private fun getCollector() {
        collectorUiState = CollectorUiState.Loading
        try {
            vinilosRepository.getCollectorbyId(collectorId, {
                collectorUiState = CollectorUiState.Success(it)
            }, {
                collectorUiState = CollectorUiState.Error(it)
            })
        } catch (e: Exception) {
            val message = "Error consumiendo servicio " + e.message
            Log.i("Error", message)
            collectorUiState = CollectorUiState.Error(message)
        }

    }

    private fun getAlbums() {
        collectoralbumsUiState = CollectorAlbumsUiState.Loading
        try {
            vinilosRepository.getCollectorAlbums(collectorId, { albumDetails ->
                collectoralbumsUiState =
                    CollectorAlbumsUiState.Success(albumDetails.filter { it.status == "Active" }
                        .map { it.album }
                        .toCollection(ArrayList()))
            }, {
                collectoralbumsUiState = CollectorAlbumsUiState.Error(it)
            })
        } catch (e: Exception) {
            Log.i("Error", "Error consumiendo servicio " + e.message)
            collectoralbumsUiState =
                CollectorAlbumsUiState.Error("Error consumiendo servicio " + e.message)
        }
    }

    companion object {
        val Factory: (Int) -> ViewModelProvider.Factory = { idDetail ->
            viewModelFactory {
                initializer {
                    val application =
                        (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as VinilosApplication)
                    val vinilosRepository = application.vinilosRepository
                    CollectorDetailViewModel(
                        vinilosRepository = vinilosRepository,
                        collectorId = idDetail
                    )
                }
            }
        }
    }
}