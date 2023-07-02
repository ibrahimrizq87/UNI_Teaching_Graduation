package com.uni.uniteaching.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.uni.uniteaching.R
import com.uni.uniteaching.classes.Attendance
import com.uni.uniteaching.classes.user.UserStudent

class AttendanceAdapter (
    val context: Context,
    var attendanceList:MutableList<Attendance>,
    val delete:( Attendance) ->Unit

    )
    : RecyclerView.Adapter<AttendanceAdapter.myViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {
            val view : View = LayoutInflater.from(context).inflate(R.layout.attendance_item,parent,false)
            return myViewHolder(view)
        }

        override fun onBindViewHolder(holder: myViewHolder, position: Int) {
            val currentItem = attendanceList[position]



            holder.name.text = currentItem.userName
            holder.code.text = currentItem.userCode
            holder.time.text = currentItem.time.toString()




        }
        fun update(list: MutableList<Attendance>){
            this.attendanceList=list
            notifyDataSetChanged()
        }




        override fun getItemCount(): Int {
            return attendanceList.size
        }


        inner    class myViewHolder(item: View) : RecyclerView.ViewHolder(item){

            val name = item.findViewById<TextView>(R.id.text_student_name)
            val code = item.findViewById<TextView>(R.id.text_student_code)
            val time = item.findViewById<TextView>(R.id.text_student_time)

            val add = item.findViewById<Button>(R.id.button_add)
            init {
                add.setOnClickListener {
                    delete.invoke(attendanceList[adapterPosition])
                }
            }
        }




    }
