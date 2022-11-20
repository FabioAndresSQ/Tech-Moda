package com.techmoda.view.ui.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.*
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.firebase.firestore.FirebaseFirestore
import com.techmoda.R
import com.techmoda.model.Producto
import com.techmoda.view.adapter.CarritoAdapter
import com.techmoda.viewmodel.CarritoViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.io.Serializable

class CarritoFragment : Fragment(), CarritoAdapter.OnItemClickListener{
    private lateinit var emptyCartLayout: ConstraintLayout
    private lateinit var carritoLayout: ConstraintLayout
    private lateinit var myRecyclerView: RecyclerView
    private lateinit var pagoCarritoBtn: Button
    private lateinit var email: String
    private lateinit var itemAdapter: CarritoAdapter
    private val db = FirebaseFirestore.getInstance()
    private val viewModel by lazy { ViewModelProvider(this).get(CarritoViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefsEmail = context?.getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        email = prefsEmail?.getString("email", null)!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_carrito, container, false)

        myRecyclerView = view.findViewById(R.id.carritoRecyclerView)
        myRecyclerView.apply {
            layoutManager = GridLayoutManager(context, 1)
        }

        carritoLayout = view.findViewById(R.id.carritoLayout)
        emptyCartLayout = view.findViewById(R.id.emptyCartLayout)
        pagoCarritoBtn = view.findViewById(R.id.pagoBtn)

        getLiveDataCarrito()

        pagoCarritoBtn.setOnClickListener {
            val navController = findNavController()
            navController?.navigate(R.id.facturaFragment)
        }
        return view
    }

    private fun getLiveDataCarrito(){
        viewModel.productosData(email).observe(viewLifecycleOwner, Observer {
            itemAdapter = CarritoAdapter(this.requireContext(), it, this)
            myRecyclerView.adapter = itemAdapter
            itemAdapter.notifyDataSetChanged()
        })
    }

    override fun onItemClick(item: HashMap<String, Serializable>, id: Int, adapterPosition: Int) {
        if (id == R.id.itemLessCarritoBtn){
            removeItemCantCart(item)
        } else if (id == R.id.itemPlusCarritoBtn){
            addItemCantCart(item)
        }
    }

    private fun removeItemCantCart(item: HashMap<String, Serializable>){
        var cantidad = item["cantidad"] as Long
        val producto = item["producto"] as Producto
        val id = producto.id.toString()
        val itemCarrito = db.collection("carrito").document(email).collection("cart").document(id)
        //Remove 1 quantity item
        if (cantidad > 1){
            cantidad--
            itemCarrito.update("cantidad", cantidad)
            reloadCart()
        } else if (cantidad == 1L){
            //Confirm Delete
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Borrar del Carrito")
            builder.setMessage("¿Seguro que quieres borrar el producto del carrito?")
            builder.setPositiveButton("Si"){ dialog, which ->
                Toast.makeText(requireContext(), "Producto Eliminado", Toast.LENGTH_SHORT).show()
                itemCarrito.delete()
                reloadCart()
            }
            builder.setNegativeButton("No"){ dialog, which->
            }
            builder.show()
        } else {
            Log.d("cart", "Clicked on Less: $item")
        }
    }

    private fun addItemCantCart(item: HashMap<String, Serializable>){
        var cantidad = item["cantidad"] as Long
        val producto = item["producto"] as Producto
        val disponible = producto.disponible!!
        val id = producto.id.toString()
        val itemCarrito = db.collection("carrito").document(email).collection("cart").document(id)
        if (cantidad < disponible){
            cantidad++
            itemCarrito.update("cantidad", cantidad)
            reloadCart()
        } else if (cantidad > disponible){
            itemCarrito.update("cantidad", disponible)
        } else{
            Toast.makeText(context, "Cantidad no Disponible", Toast.LENGTH_SHORT).show()
        }
    }

    private fun reloadCart(){
        val navController = findNavController()
        navController?.navigateUp()
        navController?.navigate(R.id.carritoFragment)
    }

    private fun findNavController(): NavController? {
        val navHostFragment = activity?.supportFragmentManager?.findFragmentById(R.id.fragmentContainerView) as? NavHostFragment
        return navHostFragment?.navController
    }
}