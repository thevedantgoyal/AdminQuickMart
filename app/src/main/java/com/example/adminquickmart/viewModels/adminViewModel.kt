package com.example.adminquickmart.viewModels

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.adminquickmart.models.Orders
import com.example.adminquickmart.models.Product
import com.example.adminquickmart.models.cartProducts
import com.example.adminquickmart.utils.utils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import java.util.UUID

class adminViewModel : ViewModel() {

    private val _isImageUploaded = MutableStateFlow(false)
    var imageUploaded : StateFlow<Boolean> = _isImageUploaded


    private val _isDowloadurl = MutableStateFlow<ArrayList<String?>>(arrayListOf())
    val dowloadUrl : StateFlow<ArrayList<String?>> = _isDowloadurl

    private val _isProductSaved = MutableStateFlow(false)
    val productsaved : StateFlow<Boolean> = _isProductSaved



    fun saveImageinDB(imageUri : ArrayList<Uri>){

        val downloadUrls = ArrayList<String?>()

        imageUri.forEach{uri->
            val imageRef = FirebaseStorage.getInstance().reference.child(utils.getCurrentUserid()!!).child("images").child(UUID.randomUUID().toString())
            imageRef.putFile(uri).continueWithTask{
                imageRef.downloadUrl
            }.addOnCompleteListener { task->
                val url = task.result
                downloadUrls.add(url.toString())


                if(downloadUrls.size == imageUri.size){
                    _isImageUploaded.value = true
                    _isDowloadurl.value = downloadUrls
                }
            }
        }

    }

    fun saveProduct(product: Product){
        FirebaseDatabase.getInstance().getReference("Admins").child("AllProducts/${product.productRandomId}").setValue(product)
            .addOnCompleteListener {
                FirebaseDatabase.getInstance().getReference("Admins").child("ProductCategory/${product.productcategory}/${product.productRandomId}").setValue(product)
                    .addOnCompleteListener {
                        FirebaseDatabase.getInstance().getReference("Admins").child("ProductType/${product.producttype}/${product.productRandomId}").setValue(product)
                            .addOnSuccessListener {
                                _isProductSaved.value = true
                            }
                    }
            }
    }

    fun fetchAllProducts(categoryTitle: String): Flow<List<Product>> = callbackFlow {
        val db = FirebaseDatabase.getInstance().getReference("Admins").child("AllProducts")

        val eventListerner = object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val products = ArrayList<Product>()
                for (product in snapshot.children){
                    val pro = product.getValue(Product::class.java)
                    if(categoryTitle == "All" || pro?.productcategory == categoryTitle){
                        products.add(pro!!)
                    }

                }
                trySend(products)
//                trysend is used to send the list
            }

            override fun onCancelled(error: DatabaseError) {

            }

        }
        db.addValueEventListener(eventListerner)
        awaitClose{ db.removeEventListener(eventListerner)}
    }

    fun savingUpdateProduct(product: Product){
        FirebaseDatabase.getInstance().getReference("Admins").child("AllProducts/${product.productRandomId}").setValue(product)
        FirebaseDatabase.getInstance().getReference("Admins").child("ProductCategory/${product.productcategory}/${product.productRandomId}").setValue(product)
        FirebaseDatabase.getInstance().getReference("Admins").child("ProductType/${product.producttype}/${product.productRandomId}").setValue(product)

    }


    fun getAllProductsForStautus() : Flow<List<Orders>> = callbackFlow {
        val db = FirebaseDatabase.getInstance().getReference("Admins").child("Orders").orderByChild("orderStatus")

        val eventListener = object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val orderList = ArrayList<Orders>()
                for(orders in snapshot.children){
                    val order = orders.getValue(Orders::class.java)
                    if(order != null){
                        orderList.add(order)
                    }
                }
                trySend(orderList)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        }
        db.addValueEventListener(eventListener)
        awaitClose{ db.removeEventListener(eventListener) }

    }


    fun getOrderedProducts(orderId : String) : Flow<List<cartProducts>> = callbackFlow{
        val db = FirebaseDatabase.getInstance().getReference("Admins").child("Orders").child("orderId")
        val eventListener = object  : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val order = snapshot.getValue(Orders::class.java)
                trySend(order?.orderList!!)

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }
        db.addValueEventListener(eventListener)
        awaitClose{db.removeEventListener(eventListener)}
    }

    fun updateOrderStatus(orderId: String , status : Int){
        FirebaseDatabase.getInstance().getReference("Admins").child("Orders").child(orderId).child("orderStatus").setValue(status )
    }

    fun LogOutUser(){
        FirebaseAuth.getInstance().signOut()
    }
}