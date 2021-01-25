package com.example.firebase

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import kotlinx.android.synthetic.main.activity_reset_password.*


class ResetPassword : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
        setContentView(R.layout.activity_reset_password)

        restorePassword.setOnClickListener {
            var a = firebaseAuth.sendPasswordResetEmail(editTextTextEmailAddress.text.toString())
        }
    }
}