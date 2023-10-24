package com.example.bank_sampah_app2023

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint

class SettingsFragment : Fragment() {

    private lateinit var btnLogOut: Button
    private lateinit var emailInfo: TextView
    private lateinit var rulesInfo: TextView

    private lateinit var adminLayout : LinearLayout
    private lateinit var namabsInfo: TextView
    private lateinit var legalbsInfo: TextView
    private lateinit var lokasibsInfo: TextView
    private lateinit var alamatbsInfo: TextView

    private var auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        btnLogOut = view.findViewById(R.id.btn_logout)

        btnLogOut.setOnClickListener {
            auth.signOut()
            val intent = Intent(requireContext(), SignInActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        initView(view)

        return view
    }

    private fun initView(view: View) {
        val user = auth.currentUser
        val uid = user?.uid

        emailInfo = view.findViewById(R.id.emailInfo)
        rulesInfo = view.findViewById(R.id.rulesInfo)
        adminLayout = view.findViewById(R.id.admininfoLayout)
        namabsInfo = view.findViewById(R.id.bsInfo)
        legalbsInfo = view.findViewById(R.id.legalInfo)
        lokasibsInfo = view.findViewById(R.id.lokasiInfo)
        alamatbsInfo = view.findViewById(R.id.alamatInfo)

        if (uid != null) {
            val firestore = FirebaseFirestore.getInstance()
            val userDocumentReference = firestore.collection("users").document(uid)

            userDocumentReference.get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val userData = document.data
                        if (userData != null) {
                            val email = userData["email"] as String
                            val hakakses = userData["rules"] as String
                            val idbs = userData["banksampahId"] as String

                            emailInfo.text = email
                            rulesInfo.text = hakakses

                            if (hakakses == "admin")
                            {
                                val bsDocumentReference = firestore.collection("banksampah").document(idbs)
                                bsDocumentReference.get()
                                    .addOnSuccessListener {
                                            documentbs ->
                                        if (documentbs != null && documentbs.exists()) {
                                            val bsData = documentbs.data

                                            val nm = bsData!!["nama"] as String
                                            val legal = bsData["legalitas"] as String
                                            val lokasi = bsData["lokasi"] as GeoPoint
                                            val alamat = bsData["alamat"] as String

                                            namabsInfo.text = nm
                                            legalbsInfo.text = legal
                                            lokasibsInfo.text = "${lokasi.latitude}, ${lokasi.longitude}"
                                            alamatbsInfo.text = alamat

                                            adminLayout.visibility = View.VISIBLE

                                        }
                                    }
                            } else adminLayout.visibility = View.GONE

                        }
                    }
                }
        }
    }

}