package com.chairul.githubuser

import android.app.SearchManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.ContentObserver
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_list_user.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.view.View.OnAttachStateChangeListener
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.chairul.githubuser.db.DatabaseContract
import com.chairul.githubuser.db.DatabaseContract.UserColumns.Companion.CONTENT_URI
import com.chairul.githubuser.db.MappingHelper
import com.chairul.githubuser.db.UserHelper
import com.chairul.githubuser.entity.SearchUser
import com.chairul.githubuser.entity.User
import com.chairul.githubuser.entity.UserDTO
import com.chairul.githubuser.entity.UserURL
import com.chairul.githubuser.view.DetailActivity
import com.chairul.githubuser.view.FavoriteUserActivity
import com.chairul.githubuser.viewModel.MainViewModel
import com.chairul.githubuser.webservice.ClientBuilder


class MainActivity : AppCompatActivity() {

    private lateinit var adapter: UserAdapter
    var allUser = ArrayList<User>()
    private lateinit var mainViewModel: MainViewModel
    var listUser = ArrayList<User>()
    var listURL = ArrayList<UserURL>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.v("ADX", "MAIN ON CREATE")
        setContentView(R.layout.activity_main)
        try {
            supportActionBar?.title = "Consumer User"
            adapter = UserAdapter()
            adapter.notifyDataSetChanged()
            val context = applicationContext



            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = adapter
            mainViewModel = ViewModelProvider(
                this,
                ViewModelProvider.NewInstanceFactory()
            ).get(MainViewModel::class.java)



        } catch (e: Exception) {
            Log.v("ADX", "Error Main $e")
        }
    }

    override fun onResume() {
        Log.v("ADX", "OnResume")
        super.onResume()

    }

    fun threadCheckSQL() {
        Log.v("ADX","threadCheckSQL")
        val handlerThread = HandlerThread("DataObserver")
        handlerThread.start()
        val handler = Handler(handlerThread.looper)
        val myObserver = object : ContentObserver(handler) {
            override fun onChange(self: Boolean) {
                Log.v("ADX", "checkSQL")
                checkSQL()
            }
        }


        contentResolver.registerContentObserver(CONTENT_URI, true, myObserver)
    }


    fun checkSQL() {
        try {
            val cursor = contentResolver?.query(CONTENT_URI, null, null, null, null)
            listUser = MappingHelper.mapCursorToArrayList(cursor)
            adapter.notifyDataSetChanged()


            Log.v("ADX", "Check SQL List $listUser")

        } catch (e: Exception) {
            Log.v("ADX", "Error check sql $e")
        }
    }

    fun viewModelGetAllUser() {
        mainViewModel.getAllUser().observe(this, Observer { itemAllUser ->
            if (itemAllUser != null) {
                //adapter.setData(itemAllUser)
                //showLoading(false)
            }
        })

    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.option_menu, menu)

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu!!.findItem(R.id.search).actionView as SearchView

        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.queryHint = resources.getString(R.string.search_hint)

        searchView.addOnAttachStateChangeListener(object : OnAttachStateChangeListener {

            override fun onViewDetachedFromWindow(arg0: View) {
                Log.v("ADX", "Search View is Close")
                recyclerView.visibility = View.VISIBLE
                listUser.clear()
                //checkSQL()
                adapter.setData(listUser)

            }

            override fun onViewAttachedToWindow(arg0: View) {
            }
        })

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {

                mainViewModel.setUserSearch(query)

                mainViewModel.getUserSearch().observe(this@MainActivity, Observer {
                    if (it != null) {
                        showUserSearch(it)
                    }
                })

                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.v("ADX", "OnActivity Result")

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item!!.itemId) {
            R.id.favorite -> {
                goToFavorite()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun goToFavorite() {
        startActivityForResult(Intent(this, FavoriteUserActivity::class.java), -1)
    }

    private fun showUserSearch(it: SearchUser?) {
        if (it!!.total_count == 0) {
            recyclerView.visibility = View.GONE
            adapter.setData(allUser)
        } else {
            Log.v("ADX", "List User Search \n " + it)
            allUser.clear()
            allUser.addAll(it.items)
            adapter.setData(allUser)
            recyclerView.visibility = View.VISIBLE
        }

    }

    private fun showLoading(state: Boolean) {
        if (state) {
            progressBar.visibility = View.VISIBLE
        } else {
            progressBar.visibility = View.GONE
        }
    }


    private fun showUserList(list: ArrayList<User>?): Any {
        Log.v("ADX", "List User \n " + list)
        adapter.setData(list!!)
        allUser = list
        return list!!
    }


    inner class UserAdapter : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

        private val mData = ArrayList<User>()
        fun setData(items: ArrayList<User>) {
            mData.clear()
            mData.addAll(items)
            notifyDataSetChanged()
        }

        fun searchUser(urlUser: ArrayList<UserURL>) {
            listURL.clear()
            listURL.addAll(urlUser)
        }

        override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): UserViewHolder {
            val mView = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.item_list_user, viewGroup, false)
            return UserViewHolder(mView)
        }

        override fun getItemCount(): Int = mData.size

        override fun onBindViewHolder(userViewHolder: UserViewHolder, position: Int) {
            try {

                Log.v("ADX", "Position $position")
                if (mData[position].isFavorite == null) {
                    Log.v("ADX", "Data Search View " + mData[position].login)
                    checkSQL()
                    userViewHolder.reqUserData(mData[position].login)
                    Log.v("ADX", "Request Data Search View")
                } else userViewHolder.bind(mData[position])


                if (position % 2 == 0) userViewHolder.itemView.itemUser.setBackgroundColor(
                    resources.getColor(
                        R.color.blue1
                    )
                )
                else userViewHolder.itemView.itemUser.setBackgroundColor(
                    resources.getColor(
                        R.color.blue2
                    )
                )
                userViewHolder.itemView.btnAdd.setOnClickListener {
                    userViewHolder.itemView.btnAdd.visibility = View.INVISIBLE
                    userViewHolder.updateFavorite(position, mData[position])
                }
            } catch (e: Exception) {
                Log.v("ADX", "OnBindViewHoolder Main Error $e")
            }
        }


        private fun goToDetailUser(username: String, user: User) {
            val intent = Intent(this@MainActivity, DetailActivity::class.java)
            intent.putExtra(DetailActivity.DETAIL_USER, username)
            intent.putExtra(DetailActivity.IS_FAVORITE, user)
            startActivity(intent)
        }


        inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            fun bind(user: User?) {
                Log.v("ADX", "Data User \n" + user)
                if (user?.name != null) {
                    with(itemView) {
                        txtNama.text = user?.name
                        if (user?.company != null) txtCompany.text =
                            user?.company else txtCompany.text = "No Company"
                        txtUsername.text = user?.login + "@github.com"
                        Glide.with(context).load(user?.avatar_url).into(imgUser)

                        if(user?.isFavorite == "true")btnAdd.visibility = View.INVISIBLE else btnAdd.visibility = View.VISIBLE
                    }
                    itemView.setOnClickListener { goToDetailUser(user.login, user) }
                }
            }

            fun updateFavorite(index: Int, user: User?) {
                try {
                    Log.v("ADX", "ID User Update ${user?.id}")
                    if (user != null) {
                        var uriWithId = Uri.parse(CONTENT_URI.toString() + "/" + user?.id)
                        var updateFavorite = "true"
                        if (user?.isFavorite != "" && user?.isFavorite != "false") updateFavorite =
                            "false"
                        val values = ContentValues()
                        values.put(DatabaseContract.UserColumns.LOGIN, user?.login)
                        values.put(DatabaseContract.UserColumns.NAME, user?.name ?: "-")
                        values.put(
                            DatabaseContract.UserColumns.COMPANY,
                            user?.company ?: "No Company"
                        )
                        values.put(DatabaseContract.UserColumns.AVATAR_URL, user?.avatar_url ?: "-")
                        values.put(DatabaseContract.UserColumns.ISFAVORITE, updateFavorite)
                        contentResolver.update(uriWithId, values, null, null)
                        Toast.makeText(
                            applicationContext,
                            "Success add to favorite",
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                } catch (e: Exception) {
                    Log.v("ADX", "updateFavorite Error $e")
                }
            }

            fun reqUserData(user: String) {
                Log.v("ADX", "reqUserData")
                val name = user
                try {
                    ClientBuilder().services.getUserDetail(name).enqueue(object :
                        Callback<UserDTO> {
                        override fun onFailure(call: Call<UserDTO>, t: Throwable) {
                            Log.v("ADX", "FailGetUser : $t")
                        }

                        override fun onResponse(
                            call: Call<UserDTO>,
                            response: Response<UserDTO>
                        ) {
                            Log.v("ADX", "Respone Code API ${response.code()}")
                            if (response.code() == 200) {
                                response.body().let {
                                    parseUserDto(it)
                                }
                            }
                        }

                    })
                } catch (e: Exception) {
                    Log.v("ADX", "Error $e")
                }

            }

            private fun parseUserDto(user: UserDTO?) {
                if (user != null) {
                    val data = User(
                        id = user?.id,
                        login = user?.login,
                        name = user?.name,
                        avatar_url = user?.avatar_url,
                        company = user?.company,
                        isFavorite = "true"
                    )
                    bind(data)
                }
            }


        }

        private fun deleteUser(user: UserDTO?) {
            val uriWithId = Uri.parse(CONTENT_URI.toString() + "/" + user?.id)
            contentResolver.delete(uriWithId, null, null)
            Toast.makeText(this@MainActivity, "Satu item berhasil dihapus", Toast.LENGTH_SHORT)
                .show()
            finish()
        }


    }


}
