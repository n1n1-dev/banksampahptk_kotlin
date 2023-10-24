package com.example.bank_sampah_app2023

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bank_sampah_app2023.adapter.viewholder.UsersAdapter
import com.example.bank_sampah_app2023.crud.CrudUsers
import com.example.bank_sampah_app2023.model.UserDetailItem
import com.example.bank_sampah_app2023.model.UsersItem
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore


class UsersFragment : Fragment() {

    private lateinit var mAdapterUsers: UsersAdapter
    private val mFirestore = FirebaseFirestore.getInstance()
    private val mBSColl = mFirestore.collection("banksampah")

    private lateinit var rvList: RecyclerView
    private lateinit var fabaddusers: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_users, container, false)

        fabaddusers = view.findViewById(R.id.fabaddusers)
        fabaddusers.setOnClickListener {
            val intent = Intent(requireContext(), CrudUsers::class.java)
            intent.putExtra(CrudUsers.titleCrudUsers, "Tambah Admin Bank Sampah")
            startActivity(intent)
        }

        mBSColl.get()
            .addOnSuccessListener { querySnapshot ->
                val dataUserItem: MutableList<UsersItem> = mutableListOf()

                for (document in querySnapshot) {
                    val id = document.id
                    val nama = document.get("nama") as String

                    mFirestore.collection("users").whereEqualTo("banksampahId", id).get()
                        .addOnSuccessListener { docUsers ->
                            val userItem: MutableList<UserDetailItem> = mutableListOf()
                            for (dataUsers in docUsers) {
                                val idUsers = dataUsers.id
                                val email = dataUsers.get("email") as String
                                val rules = dataUsers.get("rules") as String
                                val banksampahId = dataUsers.get("banksampahId") as String

                                val dataUsr = UserDetailItem(idUsers, email, rules, banksampahId)
                                userItem.add(dataUsr)
                            }

                            if (userItem.size == docUsers.size()) {
                                val dataUser = UsersItem(id, nama, userItem)
                                dataUserItem.add(dataUser)

                                if (dataUserItem.size == querySnapshot.size()) {
                                    rvList = view.findViewById(R.id.usersRecyclerView)
                                    mAdapterUsers = UsersAdapter(dataUserItem)
                                    rvList.layoutManager = LinearLayoutManager(requireContext())
                                    rvList.adapter = mAdapterUsers
                                }
                            }
                        }
                }
            }

        return view
    }

}