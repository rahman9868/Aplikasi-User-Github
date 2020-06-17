package com.chairul.githubuser.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.chairul.githubuser.webservice.ClientBuilder
import com.chairul.githubuser.entity.SearchUser
import com.chairul.githubuser.entity.UserDTO
import com.chairul.githubuser.entity.UserURL
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel : ViewModel() {

    val listAllUser = MutableLiveData<ArrayList<UserURL>>()
    val dataSearchUser = MutableLiveData<SearchUser>()
    val detailUser = MutableLiveData<UserDTO>()
    val listFollowingUser = MutableLiveData<ArrayList<UserURL>>()
    val listFollowerUser = MutableLiveData<ArrayList<UserURL>>()


    fun setAllUser() {
        try {
            ClientBuilder().services.getAllUsers().enqueue(object :
                Callback<ArrayList<UserURL>> {
                override fun onFailure(call: Call<ArrayList<UserURL>>, t: Throwable) {
                    Log.v("ADX", "FailGetUser : $t")
                }

                override fun onResponse(
                    call: Call<ArrayList<UserURL>>,
                    response: Response<ArrayList<UserURL>>
                ) {
                    if (response.code() == 200) {
                        response.body().let {
                            listAllUser.postValue(it)
                        }
                    }
                }

            })
        } catch (e: Exception) {
            Log.v("ADX", "Error $e")
        }
    }

    fun getAllUser(): LiveData<ArrayList<UserURL>> {
        return listAllUser
    }

    fun setUserSearch(username: String) {
        try {


            ClientBuilder().services.getSearchUser(username).enqueue(object :
                Callback<SearchUser> {
                override fun onFailure(call: Call<SearchUser>, t: Throwable) {
                    Log.v("ADX", "FailGetUser : $t")
                }

                override fun onResponse(
                    call: Call<SearchUser>,
                    response: Response<SearchUser>
                ) {
                    if (response.code() == 200) {
                        response.body().let {
                            dataSearchUser.postValue(it)

                        }
                    }
                }

            })
        } catch (e: Exception) {
            Log.v("ADX", "Error $e")
        }
    }

    fun getUserSearch(): LiveData<SearchUser>{
        return  dataSearchUser
    }

    fun setDetailUser(user: String){
        try {
            ClientBuilder().services.getUserDetail(user).enqueue(object :
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
                            detailUser.postValue(it)
                        }
                    }
                }

            })
        } catch (e: Exception) {
            Log.v("ADX","Error $e")
        }
    }

    fun getDetailUser() : LiveData<UserDTO>{
        return detailUser
    }

    fun setFollowingUser(user : String){
        try{
            ClientBuilder().services.getFollowing(user).enqueue(object :
                Callback<ArrayList<UserURL>> {
                override fun onFailure(call: Call<ArrayList<UserURL>>, t: Throwable) {
                    Log.v("ADX", "FailGetUser : $t")
                }

                override fun onResponse(
                    call: Call<ArrayList<UserURL>>,
                    response: Response<ArrayList<UserURL>>
                ) {
                    if (response.code() == 200) {
                        response.body().let {
                            listFollowingUser.postValue(it)
                        }
                    }
                }

            })
        }catch (e: java.lang.Exception){

        }
    }

    fun getFollowingUer() : LiveData<ArrayList<UserURL>>{
        return listFollowingUser
    }

    fun setFollowerUser(user: String){
        try{
            ClientBuilder().services.getFollower(user).enqueue(object :
                Callback<ArrayList<UserURL>> {
                override fun onFailure(call: Call<ArrayList<UserURL>>, t: Throwable) {
                    Log.v("ADX", "FailGetUser : $t")
                }

                override fun onResponse(
                    call: Call<ArrayList<UserURL>>,
                    response: Response<ArrayList<UserURL>>
                ) {
                    if (response.code() == 200) {
                        response.body().let {
                            listFollowerUser.postValue(it)
                        }
                    }
                }

            })
        }catch (e: Exception){

        }
    }

    fun getFollowerUser(): LiveData<ArrayList<UserURL>>{
        return listFollowerUser
    }
}