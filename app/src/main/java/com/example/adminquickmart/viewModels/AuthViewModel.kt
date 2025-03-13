package com.example.adminquickmart.viewModels

import android.app.Activity
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.adminquickmart.models.Admins
import com.example.adminquickmart.utils.utils
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.concurrent.TimeUnit

class AuthViewModel : ViewModel() {

    lateinit var mauth : FirebaseAuth
    private val _verificationId = MutableStateFlow<String?>(null)

    private val _isCurrentUser = MutableStateFlow(false)
    val currentUser = _isCurrentUser



    private val _sendOtp = MutableStateFlow(false)
    val otpSent = _sendOtp


    private var _isSignedSuccessfully = MutableStateFlow(false)
    val signedSuccessfully = _isSignedSuccessfully


    init {
        utils.getAuthInstance().currentUser?.let {
            _isCurrentUser.value = true
        }
    }


    fun sendOtp(userNumber : String , activity : Activity) {

        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {

            }

            override fun onVerificationFailed(e: FirebaseException) {

            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken,
            ) {

                _verificationId.value = verificationId
                _sendOtp.value = true
            }
        }

        val options = PhoneAuthOptions.newBuilder(utils.getAuthInstance())
            .setPhoneNumber("+91${userNumber}") // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(activity) // Activity (for callback binding)
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun signInWithPhoneAuthCredential(otp: String, userNumber: String, admins: Admins) {
        val credential = PhoneAuthProvider.getCredential(_verificationId.value.toString(), otp)
        Log.d("otp" , "credential : ${credential}")
        Log.d("otp" , "verification : ${_verificationId.value.toString()}")
        utils.getAuthInstance().signInWithCredential(credential)
            .addOnCompleteListener{task->
                admins.uid = utils.getCurrentUserid()
                Log.d("otp" , "useruid = ${utils.getCurrentUserid()}")
                if(task.isSuccessful) {
                    Log.d("otp", "task is successful : ${task.isSuccessful.toString()}")
//                    val user = task.result?.user
//                    Log.d("otp" ,"user : ${user}")
//                    val userUid = user?.uid
//                    val adminfirebase = Admins(uid = userUid , adminPhoneNumber = userNumber , adminAddress = "1234")
//                    Log.d("otp" , "userUId = ${userUid}")
                    FirebaseDatabase.getInstance().getReference("Admins").child("AdminInfo")
                        .child(admins.uid!!).setValue(admins)
                        .addOnCompleteListener { setvalueTask ->
                            if (setvalueTask.isSuccessful) {
                                _isSignedSuccessfully.value = true
                            } else {
                                Log.d("otp", "firebase is failed ${setvalueTask.exception?.message}")
                            }
                        }
                }
                else{
                    Log.d("otp" , "task is failed ${task.exception?.message}")
                }
            }.addOnFailureListener {exception->
                Log.d("otp" , "message : ${exception.message}")
            }
    }
}