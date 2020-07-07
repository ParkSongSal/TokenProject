package com.example.tokenproject

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.tokenproject.Retrofit2.ResultModel
import com.example.tokenproject.Retrofit2.RetrofitUtil
import com.example.tokenproject.Retrofit2.UserApi
import com.example.tokenproject.Retrofit2.UserService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private val PERMISSION_ALLOW = 1


    private lateinit var id_edit: EditText
    private lateinit var password_edit: EditText

    private lateinit var mSignInBtn: Button
    private lateinit var mSignUpBtn: Button


    var chk_auto: CheckBox? = null

    //아이디 저장 기능
    var setting: SharedPreferences? = null
    var editor: SharedPreferences.Editor? = null

    var mUserApi : UserApi? = null

    var userId = ""
    var userPassword = ""

    var server : UserService? = null

    var asyncDialog : ProgressDialog? = null
    private var dialog: AlertDialog? = null

    //필수권한
    var REQUIRED_PERMISSIONS = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,  //저장소 접근
        Manifest.permission.READ_EXTERNAL_STORAGE
    )
    @SuppressLint("CommitPrefEdits")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) !== PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) !== PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,  // 저장공간
                    Manifest.permission.READ_EXTERNAL_STORAGE  // 저장공간
                ),
                PERMISSION_ALLOW
            )
        }

        asyncDialog = ProgressDialog(this@LoginActivity)

        id_edit = findViewById(R.id.id_edit)
        password_edit = findViewById(R.id.password_edit)


        mSignInBtn = findViewById<Button>(R.id.signInBtn)
        mSignUpBtn = findViewById<Button>(R.id.signUpBtn)

        chk_auto = findViewById(R.id.chk_auto)

        mUserApi = RetrofitUtil().userApi



        //로그인 버튼 클릭스

        mSignInBtn.setOnClickListener{

            asyncDialog!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
            asyncDialog!!.setMessage("잠시만 기다려주세요...")

            asyncDialog!!.show()

            Handler().postDelayed(Runnable {
                userId = id_edit.text.toString()
                userPassword = password_edit.text.toString()
                loginEvent(userId, userPassword)
                //여기에 딜레이 후 시작할 작업들을 입력
            }, 2000) // 0.5초 정도 딜레이를 준 후 시작
        }

        mSignUpBtn.setOnClickListener{
            Register()
        }


        setting = getSharedPreferences("setting", Activity.MODE_PRIVATE)
        editor = setting?.edit()


        chk_auto?.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if (chk_auto!!.isChecked()) {
                val ID: String = id_edit.text.toString()
                val PW: String = password_edit.text.toString()
                //Toast.makeText(Login2Activity.this, "ID PW 값 " + ID + PW, Toast.LENGTH_SHORT).show();
                if (ID == "" || PW == "") {
                    val builder: AlertDialog.Builder = AlertDialog.Builder(this@LoginActivity)
                    dialog = builder.setMessage("닉네임과 패스워드를 입력해주세요")
                        .setNegativeButton("다시 시도", null)
                        .create()
                    dialog?.show()
                    chk_auto?.setChecked(false)
                }
                editor!!.putString("ID", ID)
                editor!!.putString("PW", PW)
                editor!!.putBoolean("chk_auto", true)
                editor!!.apply()
            } else {
                setting = getSharedPreferences("setting", Activity.MODE_PRIVATE)
                editor = setting?.edit()
                editor?.clear()
                editor?.commit()
            }
        })


        // 아이디 저장 기능

        // 아이디 저장 기능
        if (setting!!.getBoolean("chk_auto", false)) {


            userId = setting?.getString("ID", "")!!
            userPassword = setting?.getString("PW", "")!!

            id_edit.setText(userId)
            password_edit.setText(userPassword) //패스워드

            loginEvent(userId, userPassword)

/*
            if (userId == "") {
                Toast.makeText(this, "NickName을 입력해주세요", Toast.LENGTH_SHORT).show()
                return
            } else if (userPassword == "") {
                Toast.makeText(this, "Password를 입력해주세요", Toast.LENGTH_SHORT).show()
                return
            }

            val call: Call<ResultModel> =
                mUserApi!!.User_login(userId, userPassword)


            call.enqueue(object : Callback<ResultModel> {
                override fun onResponse(
                    call: Call<ResultModel>,
                    response: Response<ResultModel>
                ) {
                    //정상 결과
                    if (response.body()?.result.equals("success")) {

                        var sharedPreferences: SharedPreferences =
                            getSharedPreferences("tokenApp", Context.MODE_PRIVATE)

                        var editor: SharedPreferences.Editor = sharedPreferences.edit()

                        editor.putString("userId", userId)
                        editor.apply()


                        //로그인 성공
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()

                        Toast.makeText(this@LoginActivity, "로그인 성공", Toast.LENGTH_SHORT).show()
                    } else {
                        //로그인 실패
                        Toast.makeText(this@LoginActivity, "로그인 실패", Toast.LENGTH_SHORT).show()
                    }
                    asyncDialog?.dismiss()
                }

                override fun onFailure(
                    call: Call<ResultModel>,
                    t: Throwable
                ) {
                    asyncDialog?.dismiss()
                    // 네트워크 문제
                    Toast.makeText(
                        this@LoginActivity,
                        "데이터 접속 상태를 확인 후 다시 시도해주세요.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })*/
            chk_auto?.isChecked = true
        }
    }

    fun Register() {
        intent = Intent(applicationContext, SignUpActivity::class.java)
        startActivity(intent)
    }
    fun loginEvent(Id:String, Password:String){

      
        /*userId = id_edit.text.toString()
        userPassword = password_edit.text.toString()*/

        if (Id == "") {
            Toast.makeText(this, "NickName을 입력해주세요", Toast.LENGTH_SHORT).show()
            return
        } else if (Password == "") {
            Toast.makeText(this, "Password를 입력해주세요", Toast.LENGTH_SHORT).show()
            return
        }

        /*  Retrofit - 코틀린으로 된 UserService 사용  
        var retrofit = Retrofit.Builder()
                   .baseUrl("http://tkdanr2427.cafe24.com/Study/")
                   .addConverterFactory(GsonConverterFactory.create())
                   .build()
       
               var call = retrofit.create(UserService::class.java)
       
        call.User_login(userId, userPassword).enqueue(object : Callback<ResponseDTO>{
            override fun onFailure(call: Call<ResponseDTO>, t: Throwable) {
                Toast.makeText(
                    this@LoginActivity,
                    "데이터 접속 w상태를 확인 후 다시 시도해주세요." + t.message,
                    Toast.LENGTH_LONG
                ).show()
            }

            override fun onResponse(call: Call<ResponseDTO>, response: Response<ResponseDTO>) {

                Log.d("TAG","onResponse : " + response.body().toString())
                //정상 결과
                if (response.body()?.result.equals("success")) {
                    //로그인 성공
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                    Toast.makeText(this@LoginActivity, "로그인 성공", Toast.LENGTH_SHORT).show()
                } else {
                    //로그인 실패
                    Toast.makeText(this@LoginActivity, "로그인 실패", Toast.LENGTH_SHORT).show()
                }            }

        })*/
        val call: Call<ResultModel> =
            mUserApi!!.User_login(Id, Password)


        call.enqueue(object : Callback<ResultModel> {
            override fun onResponse(
                call: Call<ResultModel>,
                response: Response<ResultModel>
            ) {
                //정상 결과
                if (response.body()?.result.equals("success")) {

                    var sharedPreferences : SharedPreferences = getSharedPreferences("tokenApp", Context.MODE_PRIVATE)

                    var editor : SharedPreferences.Editor = sharedPreferences.edit()

                    editor.putString("userId",Id)
                    editor.apply()


                    //로그인 성공
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()

                    Toast.makeText(this@LoginActivity, "로그인 성공", Toast.LENGTH_SHORT).show()
                } else {
                    //로그인 실패
                    Toast.makeText(this@LoginActivity, "로그인 실패", Toast.LENGTH_SHORT).show()
                }
                asyncDialog?.dismiss()
            }

            override fun onFailure(
                call: Call<ResultModel>,
                t: Throwable
            ) {
                asyncDialog?.dismiss()
                // 네트워크 문제
                Toast.makeText(
                    this@LoginActivity,
                    "데이터 접속 상태를 확인 후 다시 시도해주세요.",
                    Toast.LENGTH_LONG
                ).show()
            }


        })
    }
}