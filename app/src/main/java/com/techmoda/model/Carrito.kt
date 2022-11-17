package com.techmoda.model

import java.io.Serializable

data class Carrito (
    var listaProductos : MutableList<HashMap<String, Serializable>>
        ) : Serializable