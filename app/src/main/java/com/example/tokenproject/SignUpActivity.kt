package com.example.tokenproject

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tokenproject.Common.Common
import com.example.tokenproject.Retrofit2.ResultModel
import com.example.tokenproject.Retrofit2.RetrofitUtil
import com.example.tokenproject.Retrofit2.UserApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpActivity : AppCompatActivity() {

    private var dialog: AlertDialog? = null

    private lateinit var mName : EditText
    private lateinit var mPhone : EditText
    private lateinit var mId : EditText
    private lateinit var mPw : EditText

    private lateinit var mJoinBtn : Button
    private lateinit var mBackBtn : Button

    var mUserApi : UserApi? = null
    private val validate = false

    var asyncDialog : ProgressDialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)


        asyncDialog = ProgressDialog(this@SignUpActivity)


        mName = findViewById(R.id.nameEdit)
        mPhone = findViewById(R.id.phoneEdit)
        mId = findViewById(R.id.userIdEdit)
        mPw = findViewById(R.id.userPwEdit)

        mJoinBtn = findViewById(R.id.joinBtn)
        mBackBtn = findViewById(R.id.backBtn)

        mUserApi = RetrofitUtil().userApi




        mJoinBtn.setOnClickListener {
            asyncDialog!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
            asyncDialog!!.setMessage("가입 신청중입니다...")

            asyncDialog!!.show()

            Handler().postDelayed(Runnable {
                Register()                //여기에 딜레이 후 시작할 작업들을 입력
            }, 2000) // 0.5초 정도 딜레이를 준 후 시작


        }

        mBackBtn.setOnClickListener {
            finish()
        }

    }


    //회원가입 신청
    fun Register() {
        val userNM = mName.text.toString()
        val userPhn: String = mPhone.text.toString()
        val userNN: String = mId.text.toString()
        val userPW: String = mPw.text.toString()
        val studID: String? = null //mStudentId.getText().toString();
        val department : String? = null
        val rgsDate: String = Common.nowDate("yyyy-MM-dd HH:mm:ss")
       /* if (!validate) {
            val builder: AlertDialog.Builder = AlertDialog.Builder(this@SignUpActivity)
            dialog = builder.setMessage("먼저 중복 체크를 해주세요.")
                .setNegativeButton("확인", null)
                .create()
            this.dialog.show()
            return
        }*/
        if (userNN == "" || userPW == "") {
            /*val builder: AlertDialog.Builder = AlertDialog.Builder(this@SignUpActivity)
            dialog = builder.setMessage("빈 칸 없이 입력해주세요.")
                .setNegativeButton("확인", null)
                .create()
            dialog.show()*/


            val alertDialog = AlertDialog.Builder(this)
                .setMessage("빈 칸 없이 입력해주세요.")
                .setNegativeButton("확인",null)
                .create()

            alertDialog.show()
            return
        }
        val call = mUserApi?.UserRegister(
            userNM,
            userPhn,
            userNN,
            userPW,
            studID,
            department,
            rgsDate
        )
        call?.enqueue(object : Callback<ResultModel> {
            override fun onResponse(
                call: Call<ResultModel>,
                response: Response<ResultModel>
            ) {
                //정상 결과
                if (response.body()?.getResult().equals("success")) {

                    val alertDialog = AlertDialog.Builder(this@SignUpActivity)
                        .setMessage("회원등록에 성공했습니다.")
                        .setPositiveButton("확인",
                            DialogInterface.OnClickListener { dialogInterface, i ->
                                Common.intentCommon(
                                    this@SignUpActivity,
                                    LoginActivity::class.java
                                )
                                finish()
                            }).create()
                    alertDialog.show()
                } else {

                    val alertDialog = AlertDialog.Builder(this@SignUpActivity)
                        .setMessage("회원등록에 실패했습니다.")
                        .setPositiveButton("확인",null)
                        .create()
                    alertDialog.show()

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
                    this@SignUpActivity,
                    "데이터 접속 상태를 확인 후 다시 시도해주세요.",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }
}