package com.chairul.githubuser.view

import android.content.ContentValues
import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.chairul.githubuser.R
import com.chairul.githubuser.db.DatabaseContract
import com.chairul.githubuser.db.MappingHelper
import com.chairul.githubuser.db.UserHelper
import com.chairul.githubuser.entity.User
import com.chairul.githubuser.entity.UserDTO
import com.chairul.githubuser.viewModel.MainViewModel
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {

    var countFollower = 0
    var countFollowing = 0
    private lateinit var mainViewModel: MainViewModel
    var listUser = ArrayList<User>()

    var isUserLocal = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        view_pager.adapter = sectionsPagerAdapter
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.elevation = 0f
        supportActionBar?.title = "Detail User"
        threadCheckSQL()
        username = intent.getStringExtra(DETAIL_USER)
        val cursor = contentResolver?.query(DatabaseContract.UserColumns.CONTENT_URI, null, null, null, null)
        listUser = MappingHelper.mapCursorToArrayList(cursor)
        Log.v("ADX","Main List User $listUser")

        if(listUser.find { it.login == username } != null){
            isUserLocal = true
        }

        tabs.setupWithViewPager(view_pager)
        mainViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(
            MainViewModel::class.java)
        showLoading(true)
        mainViewModel.setDetailUser(username)


        mainViewModel.getDetailUser().observe(this, Observer { itemUser ->
            if(itemUser != null){
                fetcUserData(itemUser)
                showLoading(false)
            }

        })




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


        contentResolver.registerContentObserver(DatabaseContract.UserColumns.CONTENT_URI, true, myObserver)
    }

    fun checkSQL() {
        try {
            val cursor = contentResolver?.query(DatabaseContract.UserColumns.CONTENT_URI, null, null, null, null)
            listUser = MappingHelper.mapCursorToArrayList(cursor)


            Log.v("ADX", "Check SQL List $listUser")

        } catch (e: Exception) {
            Log.v("ADX", "Error check sql $e")
        }
    }


    private fun showLoading(state: Boolean) {
        if (state) {
            progressBar.visibility = View.VISIBLE
        } else {
            progressBar.visibility = View.GONE
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }


    private fun fetcUserData(it: UserDTO?) {
        try {
            countFollower = it?.followers ?: 0
            countFollowing = it?.following ?: 0
            Glide.with(applicationContext).load(it?.avatar_url).into(detailImg)
            detailUser.text = it?.login+"@github.com"
            detailName.text = it?.name
            detailEmail.text = it?.email ?: "no email"
            detailLocation.text = it?.location ?: "no location"
            detailOrg.text = it?.organization ?: "no organization"
            Log.v("ADX","Is User Local ? $isUserLocal")
            if(isUserLocal){
                val userLocal = listUser.find { it.login == it.login }
                if(userLocal?.isFavorite == "false")addUserFavorite.setImageResource(R.drawable.ic_plus)
                if(userLocal?.isFavorite == "true")addUserFavorite.setImageResource(R.drawable.ic_star)
            }

            addUserFavorite.visibility = View.VISIBLE
            addUserFavorite.setOnClickListener {view ->
                updateFavorite(it!!)
            }
        } catch (e: Exception){
            Log.v("ADX","FetchUser Error $e")
        }
    }

    private fun chekUserIsFavorite(user: User?) {
        if(user?.isFavorite == "false") addUserFavorite.setImageResource(R.drawable.ic_plus)
        if(user?.isFavorite == "true") addUserFavorite.setImageResource(R.drawable.ic_star)
        addUserFavorite.visibility = View.VISIBLE



    }

    private fun updateFavorite(user: UserDTO) {
        try {

            if(!isUserLocal){
                val values = ContentValues()
                values.put(DatabaseContract.UserColumns.LOGIN, user?.login)
                values.put(DatabaseContract.UserColumns.NAME, user?.name ?: "-")
                values.put(
                    DatabaseContract.UserColumns.COMPANY,
                    user?.company ?: "No Company"
                )
                values.put(DatabaseContract.UserColumns.AVATAR_URL, user?.avatar_url ?: "-")
                values.put(DatabaseContract.UserColumns.ISFAVORITE, "true")
                contentResolver.insert(DatabaseContract.UserColumns.CONTENT_URI, values)
                addUserFavorite.setImageResource(R.drawable.ic_star)
            }else{
                val userLocal = listUser.find { it.login == user.login }
                var updateFavorite = "false"
                if (userLocal?.isFavorite == "false") {
                    updateFavorite = "true"
                    addUserFavorite.setImageResource(R.drawable.ic_star)
                } else {
                    addUserFavorite.setImageResource(R.drawable.ic_plus)
                }
                var uriWithId =  Uri.parse(DatabaseContract.UserColumns.CONTENT_URI.toString() + "/" + userLocal?.id)
                val values = ContentValues()
                values.put(DatabaseContract.UserColumns.LOGIN, userLocal?.login)
                values.put(DatabaseContract.UserColumns.NAME, userLocal?.name ?: "-")
                values.put(
                    DatabaseContract.UserColumns.COMPANY,
                    userLocal?.company ?: "No Company"
                )
                values.put(DatabaseContract.UserColumns.AVATAR_URL, userLocal?.avatar_url ?: "-")
                values.put(DatabaseContract.UserColumns.ISFAVORITE, updateFavorite)
                contentResolver.update(uriWithId, values, null, null)
            }


            Toast.makeText(
                applicationContext,
                "Success Update Favorite",
                Toast.LENGTH_SHORT
            ).show()
        }catch (e: Exception){
            Log.v("ADX","Update Favorite Gagal $e")
        }

    }


    inner class SectionsPagerAdapter(private val mContext: Context, fm: FragmentManager) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        private val TAB_TITLES = intArrayOf(
            R.string.tab_text_1,
            R.string.tab_text_2
        )

        override fun getItem(position: Int): Fragment {
            var fragment: Fragment? = null
            when (position) {
                0 -> fragment = FollowerFragment()
                1 -> fragment = FollowingFragment()
            }
            return fragment as Fragment
        }

        override fun getCount(): Int {
            return 2
        }

        @Nullable
        override fun getPageTitle(position: Int): CharSequence? {
            if(position == 0 && countFollower > 0)return "Follower ($countFollower)"
            else if(position == 1 && countFollowing > 0)return "Follower ($countFollowing)"
            else return mContext.resources.getString(TAB_TITLES[position])
        }

    }

    companion object{
        var DETAIL_USER = "DETAIL USER"
        var IS_FAVORITE = "FAVORITE USER"
        var username = ""
    }
}
