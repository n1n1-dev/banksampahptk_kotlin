package com.example.bank_sampah_app2023.crud

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bank_sampah_app2023.MainActivity
import com.example.bank_sampah_app2023.R
import com.example.bank_sampah_app2023.model.Sampah
import com.google.firebase.firestore.FirebaseFirestore

class CrudSampah : AppCompatActivity() {

    companion object {
        val titleCrudSampah = ""
        val MODEL_DATA = "model_data"
    }

    private var title = ""
    private var sampah: Sampah? = null
    private val mFirestore = FirebaseFirestore.getInstance()
    private val mSampahCollection = mFirestore.collection("sampah")

    private lateinit var btnBack: ImageButton
    private lateinit var judulCrud: TextView
    private lateinit var btnSave: Button
    private lateinit var namaText: TextView
    private lateinit var hargaText: TextView
    private lateinit var satuanText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crud_sampah)

        title = intent.getStringExtra(titleCrudSampah).toString()
        sampah = intent.getParcelableExtra(MODEL_DATA)

        btnBack = findViewById(R.id.back_buttonsampah)
        judulCrud = findViewById(R.id.judulCrudSampah)
        namaText = findViewById(R.id.edTextNamaSampah)
        hargaText = findViewById(R.id.edTextHargaSampah)
        satuanText = findViewById(R.id.edTextSatuanSampah)
        btnSave = findViewById(R.id.btn_savesampah)

        judulCrud.text = title

        btnSave.setOnClickListener { saveData(sampah?.id)}
        initView()

        btnBack.setOnClickListener {
           back()
        }

    }

    private fun back() {
        startActivity(Intent(this, MainActivity::class.java).apply {
            putExtra(MainActivity.SIGNIN, "backfromcrudsampah")
        })
    }

    private fun saveData(id: String?) {
        val nama = namaText.text.toString()
        val satuan = satuanText.text.toString()
        val harga = hargaText.text.toString()

        if (nama.isNotEmpty() && satuan.isNotEmpty() && harga.isNotEmpty()) {
            if ((title == "Tambah Sampah") && (id == null)) {
                val data = HashMap<String,Any>()
                data["nama"] = nama
                data["satuan"] = satuan
                data["hargaBeli"] = harga.toInt()
                mSampahCollection.add(data)
                    .addOnSuccessListener {
                        back()
                        Toast.makeText(this, "Tambah Data Berhasil ", Toast.LENGTH_LONG).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, " Tambah Data Gagal ", Toast.LENGTH_LONG).show()
                    }
            }
            else if ((title == "Ubah Sampah") && (id != null)) {
                mSampahCollection.document(id).update("nama", nama,
                    "satuan", satuan,
                    "hargaBeli", harga.toInt())
                    .addOnSuccessListener {
                        back()
                        Toast.makeText(this, "Ubah Data Berhasil ", Toast.LENGTH_LONG).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, " Ubah Data Gagal ", Toast.LENGTH_LONG).show()
                    }
            }
        }
        else {Toast.makeText(this, " Form Wajib Diisi Semua ", Toast.LENGTH_LONG).show()}

    }

    private fun initView() {
        if (title == "Ubah Sampah") {
            namaText.text = sampah!!.nama
            satuanText.text = sampah!!.satuan
            hargaText.text = sampah!!.hargaBeli.toString()
        }
    }

}