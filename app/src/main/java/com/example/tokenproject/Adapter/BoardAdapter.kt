package com.example.tokenproject.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tokenproject.Common.Common.formatTimeString
import com.example.tokenproject.Model.BoardModel
import com.example.tokenproject.R
import com.github.siyamed.shapeimageview.RoundedImageView
import org.greenrobot.eventbus.EventBus

class BoardAdapter(
    private var context: Context,
    dataList: List<BoardModel>,
    saveList: List<BoardModel>
) :
    RecyclerView.Adapter<BoardAdapter.ViewHolder?>() {

    private var mData: List<BoardModel> = dataList
    private var saveList: List<BoardModel> = saveList

    //Event Bus 클래스
    class ItemClickEvent //this.id = id;
        (//public long id;
        var position: Int
    )

    @NonNull
    override fun onCreateViewHolder(@NonNull parent: ViewGroup, i: Int): ViewHolder {
        val convertView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_image_board, parent, false)
        return ViewHolder(convertView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(
        @NonNull viewHolder: ViewHolder,
        position: Int
    ) {
        viewHolder.user_txt.setText(mData[position].user_id)
        var msg: String? = ""
        msg = formatTimeString(mData[position].date)
        viewHolder.date_txt.text = msg
        viewHolder.title_txt.setText(mData[position].title)
        viewHolder.title_txt.isSelected = true
        if ("0" == mData[position].reply_count) {
            viewHolder.reply_count.text = ""
        } else {
            viewHolder.reply_count.text =
                " [ " + mData[position].reply_count.toString() + " ] "
        }
        val path: String? = mData[position].path
        if (path != null) {
            Glide.with(context)
                .load(path)
                .override(600, 300)
                .fitCenter()
                .into(viewHolder.img_view)
        } else {
            Glide.with(context)
                .load(R.drawable.alarm)
                .override(600, 300)
                .fitCenter()
                .into(viewHolder.img_view)
        }
        viewHolder.itemView.setOnClickListener(View.OnClickListener { // MainActivity에 onItemClick이 받음
            EventBus.getDefault().post(ItemClickEvent(position))
        })
    }

    class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var img_view: RoundedImageView
        var title_txt: TextView
        var user_txt: TextView
        var date_txt: TextView
        var reply_count: TextView

        init {
            // 레이아웃 들고 오기
            val img_view: RoundedImageView = itemView.findViewById(R.id.image_view)
            val title_txt = itemView.findViewById<TextView>(R.id.titleTxt)
            val user_txt = itemView.findViewById<TextView>(R.id.userTxt)
            val date_txt = itemView.findViewById<TextView>(R.id.dateTxt)
            val reply_count = itemView.findViewById<TextView>(R.id.reply_count)
            this.img_view = img_view
            this.title_txt = title_txt
            this.user_txt = user_txt
            this.date_txt = date_txt
            this.reply_count = reply_count
        }
    }

    override fun getItemCount(): Int {
        return mData.size

    }
}