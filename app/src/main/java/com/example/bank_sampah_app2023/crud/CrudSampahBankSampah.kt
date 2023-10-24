package com.example.bank_sampah_app2023.crud

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bank_sampah_app2023.MainActivity
import com.example.bank_sampah_app2023.R
import com.example.bank_sampah_app2023.adapter.viewholder.SampahBSAddAdapter
import com.example.bank_sampah_app2023.model.SampahListAdd
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CrudSampahBankSampah : AppCompatActivity(), SampahBSAddAdapter.CheckBoxListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SampahBSAddAdapter
    private lateinit var btnBackSBS: ImageButton

    private val mFirestore = FirebaseFirestore.getInstance()
    private var auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    val uid = user?.uid
    var idBS = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crud_sampah_bank_sampah)

        recyclerView = findViewById(R.id.recyclerViewAddSBS)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = SampahBSAddAdapter(this)

        btnBackSBS = findViewById(R.id.back_buttonsbs)
        btnBackSBS.setOnClickListener {
            back()
        }

        if (uid != null) {
            val userDocumentReference = mFirestore.collection("users").document(uid)
            userDocumentReference.get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val userData = document.data
                        if (userData != null) {
                            idBS = userData["banksampahId"] as String

                            val targetBSRef = FirebaseFirestore.getInstance().document("banksampah/$idBS")

                            mFirestore.collection("sampah")
                                .get()
                                .addOnSuccessListener { querySampah ->
                                    val data: MutableList<SampahListAdd> = mutableListOf()

                                    for (document in querySampah) {
                                        val id = document.id
                                        val nama = document.get("nama") as String
                                        val satuan = document.get("satuan") as String
                                        val hargaBeli = document.get("hargaBeli") as Long

                                        val targetsampahRef = FirebaseFirestore.getInstance().document("sampah/$id")

                                        mFirestore.collection("sampahbanksampah").whereEqualTo("banksampahRef",targetBSRef).whereEqualTo("sampahRef",targetsampahRef).get()
                                            .addOnSuccessListener { docSampah ->
                                                if (docSampah.size()==0) {
                                                    val sampahBankSampah = SampahListAdd(id, nama, satuan, hargaBeli.toInt())
                                                    data.add(sampahBankSampah)

                                                    adapter.setData(data)
                                                    recyclerView.adapter = adapter
                                                }
                                            }
                                    }
                                }
                                .addOnFailureListener { exception ->
                                    // Handle any errors here
                                }
                        }
                    }
                }

        }
    }

    override fun onCheckBoxChecked(item: SampahListAdd) {
        if (uid != null) {
            val userDocumentReference = mFirestore.collection("users").document(uid)
            userDocumentReference.get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val userData = document.data
                        if (userData != null) {
                            idBS = userData["banksampahId"] as String

                            val addsampahRef = FirebaseFirestore.getInstance().document("sampah/${item.id}")
                            val addBSRef = FirebaseFirestore.getInstance().document("banksampah/$idBS")

                            val data = HashMap<String,Any>()
                            data["banksampahRef"] = addBSRef
                            data["sampahRef"] = addsampahRef
                            data["hargaBeli"] = item.hargaBeli.toInt()
                            mFirestore.collection("sampahbanksampah").add(data)
                                .addOnSuccessListener {
                                    back()
                                    Toast.makeText(this, "Tambah Data Berhasil ", Toast.LENGTH_LONG).show()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(this, " Tambah Data Gagal ", Toast.LENGTH_LONG).show()
                                }
                        }
                    }
                }
        }

    }

    override fun onCheckBoxUnchecked(item: SampahListAdd) {

    }

    private fun back() {
        startActivity(Intent(this, MainActivity::class.java).apply {
            putExtra(MainActivity.SIGNIN, "backfromcrudsampahbanksampah")
        })
    }
}