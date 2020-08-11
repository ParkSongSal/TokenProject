package com.example.tokenproject

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.example.tokenproject.Common.Common
import com.example.tokenproject.Common.FileUtils
import com.example.tokenproject.Retrofit2.BoardApi
import com.example.tokenproject.Retrofit2.RetrofitBoard
import com.github.siyamed.shapeimageview.RoundedImageView
import com.opensooq.supernova.gligar.GligarPicker
import kotlinx.android.synthetic.main.activity_board_insert.*
import kotlinx.android.synthetic.main.activity_board_update.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class BoardUpdateActivity : AppCompatActivity() {

    private val PICKER_REQUEST_CODE = 101
    private var mBoardApi: BoardApi = RetrofitBoard().imageApi

    var seq = 0
    var title =""
    var content =""
    var date =""
    var id = ""
    var writer =""
    var token =""
    var replyCount =""
    var count =0
    private var pathsList = emptyArray<String>()
    var pathList = ArrayList<String>()
    var originalPathList = ArrayList<String>()

    private var uriList2 : MutableList<Uri> = mutableListOf()

    private var dialog: AlertDialog? = null

    var call: Call<ResponseBody>? = null

    var setting //아이디 저장 기능
            : SharedPreferences? = null
    var editor: SharedPreferences.Editor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board_update)

        setting = getSharedPreferences("tokenApp", Activity.MODE_PRIVATE)
        editor = setting?.edit()

        id = setting?.getString("ID", "").toString() //로그인계정
        token = setting?.getString("token", "").toString()    //작성자 토큰

        if(intent != null){
            seq = intent.getIntExtra("Seq",0)
            title = intent.getStringExtra("TITLE")
            content = intent.getStringExtra("CONTENT")
            date = intent.getStringExtra("DATE")
            writer = intent.getStringExtra("WRITER")
            replyCount = intent.getStringExtra("ReplyCount")
            pathList = intent.getSerializableExtra("pathList") as ArrayList<String>
            originalPathList = intent.getSerializableExtra("originalPathList") as ArrayList<String>

            update_titleTxt.setText(title)
            update_contentTxt.setText(content)
            update_dateTxt.text = date
            update_userTxt.text = writer

            for(i in pathList.indices){
                Log.d("TAG", "image $i : "+pathList[i])
                uriList2.add(Uri.fromFile(File(originalPathList[i])))
                count++
                setImageUpdate(pathList[i])
            }
        }




        //이미지 불러오기기
        update_cameraIcon.setOnClickListener{
            if (pathList != null) {
                Toast.makeText(this@BoardUpdateActivity, "현재 이미지 개수 $count", Toast.LENGTH_SHORT).show()
                if (count == 4) {
                    Toast.makeText(this@BoardUpdateActivity, "이미지는 최대 4장까지 선택가능합니다.", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    GligarPicker()
                        .requestCode(PICKER_REQUEST_CODE)
                        .limit(4 - count) // 최대 이미지 수
                        .withActivity(this@BoardUpdateActivity) //Activity
                        //.withFragment -> Fragment
                        // .disableCamera(false) -> 카메라 캡처를 사용할지
                        // .cameraDirect(true) -> 바로 카메라를 실행할지
                        .show()
                }
            } else {
                GligarPicker()
                    .requestCode(PICKER_REQUEST_CODE)
                    .limit(4) // 최대 이미지 수
                    .withActivity(this@BoardUpdateActivity) //Activity
                    //.withFragment -> Fragment
                    // .disableCamera(false) -> 카메라 캡처를 사용할지
                    // .cameraDirect(true) -> 바로 카메라를 실행할지
                    .show()
            }
        }


        updateBtn.setOnClickListener {
            hasContent()
        }

    }


    private fun hasContent(): Boolean {
        val TITLE = update_titleTxt.text.toString()
        val CONTENT = update_contentTxt.text.toString()
        return if (TITLE == "" || CONTENT == "" || TITLE.isEmpty() || CONTENT.isEmpty()) {
            val builder: AlertDialog.Builder = AlertDialog.Builder(this@BoardUpdateActivity)
            dialog = builder.setMessage("빈 칸은 등록할 수 없습니다.")
                .setNegativeButton("확인", null)
                .create()
            dialog?.show()
            false
        } else {
            updateBoard(uriList2)
            true
        }
    }

    private fun updateBoard(filePath : MutableList<Uri>?){

        val Seq = seq
        val User = id
        val Title = update_titleTxt.text.toString()
        val Content = update_contentTxt.text.toString()
        val Date = Common.nowDate("yyyy-MM-dd HH:mm:ss")
        val TOKEN = token

        val seqPart = RequestBody.create(MultipartBody.FORM, Seq.toString())
        val userPart = RequestBody.create(MultipartBody.FORM, User)
        val titlePart = RequestBody.create(MultipartBody.FORM, Title)
        val contentPart = RequestBody.create(MultipartBody.FORM, Content)
        val datePart = RequestBody.create(MultipartBody.FORM, Date)
        val tokenPart = RequestBody.create(MultipartBody.FORM, TOKEN)

        Log.d("TAG","updateBoard imagePath Size : " + filePath?.size)
        Log.d("TAG", "updateBoard imagePath : $filePath")

        when (filePath?.size) {
            0 -> {
                call = mBoardApi.UpdateBoard_NoImage(seqPart,userPart, titlePart, contentPart, datePart, tokenPart)
            }
            1 -> {
                val originalPath = RequestBody.create(MultipartBody.FORM, filePath[0].toString())

                val originalFile: File = FileUtils.getFile(this@BoardUpdateActivity, filePath[0])
                val imagePart = RequestBody.create(MediaType.parse("multipart/form-data"),originalFile)
                val file = MultipartBody.Part.createFormData("image[]", originalFile.name, imagePart)

                call = mBoardApi.TestUpdateBoard(seqPart,userPart, titlePart, contentPart, datePart, file, null, null, null, originalPath, null, null, null)
            }
            2 -> {
                val originalPath = RequestBody.create(MultipartBody.FORM, filePath[0].toString())
                val originalPath2 = RequestBody.create(MultipartBody.FORM, filePath[1].toString())

                val originalFile: File = FileUtils.getFile(this@BoardUpdateActivity, filePath[0])


                val imagePart = RequestBody.create(MediaType.parse("multipart/form-data"),originalFile)
                val file = MultipartBody.Part.createFormData("image[]", originalFile.name, imagePart)

                val originalFile2: File = FileUtils.getFile(this@BoardUpdateActivity, filePath[1])
                val imagePart2 = RequestBody.create(MediaType.parse("multipart/form-data"),originalFile2)
                val file2 = MultipartBody.Part.createFormData("image[]", originalFile.name, imagePart2)

                call = mBoardApi.TestUpdateBoard(seqPart,userPart, titlePart, contentPart, datePart, file, file2, null, null, originalPath, originalPath2, null, null)
            }
            3 -> {
                val originalPath = RequestBody.create(MultipartBody.FORM, filePath[0].toString())
                val originalPath2 = RequestBody.create(MultipartBody.FORM, filePath[1].toString())
                val originalPath3 = RequestBody.create(MultipartBody.FORM, filePath[2].toString())

                val originalFile: File = FileUtils.getFile(this@BoardUpdateActivity, filePath[0])
                val imagePart = RequestBody.create(MediaType.parse("multipart/form-data"),originalFile)
                val file = MultipartBody.Part.createFormData("image[]", originalFile.name, imagePart)

                val originalFile2: File = FileUtils.getFile(this@BoardUpdateActivity, filePath[1])
                val imagePart2 = RequestBody.create(MediaType.parse("multipart/form-data"),originalFile2)
                val file2 = MultipartBody.Part.createFormData("image[]", originalFile.name, imagePart2)

                val originalFile3: File = FileUtils.getFile(this@BoardUpdateActivity, filePath[2])
                val imagePart3 = RequestBody.create(MediaType.parse("multipart/form-data"),originalFile3)
                val file3 = MultipartBody.Part.createFormData("image[]", originalFile.name, imagePart3)

                call = mBoardApi.TestUpdateBoard(seqPart,userPart, titlePart, contentPart, datePart, file, file2, file3, null, originalPath, originalPath2, originalPath3, null)
            }
            4 -> {

                val originalPath = RequestBody.create(MultipartBody.FORM, filePath[0].toString())
                val originalPath2 = RequestBody.create(MultipartBody.FORM, filePath[1].toString())
                val originalPath3 = RequestBody.create(MultipartBody.FORM, filePath[2].toString())
                val originalPath4 = RequestBody.create(MultipartBody.FORM, filePath[3].toString())

                val originalFile: File = FileUtils.getFile(this@BoardUpdateActivity, filePath[0])
                val imagePart = RequestBody.create(MediaType.parse("multipart/form-data"),originalFile)
                val file = MultipartBody.Part.createFormData("image[]", originalFile.name, imagePart)

                val originalFile2: File = FileUtils.getFile(this@BoardUpdateActivity, filePath[1])
                val imagePart2 = RequestBody.create(MediaType.parse("multipart/form-data"),originalFile2)
                val file2 = MultipartBody.Part.createFormData("image[]", originalFile.name, imagePart2)

                val originalFile3: File = FileUtils.getFile(this@BoardUpdateActivity, filePath[2])
                val imagePart3 = RequestBody.create(MediaType.parse("multipart/form-data"),originalFile3)
                val file3 = MultipartBody.Part.createFormData("image[]", originalFile.name, imagePart3)

                val originalFile4: File = FileUtils.getFile(this@BoardUpdateActivity, filePath[3])
                val imagePart4 = RequestBody.create(MediaType.parse("multipart/form-data"),originalFile4)
                val file4 = MultipartBody.Part.createFormData("image[]", originalFile.name, imagePart4)

                call = mBoardApi.TestUpdateBoard(seqPart,userPart, titlePart, contentPart, datePart, file, file2, file3, file4, originalPath, originalPath2, originalPath3, originalPath4)
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
                Toast.makeText(this@BoardUpdateActivity, "수정되었습니다.", Toast.LENGTH_LONG).show()
            }

            override fun onFailure(
                call: Call<ResponseBody?>,
                t: Throwable
            ) {
                Toast.makeText(
                    this@BoardUpdateActivity,
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
                pathList.add(pathsList[i])
                uriList2.add(Uri.fromFile(File(pathsList[i])))
                count++
                setImage(pathsList[i])
            }
            Log.d("TAG","update Img : $pathList")
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

                if (pathList.contains(imagePath)) {
                    pathList.remove(imagePath)
                    uriList2.remove(Uri.fromFile(File(imagePath)))
                    count--
                    update_imageLinear.removeView(statLayoutItem)
                    update_imageTxtCount.text = "$count/4"

                }
                Toast.makeText(
                    this@BoardUpdateActivity,
                    "ImageView $imagePath",
                    Toast.LENGTH_SHORT
                ).show()
            }
            Glide.with(applicationContext)
                .load(myBitmap)
                .override(300, 300)
                .fitCenter()
                .into(addImg)
            update_imageLinear.addView(statLayoutItem)
            update_imageTxtCount.text = "$count/4"
        }
    }

    fun setImageUpdate(imagePath: String) {
        val inflater2 =
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val statLayoutItem2 =
            inflater2.inflate(R.layout.addimage, null) as LinearLayout
        val addImg: RoundedImageView = statLayoutItem2.findViewById(R.id.addImage)
        val delImg =
            statLayoutItem2.findViewById<ImageView>(R.id.delImage)
        delImg.setOnClickListener {

            if (pathList.contains(imagePath)) {
                pathList.remove(imagePath)
                uriList2.remove(Uri.fromFile(File(imagePath)))
                count--
                update_imageLinear.removeView(statLayoutItem2)
                update_imageTxtCount.text = "$count/4"

            }
            Toast.makeText(
                this@BoardUpdateActivity,
                "ImageView $imagePath",
                Toast.LENGTH_SHORT
            ).show()
        }
        Glide.with(applicationContext)
            .load(imagePath)
            .override(300, 300)
            .fitCenter()
            .into(addImg)
        update_imageLinear.addView(statLayoutItem2)
        update_imageTxtCount.text = "$count/4"
    }


}


