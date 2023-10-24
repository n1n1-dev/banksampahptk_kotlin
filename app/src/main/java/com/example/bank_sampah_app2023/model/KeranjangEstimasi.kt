package com.example.bank_sampah_app2023.model

import android.os.Parcelable
import com.google.firebase.firestore.DocumentReference
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class KeranjangEstimasi(
    val id: String?,
    val sampahbanksampahRef: @RawValue DocumentReference,
    val jumlah: Int,
    val usersId: String
): Parcelable

data class KeranjangEstimasiList(
    val id: String,
    val namabanksampah: String,
    val nama: String,
    val satuan: String,
    var jumlah: Int,
    val hargaBeli: Int,
    var subtotal: Double,
    val userId: String
)
