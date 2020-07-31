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
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tokenproject.Common.Common
import com.example.tokenproject.Common.Common.formatTimeString
import com.example.tokenproject.Common.FileUtils
import com.example.tokenproject.Model.BoardReplyModel
import com.example.tokenproject.Retrofit2.BoardApi
import com.example.tokenproject.Retrofit2.ResultBoard_Reply
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


    private val IMG_REQUEST = 777
    private val PICKER_REQUEST_CODE = 101

    private var bitmap: Bitmap? = null
    private var testPath : String? = null
    private var path: Uri? = null
    private var mImg: RoundedImageView? = null
    private var mInsert_img: TextView? = null
    private var mInsertBtn: Button? = null
    private var ReplyBtn:android.widget.Button? = null

    private var mBoardApi: BoardApi? = null

    private var dateTxt: TextView? = null
    private  var userTxt:TextView? = null
    private  var mReply_OnOff:TextView? = null

    private var titleTxt: EditText? = null
    private  var contentTxt:EditText? = null
    private  var ReplyTxt:EditText? = null

    private var gbdata = 0
    private var mReply_List: RecyclerView? = null
    private var mScroll_view: ScrollView? = null
    private var mReplyll: LinearLayout? = null
    private  var mSubtitle_ll:LinearLayout? = null

    private val replyList: MutableList<BoardReplyModel>? = null
    private var uriList : MutableList<String> = mutableListOf()
    private var pathsList = emptyArray<String>()
    private var mAdapter: BoardReplyAdapter? = null
    private var dialog: AlertDialog? = null

    var setting //아이디 저장 기능
            : SharedPreferences? = null
    var editor: SharedPreferences.Editor? = null

    private var BoardSeq = 0
    private var id = ""
    private var token =""
    var replyOnOff = 0

    private var mIntent : Intent? = null


    var count = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board_insert)

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        setting = getSharedPreferences("tokenApp", Activity.MODE_PRIVATE)
        editor = setting?.edit()



        id = setting?.getString("ID", "")!! //로그인계정
        token = setting?.getString("token","")!!    //작성자 토큰


        init()

        val manager = LinearLayoutManager(this)
        val decoration = DividerItemDecoration(this, manager.orientation)
        mReply_List!!.addItemDecoration(decoration)
        mReply_List!!.layoutManager = VegaLayoutManager()

        userTxt!!.text = id

        //replyList = ArrayList<getBoardReply>()

        //mAdapter = Board_ReplyAdapter(applicationContext, replyList)
        //mReply_List!!.adapter = mAdapter

        mInsert_img!!.setOnClickListener { selectImage() }

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


        if (intent != null) {
            //Insert
            if (intent.getStringExtra("TITLE") == null || intent.getIntExtra("Seq", 0) == 0) {
                gbdata = 0
                mInsertBtn!!.visibility = View.VISIBLE
                mReply_List!!.visibility = View.GONE
                mReplyll!!.visibility = View.GONE
                mSubtitle_ll!!.visibility = View.GONE
                userTxt!!.visibility = View.GONE
                dateTxt!!.visibility = View.GONE
                mReply_OnOff!!.visibility = View.GONE
            } else {    //Update

                //getData()

                BoardSeq = intent.getIntExtra("Seq", 1) //글번호
                Log.d("TAG", "글번호 ===> $BoardSeq")
                val image_path = intent.getStringExtra("PATH")
                if (image_path != null) {
                    Glide.with(applicationContext)
                        .load(image_path) //.override(500,200)
                        .into(mImg!!)
                    mImg!!.visibility = View.VISIBLE
                } else {
                    mImg!!.visibility = View.GONE
                }
                val writer = intent.getStringExtra("WRITER") //작성자
                if (id == writer) {          // 로그인계정과 작성자가 같으면
                    titleTxt!!.isEnabled = true // 제목, 내용 ReadOnly false
                    contentTxt!!.isEnabled = true
                    mInsert_img!!.visibility = View.VISIBLE //이미지추가 버튼 보이게
                    mInsertBtn!!.visibility = View.VISIBLE // 글쓰기 버튼 보이게
                } else {
                    titleTxt!!.isEnabled = false // 제목, 내용 ReadOnly true
                    contentTxt!!.isEnabled = false
                    mInsert_img!!.visibility = View.GONE //이미지추가 버튼 안보이게
                    mInsertBtn!!.visibility = View.GONE // 글쓰기 버튼 안보이게
                }
                titleTxt!!.setText(intent.getStringExtra("TITLE")) //제목
                contentTxt!!.setText(intent.getStringExtra("CONTENT")) //내용
                userTxt!!.text = writer //작성자
                dateTxt!!.text = intent.getStringExtra("DATE").substring(0, 16) //날짜
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    titleTxt!!.setHintTextColor(getColor(R.color.colorBlack))
                    contentTxt!!.setHintTextColor(getColor(R.color.colorBlack))
                }
                gbdata = 1
                if ("0" == intent.getStringExtra("ReplyCount")) {
                    mReply_OnOff!!.text = "댓글이 없습니다."
                    mReply_OnOff!!.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
                    mScroll_view!!.visibility = View.GONE
                } else {
                    mReply_OnOff!!.text = "댓글 보기"
                }
                mReply_OnOff!!.setOnClickListener {
                    if (mReply_OnOff!!.text.toString().contains("보기")) {
                        replyOnOff = 1
                        mReply_OnOff!!.text = "댓글 접기"
                        mReply_List!!.visibility = View.VISIBLE // 댓글리스트 보이게
                        mScroll_view!!.visibility = View.VISIBLE
                    } else if (mReply_OnOff!!.text.toString().contains("접기")) {
                        replyOnOff = 0
                        mReply_OnOff!!.text = "댓글 보기"
                        mReply_List!!.visibility = View.GONE // 댓글리스트 안보이게
                        mScroll_view!!.visibility = View.GONE
                    }
                }
                mReplyll!!.visibility = View.VISIBLE
            }
        }


        mBoardApi = RetrofitBoard().imageApi



        mInsertBtn!!.setOnClickListener {
            if (gbdata == 0) {
                hasContent()
            } else {
                updateBoard(path)
            }
        }

    }

    fun init(){
        titleTxt = findViewById(R.id.titleTxt) //제목

        contentTxt = findViewById(R.id.contentTxt) //내용

        mSubtitle_ll = findViewById(R.id.subtitle_ll) //날짜,등록일시 linear

        dateTxt = findViewById(R.id.dateTxt) //날짜

        userTxt = findViewById(R.id.userTxt) //등록자

        mImg = findViewById(R.id.imageView) //이미지


        mInsertBtn = findViewById(R.id.insertBtn) //글쓰기버튼

        mInsert_img = findViewById(R.id.insert_img) //이미지추가 버튼


        mReply_OnOff = findViewById(R.id.reply_OnOff) //댓글 On/Off

        mReply_List = findViewById(R.id.reply_list) //댓글 리스트

        mScroll_view = findViewById(R.id.scroll_view)

        mReplyll = findViewById(R.id.reply_ll)
        ReplyBtn = findViewById(R.id.ReplyBtn) //댓글달기 버튼

        ReplyTxt = findViewById(R.id.ReplyTxt) //댓글 입력


    }

    private fun selectImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, IMG_REQUEST)
    }

    private fun hasContent(): Boolean {
        val TITLE = titleTxt!!.text.toString()
        val CONTENT = contentTxt!!.text.toString()
        return if (TITLE == "" || CONTENT == "" || TITLE.length == 0 || CONTENT.length == 0) {
            val builder: AlertDialog.Builder = AlertDialog.Builder(this@BoardInsertActivity)
            dialog = builder.setMessage("빈 칸은 등록할 수 없습니다.")
                .setNegativeButton("확인", null)
                .create()
            dialog?.show()
            false
        } else {
            uploadImage(path)
            true
        }
    }

    /*private fun getData() {
        val Board_Seq = BoardSeq.toString()
        val boardSeqPart = RequestBody.create(MultipartBody.FORM, Board_Seq)
        val call: Call<List<ResultBoard_Reply>> =
            mBoardApi?.getBoardReplyList(boardSeqPart)!!
        call.enqueue(object : Callback<List<ResultBoard_Reply>> {
            override fun onResponse(
                call: Call<List<ResultBoard_Reply>>,
                response: Response<List<ResultBoard_Reply>>
            ) {

                //정상 결과
                val result: List<ResultBoard_Reply> = response.body()!!
                for (i in result.indices) {
                    val SEQ: Int = result[i].seq
                    val USER: String = result[i].nick
                    val REPLY: String = result[i].reply
                    val DATE: String = result[i].date
                    var msg: String? = ""
                    msg = formatTimeString(DATE)
                    val getReplyData = BoardReplyModel(SEQ, USER, REPLY, msg)
                    replyList?.add(getReplyData)
                    mAdapter = BoardReplyAdapter(applicationContext, replyList!!)
                    mReply_List!!.adapter = mAdapter
                    mAdapter!!.notifyDataSetChanged()
                }

                //Glide.with(getApplicationContext()).load(result.get(1).getPath()).into(imageView);
            }

            override fun onFailure(
                call: Call<List<ResultBoard_Reply>>,
                t: Throwable
            ) {
                Toast.makeText(
                    this@BoardInsertActivity,
                    "데이터 접속 상태를 확인 후 다시 시도해주세요.",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }*/

    //Insert
    private fun uploadImage(filePath: Uri?) {

        //String Image = imageToString();
        val USER = userTxt!!.text.toString()
        val TITLE = titleTxt!!.text.toString()
        val CONTENT = contentTxt!!.text.toString()
        val DATE: String = Common.nowDate("yyyy-MM-dd HH:mm:ss")
        val TOKEN : String = token
        val userPart = RequestBody.create(MultipartBody.FORM, USER)
        val titlePart = RequestBody.create(MultipartBody.FORM, TITLE)
        val contentPart = RequestBody.create(MultipartBody.FORM, CONTENT)
        val datePart = RequestBody.create(MultipartBody.FORM, DATE)
        val tokenPart = RequestBody.create(MultipartBody.FORM, TOKEN)

        val call: Call<ResponseBody>
        call = if (filePath != null) {
            val originalFile: File = FileUtils.getFile(this@BoardInsertActivity, filePath)

            val imagePart = RequestBody.create(MediaType.parse("multipart/form-data"),originalFile)

            val file = MultipartBody.Part.createFormData("image", originalFile.name, imagePart)

            mBoardApi!!.InsertBoard(userPart, titlePart, contentPart, datePart, file, tokenPart)
        } else {
            mBoardApi!!.InsertBoard_NoImage(userPart, titlePart, contentPart, datePart, tokenPart)
        }



        // Call<ResponseBody> call = apiInterface.uploadImage(titlePart,file);
        call.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(
                call: Call<ResponseBody?>,
                response: Response<ResponseBody?>
            ) {
                /*intent = Intent(this@BoardInsertActivity, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                startActivity(intent)*/
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



    // Update
    fun updateBoard(filePath: Uri?) {
        val USER = userTxt!!.text.toString()
        val TITLE = titleTxt!!.text.toString()
        val CONTENT = contentTxt!!.text.toString()
        val DATE =
            Common.nowDate("yyyy-MM-dd HH:mm:ss") //dateTxt.getText().toString();
        val SEQ = intent.getIntExtra("Seq", 0).toString()
        val seqPart = RequestBody.create(MultipartBody.FORM, SEQ)
        val userPart = RequestBody.create(MultipartBody.FORM, USER)
        val titlePart = RequestBody.create(MultipartBody.FORM, TITLE)
        val contentPart = RequestBody.create(MultipartBody.FORM, CONTENT)
        val datePart = RequestBody.create(MultipartBody.FORM, DATE)
        val call: Call<ResponseBody>
        call = if (filePath == null) {
            mBoardApi!!.UpdateBoard_NoImage(seqPart, userPart, titlePart, contentPart, datePart)
        } else {
            val originalFile = FileUtils.getFile(this, filePath)
            val imagePart =
                RequestBody.create( //MediaType.parse(getContentResolver().getType(filePath)),
                    MediaType.parse("multipart/form-data"),
                    originalFile
                )
            val file =
                MultipartBody.Part.createFormData("image", originalFile?.name, imagePart)
            mBoardApi!!.UpdateBoard(seqPart, userPart, titlePart, contentPart, datePart, file)
        }

        // Call<ResponseBody> call = apiInterface.uploadImage(titlePart,file);
        call.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(
                call: Call<ResponseBody?>,
                response: Response<ResponseBody?>
            ) {
                intent = Intent(this@BoardInsertActivity, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
                finish()
                Toast.makeText(this@BoardInsertActivity, "수정되었습니다.", Toast.LENGTH_LONG).show()
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
            }
        })
    }


    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        @Nullable data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMG_REQUEST && resultCode == RESULT_OK && data != null) {
            path = data.data
            testPath = data.dataString
            Log.d("TAG"," test : $testPath" )


            //uploadImage(path);
            try {
                bitmap = MediaStore.Images.Media.getBitmap(contentResolver, path)
                mImg!!.setImageBitmap(bitmap)
                mImg!!.visibility = View.VISIBLE
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }else if(requestCode == PICKER_REQUEST_CODE && resultCode == RESULT_OK && data != null){
            pathsList =
                data.extras?.getStringArray(GligarPicker.IMAGES_RESULT) as Array<String> // return list of selected images paths.
            Log.d("TAG","uriList : $pathsList")

            for (i in pathsList.indices) {
                uriList.add(pathsList[i])
                Log.d("TAG","uriList : $uriList")
                //imagePathMap.put(i, pathsList[i]);
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

    /*
     * Bitmap을 String형으로 변환
     * */
    fun BitmapToString(bitmap: Bitmap): String? {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 70, baos)
        val bytes: ByteArray = baos.toByteArray()
        return Base64.encodeToString(bytes, Base64.DEFAULT)
    }


}