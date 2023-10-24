package com.example.bank_sampah_app2023

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bank_sampah_app2023.adapter.viewholder.BankSampahViewHolder
import com.example.bank_sampah_app2023.crud.CrudBankSampah
import com.example.bank_sampah_app2023.model.BankSampah
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore

class BankSampahFragment : Fragment() {

    private lateinit var mAdapterBS: FirestoreRecyclerAdapter<BankSampah, BankSampahViewHolder>
    private val mFirestoreBS = FirebaseFirestore.getInstance()
    private val mBankSampahCollection = mFirestoreBS.collection("banksampah")

    private lateinit var rvListBS: RecyclerView
    private lateinit var btnEditBS: ImageButton
    private lateinit var btnDeleteBS: ImageButton
    private lateinit var fabaddbs: FloatingActionButton


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_bank_sampah, container, false)

        initView(view)
        setupAdapter()

        return view
    }

    private fun initView(view: View) {
        rvListBS = view.findViewById(R.id.banksampahRV)
        fabaddbs = view.findViewById(R.id.fabaddbs)

        rvListBS.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
        }

        fabaddbs.setOnClickListener {
            val intent = Intent(requireContext(), CrudBankSampah::class.java)
            intent.putExtra(CrudBankSampah.titleCrudBankSampah, "Tambah Bank Sampah")
            startActivity(intent)
        }

    }

    private fun setupAdapter() {
        /* set adapter yang akan menampilkan data pada recyclerview */
        val options = FirestoreRecyclerOptions.Builder<BankSampah>()
            .setQuery(mBankSampahCollection, BankSampah::class.java)
            .build()

        mAdapterBS = object : FirestoreRecyclerAdapter<BankSampah, BankSampahViewHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BankSampahViewHolder {
                return BankSampahViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_bank_sampah, parent, false))

            }

            override fun onBindViewHolder(viewHolder: BankSampahViewHolder, position: Int, model: BankSampah) {
                viewHolder.bindItem(model)

                btnEditBS = viewHolder.itemView.findViewById(R.id.editbsBtn)
                btnEditBS.setOnClickListener {
                    startActivity(Intent(requireContext(), CrudBankSampah::class.java).apply {
                        putExtra(CrudBankSampah.titleCrudBankSampah, "Ubah Bank Sampah")
                        val list = mutableListOf(model.id.toString(), model.nama.toString(), model.legalitas.toString(), model.lokasi!!.latitude.toString(), model.lokasi!!.longitude.toString(), model.alamat.toString())
                        putExtra(CrudBankSampah.dataBSList, ArrayList(list))

                    })
                }

                btnDeleteBS = viewHolder.itemView.findViewById(R.id.hapusbsBtn)
                btnDeleteBS.setOnClickListener{showDialogDel(model.id!!, model.nama!!)}
            }

        }
        mAdapterBS.notifyDataSetChanged()
        rvListBS.adapter = mAdapterBS

    }

    private fun showDialogDel(id: String, nama: String) {
        /* dialog pop hapus */
        val builder = AlertDialog.Builder(requireContext())
            .setTitle("Hapus Data")
            .setMessage("Hapus '$nama' ?")
            .setPositiveButton("Ya"){dialog, which ->
                deleteById(id)
            }
            .setNegativeButton("Batal", null)
        builder.create().show()
    }

    private fun deleteById(id: String) {
        mBankSampahCollection.document(id)
            .delete()
            .addOnCompleteListener { Toast.makeText(requireContext(), "Hapus Data Berhasil", Toast.LENGTH_SHORT).show() }
            .addOnFailureListener { Toast.makeText(requireContext(), "Gagal Hapus data", Toast.LENGTH_SHORT).show() }
    }

    override fun onStart() {
        super.onStart()
        mAdapterBS.startListening()
    }

    override fun onStop() {
        super.onStop()
        mAdapterBS.stopListening()
    }

}