package com.example.tokenproject

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import com.github.tntkhang.fullscreenimageview.library.FullScreenImageViewActivity
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.activity_board_detail_view.*

class BoardDetailViewActivity : AppCompatActivity() {

    //아이디 저장 기능


    private var id = ""
    private var token = ""
    private var count = 0
    private var date : String? = null
    var seq : Int = 0
    var title : String? = null
    var writer : String? = null
    var content : String? = null
    var replyCount : String? = null

    private var menuGb = 0
    var pathList = ArrayList<String>()

    private var slideModels : ArrayList<SlideModel> = ArrayList<SlideModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board_detail_view)

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        var setting: SharedPreferences? = getSharedPreferences("tokenApp", Activity.MODE_PRIVATE)
        var editor: SharedPreferences.Editor? = setting?.edit()

        id = setting?.getString("ID", "").toString() //로그인계정
        token = setting?.getString("token", "").toString()    //작성자 토큰

        seq = intent.getIntExtra("Seq",0)

        title = intent.getStringExtra("TITLE")    //제목
        writer = intent.getStringExtra("WRITER") //작성자
        content = intent.getStringExtra("CONTENT")  //내용
        date = intent.getStringExtra("DATE")    //작성일자
        replyCount = intent.getStringExtra("ReplyCount")    //댓글개수
        pathList = intent.getSerializableExtra("pathList") as ArrayList<String>

        writerTxt.text = "작성자 : $writer"
        dateTxt.text = "작성일자 : $date"
        contents_edit.text = content

        for (j in pathList.indices){
            if("null" == pathList[j] || "" == pathList[j]){
                pathList[j] = "android.resource://$packageName/drawable/deleteiconblack2"
            }else{
                continue
            }
        }

        for (i in pathList.indices) {
            if(pathList[i].isNotEmpty()){
                if(pathList[i].contains("deleteiconblack2")) {
                    continue
                }else{
                    count++
                    slideModels.add(SlideModel(pathList[i],title))
                    //setImage(pathList[i], i)
                }
            }else{
                continue
            }
        }

        img_slider.setImageList(slideModels, true)

        setSupportActionBar(toolbar)
        supportActionBar?.title = ""

        app_bar.addOnOffsetChangedListener(object : AppBarLayout.OnOffsetChangedListener {
            override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
                when {
                    verticalOffset == 0 -> { //  이미지가 보일때
                        supportActionBar?.title = ""
                        toolbar_layout.title = ""
                    }
                    Math.abs(verticalOffset) >= app_bar.totalScrollRange -> { // 이미지 안보이고 툴바만 보일떄
                        supportActionBar?.title = title

                    }
                    Math.abs(verticalOffset) <= app_bar.totalScrollRange -> {// 중간
                        toolbar_layout.title = ""
                        supportActionBar?.subtitle=""
                    }
                    else -> {
                        supportActionBar?.title = ""
                        toolbar_layout.title = ""
                    }
                }
            }
        })
    }

    //메뉴를 붙이는 부분
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        if(id == writer){   // 작성자와 로그인 계정이 같으면 수정, 삭제 메뉴 추가
            val inflater = menuInflater
            inflater.inflate(R.menu.menu_board, menu)
        }else{
            return false
        }

        return true
    }

    //메뉴클릭시!
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_setting -> {
                updateBoard()   // 수정화면으로
                true
            }
            R.id.action_menu2 -> {
                Toast.makeText(this,"삭제",Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun updateBoard(){
        intent = Intent(this, BoardUpdateActivity::class.java)
        intent.putExtra("Seq",seq)
        intent.putExtra("TITLE",title)
        intent.putExtra("WRITER",writer)
        intent.putExtra("CONTENT",content)
        intent.putExtra("DATE",date)

        var list = java.util.ArrayList<String>()

        for (i in pathList.indices) {
            if(pathList[i].isNotEmpty()){
                if(pathList[i].contains("deleteiconblack2")) {
                    continue
                }else{
                    list.add(pathList[i])
                    //setImage(pathList[i], i)
                }
            }else{
                continue
            }
        }
        intent.putExtra("pathList",list)
        intent.putExtra("ReplyCount", replyCount)
        startActivity(intent)
        Toast.makeText(this, "수정", Toast.LENGTH_SHORT).show()
    }




    private fun onImageClickAction(uriString: ArrayList<String>, pos: Int) {
        val fullImageIntent = Intent(this, FullScreenImageViewActivity::class.java)
        fullImageIntent.putExtra(FullScreenImageViewActivity.URI_LIST_DATA, uriString)
        fullImageIntent.putExtra(FullScreenImageViewActivity.IMAGE_FULL_SCREEN_CURRENT_POS, pos)
        startActivity(fullImageIntent)
    }


}





