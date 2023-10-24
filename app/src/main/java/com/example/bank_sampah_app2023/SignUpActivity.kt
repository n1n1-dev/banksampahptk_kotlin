package com.example.bank_sampah_app2023

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.example.bank_sampah_app2023.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestore

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private val mFirestoreUsers = FirebaseFirestore.getInstance()
    private val mUsersCollection = mFirestoreUsers.collection("users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.textView.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }
        binding.button.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val pass = binding.passET.text.toString()
            val confirmPass = binding.confirmPassEt.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty() && confirmPass.isNotEmpty()) {
                if (pass == confirmPass) {

                    firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener {
                        if (it.isSuccessful) {
                            val user = firebaseAuth.currentUser
                            if (user != null) {
                                val data = HashMap<String,Any>()
                                data["userId"] = user.uid
                                data["email"] = user.email.toString()
                                data["rules"] = "nasabah"
                                data["banksampahId"] = "-"

                                mUsersCollection.document(user.uid)
                                    .set(data)
                                    .addOnCompleteListener {
                                        if (it.isSuccessful) {
                                            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
                                            val editor: SharedPreferences.Editor = sharedPreferences.edit()
                                            editor.putString(email, pass)
                                            editor.apply()

                                            Toast.makeText(this, "Pendaftaran Berhasil. Silahkan Login", Toast.LENGTH_LONG).show()
                                            FirebaseAuth.getInstance().signOut()
                                            Intent(this, SignInActivity::class.java)
                                            finish()
                                        }
                                    }
                            } else {
                                Toast.makeText(this, "Gagal mendapatkan pengguna saat ini", Toast.LENGTH_LONG).show()
                            }
                        } else {
                            val errorCode = (it.exception as FirebaseAuthException).errorCode
                            when (errorCode) {
                                "ERROR_INVALID_EMAIL" -> {
                                    Toast.makeText(this, "Format Email Tidak Valid", Toast.LENGTH_LONG).show()
                                }
                                "ERROR_EMAIL_ALREADY_IN_USE" -> {
                                    Toast.makeText(this, "Email Sudah Ada", Toast.LENGTH_LONG).show()
                                }
                                "ERROR_WEAK_PASSWORD" -> {
                                    Toast.makeText(this, "Password Terlalu Lemah", Toast.LENGTH_LONG).show()
                                }
                                else -> {
                                    Toast.makeText(this, errorCode, Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    }
                } else {
                    Toast.makeText(this, "Password Tidak Cocok", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this, "Form Pendaftaran Tidak Boleh Kosong !!", Toast.LENGTH_LONG).show()

            }
        }
    }
}