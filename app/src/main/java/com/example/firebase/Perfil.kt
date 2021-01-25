package com.example.firebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_perfil.*

class Perfil : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private var db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)


        firebaseAuth = FirebaseAuth.getInstance()
        emailAddres.text = firebaseAuth.currentUser?.displayName.toString();
        cargar()

        click()


    }

    fun click() {
        cGuardar.setOnClickListener {
            guardar()
        }

        cEliminar.setOnClickListener {
            db.collection("usuarios").document(firebaseAuth.currentUser?.email.toString()).delete()
            FirebaseAuth.getInstance().currentUser?.let { it1 ->
                db.collection("usuarios").document(firebaseAuth.currentUser?.email.toString())
                    .delete()
            }
            firebaseAuth.currentUser?.delete()
            cerrarSesion()

        }

        cSesion.setOnClickListener {

            cerrarSesion()

        }
    }

    fun cerrarSesion() {

        firebaseAuth.signOut()
        GoogleSignIn.getClient(
            this,
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        ).signOut()
        LoginManager.getInstance().logOut()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    fun guardar() {

        db.collection("usuarios").document(firebaseAuth.currentUser?.email.toString()).set(
            hashMapOf(
                "nombre" to firebaseAuth.currentUser?.displayName.toString(),
                "telefono" to editTextPhone.text.toString(),
                "edad" to edEdad.text.toString(),
                "altura" to edAltura.text.toString()
            )
        )
    }

    fun cargar() {
        db.collection("usuarios").document(firebaseAuth.currentUser?.email.toString()).get()
            .addOnSuccessListener {
                name.setText(it.get("nombre") as String?)
                editTextPhone.setText(it.get("telefono") as String?)
                edEdad.setText(it.get("edad") as String?)
                edAltura.setText(it.get("altura") as String?)
            }
    }
}

