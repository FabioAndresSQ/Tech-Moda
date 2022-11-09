package com.techmoda.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import com.techmoda.R
import com.techmoda.modelos.Producto
import java.lang.reflect.Type

class ProductoAdapter(val productos:ArrayList<Producto>) : RecyclerView.Adapter<ProductoAdapter.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoAdapter.ViewHolder{
        var vista= LayoutInflater.from(parent.context).inflate(R.layout.item_producto,parent, false)
        return ViewHolder(vista)
    }

    override fun getItemCount(): Int {
        return productos.size

    }

    override fun onBindViewHolder(holder: ProductoAdapter.ViewHolder, position: Int) {
        holder.binItems(productos[position])
    }
    class ViewHolder (itemView:View) : RecyclerView.ViewHolder(itemView){
        init {

        }
        fun binItems(producto: Producto){
            val nombre= itemView.findViewById<TextView>(R.id.item_nombre)
            nombre.text =producto.nombre
        }
    }

}
