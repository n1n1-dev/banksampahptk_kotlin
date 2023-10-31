package com.example.bank_sampah_app2023.adapter.viewholder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bank_sampah_app2023.OnLocationClickListener
import com.example.bank_sampah_app2023.R
import com.example.bank_sampah_app2023.model.BankSampahMaps
import com.example.bank_sampah_app2023.model.SampahItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.NumberFormat
import java.util.Currency


class BSMapsAdapter(private var data: List<BankSampahMaps>, private val locationClickListener: OnLocationClickListener, private val filteredData: List<BankSampahMaps> = emptyList()) : RecyclerView.Adapter<BSMapsAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val namaBankSampahTextView: TextView = itemView.findViewById(R.id.namabsmuText)
        val alamatBankSampahTextView: TextView = itemView.findViewById(R.id.alamatbsmuText)
        val lokasiBankSampahTextView: TextView = itemView.findViewById(R.id.lokasibsmuText)
        val legalitasBankSampahTextView: TextView = itemView.findViewById(R.id.legalitasbsmuText)
        val jaraksbsmuTextView: TextView = itemView.findViewById(R.id.jaraksbsmuText)
        val sampahRecyclerView: RecyclerView = itemView.findViewById(R.id.sampahMapsUsersRV)
        val btnLokOn: ImageButton = itemView.findViewById(R.id.button_maps)

        private val parentBSText: LinearLayout = itemView.findViewById(R.id.parentMapsUsers)
        private val expandBSButton: ImageButton = itemView.findViewById(R.id.expand_buttonbsmu)
        private val expandedBSArea: LinearLayout = itemView.findViewById(R.id.expanded_areabsmu)

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

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_maps_users, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var item : BankSampahMaps
        if (filteredData.isNotEmpty()) {
            item = filteredData[position]
        } else{
            item = data[position]
        }
        holder.namaBankSampahTextView.text = item.namaBankSampah
        holder.legalitasBankSampahTextView.text =
            "Legalitas \n ${item.legalitasBankSampah}"
        holder.lokasiBankSampahTextView.text =
            "Lokasi \n ${item.lokasilatBankSampah}, ${item.lokasilongBankSampah}"
        holder.alamatBankSampahTextView.text = "Alamat \n ${item.alamatBankSampah}"
        holder.jaraksbsmuTextView.text = "Jarak \n ${item.jarakBankSampah} km"

        holder.btnLokOn.setOnClickListener {
            locationClickListener.onClickLocation(item.lokasilongBankSampah, item.lokasilatBankSampah)
        }

        val sampahAdapter = BSMapsItemAdapter(item.daftarSampah)
        holder.sampahRecyclerView.layoutManager = LinearLayoutManager(holder.itemView.context)
        holder.sampahRecyclerView.adapter = sampahAdapter
    }

    override fun getItemCount(): Int {
        return if (filteredData.isNotEmpty()) {
            filteredData.size
        } else {
            data.size
        }
    }

    fun updateData(newData: List<BankSampahMaps>) {
        data = newData
        notifyDataSetChanged()
    }
}

class BSMapsItemAdapter(private val dataItem: List<SampahItem>) : RecyclerView.Adapter<BSMapsItemAdapter.ViewHolder>() {

    private val mFirestore = FirebaseFirestore.getInstance()
    private val mKECollection = mFirestore.collection("keranjangestimasi")

    private var auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    val uid = user?.uid

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val namaSampahTextView: TextView = itemView.findViewById(R.id.namaSampahMapsUser)
        val satuanSampahTextView: TextView = itemView.findViewById(R.id.satuanSampahMapsUser)
        val hargaBeliSampahTextView: TextView = itemView.findViewById(R.id.hargaBeliSampahMapsUser)
        val btn_addchart: Button = itemView.findViewById(R.id.btn_addchart)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_sampah_maps_users, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataItem[position]
        val format: NumberFormat = NumberFormat.getCurrencyInstance()
        format.setMaximumFractionDigits(0)
        format.setCurrency(Currency.getInstance("IDR"))
        val harga = format.format(item.hargaBeli)
        holder.namaSampahTextView.text = item.nama
        holder.satuanSampahTextView.text = "Satuan: ${item.satuan} \n terbaik ${item.rekomendasiTerbaik} "
        holder.hargaBeliSampahTextView.text = "Harga Beli: $harga"

        val targetRef = FirebaseFirestore.getInstance().document("sampahbanksampah/${item.idSampahBankSampah}")

        holder.btn_addchart.setOnClickListener {
            mFirestore.collection("keranjangestimasi").whereEqualTo("usersId", uid).whereEqualTo("sampahbanksampahRef", targetRef)
                .get()
                .addOnSuccessListener { queryData ->
                    if (queryData.size()<=0) {
                        val data = HashMap<String,Any>()
                        data["sampahbanksampahRef"] = targetRef
                        data["jumlah"] = 1
                        data["usersId"] = uid.toString()
                        mKECollection.add(data)
                            .addOnSuccessListener {
                                Toast.makeText(holder.itemView.context, "Tambah Data Berhasil", Toast.LENGTH_LONG).show()
                            }
                            .addOnFailureListener {
                                Toast.makeText(holder.itemView.context, "Tambah Data Gagal", Toast.LENGTH_LONG).show()
                            }
                    }else{
                        Toast.makeText(holder.itemView.context, "Jenis Sampah Sudah Ditambahkan Pada Keranjang Estimasi", Toast.LENGTH_LONG).show()
                    }
                }

        }

    }

    override fun getItemCount(): Int {
        return dataItem.size
    }
}