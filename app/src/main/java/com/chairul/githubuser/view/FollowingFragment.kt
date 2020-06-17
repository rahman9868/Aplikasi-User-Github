package com.chairul.githubuser.view


import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.chairul.githubuser.R
import com.chairul.githubuser.entity.UserDTO
import com.chairul.githubuser.entity.UserURL
import com.chairul.githubuser.viewModel.MainViewModel
import com.chairul.githubuser.webservice.ClientBuilder
import kotlinx.android.synthetic.main.fragment_following.*
import kotlinx.android.synthetic.main.item_list_user.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

/**
 * A simple [Fragment] subclass.
 */
class FollowingFragment : Fragment() {

    private lateinit var adapter: UserAdapter
    private lateinit var mainViewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_following, container, false)
    }

    @SuppressLint("FragmentLiveDataObserve")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val user  = DetailActivity.username
        adapter = UserAdapter()
        adapter.notifyDataSetChanged()
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        mainViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(
            MainViewModel::class.java)

        mainViewModel.setFollowingUser(user)

        mainViewModel.getFollowingUer().observe(this, Observer { itemUser ->
            if(itemUser != null){
                fetcUserData(itemUser)
            }

        })
    }


    private fun fetcUserData(list: ArrayList<UserURL>) {
        Log.v("ADX", "List User \n " + list)
        adapter.setData(list)
    }

    inner class UserAdapter : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

        private val mData = ArrayList<UserURL>()
        fun setData(items: ArrayList<UserURL>) {
            mData.clear()
            mData.addAll(items)
            notifyDataSetChanged()
        }


        override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): UserViewHolder {
            val mView = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_list_user, viewGroup, false)
            return UserViewHolder(mView)
        }

        override fun getItemCount(): Int = mData.size

        override fun onBindViewHolder(userViewHolder: UserViewHolder, position: Int) {
            if(position % 2 == 0)userViewHolder.itemView.itemUser.setBackgroundColor(resources.getColor(
                R.color.blue1
            ))
            else userViewHolder.itemView.itemUser.setBackgroundColor(resources.getColor(
                R.color.blue2
            ))
            userViewHolder.reqUserData(mData[position].url)
        }



        inner class UserViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
            fun bind(user: UserDTO?) {
                Log.v("ADX","Data User \n"+user)
                if(user?.name != null) {
                    with(itemView) {
                        txtNama.text = user?.name
                        if (user?.company != null) txtCompany.text =
                            user?.company else txtCompany.text = "No Company"
                        txtUsername.text = user?.login+"@github.com"
                        Glide.with(context).load(user?.avatar_url).into(imgUser)
                    }
                }
            }
            fun reqUserData(user: String){
                val name = user.split("/users/")[1]
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
                            if (response.code() == 200) {
                                response.body().let {
                                    bind(it)
                                }
                            }
                        }

                    })
                } catch (e: Exception) {
                    Log.v("ADX","Error $e")
                }

            }


        }

    }


}

