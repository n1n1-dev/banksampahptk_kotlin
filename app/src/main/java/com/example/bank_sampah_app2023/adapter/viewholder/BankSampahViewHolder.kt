package com.example.bank_sampah_app2023.adapter.viewholder

import android.annotation.SuppressLint
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bank_sampah_app2023.R
import com.example.bank_sampah_app2023.model.BankSampah

class BankSampahViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    private val namaBSText: TextView = itemView.findViewById(R.id.namabsText)
    private val legalitasBSText: TextView = itemView.findViewById(R.id.legalitasbsText)
    private val lokasiBSText: TextView = itemView.findViewById(R.id.lokasibsText)
    private val alamatBSText: TextView = itemView.findViewById(R.id.alamatbsText)

    private val parentBSText: LinearLayout = itemView.findViewById(R.id.parentBSSampah)
    private val expandBSButton: ImageButton = itemView.findViewById(R.id.expand_buttonbs)
    private val expandedBSArea: LinearLayout = itemView.findViewById(R.id.expanded_areabs)

    init {
        /* Atur listener klik pada tombol expand/collapse */
        parentBSText.setOnClickListener {
            /* Ubah visibilitas expandedArea */
            expandedBSArea.visibility = if (expandedBSArea.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        }
        expandBSButton.setOnClickListener {
            /* Ubah visibilitas expandedArea */
            expandedBSArea.visibility = if (expandedBSArea.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        }
    }

    @SuppressLint("SetTextI18n")
    fun bindItem(bankSampah: BankSampah) {
        itemView.apply {
            namaBSText.text = bankSampah.nama
            legalitasBSText.text = "Legalitas \n ${bankSampah.legalitas}"
            lokasiBSText.text = "Lokasi \n ${bankSampah.lokasi!!.latitude}, ${bankSampah.lokasi!!.longitude}"
            alamatBSText.text = "Alamat \n ${bankSampah.alamat}"
        }
    }

}