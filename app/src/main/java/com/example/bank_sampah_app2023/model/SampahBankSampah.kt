package com.example.bank_sampah_app2023.model

import android.os.Parcelable
import com.google.firebase.firestore.DocumentReference
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class SampahBankSampah(
    val id: String?,
    val banksampahRef: @RawValue DocumentReference,
    val sampahRef: @RawValue DocumentReference,
    val hargaBeli: Int
): Parcelable


data class SampahList(
    val id: String,
    val nama: String,
    val satuan: String,
    val hargaBeli: Int,
    val hargaReferensi: Int
)

data class SampahListAdd(
    val id: String,
    val nama: String,
    val satuan: String,
    val hargaBeli: Int
)

data class BankSampahMaps(
    val idBankSampah: String,
    val namaBankSampah: String,
    val legalitasBankSampah: String,
    val lokasilatBankSampah: Double,
    val lokasilongBankSampah: Double,
    val alamatBankSampah: String,
    val daftarSampah: MutableList<SampahItem>,
    var jarakBankSampah: Double,
    var rekomendasiJarak: Double
)

data class SampahItem(val idBankSampah: String, val idSampahBankSampah: String, val nama: String, val satuan: String, var hargaBeli: Int, var rekomendasiHarga: Double, var rekomendasiTerbaik: Double)
