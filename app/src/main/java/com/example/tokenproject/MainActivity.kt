package com.example.tokenproject

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tokenproject.Fragment.FirstFragment
import com.example.tokenproject.Fragment.SecondFragment
import com.example.tokenproject.Fragment.ThirdFragment
import com.example.tokenproject.Retrofit2.ResultModel
import com.example.tokenproject.Retrofit2.RetrofitUtil
import com.example.tokenproject.Retrofit2.UserApi
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId
import nl.joery.animatedbottombar.AnimatedBottomBar
import nl.joery.animatedbottombar.AnimatedBottomBar.OnTabSelectListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {


    var mUserApi : UserApi? = null


    private val FRAGMENT1 = 1
    private val FRAGMENT2 = 2
    private val FRAGMENT3 = 3


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mUserApi = RetrofitUtil().userApi



        val mBottom_navigation =
            findViewById<AnimatedBottomBar>(R.id.bottomNavigationView)

        mBottom_navigation.setOnTabSelectListener(object : OnTabSelectListener {
            override fun onTabSelected(
                i: Int,
                tab: AnimatedBottomBar.Tab?,
                i1: Int,
                tab1: AnimatedBottomBar.Tab
            ) {
                when (tab1.id) {
                    R.id.action_people -> callFragment(FRAGMENT1)
                    R.id.action_chat -> callFragment(FRAGMENT2)
                    R.id.action_account -> callFragment(FRAGMENT3)
                }
            }

            override fun onTabReselected(
                i: Int,
                tab: AnimatedBottomBar.Tab
            ) {
            }
        })
        callFragment(FRAGMENT1)

        passPushTokenToServer()
    }

    // 선택한 Fragment 호출
    private fun callFragment(frament_no: Int) {

        // 프래그먼트 사용을 위해
        val transaction =
            supportFragmentManager.beginTransaction()
        when (frament_no) {
            1 -> {
                // 'Home' 호출
                val fragment1 = FirstFragment()
                transaction.replace(R.id.mainFrame, fragment1)
                transaction.commit()
            }
            2 -> {
                // '1등판매점' 호출
                val fragment2 = SecondFragment()
                transaction.replace(R.id.mainFrame, fragment2)
                transaction.commit()
            }
            3 -> {
                // '1등판매점' 호출
                val fragment3 = ThirdFragment()
                transaction.replace(R.id.mainFrame, fragment3)
                transaction.commit()
            }
        }
    }


    fun passPushTokenToServer() {


        var sharedPreferences : SharedPreferences = getSharedPreferences("tokenApp", Context.MODE_PRIVATE)

        var tokenEditor : SharedPreferences.Editor = sharedPreferences.edit()



        var userId = sharedPreferences.getString("userId","")

        FirebaseDatabase.getInstance().reference


        val token: String? = FirebaseInstanceId.getInstance().token

        tokenEditor.putString("token",token)
        tokenEditor.apply()




        val call: Call<ResultModel> =
            mUserApi!!.UserTokenUpdate(userId, token)

        call.enqueue(object : Callback<ResultModel> {
            override fun onResponse(
                call: Call<ResultModel>,
                response: Response<ResultModel>
            ) {
                Log.d("TAG", "userTOken : $token")

            }

            override fun onFailure(
                call: Call<ResultModel>,
                t: Throwable
            ) {
                // 네트워크 문제
                Toast.makeText(
                    this@MainActivity,
                    "데이터 접속 상태를 확인 후 다시 시도해주세요.",
                    Toast.LENGTH_LONG
                ).show()
            }


        })


    }
}