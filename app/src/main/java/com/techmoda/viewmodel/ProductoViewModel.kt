package com.techmoda.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.techmoda.model.Producto
import com.techmoda.repo.RepoConnect

class ProductoViewModel:ViewModel() {
    val repoConnect = RepoConnect()

    fun productosData():LiveData<MutableList<Producto>>{
        val mutableData = MutableLiveData<MutableList<Producto>>()
        repoConnect.getProductos().observeForever {
            mutableData.value = it
        }
        return mutableData
    }
}