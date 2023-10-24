package com.example.bank_sampah_app2023

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bank_sampah_app2023.adapter.viewholder.SampahBankSampahAdapter
import com.example.bank_sampah_app2023.crud.CrudSampahBankSampah
import com.example.bank_sampah_app2023.model.SampahList
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class SampahBankSampahFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SampahBankSampahAdapter
    private lateinit var fabaddsampahbs: FloatingActionButton

    private val mFirestore = FirebaseFirestore.getInstance()
    private var auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    val uid = user?.uid
    var idBS = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_sampah_bank_sampah, container, false)

        recyclerView = view.findViewById(R.id.sampahbanksampahRV)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = SampahBankSampahAdapter()

        fabaddsampahbs = view.findViewById(R.id.fabaddsampahbs)
        fabaddsampahbs.setOnClickListener {
            val intent = Intent(requireContext(), CrudSampahBankSampah::class.java)
            startActivity(intent)
        }

        if (uid != null) {
            val userDocumentReference = mFirestore.collection("users").document(uid)
            userDocumentReference.get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val userData = document.data
                        if (userData != null) {
                            idBS = userData["banksampahId"] as String

                            val targetRef = FirebaseFirestore.getInstance().document("banksampah/$idBS")

                            mFirestore.collection("sampahbanksampah").whereEqualTo("banksampahRef", targetRef)
                                .get()
                                .addOnSuccessListener { querySnapshot ->
                                    val data: MutableList<SampahList> = mutableListOf()

                                    for (document in querySnapshot) {
                                        val id = document.id
                                        val banksampahRef = document.get("banksampahRef") as DocumentReference
                                        val sampahRef = document.get("sampahRef") as DocumentReference
                                        val hargaBeli = document.get("hargaBeli") as Long

                                        val dataSampah = sampahRef.path
                                        val idSampah = dataSampah.split("/")

                                        mFirestore.collection("sampah").document(idSampah[1]).get()
                                            .addOnSuccessListener { docSampah ->
                                                if (docSampah != null && docSampah.exists()) {
                                                    val sampahData = docSampah.data
                                                    if (sampahData != null) {
                                                        val nama = sampahData["nama"] as String
                                                        val satuan = sampahData["satuan"] as String
                                                        val hargaReferensi = sampahData["hargaBeli"] as Long

                                                        val sampahBankSampah = SampahList(id, nama, satuan, hargaBeli.toInt(), hargaReferensi.toInt())
                                                        data.add(sampahBankSampah)

                                                        adapter.setData(data)
                                                        recyclerView.adapter = adapter
                                                    }
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

        return view
    }
}