package com.chairul.githubuser.view

import android.app.Activity
import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import android.database.ContentObserver
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.chairul.githubuser.R
import com.chairul.githubuser.db.DatabaseContract
import com.chairul.githubuser.db.MappingHelper
import com.chairul.githubuser.entity.User
import kotlinx.android.synthetic.main.activity_favorite_user.*
import kotlinx.android.synthetic.main.item_list_user.view.*

class FavoriteUserActivity : AppCompatActivity() {

    private lateinit var adapter: UserAdapter
    var listUser: MutableList<User> = mutableListOf()
    private var lastCheckedUser = arrayListOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite_user)
        supportActionBar?.title = "Favorite User"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        adapter = UserAdapter()
        adapter.notifyDataSetChanged()
        val handlerThread = HandlerThread("DataObserver")
        handlerThread.start()
        checkSQL()
        val handler = Handler(handlerThread.looper)
        val myObserver = object : ContentObserver(handler) {
            override fun onChange(self: Boolean) {
                Log.v("ADX","checkSQL")
                checkSQL()
            }
        }


        contentResolver.registerContentObserver(DatabaseContract.UserColumns.CONTENT_URI, true, myObserver)

        rvFavorite.layoutManager = LinearLayoutManager(this)
        rvFavorite.adapter = adapter
        if(listUser.isNotEmpty()){
            setToAdapter()
        }


    }

    private fun setToAdapter() {
        adapter.setData(listUser)
    }



    override fun onResume() {
        super.onResume()
        checkSQL()
        adapter.setData(listUser)
    }


    override fun onSupportNavigateUp(): Boolean {
        setResult(Activity.RESULT_OK)
        finish()
        return true
    }

    private fun checkSQL() {
        try {
            listUser.clear()
            val cursor = contentResolver?.query(
                DatabaseContract.UserColumns.CONTENT_URI,
                null,
                null,
                null,
                null
            )

            listUser.addAll(MappingHelper.mapCursorToArrayList(cursor).filter { it.isFavorite != "false" })
            Log.v("ADX", "Check SQL List ${listUser.size} \n $listUser")

        }catch (e: Exception){
            Log.v("ADX","Error Update Favorite $e")
        }
    }

    inner class UserAdapter : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {
        private val mData = ArrayList<User>()
        lateinit var itemUser: User
        fun setData(items: MutableList<User>) {
            mData.clear()
            mData.addAll(items)
            notifyDataSetChanged()
        }
        inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


            fun bind(user: User) {
                try {
                    Log.v("ADX", "Data User \n" + user)
                    if (user?.name != null) {

                        with(itemView) {
                            txtNama.text = user?.name
                            if (user?.company != null) txtCompany.text =
                                user?.company else txtCompany.text = "No Company"
                            if(user?.login.equals("N/A")) {
                                txtUsername.text = user?.login
                                Glide.with(context).load(R.drawable.ic_user).into(imgUser)
                            }
                            else {
                                txtUsername.text = user?.login + "@github.com"
                                Glide.with(context).load(user?.avatar_url).into(imgUser)
                            }
                        }

                    }
                }catch (e: Exception){
                    Log.v("ADX","Erorr $e")
                }
            }

            fun goToDetailUser(username: String) {
                Log.v("ADX","Username : $username")
                if(username.equals("N/A")){
                    Toast.makeText(applicationContext,"User ini belum terdaftar di Server", Toast.LENGTH_LONG).show()
                }else {
                    val intent = Intent(this@FavoriteUserActivity, DetailActivity::class.java)
                    intent.putExtra(DetailActivity.DETAIL_USER, username)
                    startActivity(intent)
                }
            }


            fun itemChecked(user : User){
                showMessage("Konfirmasi",
                    "Anda yakin ingin menghapus user ini dari daftar favorite ?",
                    "Yes",{
                        deleteUser(user)
                        it.dismiss()
                        it.cancel()
                        finish()
                    },
                    "No",{},
                    false,{})
            }

        }

        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): UserViewHolder {
            val mView = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.item_list_user, viewGroup, false)
            return UserViewHolder(mView)
        }

        override fun getItemCount(): Int = mData.size

        override fun onBindViewHolder(userViewHolder: UserViewHolder, position: Int) {
            userViewHolder.itemView.icDelete.visibility = View.VISIBLE

            itemUser = mData[position]
            if (position % 2 == 0) userViewHolder.itemView.itemUser.setBackgroundColor(
                resources.getColor(
                    R.color.blue1
                )
            )
            else userViewHolder.itemView.itemUser.setBackgroundColor(resources.getColor(
                R.color.blue2
            ))
            if(mData[position].isFavorite == "true") userViewHolder.bind(mData[position])
            userViewHolder.itemView.setOnClickListener { userViewHolder.goToDetailUser(mData[position].login) }
            userViewHolder.itemView.icDelete.setOnClickListener { userViewHolder.itemChecked(mData[position]) }


        }



    }

    fun deleteUser(user: User){
        var uriWithId = Uri.parse(DatabaseContract.UserColumns.CONTENT_URI.toString() + "/" + user?.id)
        var updateFavorite = "true"
        if(user?.isFavorite != "" && user?.isFavorite != "false") updateFavorite = "false"
        val values = ContentValues()
        values.put(DatabaseContract.UserColumns.LOGIN, user?.login)
        values.put(DatabaseContract.UserColumns.NAME, user?.name ?: "-")
        values.put(DatabaseContract.UserColumns.COMPANY, user?.company ?: "No Company")
        values.put(DatabaseContract.UserColumns.AVATAR_URL, user?.avatar_url ?: "-")
        values.put(DatabaseContract.UserColumns.ISFAVORITE, updateFavorite)
        contentResolver.update(uriWithId, values, null, null)
        checkSQL()
        adapter.setData(listUser)
        adapter.notifyDataSetChanged()
    }

    fun showMessage(title: String, message: String, positiveButton: String?, positiveListener: (DialogInterface) -> Unit?, negativeButton: String?, negativeListener: (DialogInterface) -> Unit?, cancelable: Boolean, onDismissListener: () -> Unit?) {
        val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(this,
            R.style.Theme_AppTheme_Dialog_Alert
        )
            .setTitle(title)
            .setMessage(message)
            .setCancelable(cancelable)
        positiveButton?.let { dialogBuilder.setPositiveButton(it) { dialogInterface, _ -> positiveListener(dialogInterface) } }
        negativeButton?.let { dialogBuilder.setNegativeButton(it) { dialogInterface, _ -> negativeListener(dialogInterface) } }
        dialogBuilder.setOnDismissListener { onDismissListener() }
        dialogBuilder.show()
    }

}
