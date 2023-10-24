package com.example.bank_sampah_app2023.model

import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.GeoPoint
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class BankSampah (
    @DocumentId
    var id: String? = null,
    var nama: String? = null,
    var legalitas: String? = null,
    var lokasi: @RawValue GeoPoint? = null,
    var alamat: String? = null
) : Parcelable