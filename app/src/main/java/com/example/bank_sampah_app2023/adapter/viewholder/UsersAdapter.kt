package com.example.bank_sampah_app2023.adapter.viewholder

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bank_sampah_app2023.MainActivity
import com.example.bank_sampah_app2023.R
import com.example.bank_sampah_app2023.model.UserDetailItem
import com.example.bank_sampah_app2023.model.UsersItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.runBlocking

class UsersAdapter(private val data: List<UsersItem>) : RecyclerView.Adapter<UsersAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val userbsText: TextView = itemView.findViewById(R.id.namauserbsText)
        val itemRecyclerView: RecyclerView = itemView.findViewById(R.id.itemsUsersRecyclerView)

        private val parentusersText: LinearLayout = itemView.findViewById(R.id.parentUsers)
        private val expandButton: ImageButton = itemView.findViewById(R.id.expand_buttonusers)
        private val expandedArea: LinearLayout = itemView.findViewById(R.id.expanded_areausers)

        init {
            /* Atur listener klik pada tombol expand/collapse */
            parentusersText.setOnClickListener {
                /* Ubah visibilitas expandedArea */
                expandedArea.visibility = if (expandedArea.visibility == View.VISIBLE) View.GONE else View.VISIBLE
            }
            expandButton.setOnClickListener {
                /* Ubah visibilitas expandedArea */
                expandedArea.visibility = if (expandedArea.visibility == View.VISIBLE) View.GONE else View.VISIBLE
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_users, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.userbsText.text = item.nama

        val itemAdapter = ItemsUsersAdapter(item.daftarUserItem)
        holder.itemRecyclerView.layoutManager = LinearLayoutManager(holder.itemView.context)
        holder.itemRecyclerView.adapter = itemAdapter
    }

    override fun getItemCount(): Int {
        return data.size
    }

}


class ItemsUsersAdapter(private val dataItem: List<UserDetailItem>) : RecyclerView.Adapter<ItemsUsersAdapter.ViewHolder>() {

    private val mFirestore = FirebaseFirestore.getInstance()
    private val mUsersColl = mFirestore.collection("users")

    private var auth = FirebaseAuth.getInstance()
    val user = auth.currentUser

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val emailText: TextView = itemView.findViewById(R.id.emailuserText)
        val deleteBtn: ImageButton = itemView.findViewById(R.id.hapususersBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_detail_users, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataItem[position]
        holder.emailText.text = item.email

        holder.deleteBtn.setOnClickListener {
            showDialogDel(item.userId, item.email, holder)
        }
    }

    override fun getItemCount(): Int {
        return dataItem.size
    }

    private fun showDialogDel(id: String, email: String, holder: ViewHolder) {
        /* dialog pop hapus */
        val builder = AlertDialog.Builder(holder.itemView.context)
            .setTitle("Hapus Data")
            .setMessage("Hapus '$email' ?")
            .setPositiveButton("Ya"){dialog, which ->
                runBlocking {deleteById(id, email, holder.itemView.context)}
            }
            .setNegativeButton("Batal", null)
        builder.create().show()
    }

    private suspend fun deleteById(id: String, email: String, context: Context) {
        try {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            val pass = sharedPreferences.getString(email, "")

            auth.signInWithEmailAndPassword(email, pass!!)
                .addOnCompleteListener { signInTask ->
                    if (signInTask.isSuccessful) {
                        val userToDelete = auth.currentUser
                        userToDelete?.delete()
                            ?.addOnCompleteListener { deleteTask ->
                                if (deleteTask.isSuccessful) {
                                    FirebaseAuth.getInstance().signOut()
                                    val passSuperA = sharedPreferences.getString("adminbanksampahptk23@gmail.com", "")
                                    auth.signInWithEmailAndPassword("adminbanksampahptk23@gmail.com", passSuperA!!)
                                        .addOnCompleteListener { signInTask ->
                                            if (signInTask.isSuccessful) {
                                                mUsersColl.document(id).delete()
                                                    .addOnCompleteListener {
                                                        if (user!!.email == "adminbanksampahptk23@gmail.com") {
                                                            val editor = sharedPreferences.edit()
                                                            editor.remove(email).apply()
                                                            Toast.makeText(context, "Admin Bank Sampah Berhasil Dihapus", Toast.LENGTH_SHORT).show()
                                                            back(context)
                                                        }
                                                    }
                                                    .addOnFailureListener { Toast.makeText(context, "Admin Bank Sampah Gagal Dihapus", Toast.LENGTH_SHORT).show() }

                                            }
                                        }
                                        .addOnFailureListener { Toast.makeText(
                                            context,
                                            "Gagal Hapus Admin Bank Sampah",
                                            Toast.LENGTH_SHORT
                                        ).show() }
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Gagal Hapus User",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    } else {
                        Toast.makeText(
                            context,
                            "Gagal masuk dengan email dan kata sandi",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

        } catch (e: Exception) {
            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun back(context: Context) {
        context.startActivity(Intent(context, MainActivity::class.java).apply {
            putExtra(MainActivity.SIGNIN, "backfromcrudusers")
        })
    }

}