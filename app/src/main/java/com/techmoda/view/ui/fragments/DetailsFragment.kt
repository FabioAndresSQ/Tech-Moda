package com.techmoda.view.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.techmoda.R
import com.techmoda.model.Producto

class DetailsFragment : Fragment() {

    private lateinit var detailedTitle: TextView
    private lateinit var detailedDescription: TextView
    private lateinit var detailedPrice: TextView
    private lateinit var producto: Producto

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_details, container, false)
        val args =this.arguments
        producto = args?.getSerializable("selected_product") as Producto

        detailedTitle = view.findViewById(R.id.detailedTitle)
        detailedDescription = view.findViewById(R.id.detailedDescription)
        detailedPrice = view.findViewById(R.id.detailedPrice)

        setup()

        return view
    }

    private fun setup(){
        detailedTitle.text = producto.titulo.toString()
        detailedDescription.text = producto.descripcion.toString()
        detailedPrice.text = producto.precio.toString()
    }

}