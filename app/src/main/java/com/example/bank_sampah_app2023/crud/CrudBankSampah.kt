package com.example.bank_sampah_app2023.crud

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bank_sampah_app2023.MainActivity
import com.example.bank_sampah_app2023.MapsAdmin
import com.example.bank_sampah_app2023.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint

class CrudBankSampah : AppCompatActivity() {

    companion object {
        val titleCrudBankSampah = ""
        val dataBSList = null

    }

    private var title = ""
    private val mFirestoreBs = FirebaseFirestore.getInstance()
    private val mBankSampahCollection = mFirestoreBs.collection("banksampah")

    private lateinit var btnBackBS: ImageButton
    private lateinit var judulCrudBS: TextView
    private lateinit var edNama : TextView
    private lateinit var edLegalitas : TextView
    private lateinit var edAlamat : TextView
    private lateinit var edLatLokasi : TextView
    private lateinit var edLongLokasi : TextView
    private lateinit var btnLokasi : Button
    private lateinit var btnSave: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crud_bank_sampah)

        btnBackBS = findViewById(R.id.back_buttonbs)
        judulCrudBS = findViewById(R.id.judulCrudBS)
        btnLokasi = findViewById(R.id.btn_lokasi)
        edNama = findViewById(R.id.edTextNamaBS)
        edLegalitas = findViewById(R.id.edTextLegalitasBS)
        edLatLokasi = findViewById(R.id.edTextLokasiLatBS)
        edLongLokasi = findViewById(R.id.edTextLokasiLongBS)
        edAlamat = findViewById(R.id.edTextAlamatBS)
        btnSave = findViewById(R.id.btn_savebanksampah)

        title = intent.getStringExtra(titleCrudBankSampah).toString()
        judulCrudBS.text = title

        btnLokasi.setOnClickListener {
            val intent = Intent(this, MapsAdmin::class.java)
            startActivityForResult(intent, 1)
        }

        btnSave.setOnClickListener { saveData()}
        initView()

        btnBackBS.setOnClickListener {
            back()
        }

    }

    private fun back() {
        startActivity(Intent(this, MainActivity::class.java).apply {
            putExtra(MainActivity.SIGNIN, "backfromcrudbanksampah")
        })
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            val loklat = data?.getStringExtra("lokasilat")
            val loklong = data?.getStringExtra("lokasilong")
            val alamat = data?.getStringExtra("alamat")

            edLatLokasi.text = loklat
            edLongLokasi.text = loklong
            edAlamat.text = alamat
        }
    }

    private fun saveData() {
        val dataList = intent.getStringArrayListExtra(dataBSList)
        val namaBSInput = edNama.text.toString()
        val legalitasBSInput = edLegalitas.text.toString()
        val lat = edLatLokasi.text.toString()
        val long = edLongLokasi.text.toString()
        val alamatBSInput = edAlamat.text.toString()

        if (namaBSInput.isNotEmpty() && legalitasBSInput.isNotEmpty() && lat.isNotEmpty() && long.isNotEmpty() && alamatBSInput.isNotEmpty()) {
            val lok = GeoPoint(lat.toDouble(), long.toDouble())
            if ((title == "Tambah Bank Sampah") && (dataList == null)) {
                val data = HashMap<String,Any>()
                data["nama"] = namaBSInput
                data["legalitas"] = legalitasBSInput
                data["lokasi"] = lok
                data["alamat"] = alamatBSInput
                mBankSampahCollection.add(data)
                    .addOnSuccessListener {
                        back()
                        Toast.makeText(this, "Tambah Data Berhasil ", Toast.LENGTH_LONG).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, " Tambah Data Gagal ", Toast.LENGTH_LONG).show()
                    }
            }
            else if ((title == "Ubah Bank Sampah") && (dataList != null)) {
                mBankSampahCollection.document(dataList[0]).update("nama", namaBSInput,
                    "legalitas", legalitasBSInput,
                    "lokasi", lok,
                    "alamat", alamatBSInput)
                    .addOnSuccessListener {
                        back()
                        Toast.makeText(this, "Ubah Data Berhasil ", Toast.LENGTH_LONG).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, " Ubah Data Gagal ", Toast.LENGTH_LONG).show()
                    }
            }
        }
        else {
            Toast.makeText(this, " Form Wajib Diisi Semua ", Toast.LENGTH_LONG).show()}

    }

    private fun initView() {
        val receivedList = intent.getStringArrayListExtra(dataBSList)
        if (title == "Ubah Bank Sampah") {
            if (receivedList != null) {
                edNama.text = receivedList[1].toString()
                edLegalitas.text = receivedList[2].toString()
                edLatLokasi.text = receivedList[3].toString()
                edLongLokasi.text = receivedList[4].toString()
                edAlamat.text = receivedList[5].toString()
            }
        }
    }
}