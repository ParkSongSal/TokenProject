package com.example.tokenproject

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.annotation.Nullable
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.denzcoskun.imageslider.models.SlideModel
import com.example.tokenproject.Common.Common
import com.example.tokenproject.Common.FileUtils
import com.example.tokenproject.Model.BoardReplyModel
import com.example.tokenproject.Retrofit2.BoardApi
import com.example.tokenproject.Retrofit2.RetrofitBoard
import com.github.siyamed.shapeimageview.RoundedImageView
import com.opensooq.supernova.gligar.GligarPicker
import com.psmstudio.user.mygwangju.new_Board.BoardReplyAdapter
import com.stone.vega.library.VegaLayoutManager
import kotlinx.android.synthetic.main.activity_board_insert.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException

class BoardInsertActivity : AppCompatActivity() {


    private val PICKER_REQUEST_CODE = 101
    private var mBoardApi: BoardApi = RetrofitBoard().imageApi

    private var uriList : MutableList<String> = mutableListOf()
    private var uriList2 : MutableList<Uri> = mutableListOf()
    private var pathsList = emptyArray<String>()
    private var dialog: AlertDialog? = null

    var setting //아이디 저장 기능
            : SharedPreferences? = null
    var editor: SharedPreferences.Editor? = null

    private var id = ""
    private var token =""
    var call: Call<ResponseBody>? = null
    var count = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board_insert)

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        setting = getSharedPreferences("tokenApp", Activity.MODE_PRIVATE)
        editor = setting?.edit()

        id = setting?.getString("ID", "")!! //로그인계정
        token = setting?.getString("token","")!!    //작성자 토큰

        //이미지 불러오기기
       cameraIcon.setOnClickListener{
            if (pathsList != null) {
                Toast.makeText(this@BoardInsertActivity, "현재 이미지 개수 $count", Toast.LENGTH_SHORT).show()
                if (count == 4) {
                    Toast.makeText(this@BoardInsertActivity, "이미지는 최대 4장까지 선택가능합니다.", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    GligarPicker()
                        .requestCode(PICKER_REQUEST_CODE)
                        .limit(4 - count) // 최대 이미지 수
                        .withActivity(this@BoardInsertActivity) //Activity
                        //.withFragment -> Fragment
                        // .disableCamera(false) -> 카메라 캡처를 사용할지
                        // .cameraDirect(true) -> 바로 카메라를 실행할지
                        .show()
                }
            } else {
                GligarPicker()
                    .requestCode(PICKER_REQUEST_CODE)
                    .limit(4) // 최대 이미지 수
                    .withActivity(this@BoardInsertActivity) //Activity
                    //.withFragment -> Fragment
                    // .disableCamera(false) -> 카메라 캡처를 사용할지
                    // .cameraDirect(true) -> 바로 카메라를 실행할지
                    .show()
            }
        }

        // 게시물 등록
        insertBtn.setOnClickListener {
                hasContent()
        }

    }

    private fun hasContent(): Boolean {
        val TITLE = titleTxt.text.toString()
        val CONTENT = contentTxt.text.toString()
        return if (TITLE == "" || CONTENT == "" || TITLE.isEmpty() || CONTENT.isEmpty()) {
            val builder: AlertDialog.Builder = AlertDialog.Builder(this@BoardInsertActivity)
            dialog = builder.setMessage("빈 칸은 등록할 수 없습니다.")
                .setNegativeButton("확인", null)
                .create()
            dialog?.show()
            false
        } else {
            uploadImage(uriList2)
            true
        }
    }


    //Insert
    private fun uploadImage(filePath: MutableList<Uri>?) {

        //String Image = imageToString();
        val USER = id
        val TITLE = titleTxt.text.toString()
        val CONTENT = contentTxt.text.toString()
        val DATE: String = Common.nowDate("yyyy-MM-dd HH:mm:ss")
        val TOKEN : String = token
        val userPart = RequestBody.create(MultipartBody.FORM, USER)
        val titlePart = RequestBody.create(MultipartBody.FORM, TITLE)
        val contentPart = RequestBody.create(MultipartBody.FORM, CONTENT)
        val datePart = RequestBody.create(MultipartBody.FORM, DATE)
        val tokenPart = RequestBody.create(MultipartBody.FORM, TOKEN)


        /*intent = Intent(this@BoardInsertActivity, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                startActivity(intent)*/
        when (filePath?.size) {
            0 -> {
                call = mBoardApi.InsertBoard_NoImage(userPart, titlePart, contentPart, datePart, tokenPart)
            }
            1 -> {
                val originalPath = RequestBody.create(MultipartBody.FORM, filePath[0].toString())

                val originalFile: File = FileUtils.getFile(this@BoardInsertActivity, filePath[0])
                val imagePart = RequestBody.create(MediaType.parse("multipart/form-data"),originalFile)
                val file = MultipartBody.Part.createFormData("image[]", originalFile.name, imagePart)

                call = mBoardApi.TestInsertBoard(userPart, titlePart, contentPart, datePart, file, null, null, null,originalPath, null, null, null,  tokenPart)
            }
            2 -> {

                val originalPath = RequestBody.create(MultipartBody.FORM, filePath[0].toString())
                val originalPath2 = RequestBody.create(MultipartBody.FORM, filePath[1].toString())

                val originalFile: File = FileUtils.getFile(this@BoardInsertActivity, filePath[0])


                val imagePart = RequestBody.create(MediaType.parse("multipart/form-data"),originalFile)
                val file = MultipartBody.Part.createFormData("image[]", originalFile.name, imagePart)

                val originalFile2: File = FileUtils.getFile(this@BoardInsertActivity, filePath[1])
                val imagePart2 = RequestBody.create(MediaType.parse("multipart/form-data"),originalFile2)
                val file2 = MultipartBody.Part.createFormData("image[]", originalFile.name, imagePart2)

                call = mBoardApi.TestInsertBoard(userPart, titlePart, contentPart, datePart, file, file2, null, null,originalPath, originalPath2, null, null, tokenPart)
            }
            3 -> {
                val originalPath = RequestBody.create(MultipartBody.FORM, filePath[0].toString())
                val originalPath2 = RequestBody.create(MultipartBody.FORM, filePath[1].toString())
                val originalPath3 = RequestBody.create(MultipartBody.FORM, filePath[2].toString())

                val originalFile: File = FileUtils.getFile(this@BoardInsertActivity, filePath[0])
                val imagePart = RequestBody.create(MediaType.parse("multipart/form-data"),originalFile)
                val file = MultipartBody.Part.createFormData("image[]", originalFile.name, imagePart)

                val originalFile2: File = FileUtils.getFile(this@BoardInsertActivity, filePath[1])
                val imagePart2 = RequestBody.create(MediaType.parse("multipart/form-data"),originalFile2)
                val file2 = MultipartBody.Part.createFormData("image[]", originalFile.name, imagePart2)

                val originalFile3: File = FileUtils.getFile(this@BoardInsertActivity, filePath[2])
                val imagePart3 = RequestBody.create(MediaType.parse("multipart/form-data"),originalFile3)
                val file3 = MultipartBody.Part.createFormData("image[]", originalFile.name, imagePart3)

                call = mBoardApi.TestInsertBoard(userPart, titlePart, contentPart, datePart, file, file2, file3, null, originalPath, originalPath2, originalPath3, null,  tokenPart)
            }
            4 -> {
                val originalPath = RequestBody.create(MultipartBody.FORM, filePath[0].toString())
                val originalPath2 = RequestBody.create(MultipartBody.FORM, filePath[1].toString())
                val originalPath3 = RequestBody.create(MultipartBody.FORM, filePath[2].toString())
                val originalPath4 = RequestBody.create(MultipartBody.FORM, filePath[3].toString())


                val originalFile: File = FileUtils.getFile(this@BoardInsertActivity, filePath[0])
                val imagePart = RequestBody.create(MediaType.parse("multipart/form-data"),originalFile)
                val file = MultipartBody.Part.createFormData("image[]", originalFile.name, imagePart)

                val originalFile2: File = FileUtils.getFile(this@BoardInsertActivity, filePath[1])
                val imagePart2 = RequestBody.create(MediaType.parse("multipart/form-data"),originalFile2)
                val file2 = MultipartBody.Part.createFormData("image[]", originalFile.name, imagePart2)

                val originalFile3: File = FileUtils.getFile(this@BoardInsertActivity, filePath[2])
                val imagePart3 = RequestBody.create(MediaType.parse("multipart/form-data"),originalFile3)
                val file3 = MultipartBody.Part.createFormData("image[]", originalFile.name, imagePart3)

                val originalFile4: File = FileUtils.getFile(this@BoardInsertActivity, filePath[3])
                val imagePart4 = RequestBody.create(MediaType.parse("multipart/form-data"),originalFile4)
                val file4 = MultipartBody.Part.createFormData("image[]", originalFile.name, imagePart4)

                call = mBoardApi.TestInsertBoard(userPart, titlePart, contentPart, datePart, file, file2, file3, file4, originalPath, originalPath2, originalPath3, originalPath4, tokenPart)
            }
        }


        call?.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(
                call: Call<ResponseBody?>,
                response: Response<ResponseBody?>
            ) {
                editor?.putInt("fragInsert",0)
                editor?.apply()
                finish()
                Toast.makeText(this@BoardInsertActivity, "등록되었습니다.", Toast.LENGTH_LONG).show()
            }

            override fun onFailure(
                call: Call<ResponseBody?>,
                t: Throwable
            ) {
                Toast.makeText(
                    this@BoardInsertActivity,
                    "데이터 접속 상태를 확인 후 다시 시도해주세요.",
                    Toast.LENGTH_LONG
                ).show()
                Log.d("TAG", "imageUpload Faild.." + t.message)
            }
        })
    }






    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        @Nullable data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PICKER_REQUEST_CODE && resultCode == RESULT_OK && data != null){
            pathsList =
                data.extras?.getStringArray(GligarPicker.IMAGES_RESULT) as Array<String> // return list of selected images paths.
            for (i in pathsList.indices) {
                val uri : Uri = Uri.parse(pathsList[i])
                uriList.add(pathsList[i])
                uriList2.add(Uri.fromFile(File(pathsList[i])))
                Log.d("TAG", "uriList2 : $uriList2")
                count++
                setImage(pathsList[i])
            }
        }
    }

    // 파일 경로를 받아와 Bitmap 으로 변환후 ImageView 적용
    fun setImage(imagePath: String) {
        val imgFile = File(imagePath)
        if (imgFile.exists()) {
            val myBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
            val inflater =
                getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val statLayoutItem =
                inflater.inflate(R.layout.addimage, null) as LinearLayout
            val addImg: RoundedImageView = statLayoutItem.findViewById(R.id.addImage)
            val delImg =
                statLayoutItem.findViewById<ImageView>(R.id.delImage)
            delImg.setOnClickListener {

                if (uriList.contains(imagePath)) {
                    uriList.remove(imagePath)
                    count--
                    imageLinear.removeView(statLayoutItem)
                    imageTxtCount.text = "$count/4"

                }
                Toast.makeText(
                    this@BoardInsertActivity,
                    "ImageView $imagePath",
                    Toast.LENGTH_SHORT
                ).show()
            }
            Glide.with(applicationContext)
                .load(myBitmap)
                .override(300, 300)
                .fitCenter()
                .into(addImg)
            imageLinear.addView(statLayoutItem)
            imageTxtCount.text = "$count/4"
        }
    }
}