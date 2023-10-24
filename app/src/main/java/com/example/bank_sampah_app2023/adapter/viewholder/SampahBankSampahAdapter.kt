package com.example.bank_sampah_app2023.adapter.viewholder

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.bank_sampah_app2023.MainActivity
import com.example.bank_sampah_app2023.R
import com.example.bank_sampah_app2023.model.SampahList
import com.google.firebase.firestore.FirebaseFirestore
import java.text.NumberFormat
import java.util.Currency

class SampahBankSampahAdapter : RecyclerView.Adapter<SampahBankSampahAdapter.ViewHolder>() {

    private var data : List<SampahList> = emptyList()
    private lateinit var hapussampahBSBtn : ImageButton
    private lateinit var editsampahBSBtn : ImageButton

    private val mFirestoreBS = FirebaseFirestore.getInstance()
    private val mSBSollection = mFirestoreBS.collection("sampahbanksampah")


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val namaText: TextView = itemView.findViewById(R.id.namasampahBSText)
        val hargaText: TextView = itemView.findViewById(R.id.hargasampahBSText)
        val satuanText: TextView = itemView.findViewById(R.id.satuansampahBSText)
        val hargareferensiText: TextView = itemView.findViewById(R.id.hargareferensiBSText)

        private val parentsampahText: LinearLayout = itemView.findViewById(R.id.parentSampahBS)
        private val expandButton: ImageButton = itemView.findViewById(R.id.expand_buttonsampahBS)
        private val expandedArea: LinearLayout = itemView.findViewById(R.id.expanded_areasampahBS)

        init {
            /* Atur listener klik pada tombol expand/collapse */
            parentsampahText.setOnClickListener {
                /* Ubah visibilitas expandedArea */
                expandedArea.visibility = if (expandedArea.visibility == View.VISIBLE) View.GONE else View.VISIBLE
            }
            expandButton.setOnClickListener {
                /* Ubah visibilitas expandedArea */
                expandedArea.visibility = if (expandedArea.visibility == View.VISIBLE) View.GONE else View.VISIBLE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_sampah_bank_sampah, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        val format: NumberFormat = NumberFormat.getCurrencyInstance()
        format.setMaximumFractionDigits(0)
        format.setCurrency(Currency.getInstance("IDR"))
        val harga = format.format(item.hargaBeli)
        val hargaRef = format.format(item.hargaReferensi)
        holder.satuanText.text = "Satuan: ${item.satuan}"
        holder.namaText.text = item.nama
        holder.hargaText.text = "Harga Beli: $harga"
        holder.hargareferensiText.text = "Harga Referensi: $hargaRef"

        hapussampahBSBtn = holder.itemView.findViewById(R.id.hapussampahBSBtn)
        hapussampahBSBtn.setOnClickListener{showDialogDel(holder, item.id, item.nama)}

        editsampahBSBtn = holder.itemView.findViewById(R.id.editsampahBSBtn)
        editsampahBSBtn = holder.itemView.findViewById(R.id.editsampahBSBtn)
        editsampahBSBtn.setOnClickListener {
            val builder = AlertDialog.Builder(holder.itemView.context)
            builder.setTitle("Ubah Harga Beli Sampah")
            val input = EditText(holder.itemView.context)
            input.hint = "Harga Beli Sampah"
            input.inputType = InputType.TYPE_CLASS_NUMBER
            builder.setView(input)

            builder.setPositiveButton("OK") { dialog, which ->
                val userInput = input.text.toString()
                if (userInput.isNotEmpty()) {
                    val hargaBeli = userInput.toInt()
                    mSBSollection.document(item.id).update("hargaBeli", hargaBeli)
                        .addOnSuccessListener {
                            back(holder.itemView.context)
                            Toast.makeText(holder.itemView.context, "Ubah Harga Beli Berhasil ", Toast.LENGTH_LONG).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(holder.itemView.context, " Ubah Harga Beli Gagal ", Toast.LENGTH_LONG).show()
                        }
                }
            }

            builder.setNegativeButton("Batal") { dialog, which ->
                dialog.cancel()
            }

            builder.show()
        }


    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun setData(data: List<SampahList>) {
        this.data = data
        notifyDataSetChanged()
    }

    private fun showDialogDel(holder: ViewHolder, id: String, nama: String) {
        /* dialog pop hapus */
        val builder = AlertDialog.Builder(holder.itemView.context)
            .setTitle("Hapus Data")
            .setMessage("Hapus '$nama' ?")
            .setPositiveButton("Ya"){dialog, which ->
                deleteById(holder, id)
            }
            .setNegativeButton("Batal", null)
        builder.create().show()
    }

    private fun deleteById(holder: ViewHolder, id: String) {
        mSBSollection.document(id)
            .delete()
            .addOnCompleteListener {
                back(holder.itemView.context)
                Toast.makeText(holder.itemView.context, "Hapus Data Berhasil", Toast.LENGTH_SHORT).show() }
            .addOnFailureListener { Toast.makeText(holder.itemView.context, "Gagal Hapus data", Toast.LENGTH_SHORT).show() }
    }

    private fun back(context: Context) {
        context.startActivity(Intent(context, MainActivity::class.java).apply {
            putExtra(MainActivity.SIGNIN, "backfromcrudsampahbanksampah")
        })
    }
}
