package com.example.tokenproject.Fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.tokenproject.Adapter.BoardAdapter
import com.example.tokenproject.BoardInsertActivity
import com.example.tokenproject.Model.BoardModel
import com.example.tokenproject.R
import com.example.tokenproject.Retrofit2.BoardApi
import com.example.tokenproject.Retrofit2.ResultBoard
import com.example.tokenproject.Retrofit2.RetrofitBoard
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.stone.vega.library.VegaLayoutManager
import kotlinx.android.synthetic.main.fragment_first.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class FirstFragment : Fragment() {

    private var mRecycle_view: RecyclerView? = null
    private lateinit var boardList: MutableList<BoardModel>
    private lateinit var saveList: MutableList<BoardModel>

    private var mAdapter: BoardAdapter? = null
    private var mBoardApi: BoardApi? = null

    private var mIntent: Intent? = null

    private var fab: FloatingActionButton? = null

    var setting //아이디 저장 기능
            : SharedPreferences? = null
    var editor: SharedPreferences.Editor? = null
    var id = ""
    var token =""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view : View = inflater.inflate(R.layout.fragment_first, container, false)

        setting = activity?.getSharedPreferences("tokenApp", Activity.MODE_PRIVATE)

        editor = setting?.edit()


        id = setting?.getString("ID", "")!! // 로그인한 id셋팅
        token = setting?.getString("token","")!!

        fab = view.findViewById(R.id.fab)
        fab!!.setOnClickListener {
            mIntent = Intent(activity, BoardInsertActivity::class.java)
            startActivity(mIntent)
            editor?.putInt("fragInsert",1)
            editor?.apply()

            //activity?.finish()
        }

        boardList = ArrayList<BoardModel>()
        saveList = ArrayList<BoardModel>()


        mRecycle_view = view.findViewById(R.id.recycle_view)

        val manager = LinearLayoutManager(activity)
        val decoration =
            DividerItemDecoration(activity, manager.orientation)
        mRecycle_view!!.addItemDecoration(decoration)
        mRecycle_view!!.layoutManager = VegaLayoutManager()


        mBoardApi = RetrofitBoard().imageApi //Retrofit2 셋팅


        getServerData() // 게시판리스트 불러오기

        /*val mSwipeRefreshLayout =
            view.findViewById(R.id.swipe_layout) as SwipeRefreshLayout

        mSwipeRefreshLayout.setOnRefreshListener {
            //var ft : FragmentTransaction = fragmentManager!!.beginTransaction()
            //ft.commit()
            getServerData()
            val handler = Handler()

            handler.postDelayed(Runnable { swipe_layout.isRefreshing = false }, 500)
        }*/



        Log.d("TAG","onCreateView 호출 ")
        return view

    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("TAG","onDestroyView 호출 !!! ")
    }

    override fun onResume() {
        super.onResume()

        var fragInsert = setting?.getInt("fragInsert", -1) // 로그인한 id셋팅

        if(fragInsert == 0){
            Log.d("TAG","onResume 호출 !!! $fragInsert")
            getServerData()
        }else{
            Log.d("TAG","onResume 호출 !!! $fragInsert")
        }
        Log.d("TAG","onResume 호출 !!! ")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.d("TAG","onActivityCreated 호출 !!! ")

    }

    override fun onStart() {
        super.onStart()
        Log.d("TAG","onStart 호출 !!! ")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d("TAG","onAttach 호출 !!! ")
    }



    override fun onPause() {
        super.onPause()
        /*ft.detach(fragment1)
        ft.commit()*/
        Log.d("TAG","onPause 호출 !!! ")
    }


    //게시판 리스트 조회
    private fun getServerData() {

        boardList.clear()
        saveList.clear()

        val call: Call<List<ResultBoard>> = mBoardApi!!.boardList
        call.enqueue(object : Callback<List<ResultBoard>> {
            override fun onResponse(
                call: Call<List<ResultBoard>>,
                response: Response<List<ResultBoard>>
            ) {

                //정상 결과
                val result: List<ResultBoard> = response.body()!!
                for (i in result.indices) {
                    val SEQ: Int = result[i].seq
                    val USER: String = result[i].user_id
                    val TITLE: String = result[i].title
                    val CONTENT: String = result[i].content
                    val DATE: String = result[i].date
                    val PATH: String? = result[i].path
                    val reply_count: String = result[i].reply_count
                    val getServerdata =
                        BoardModel(SEQ, USER, TITLE, CONTENT, DATE, PATH, reply_count)
                    boardList.add(getServerdata)
                    saveList.add(getServerdata)
                    mAdapter = BoardAdapter(activity!!, boardList, saveList)
                    mRecycle_view!!.adapter = mAdapter
                    runAnimation()

                }
                editor?.putInt("fragInsert",-1)
                editor?.apply()
            }

            override fun onFailure(
                call: Call<List<ResultBoard>>,
                t: Throwable
            ) {
                // 네트워크 문제
                Toast.makeText(
                    activity,
                    "데이터 접속 상태를 확인 후 다시 시도해주세요.",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }


    fun runAnimation() {
        val controller =
            AnimationUtils.loadLayoutAnimation(
                activity,
                R.anim.layout_animation_from_right
            )
        mRecycle_view!!.layoutAnimation = controller
        mRecycle_view!!.adapter!!.notifyDataSetChanged()
        mRecycle_view!!.scheduleLayoutAnimation()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
    }


    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)

    }

    // 보낸이 : MemoRecyclerAdapter
    //@SuppressLint("RestrictedApi")
    @Subscribe
    fun onItemClick(event: BoardAdapter.ItemClickEvent) {

        mIntent = Intent(activity?.applicationContext, BoardInsertActivity::class.java)

        mIntent!!.putExtra("Seq", boardList[event.position].seq)
        mIntent!!.putExtra("TITLE", boardList[event.position].title)
        mIntent!!.putExtra("WRITER", boardList[event.position].user_id)
        mIntent!!.putExtra("CONTENT", boardList[event.position].content)
        mIntent!!.putExtra("DATE", boardList[event.position].date)
        mIntent!!.putExtra("PATH", boardList[event.position].path)
        mIntent!!.putExtra("ReplyCount", boardList[event.position].reply_count)
        startActivity(mIntent)
    }

}
