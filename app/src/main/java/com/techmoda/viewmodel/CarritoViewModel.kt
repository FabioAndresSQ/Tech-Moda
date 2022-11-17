package com.techmoda.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.techmoda.model.Producto
import com.techmoda.repo.RepoConnect
import com.techmoda.repo.RepoConnectCarrito
import java.io.Serializable

class CarritoViewModel: ViewModel() {
    val repoConnectCarrito = RepoConnectCarrito()

    fun productosData(email: String): LiveData<MutableList<HashMap<String,Serializable>>> {
        val mutableData = MutableLiveData<MutableList<HashMap<String,Serializable>>>()
        repoConnectCarrito.getProductosCarrito(email).observeForever {
            mutableData.value = it
        }
        return mutableData
    }
}