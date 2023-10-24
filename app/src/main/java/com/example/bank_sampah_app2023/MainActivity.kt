package com.example.bank_sampah_app2023

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    companion object {

        val SIGNIN = ""

        fun replaceFragment(fragment: Fragment, activity: AppCompatActivity) {
            activity.supportFragmentManager.beginTransaction()
                .replace(R.id.frame_container, fragment)
                .commit()
        }

    }

    private var auth = FirebaseAuth.getInstance()

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var bottomNasabahNavigationView: BottomNavigationView
    private lateinit var bottomAdminNavigationView: BottomNavigationView
    private var selectedItemId = 1
    private var selectedAdminItemId = 1
    private var selectedNasabahItemId = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val user = auth.currentUser
        val uid = user?.uid

        bottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNasabahNavigationView = findViewById(R.id.bottom_navigation_nasabah)
        bottomAdminNavigationView = findViewById(R.id.bottom_navigation_admin)

        val receivedValue = intent.getStringExtra(SIGNIN)

        if (uid != null) {
            val firestore = FirebaseFirestore.getInstance()
            val userDocumentReference = firestore.collection("users").document(uid)

            userDocumentReference.get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val userData = document.data
                        if (userData != null) {
                            val hakakses = userData["rules"] as String

                            if (receivedValue == "signin") {
                                if (hakakses == "superadmin") {
                                    replaceFragment(HomeFragment(), this)
                                    bottomNavigationView.setSelectedItemId(R.id.bottom_home);
                                } else if (hakakses == "admin") {
                                    replaceFragment(HomeFragment(), this)
                                    bottomAdminNavigationView.setSelectedItemId(R.id.bottom_home);
                                } else if (hakakses == "nasabah") {
                                    replaceFragment(HomeFragment(), this)
                                    bottomNasabahNavigationView.setSelectedItemId(R.id.bottom_home);
                                }

                            }
                            else if (receivedValue == "backfromcrudusers") {
                                replaceFragment(UsersFragment(), this)
                                bottomNavigationView.setSelectedItemId(R.id.bottom_admin);
                            }
                            else if ((receivedValue == "backfromcrudbanksampah") || (receivedValue == "backfrommapsadmin")) {
                                replaceFragment(BankSampahFragment(), this)
                                bottomNavigationView.setSelectedItemId(R.id.bottom_bank_sampah);
                            }
                            else if (receivedValue == "backfromcrudsampah") {
                                replaceFragment(SampahFragment(), this)
                                bottomNavigationView.setSelectedItemId(R.id.bottom_sampah);
                            }
                            else if (receivedValue == "backfromkeranjangestimasi") {
                                replaceFragment(KeranjangEstimasiFragment(), this)
                                bottomNasabahNavigationView.setSelectedItemId(R.id.bottom_keranjang_estimasi);
                            }
                            else if (receivedValue == "backfromcrudsampahbanksampah") {
                                replaceFragment(SampahBankSampahFragment(), this)
                                bottomAdminNavigationView.setSelectedItemId(R.id.bottom_sampah_banksampah);
                            }

                            if (hakakses == "superadmin") {
                                bottomNavigationView.visibility = View.VISIBLE
                                bottomNasabahNavigationView.visibility = View.GONE
                                bottomAdminNavigationView.visibility = View.GONE

                                bottomNavigationView.setOnItemSelectedListener { menuItem ->
                                    selectedItemId = menuItem.itemId
                                    menuItem.isChecked = true

                                    when (selectedItemId) {
                                        R.id.bottom_home -> {
                                            replaceFragment(HomeFragment(), this)
                                            true
                                        }

                                        R.id.bottom_admin -> {
                                            replaceFragment(UsersFragment(), this)
                                            true
                                        }

                                        R.id.bottom_bank_sampah -> {
                                            replaceFragment(BankSampahFragment(), this)
                                            true
                                        }

                                        R.id.bottom_sampah -> {
                                            replaceFragment(SampahFragment(), this)
                                            true
                                        }

                                        R.id.bottom_settings -> {
                                            replaceFragment(SettingsFragment(), this)
                                            true
                                        }

                                        else -> false
                                    }
                                }

                            } else if (hakakses == "admin") {
                                bottomNavigationView.visibility = View.GONE
                                bottomNasabahNavigationView.visibility = View.GONE
                                bottomAdminNavigationView.visibility = View.VISIBLE

                                bottomAdminNavigationView.setOnItemSelectedListener { menuItem ->
                                    selectedAdminItemId = menuItem.itemId
                                    menuItem.isChecked = true

                                    when (selectedAdminItemId) {
                                        R.id.bottom_home -> {
                                            replaceFragment(HomeFragment(), this)
                                            true
                                        }

                                        R.id.bottom_sampah_banksampah -> {
                                            replaceFragment(SampahBankSampahFragment(), this)
                                            true
                                        }

                                        R.id.bottom_settings -> {
                                            replaceFragment(SettingsFragment(), this)
                                            true
                                        }

                                        else -> false
                                    }
                                }
                            } else if (hakakses == "nasabah") {
                                bottomNavigationView.visibility = View.GONE
                                bottomNasabahNavigationView.visibility = View.VISIBLE
                                bottomAdminNavigationView.visibility = View.GONE

                                bottomNasabahNavigationView.setOnItemSelectedListener { menuItem ->
                                    selectedNasabahItemId = menuItem.itemId
                                    menuItem.isChecked = true

                                    when (selectedNasabahItemId) {
                                        R.id.bottom_home -> {
                                            replaceFragment(HomeFragment(), this)
                                            true
                                        }

                                        R.id.bottom_bank_sampah_users -> {
                                            replaceFragment(MapsUsersFragment(), this)
                                            true
                                        }

                                        R.id.bottom_keranjang_estimasi -> {
                                            replaceFragment(KeranjangEstimasiFragment(), this)
                                            true
                                        }

                                        R.id.bottom_settings -> {
                                            replaceFragment(SettingsFragment(), this)
                                            true
                                        }

                                        else -> false
                                    }
                                }
                            }

                        }
                    }
                }
        }

    }

}