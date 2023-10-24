package com.example.bank_sampah_app2023.adapter.viewholder

import android.annotation.SuppressLint
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bank_sampah_app2023.R
import com.example.bank_sampah_app2023.model.Sampah
import java.text.NumberFormat
import java.util.Currency

class SampahViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    private val namaText: TextView = itemView.findViewById(R.id.namasampahText)
    private val hargaText: TextView = itemView.findViewById(R.id.hargasampahText)
    private val satuanText: TextView = itemView.findViewById(R.id.satuansampahText)
    private val parentsampahText: LinearLayout = itemView.findViewById(R.id.parentSampah)
    private val expandButton: ImageButton = itemView.findViewById(R.id.expand_buttonsampah)
    private val expandedArea: LinearLayout = itemView.findViewById(R.id.expanded_areasampah)

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

    @SuppressLint("SetTextI18n")
    fun bindItem(sampah: Sampah) {
        val format: NumberFormat = NumberFormat.getCurrencyInstance()
        format.setMaximumFractionDigits(0)
        format.setCurrency(Currency.getInstance("IDR"))
        val harga = format.format(sampah.hargaBeli)
        itemView.apply {
            namaText.text = sampah.nama
            hargaText.text = "Harga Beli \n ${harga}"
            satuanText.text = "Satuan \n ${sampah.satuan}"

        }
    }
}