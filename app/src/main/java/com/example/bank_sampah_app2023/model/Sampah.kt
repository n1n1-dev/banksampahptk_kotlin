package com.example.bank_sampah_app2023.model

import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
import kotlinx.parcelize.Parcelize

@Parcelize
data class Sampah(
    @DocumentId
    val id: String? = null,
    var nama: String? = null,
    var hargaBeli: Int? = null,
    var satuan: String? = null
) : Parcelable
