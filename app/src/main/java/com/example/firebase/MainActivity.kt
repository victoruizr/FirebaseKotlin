package com.example.firebase

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth

    val RC_SIGN_IN: Int = 1
    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var mGoogleSignInOptions: GoogleSignInOptions

    private val callbackManager = CallbackManager.Factory.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        configureGoogleSignIn()

        setContentView(R.layout.activity_main)
        firebaseAuth = FirebaseAuth.getInstance()
        if (firebaseAuth.currentUser?.email != null) {
            showOk()
        }
        setup()

    }



    private fun configureGoogleSignIn() {
        mGoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, mGoogleSignInOptions)
    }

    private fun setup() {
        bCrear.setOnClickListener {
            if (email.text.isNotEmpty() && editTextTextPassword.text.isNotEmpty()) {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                    email.text.toString(), editTextTextPassword.text.toString()
                ).addOnCompleteListener {
                    val user = FirebaseAuth.getInstance().currentUser

                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName("")
                        .build()

                    user?.updateProfile(profileUpdates)?.addOnCompleteListener {
                        if (it.isSuccessful) {
                            showOk()
                        } else {
                            showFailRegister()
                        }

                    }
                }
            }else{
                Toast.makeText(this,"Campos vacios",Toast.LENGTH_SHORT).show()
            }
        }
        bLogin.setOnClickListener {
            if (email.text.isNotEmpty() && editTextTextPassword.text.isNotEmpty()) {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(
                    email.text.toString(), editTextTextPassword.text.toString()
                ).addOnCompleteListener {
                    if (it.isSuccessful) {
                        showOk()
                    } else {
                        showFailLoged()
                    }
                }
            }
        }
        googleButtonImage.setOnClickListener {
            val signInIntent: Intent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
        FacebookButtonImage.setOnClickListener {
            LoginManager.getInstance().logInWithReadPermissions(this, listOf("email"))
            LoginManager.getInstance().registerCallback(callbackManager,
                object : FacebookCallback<LoginResult> {
                    override fun onSuccess(result: LoginResult?) {
                        result?.let {
                            val token: AccessToken = it.accessToken
                            val credential: AuthCredential =
                                FacebookAuthProvider.getCredential(token.token)
                            FirebaseAuth.getInstance().signInWithCredential(credential)
                                .addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        showOk()
                                    } else {
                                        showFailLoged()
                                    }
                                }

                        }
                    }

                    override fun onCancel() {

                    }

                    override fun onError(error: FacebookException?) {
                        showFailLoged()
                    }
                })
        }
        bRestore.setOnClickListener {
            val intent = Intent(this, ResetPassword::class.java)
            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> =
                GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                Toast.makeText(this, "Google sign in failed:(", Toast.LENGTH_LONG).show()
            }
        }
    }


    private fun showOk() {
        val intent = Intent(this, Perfil::class.java)
        startActivity(intent)
    }

    private fun showFailRegister() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Fallo al registrarse")
        builder.setMessage("Usuario no registrado")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }


    private fun showFailLoged() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Fallo al logearse")
        builder.setMessage("Usuario no loqueado")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun persistencia() {
        val prefs: SharedPreferences.Editor =
            getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("email", email.text.toString())
        prefs.apply()
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount?) {
        val credential = GoogleAuthProvider.getCredential(acct?.idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                showOk()
                //startActivity(MainActivitygetLaunchIntent(this))
            } else {
                Toast.makeText(this, "Google sign in failed:(", Toast.LENGTH_LONG).show()
            }
        }
    }
}