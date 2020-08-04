package com.example.tokenproject

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.view.marginTop
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.github.siyamed.shapeimageview.RoundedImageView
import com.github.tntkhang.fullscreenimageview.library.FullScreenImageViewActivity
import com.stone.vega.library.VegaLayoutManager
import kotlinx.android.synthetic.main.activity_board_detail_view.*
import kotlinx.android.synthetic.main.activity_board_detail_view.contentTxt
import kotlinx.android.synthetic.main.activity_board_detail_view.dateTxt
import kotlinx.android.synthetic.main.activity_board_detail_view.imageLinear
import kotlinx.android.synthetic.main.activity_board_detail_view.imageTxtCount
import kotlinx.android.synthetic.main.activity_board_detail_view.insertBtn
import kotlinx.android.synthetic.main.activity_board_detail_view.reply_list
import kotlinx.android.synthetic.main.activity_board_detail_view.titleTxt
import kotlinx.android.synthetic.main.activity_board_detail_view.userTxt
import kotlinx.android.synthetic.main.activity_board_detail_view.view.*
import kotlinx.android.synthetic.main.activity_board_insert.*
import java.io.File

class BoardDetailViewActivity : AppCompatActivity() {

    //아이디 저장 기능


    private var id = ""
    private var token = ""
    var pathList = ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board_detail_view)

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        var setting: SharedPreferences? = getSharedPreferences("tokenApp", Activity.MODE_PRIVATE)
        var editor: SharedPreferences.Editor? = setting?.edit()

        id = setting?.getString("ID", "").toString() //로그인계정
        token = setting?.getString("token", "").toString()    //작성자 토큰

        val manager = LinearLayoutManager(this)
        val decoration = DividerItemDecoration(this, manager.orientation)

        reply_list.addItemDecoration(decoration)
        reply_list.layoutManager = VegaLayoutManager()

        val writer = intent.getStringExtra("WRITER") //작성자

        if (id == writer) {   //작성자와 로그인계정이 같으면
            titleTxt.isEnabled = true // 제목, 내용 ReadOnly true
            contentTxt.isEnabled = true
            insertBtn.visibility = View.VISIBLE // 글쓰기 버튼 보이게
        } else {
            titleTxt.isEnabled = false // 제목, 내용 ReadOnly true
            contentTxt.isEnabled = false
            insertBtn.visibility = View.GONE // 글쓰기 버튼 안보이게
        }

        titleTxt.setText(intent.getStringExtra("TITLE"))    //제목
        contentTxt.setText(intent.getStringExtra("CONTENT"))//내용
        userTxt.text = intent.getStringExtra("WRITER")    //작성자
        dateTxt.text = intent.getStringExtra("DATE").substring(0, 16)//날짜


        pathList = intent.getSerializableExtra("pathList") as ArrayList<String>

        Log.d("TAG", "pathList : $pathList")
        Log.d("TAG", "pathList : " + pathList.size)

        for (j in pathList.indices){
            if("null" == pathList[j] || "" == pathList[j]){
                pathList[j] = "android.resource://$packageName/drawable/deleteiconblack2"
            }else{
                continue
            }
        }
        Log.d("TAG", "pathList : $pathList")

        for (i in pathList.indices) {
            //Log.d("TAG","pathList : " + pathList[i].length)
            //Log.d("TAG","pathList : " + pathList[i].length)
            if(pathList[i].isNotEmpty()){
                if(pathList[i].contains("deleteiconblack2")) {
                    continue
                }else{
                    setImage(pathList[i], i)
                }
            }else{
                continue
            }

        }
    }

    /*
    *
    * */
    @SuppressLint("InflateParams")
    fun setImage(imagePath: String?, pos: Int) {
        Log.d("TAG","position : $pos")
        Log.d("TAG","imagePath : $imagePath")
        if ("null" == imagePath){
            return
        } else {
            val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

            val statLayoutItem =
                inflater.inflate(R.layout.adddetailimage, imageLinear, false) as LinearLayout

            statLayoutItem.x = 18F
            statLayoutItem.y = 22F

            val addImg: RoundedImageView = statLayoutItem.findViewById(R.id.addImage)


            addImg.setOnClickListener {
                Toast.makeText(this, "이미지가 보이지 않을 시 화면 가운데를 두손가락으로 터치 해주세요.", Toast.LENGTH_LONG)
                    .show()
                onImageClickAction(pathList, pos)
            }


            Glide.with(applicationContext)
                .load(imagePath)
                .override(300, 300)
                .fitCenter()
                .into(addImg)

            imageLinear.addView(statLayoutItem)
        }

    }

    private fun onImageClickAction(uriString: ArrayList<String>, pos: Int) {
        val fullImageIntent = Intent(this, FullScreenImageViewActivity::class.java)
        fullImageIntent.putExtra(FullScreenImageViewActivity.URI_LIST_DATA, uriString)
        fullImageIntent.putExtra(FullScreenImageViewActivity.IMAGE_FULL_SCREEN_CURRENT_POS, pos)
        startActivity(fullImageIntent)
    }


}



