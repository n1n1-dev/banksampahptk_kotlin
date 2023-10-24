package com.example.bank_sampah_app2023.crud

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.example.bank_sampah_app2023.MainActivity
import com.example.bank_sampah_app2023.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestore


class CrudUsers : AppCompatActivity() {

    companion object {
        val titleCrudUsers = ""
    }

    private var title = ""
    private lateinit var firebaseAuth: FirebaseAuth
    private val mFirestoreUsers = FirebaseFirestore.getInstance()
    private val mUsersCollection = mFirestoreUsers.collection("users")

    private lateinit var btnBack: ImageButton
    private lateinit var judulCrud: TextView
    private lateinit var btnSave: Button

    private lateinit var selectBSText: Spinner
    private lateinit var emailText: TextView
    private lateinit var passText: TextView
    private lateinit var confpassText: TextView

    private val nameToIdMap = HashMap<String, String>()
    private var idSelectBS = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crud_users)

        firebaseAuth = FirebaseAuth.getInstance()

        title = intent.getStringExtra(titleCrudUsers).toString()

        btnBack = findViewById(R.id.back_buttonusers)
        judulCrud = findViewById(R.id.judulCrudUsers)
        btnSave = findViewById(R.id.btn_savebankusers)
        emailText = findViewById(R.id.edTextEmailUsers)
        passText = findViewById(R.id.edTextPassUsers)
        confpassText = findViewById(R.id.edTextPassConfUsers)
        selectBSText = findViewById(R.id.spinnerSelectBS)

        judulCrud.text = title

        fetchDataBSFromFirestore()

        selectBSText.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                // Retrieve the selected name
                val selectedName = parent?.getItemAtPosition(position).toString()
                // Retrieve the corresponding ID from the map
                val selectedId = nameToIdMap[selectedName]

                // Now, you have the selected ID to use as needed.
                if (selectedId != null) {
                    idSelectBS = selectedId
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Toast.makeText(this@CrudUsers, "Pilih Bank Sampah", Toast.LENGTH_SHORT).show()
            }
        }

        btnSave.setOnClickListener {
            val email = emailText.text.toString()
            val pass = passText.text.toString()
            val confpass = confpassText.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty() && confpass.isNotEmpty()) {
                if (pass == confpass) {
                    firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener {
                        if (it.isSuccessful) {
                            val user = firebaseAuth.currentUser
                            if (user != null) {
                                val data = HashMap<String,Any>()
                                data["userId"] = user.uid
                                data["email"] = user.email.toString()
                                data["rules"] = "admin"
                                data["banksampahId"] = idSelectBS

                                mUsersCollection.document(user.uid)
                                    .set(data)
                                    .addOnCompleteListener {
                                         if (it.isSuccessful) {
                                            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
                                            val editor: SharedPreferences.Editor = sharedPreferences.edit()
                                            editor.putString(email, pass)
                                            editor.apply()

                                            FirebaseAuth.getInstance().signOut()
                                            val emailUser = "adminbanksampahptk23@gmail.com"
                                            val passSuperA = sharedPreferences.getString(emailUser, "")

                                            firebaseAuth.signInWithEmailAndPassword(emailUser, passSuperA!!)
                                                .addOnCompleteListener { signInTask ->
                                                    if (signInTask.isSuccessful) {
                                                        val userAdmin = firebaseAuth.currentUser
                                                        if (userAdmin != null) {
                                                             if (userAdmin.email == emailUser) {
                                                                Toast.makeText(this, "Tambah Admin Bank Sampah Berhasil", Toast.LENGTH_LONG).show()
                                                                back()
                                                            }
                                                        }
                                                    }
                                                }

                                        } else {
                                            Toast.makeText(this, "Tambah Admin Bank Sampah Gagal", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                            } else {
                                Toast.makeText(this, "Gagal mendapatkan pengguna saat ini", Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(this, "Password Tidak Cocok", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Form Pendaftaran Tidak Boleh Kosong !!", Toast.LENGTH_SHORT).show()

            }
        }

        btnBack.setOnClickListener {
            back()
        }
    }

    private fun fetchDataBSFromFirestore() {
        val items = ArrayList<String>()

        mFirestoreUsers.collection("banksampah")
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot) {
                    val id = document.id
                    val item = document.getString("nama")
                    item?.let {
                        items.add(it)
                        nameToIdMap[it] = id
                    }
                }

                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                selectBSText.adapter = adapter
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this@CrudUsers, "Tidak Ada Data Bank Sampah", Toast.LENGTH_SHORT).show()
            }
    }

    private fun back() {
        startActivity(Intent(this, MainActivity::class.java).apply {
            putExtra(MainActivity.SIGNIN, "backfromcrudusers")
        })
    }

}