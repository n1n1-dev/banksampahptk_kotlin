package com.example.bank_sampah_app2023

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.example.bank_sampah_app2023.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException


class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)


        firebaseAuth = FirebaseAuth.getInstance()
        binding.textView.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        binding.button.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val pass = binding.passET.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty()) {

                firebaseAuth.signInWithEmailAndPassword(email, pass)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
                            val editor: SharedPreferences.Editor = sharedPreferences.edit()
                            editor.putString(email, pass)
                            editor.apply()
                            if(firebaseAuth.currentUser != null){
                                startActivity(Intent(this, MainActivity::class.java).apply {
                                    putExtra(MainActivity.SIGNIN, "signin")
                                })
                            }
                        } else {
                            val errorCode = (it.exception as FirebaseAuthException).errorCode
                            when (errorCode) {
                                "ERROR_INVALID_EMAIL" -> {
                                    Toast.makeText(this, "Format Email Tidak Valid", Toast.LENGTH_LONG).show()
                                }
                                "ERROR_WRONG_PASSWORD" -> {
                                    Toast.makeText(this, "Password Anda Salah", Toast.LENGTH_LONG).show()
                                }
                                "ERROR_USER_DISABLED" -> {
                                    Toast.makeText(this, "Akun Dinonaktifkan", Toast.LENGTH_LONG).show()
                                }
                                "ERROR_USER_NOT_FOUND" -> {
                                    Toast.makeText(this, "Akun Tidak Terdaftar", Toast.LENGTH_LONG).show()
                                }
                                else -> {
                                    Toast.makeText(this, errorCode, Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                }

            } else {
                Toast.makeText(this, "Form Login Tidak Boleh Kosong !!", Toast.LENGTH_LONG).show()

            }
        }
    }

    override fun onStart() {
        super.onStart()

        if(firebaseAuth.currentUser != null){
            startActivity(Intent(this, MainActivity::class.java).apply {
                putExtra(MainActivity.SIGNIN, "signin")
            })
        }
    }
}