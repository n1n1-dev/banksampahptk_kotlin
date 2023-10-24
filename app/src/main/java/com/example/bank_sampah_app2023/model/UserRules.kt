package com.example.bank_sampah_app2023.model

import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserRules(
    @DocumentId
    val id: String? = null,
    var userId: String? = null,
    var email: String? = null,
    var rules: String? = null,
    var banksampahId: String? = null
): Parcelable


data class UsersItem(
    val id: String,
    val nama: String,
    val daftarUserItem: List<UserDetailItem>,
)

data class UserDetailItem(val userId: String, val email: String, val rules: String, val banksampahId: String)
