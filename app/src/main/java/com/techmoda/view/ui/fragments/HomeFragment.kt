package com.techmoda.view.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.techmoda.R
import com.techmoda.model.Producto
import com.techmoda.view.adapter.ProductoAdapter
import com.techmoda.view.ui.activities.HomeActivity
import java.io.Serializable


class HomeFragment : Fragment(), ProductoAdapter.OnItemClickListener{
    private lateinit var myRecyclerView: RecyclerView
    private lateinit var productoList: ArrayList<Producto>
    private lateinit var itemAdapter: ProductoAdapter
    private lateinit var db : FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val imageStorage = Firebase.storage
        val view = inflater.inflate(R.layout.fragment_home, container, false) //save inflater to view

        myRecyclerView = view.findViewById(R.id.homeRecyclerView)
        myRecyclerView.apply {
            layoutManager = GridLayoutManager(context, 2)
        }

        productoList = arrayListOf()

        itemAdapter = ProductoAdapter(this.requireContext(), productoList, this)
        myRecyclerView.adapter = itemAdapter

        eventChangeListener()
        return view
    }

    private fun findNavController(): NavController? {
        val navHostFragment = (requireActivity() as? HomeActivity)?.supportFragmentManager?.findFragmentById(R.id.fragmentContainerView) as? NavHostFragment
        return navHostFragment?.navController
    }

    override fun onItemClick(item: Producto, adapterPosition : Int) { //Check on item clicked passing item and item position
        val launchDetails = Bundle()
        val nav = findNavController()
        Log.i("Launching Product", item.titulo.toString())
        launchDetails.putSerializable("selected_product" , item as Serializable) //Save item on Intend
        nav?.navigate(R.id.detailsFragment, launchDetails)
    }

    private fun eventChangeListener(){
        db = FirebaseFirestore.getInstance()
        db.collection("productos").orderBy("disponible")
            .addSnapshotListener(object : EventListener<QuerySnapshot>{
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                if (error != null){
                    showErrorAlert("Error al conectarse con la base de datos")
                    Log.e("Error FireStore", error.message.toString())
                    return
                } else {
                    for (dc: DocumentChange in value?.documentChanges!!){
                        if (dc.type == DocumentChange.Type.ADDED){
                            val product = dc.document.toObject(Producto::class.java)
                            productoList.add(product)
                        }
                    }
                    itemAdapter.notifyDataSetChanged()
                }
            }
        })
    }

    //Error Alert
    private fun showErrorAlert(msg:String){
        val builder = AlertDialog.Builder(this.requireContext())
        builder.setTitle("Error")
        builder.setMessage(msg)
        builder.setPositiveButton("Aceptar", null)
        val dialog : AlertDialog = builder.create()
        dialog.show()
    }


}