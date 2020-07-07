package com.psmstudio.user.mygwangju.new_Board

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.example.tokenproject.Model.BoardReplyModel
import com.example.tokenproject.R
import com.github.siyamed.shapeimageview.RoundedImageView

class BoardReplyAdapter(
    private val context: Context,
    dataList: List<BoardReplyModel>
) :
    RecyclerView.Adapter<BoardReplyAdapter.ViewHolder?>() {
    private val mData: List<BoardReplyModel> = dataList

    @NonNull
    override fun onCreateViewHolder(
        @NonNull parent: ViewGroup,
        i: Int
    ): ViewHolder {
        val convertView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.reply, parent, false)
        return ViewHolder(convertView)
    }

    override fun onBindViewHolder(holder: BoardReplyAdapter.ViewHolder, position: Int) {
        holder.name_txt.text = mData[position].user_id
        holder.date_txt.text = mData[position].date
        holder.reply_txt.text = mData[position].reply
    }

    override fun getItemCount(): Int {
        return mData.size
    }



    class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var name_txt: TextView
        var date_txt: TextView
        var reply_txt: TextView

        init {
            // 레이아웃 들고 오기
            val img_view: RoundedImageView = itemView.findViewById(R.id.image_view)
            val name_txt = itemView.findViewById<TextView>(R.id.nameTxt)
            val date_txt = itemView.findViewById<TextView>(R.id.dateTxt)
            val reply_txt = itemView.findViewById<TextView>(R.id.replyTxt)
            this.name_txt = name_txt
            this.date_txt = date_txt
            this.reply_txt = reply_txt
        }
    }


}