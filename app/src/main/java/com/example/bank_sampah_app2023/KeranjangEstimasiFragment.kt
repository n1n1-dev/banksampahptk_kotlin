package com.example.bank_sampah_app2023

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bank_sampah_app2023.adapter.viewholder.KeranjangEstimasiAdapter
import com.example.bank_sampah_app2023.model.KeranjangEstimasiList
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class KeranjangEstimasiFragment : Fragment(), KeranjangEstimasiAdapter.KeranjangEstimasiListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: KeranjangEstimasiAdapter
    private lateinit var totalTextView: TextView

    private val mFirestore = FirebaseFirestore.getInstance()
    private var auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    val uid = user?.uid

    var jumlah = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_keranjang_estimasi, container, false)

        totalTextView = view.findViewById(R.id.totalTextView)

        recyclerView = view.findViewById(R.id.keranjangestimasiRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = KeranjangEstimasiAdapter(this)

        if (uid != null) {
            mFirestore.collection("keranjangestimasi").whereEqualTo("usersId", uid).get()
                .addOnSuccessListener { document ->
                    val data: MutableList<KeranjangEstimasiList> = mutableListOf()

                    if (document != null && !(document.isEmpty())) {
                        for (document in document) {
                            val id = document.id
                            val sampahbanksampahRef = document.get("sampahbanksampahRef") as DocumentReference
                            jumlah = (document.get("jumlah") as Long).toInt()

                            val dataSBS = sampahbanksampahRef.path
                            val idSBS = dataSBS.split("/")

                            mFirestore.collection("sampahbanksampah").document(idSBS[1]).get()
                                .addOnSuccessListener { docSBS ->
                                    if (docSBS != null && docSBS.exists()) {
                                        val datadocSBS = docSBS.data
                                        val banksampahRef =  datadocSBS!!["banksampahRef"] as DocumentReference
                                        val sampahRef =  datadocSBS["sampahRef"] as DocumentReference
                                        val hargaBeli = (datadocSBS["hargaBeli"] as Long).toInt()
                                        val subtotal = jumlah * hargaBeli

                                        val dataBankSampah = banksampahRef.path
                                        val idBankSampah = dataBankSampah.split("/")

                                        val dataSampah = sampahRef.path
                                        val idSampah = dataSampah.split("/")

                                        mFirestore.collection("sampah").document(idSampah[1]).get()
                                            .addOnSuccessListener { docSampah ->
                                                if (docSampah != null && docSampah.exists()) {
                                                    val sampahData = docSampah.data
                                                    if (sampahData != null) {
                                                        val nama = sampahData["nama"] as String
                                                        val satuan = sampahData["satuan"] as String

                                                        mFirestore.collection("banksampah").document(idBankSampah[1]).get()
                                                            .addOnSuccessListener { docBS->
                                                                if (docBS != null && docBS.exists()) {
                                                                    val bsData = docBS.data
                                                                    if (bsData != null) {
                                                                        val namaBS = bsData["nama"] as String
                                                                        val dataSampahList = KeranjangEstimasiList(id, namaBS, nama, satuan, jumlah, hargaBeli, subtotal.toDouble(), uid)
                                                                        data.add(dataSampahList)

                                                                        adapter.setData(data)
                                                                        recyclerView.adapter = adapter
                                                                    }
                                                                }
                                                            }

                                                    }
                                                }
                                            }
                                    }
                                }

                        }
                    }

                }
        }

        return view
    }

    override fun onQuantityChanged(total: String) {
        totalTextView.text = total
    }


}