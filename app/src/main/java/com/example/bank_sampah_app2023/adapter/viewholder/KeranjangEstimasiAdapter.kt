package com.example.bank_sampah_app2023.adapter.viewholder

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.bank_sampah_app2023.MainActivity
import com.example.bank_sampah_app2023.R
import com.example.bank_sampah_app2023.model.KeranjangEstimasiList
import com.google.firebase.firestore.FirebaseFirestore
import java.text.NumberFormat
import java.util.Currency

class KeranjangEstimasiAdapter(private val listener: KeranjangEstimasiListener) : RecyclerView.Adapter<KeranjangEstimasiAdapter.ViewHolder>() {

    private var data: List<KeranjangEstimasiList> = emptyList()

    private val mFirestore = FirebaseFirestore.getInstance()
    private val mKEColl = mFirestore.collection("keranjangestimasi")

    var quantity: Double = 0.0
    var subtotal: Double = 0.0

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val namaText: TextView = itemView.findViewById(R.id.textViewNamaKE)
        val satuanText: TextView = itemView.findViewById(R.id.textViewSatuanKE)
        val namaBSText: TextView = itemView.findViewById(R.id.textViewNamaBSKE)
        val hargaText: TextView = itemView.findViewById(R.id.textViewHargaKE)
        val hapusKEBtn: ImageButton = itemView.findViewById(R.id.hapusKEBtn)

        val jumlahTextView: TextView = itemView.findViewById(R.id.jumlahTextView)
        val decreaseButton: ImageButton = itemView.findViewById(R.id.decreaseButton)
        val increaseButton: ImageButton = itemView.findViewById(R.id.increaseButton)
        val subtotalTextView: TextView = itemView.findViewById(R.id.subtotalTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_keranjang_estimasi, parent, false)
        return ViewHolder(view)
    }
    @SuppressLint("RecyclerView")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        val format: NumberFormat = NumberFormat.getCurrencyInstance()
        format.setMaximumFractionDigits(0)
        format.setCurrency(Currency.getInstance("IDR"))
        val harga = format.format(item.hargaBeli)
        holder.namaText.text = item.nama
        holder.namaBSText.text = "Bank Sampah: ${item.namabanksampah}"
        holder.satuanText.text = "Satuan: ${item.satuan}"
        holder.hargaText.text = "Harga Beli: $harga"

        val pricePerItem = item.hargaBeli
        quantity = (item.jumlah).toDouble()

        holder.hapusKEBtn.setOnClickListener { showDialogDel(holder, item.id, item.nama) }

        holder.jumlahTextView.text = quantity.toString()
        updateSubTotal(position, pricePerItem, quantity, holder.subtotalTextView)

        holder.decreaseButton.setOnClickListener {
            val textFromTextView = holder.jumlahTextView.text.toString()
            if (textFromTextView.isNotEmpty()) {
                quantity = textFromTextView.toDouble()
                if (quantity > 0 ) quantity--
            } else {
                quantity = 0.0
                holder.jumlahTextView.text = quantity.toString()
            }

            holder.jumlahTextView.text = quantity.toString()
            updateSubTotal(position, pricePerItem, quantity, holder.subtotalTextView)
        }

        holder.increaseButton.setOnClickListener {
            val textFromTextView = holder.jumlahTextView.text.toString()
            if (textFromTextView.isNotEmpty()) {
                quantity = textFromTextView.toDouble()
                quantity++
            } else {
                quantity = 0.0
                quantity++
                holder.jumlahTextView.text = quantity.toString()
            }

            holder.jumlahTextView.text = quantity.toString()
            updateSubTotal(position, pricePerItem, quantity, holder.subtotalTextView)
        }

        holder.jumlahTextView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                val textFromTextView = holder.jumlahTextView.text.toString()
                if (textFromTextView.isNotEmpty()) {
                    quantity = textFromTextView.toDouble()
                } else {
                    quantity = 0.0
                    holder.jumlahTextView.text = quantity.toString()
                }
                updateSubTotal(position, pricePerItem, quantity, holder.subtotalTextView)
            }
        })
    }

    private fun updateSubTotal(position: Int, pricePerItem: Int, quantity: Double, totalTextView: TextView) {
        val format: NumberFormat = NumberFormat.getCurrencyInstance()
        format.maximumFractionDigits = 0
        format.currency = Currency.getInstance("IDR")

        val item = data[position]
        subtotal = (pricePerItem).toDouble() * quantity
        item.subtotal = subtotal

        var total = 0.0
        for (datasub in data) {
            total += datasub.subtotal
        }

        val subtotalText = format.format(subtotal)
        totalTextView.text = subtotalText

        val totalText = format.format(total)
        listener.onQuantityChanged("Total: $totalText")
    }


    private fun showDialogDel(holder: ViewHolder, id: String, nama: String) {
        /* dialog pop hapus */
        val builder = AlertDialog.Builder(holder.itemView.context)
            .setTitle("Hapus Data")
            .setMessage("Hapus '$nama' ?")
            .setPositiveButton("Ya") { dialog, which ->
                deleteById(holder, id)
            }
            .setNegativeButton("Batal", null)
        builder.create().show()
    }

    private fun deleteById(holder: ViewHolder, id: String) {
        mKEColl.document(id)
            .delete()
            .addOnCompleteListener {
                back(holder.itemView.context)
                Toast.makeText(holder.itemView.context, "Hapus Data Berhasil", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { Toast.makeText(holder.itemView.context, "Gagal Hapus data", Toast.LENGTH_SHORT).show() }
    }

    private fun back(context: Context) {
        context.startActivity(
            Intent(context, MainActivity::class.java).apply {
                putExtra(MainActivity.SIGNIN, "backfromkeranjangestimasi")
            })
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun setData(data: List<KeranjangEstimasiList>) {
        this.data = data
        notifyDataSetChanged()
    }

    interface KeranjangEstimasiListener {
        fun onQuantityChanged(total: String)
    }

}
