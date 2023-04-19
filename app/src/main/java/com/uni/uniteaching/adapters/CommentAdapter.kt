package com.uni.uniteaching.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.uni.uniteaching.R
import com.uni.uniteaching.classes.MyComments


class CommentAdapter(
    val context: Context,
    var commentList:MutableList<MyComments>,
    val onUpdate:(Int, MyComments) ->Unit,
    val onDelete:(Int, MyComments) ->Unit

    )
    : RecyclerView.Adapter<CommentAdapter.myViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {
        val view : View = LayoutInflater.from(context).inflate(R.layout.comment_item,parent,false)
        return myViewHolder(view)
    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {
        val currentItem = commentList[position]
        if (currentItem.myComment){
            holder.delete_bt.visibility=View.VISIBLE
            holder.update_bt.visibility=View.VISIBLE
        }
        holder.auth.text = currentItem.authorName
        holder.comment.text = currentItem.description


    }
    fun update(list: MutableList<MyComments>){
        this.commentList=list
        notifyDataSetChanged()
    }




    override fun getItemCount(): Int {
        return commentList.size
    }


    inner    class myViewHolder(item: View) : RecyclerView.ViewHolder(item){

        val auth = item.findViewById<TextView>(R.id.author_comment)
        val comment = item.findViewById<TextView>(R.id.comment_text)
        val update_bt = item.findViewById<Button>(R.id.update_comment)
        val delete_bt = item.findViewById<Button>(R.id.delete_comment)

        init {
            update_bt.setOnClickListener {
                onUpdate.invoke(adapterPosition,commentList[adapterPosition])
            }
            delete_bt.setOnClickListener {
                onDelete.invoke(adapterPosition,commentList[adapterPosition])
            }

        }




    }

}