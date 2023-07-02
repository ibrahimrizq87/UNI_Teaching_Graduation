package com.uni.uniteaching.adapters
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView

import com.uni.uniteaching.R
import com.uni.uniteaching.classes.user.UserStudent


class StudentAdapter(
    val context: Context,
    var studentList:MutableList<UserStudent>,
    val addPerm:(Int, UserStudent) ->Unit

)
    : RecyclerView.Adapter<StudentAdapter.myViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {
        val view : View = LayoutInflater.from(context).inflate(R.layout.item_add_student,parent,false)
        return myViewHolder(view)
    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {
        val currentItem = studentList[position]



        holder.name.text = currentItem.name
        holder.section.text = currentItem.section
        holder.departemt.text = currentItem.department
        holder.grade.text = currentItem.grade




    }
    fun update(list: MutableList<UserStudent>){
        this.studentList=list
        notifyDataSetChanged()
    }




    override fun getItemCount(): Int {
        return studentList.size
    }


    inner    class myViewHolder(item: View) : RecyclerView.ViewHolder(item){

        val name = item.findViewById<TextView>(R.id.text_student_name)
        val grade = item.findViewById<TextView>(R.id.text_student_grade)
        val departemt = item.findViewById<TextView>(R.id.text_student_department)
        val section = item.findViewById<TextView>(R.id.text_student_section)

        val add = item.findViewById<ImageButton>(R.id.button_add)
        val item = item.findViewById<ConstraintLayout>(R.id.student_item)
        init {

            add.setOnClickListener {
                addPerm.invoke(adapterPosition,studentList[adapterPosition])
            }


            }
        }




    }
