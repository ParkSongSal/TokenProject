package com.example.tokenproject.Fragment

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tokenproject.Adapter.BoardAdapter
import com.example.tokenproject.BoardInsertActivity
import com.example.tokenproject.Model.BoardModel
import com.example.tokenproject.R
import com.example.tokenproject.Retrofit2.BoardApi
import com.example.tokenproject.Retrofit2.ResultBoard
import com.example.tokenproject.Retrofit2.RetrofitBoard
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.stone.vega.library.VegaLayoutManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class FirstFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    var editTextFilter: EditText? = null

    private var mRecycle_view: RecyclerView? = null
    private var boardList: MutableList<BoardModel>? = null
    private var saveList: MutableList<BoardModel>? = null

    private var mAdapter: BoardAdapter? = null
    private var mBoardApi: BoardApi? = null

    private var intent: Intent? = null

    private var fab: FloatingActionButton? = null

    var setting //아이디 저장 기능
            : SharedPreferences? = null
    var editor: SharedPreferences.Editor? = null
    var id = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view : View = inflater.inflate(R.layout.fragment_first, container, false)

        setting = activity?.getSharedPreferences("setting", Activity.MODE_PRIVATE)

        editor = setting?.edit()

        id = setting?.getString("ID", "")!! // 로그인한 id셋팅

        fab = view.findViewById(R.id.fab)
        fab!!.setOnClickListener {
            intent = Intent(activity, BoardInsertActivity::class.java)
            startActivity(intent)
            //activity?.finish()
        }

        editTextFilter = view.findViewById(R.id.EditTextFilter)

        //검색

        //검색
        editTextFilter!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence,
                i: Int,
                i1: Int,
                i2: Int
            ) {
            }

            override fun onTextChanged(
                charSequence: CharSequence,
                i: Int,
                i1: Int,
                i2: Int
            ) {
                if (boardList!!.size > 0) {
                    searchUser(charSequence.toString())
                } else {
                    Toast.makeText(activity, "글이 존재하지 않습니다.", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun afterTextChanged(edit: Editable) {

                /* String filterText = edit.toString();

                ((Dept_Adapter)listview1.getAdapter()).getFilter().filter(filterText);*/
            }
        })

        mRecycle_view = view.findViewById(R.id.recycle_view)

        val manager = LinearLayoutManager(activity)
        val decoration =
            DividerItemDecoration(activity, manager.getOrientation())
        mRecycle_view!!.addItemDecoration(decoration)
        mRecycle_view!!.layoutManager = VegaLayoutManager()

        boardList = ArrayList<BoardModel>()
        saveList = ArrayList<BoardModel>()
        mBoardApi = RetrofitBoard().getImageApi() //Retrofit2 셋팅


        getServerData() // 게시판리스트 불러오기


        return view

    }


    // 게시판 검색
    private fun searchUser(search: String) {
        boardList?.clear()
        for (i in saveList!!.indices) {
            if (saveList!![i].title.contains(search)
                || saveList!![i].user_id.contains(search)
            ) {
                boardList?.add(saveList!![i])
            }
        }
        mAdapter!!.notifyDataSetChanged()
    }


    //게시판 리스트 조회
    private fun getServerData() {
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
                    boardList!!.add(getServerdata)
                    saveList!!.add(getServerdata)
                    mAdapter = BoardAdapter(activity!!, boardList, saveList)
                    mRecycle_view!!.adapter = mAdapter
                    runAnimation()
                }
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
}
