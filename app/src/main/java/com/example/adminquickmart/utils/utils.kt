package com.example.adminquickmart.utils

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.adminquickmart.databinding.ProgressDialogBinding
import com.google.firebase.auth.FirebaseAuth

object utils {

        private var dialog : AlertDialog? = null
        private lateinit var binding : ProgressDialogBinding

        fun showDialog(context: Context, message: String){
            binding = ProgressDialogBinding.inflate(LayoutInflater.from(context))
            binding.tvMessage.text = message
            dialog = AlertDialog.Builder(context).setView(binding.root).setCancelable(false).create()
            dialog!!.show()

        }

        fun hideDialog(){
            dialog?.dismiss()
        }

        fun showToast(context: android.content.Context, message:String){
            Toast.makeText(context , message, Toast.LENGTH_SHORT).show()
        }


        private var firebaseInstance : FirebaseAuth? =  null
        fun getAuthInstance() : FirebaseAuth {
            if(firebaseInstance == null){
                firebaseInstance = FirebaseAuth.getInstance()
                Log.d("otp" , "firebase instance : ${firebaseInstance}")
            }
            return firebaseInstance!!
        }

        fun getCurrentUserid() : String?{
            return FirebaseAuth.getInstance().currentUser?.uid
        }

    fun randomId() : String{
        return (1..20).map { (('A'..'Z') + ('a'..'z') + ('0'..'9')).random() }.joinToString("")
    }

    }
