package com.example.parkingmanagementsystem.ui.auth

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import com.example.parkingmanagementsystem.R
import com.example.parkingmanagementsystem.data.model.response.User
import com.example.parkingmanagementsystem.databinding.ActivityLoginBinding
import com.example.parkingmanagementsystem.ui.AppBaseActivity
import com.example.parkingmanagementsystem.ui.main.HomeActivity
import com.example.parkingmanagementsystem.utils.Constants.FirebaseKeys.KEY_USERS_COLLECTION
import com.example.parkingmanagementsystem.utils.Variables.user
import com.example.parkingmanagementsystem.utils.extentions.*
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit

class LoginActivity : AppBaseActivity() {
    companion object {
        private const val TAG: String = "LoginActivity"
    }

    private lateinit var binding: ActivityLoginBinding
    private lateinit var mAuth: FirebaseAuth
    private var resendToken: PhoneAuthProvider.ForceResendingToken? = null
    private var storedVerificationId: String? = null
    private var phoneNumber: String = ""
    private var countryCode: String = "+880"
    private lateinit var countDownTimer: CountDownTimer
    private val db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()

        binding.ccpCountryCode.setOnCountryChangeListener {
            countryCode = "+" + binding.ccpCountryCode.selectedCountryCode
        }

        binding.btnSendCode.setOnClickListener {
            var number = binding.etPhoneNumber.text.toString().trim()

            when {
                TextUtils.isEmpty(number) -> {
                    binding.etPhoneNumber.error = getString(R.string.enter_your_mobile_number)
                    binding.etPhoneNumber.requestFocus()
                }
                !isNetworkAvailable() -> {
                    toast(getString(R.string.error_no_internet))
                }
                else -> {
                    val firstChar = number[0].toString()

                    if (countryCode == "+880" && firstChar == "0" && number.length == 11) {
                        number = number.substring(1)
                    }

                    phoneNumber = "$countryCode$number"
                    sendVerificationCode(phoneNumber)

                }
            }
        }
        binding.btnVerify.setOnClickListener {

            if (binding.pinViewOTP.text.toString().isNotEmpty()) {
                val code = binding.pinViewOTP.text.toString()

                storedVerificationId?.let {
                    val credential = PhoneAuthProvider.getCredential(storedVerificationId!!, code)
                    signInWithPhoneAuthCredential(credential)
                }

            } else {
                binding.root.snackBar(getString(R.string.enter_verification_code))
            }
        }

        binding.tvResend.setOnClickListener {
            sendVerificationCode(phoneNumber)
        }
        binding.btnManagement.setOnClickListener {
            launchActivity<LoginManagementActivity>()
        }
    }

    private fun sendVerificationCode(number: String) {

        showProgress(true)

        val options = PhoneAuthOptions.newBuilder(mAuth)
            .setPhoneNumber(number)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(phoneAuthCallbacks)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)

    }

    private val phoneAuthCallbacks =
        object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                val code = credential.smsCode

                if (code != null && code.length == 6) {
                    binding.pinViewOTP.setText(code)
                }

                showProgress(false)

                binding.root.snackBar("Phone Number Successfully Verified")
                Log.d(TAG, "onVerificationCompleted:$credential")
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(exception: FirebaseException) {
                showProgress(false)

                when (exception) {
                    is FirebaseAuthInvalidCredentialsException -> {
                        binding.root.snackBar("Invalid Request")
                    }
                    is FirebaseTooManyRequestsException -> {
                        Log.d(TAG, "onVerificationFailed: ${exception.localizedMessage!!}")
                        toast(exception.localizedMessage!!)
                    }
                    else -> {
                        Log.d(TAG, "onVerificationFailed: ${exception.message!!}")
                        toast(exception.message!!)
                    }
                }
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {

                Log.d(TAG, "onCodeSent:$verificationId")

                // Save verification ID and resending token so we can use them later
                storedVerificationId = verificationId
                resendToken = token

                updatePhoneLoginUI()
            }
        }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        //show progressbar
        showProgress(true)

        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")

                    val user = task.result?.user
                    loginUser(user!!)
                } else {
                    // Sign in failed, display a message and update the UI
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        toast(task.exception!!.localizedMessage!!)
                    }

                    showProgress(false)
                    binding.layoutOtp.visibility = View.GONE
                    binding.layoutPhoneNumber.visibility = View.VISIBLE

                    resetViews()
                }
            }
    }

    private fun loginUser(firebaseUser: FirebaseUser) {
        //showProgress(false)
        db.collection(KEY_USERS_COLLECTION).document(firebaseUser.uid)
            .get().addOnSuccessListener { snapshot ->

                //to check user exists or not
                if (snapshot.exists()) {
                    /*User Already Exists*/
                    user = snapshot.toObject(User::class.java)!!
                    //hide progressbar
                    showProgress(false)

                    launchActivity<HomeActivity>()
                    finish()
                } else {
                    startActivity(
                        Intent(this, RegistrationActivity::class.java).putExtra(
                            "phone_number", phoneNumber
                        )
                    )
                    finish()
                }
            }
    }

    private fun resetViews() {
        countDownTimer.cancel()
        binding.pinViewOTP.text?.clear()
    }

    private fun updatePhoneLoginUI() {
        showProgress(false)

        binding.layoutPhoneNumber.visibility = View.GONE
        binding.layoutOtp.visibility = View.VISIBLE
        title = getString(R.string.verification)

        binding.tvPhoneNumber.text = phoneNumber
        binding.tvResendTimeCount.visibility = View.VISIBLE

        //disable resend button
        binding.tvResend.isEnabled = false
        binding.tvResend.setTextColor(
            ContextCompat.getColor(
                this@LoginActivity,
                R.color.color_primary
            )
        )

        countDownTimer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val remaining =
                    "${millisUntilFinished / 1000} ${getString(R.string.seconds)}"
                binding.tvResendTimeCount.text = remaining
            }

            override fun onFinish() {
                binding.tvResendTimeCount.visibility = View.INVISIBLE

                binding.tvResend.isEnabled = true
                binding.tvResend.setTextColor(
                    ContextCompat.getColor(
                        this@LoginActivity,
                        R.color.color_primary
                    )
                )
            }
        }

        countDownTimer.start()
    }
}