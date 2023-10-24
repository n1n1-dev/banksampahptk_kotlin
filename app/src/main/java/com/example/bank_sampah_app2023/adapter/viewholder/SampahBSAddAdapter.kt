package com.example.bank_sampah_app2023.adapter.viewholder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bank_sampah_app2023.R
import com.example.bank_sampah_app2023.model.SampahListAdd
import java.text.NumberFormat
import java.util.Currency

class SampahBSAddAdapter(private val checkBoxListener: CheckBoxListener) : RecyclerView.Adapter<SampahBSAddAdapter.ViewHolder>() {

    private var data: List<SampahListAdd> = emptyList()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val namaText: TextView = itemView.findViewById(R.id.namasampahAddText)
        val hargaText: TextView = itemView.findViewById(R.id.hargasampahAddText)
        val satuanText: TextView = itemView.findViewById(R.id.satuansampahAddText)
        val checkBox: CheckBox = itemView.findViewById(R.id.checkBox)

        init {
            checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
                val item = data[adapterPosition]
                if (isChecked) {
                    checkBoxListener.onCheckBoxChecked(item)
                } else {
                    checkBoxListener.onCheckBoxUnchecked(item)
                }
            }
        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_crud_sampah_bank_sampah, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        val format: NumberFormat = NumberFormat.getCurrencyInstance()
        format.setMaximumFractionDigits(0)
        format.setCurrency(Currency.getInstance("IDR"))
        val harga = format.format(item.hargaBeli)
        holder.satuanText.text = "Satuan: ${item.satuan}"
        holder.namaText.text = item.nama
        holder.hargaText.text = "Harga Beli: $harga"
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun setData(data: List<SampahListAdd>) {
        this.data = data
        notifyDataSetChanged()
    }

    interface CheckBoxListener {
        fun onCheckBoxChecked(item: SampahListAdd)
        fun onCheckBoxUnchecked(item: SampahListAdd)
    }

}
