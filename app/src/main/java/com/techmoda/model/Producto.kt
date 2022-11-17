package com.techmoda.model

import java.io.Serializable

data class Producto (
    var id: String ?= null,
    var disponible : Int ?= null,
    var titulo : String ?= null,
    var descripcion : String ?= null,
    var precio : Int ?= null,
    var imagenes : String ?= null,
    var categoria : String ?= null,
    var tags : String ?= null,
    var tallas : String ?= null
): Serializable