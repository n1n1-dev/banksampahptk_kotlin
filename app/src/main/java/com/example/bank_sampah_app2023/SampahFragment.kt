package com.example.bank_sampah_app2023

import android.annotation.SuppressLint
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
import com.example.bank_sampah_app2023.adapter.viewholder.SampahViewHolder
import com.example.bank_sampah_app2023.crud.CrudSampah
import com.example.bank_sampah_app2023.model.Sampah
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore


class SampahFragment : Fragment() {

    private lateinit var mAdapter: FirestoreRecyclerAdapter<Sampah, SampahViewHolder>
    private val mFirestore = FirebaseFirestore.getInstance()
    private val mSampahCollection = mFirestore.collection("sampah")

    private lateinit var rvList: RecyclerView
    private lateinit var fabaddsampah: FloatingActionButton
    private lateinit var btnDelete: ImageButton
    private lateinit var btnEdit: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_sampah, container, false)

        initView(view)
        setupAdapter()

        return view
    }

    private fun initView(view: View) {
        rvList = view.findViewById(R.id.sampahRecyclerView)
        fabaddsampah = view.findViewById(R.id.fabaddsampah)

        rvList.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
        }

        fabaddsampah.setOnClickListener {
            val intent = Intent(requireContext(), CrudSampah::class.java)
            intent.putExtra(CrudSampah.titleCrudSampah, "Tambah Sampah")
            startActivity(intent)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setupAdapter() {
        /* set adapter yang akan menampilkan data pada recyclerview */
        val options = FirestoreRecyclerOptions.Builder<Sampah>()
            .setQuery(mSampahCollection, Sampah::class.java)
            .build()

        mAdapter = object : FirestoreRecyclerAdapter<Sampah, SampahViewHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SampahViewHolder {
                return SampahViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_sampah, parent, false))

            }

            override fun onBindViewHolder(viewHolder: SampahViewHolder, position: Int, model: Sampah) {
                viewHolder.bindItem(model)
                btnDelete = viewHolder.itemView.findViewById(R.id.hapussampahBtn)
                btnEdit = viewHolder.itemView.findViewById(R.id.editsampahBtn)

                btnDelete.setOnClickListener{showDialogDel(model.id!!, model.nama!!)}

                btnEdit.setOnClickListener {
                    startActivity(Intent(requireContext(), CrudSampah::class.java).apply {
                        putExtra(CrudSampah.titleCrudSampah, "Ubah Sampah")
                        putExtra(CrudSampah.MODEL_DATA, model)
                    })
                }
            }
        }
        mAdapter.notifyDataSetChanged()
        rvList.adapter = mAdapter
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
        mSampahCollection.document(id)
            .delete()
            .addOnCompleteListener { Toast.makeText(requireContext(), "Hapus Data Berhasil", Toast.LENGTH_SHORT).show() }
            .addOnFailureListener { Toast.makeText(requireContext(), "Gagal Hapus data", Toast.LENGTH_SHORT).show() }
    }

    override fun onStart() {
        super.onStart()
        mAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        mAdapter.stopListening()
    }

}