package com.techmoda.repo

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.*
import com.techmoda.model.Producto

class RepoConnect {
    fun getProductos():LiveData<MutableList<Producto>>{
        val productosdata = MutableLiveData<MutableList<Producto>>()
        val db = FirebaseFirestore.getInstance()
        db.collection("productos").orderBy("disponible").get().addOnSuccessListener {
            val productList = mutableListOf<Producto>()
            for (document in it){
                val product = document.toObject(Producto::class.java)
                productList.add(product)
            }
            productosdata.value = productList
        }
        return productosdata
    }
}