package com.uni.uniteaching.data

import com.uni.uniteaching.classes.*
import com.uni.uniteaching.classes.user.UserStudent


interface FirebaseRepo {

    suspend fun getSectionData(
        assistantCode: String
        ,
        result: (Resource<List<SectionData>>) -> Unit
    )
    suspend fun searchStudentBySection( grade:String,section:String,department:String,result: (Resource<List<UserStudent>>) -> Unit)
    suspend fun searchStudentByID( grade:String,code:String,result: (Resource<UserStudent>) -> Unit)
    suspend fun searchStudentByDepartment( grade:String,department: String,result: (Resource<List<UserStudent>>) -> Unit)
    suspend fun searchStudentAll( grade:String,result: (Resource<List<UserStudent>>) -> Unit)


    suspend fun addGeneralPosts(posts: Posts, result: (Resource<String>) -> Unit)
    suspend fun addPersonalPosts(posts: Posts, userID: String, result: (Resource<String>) -> Unit)
    suspend fun addCoursePosts(posts: Posts, courseID: String, result: (Resource<String>) -> Unit)
    suspend fun addSectionPosts(posts: Posts, section: String, dep: String, result: (Resource<String>) -> Unit)
    suspend fun deleteCommentGeneralPosts(
        comment: Comment,
        postID: String,
        result: (Resource<String>) -> Unit
    )
    suspend fun deleteCommentSectionPosts(
        comment: Comment,
        postID: String,
        section: String,
        dep: String,

        result: (Resource<String>) -> Unit
    )
    suspend fun deleteCommentPersonalPosts(
        comment: Comment,
        postID: String,
        userID: String,
        result: (Resource<String>) -> Unit
    )
    suspend fun deleteCommentCoursePosts(
        comment: Comment,
        postID: String,
        courseID: String,
        result: (Resource<String>) -> Unit
    )
    suspend fun updateCommentGeneralPosts(
        comment: Comment,
        postID: String,
        result: (Resource<String>) -> Unit
    )
    suspend fun updateCommentSectionPosts(
        comment: Comment,
        postID: String,
        section: String,
        dep: String,

        result: (Resource<String>) -> Unit
    )
    suspend fun updateCommentCoursePosts(
        comment: Comment,
        postID: String,
        courseID: String,
        result: (Resource<String>) -> Unit
    )
    suspend fun updateCommentPersonalPosts(
        comment: Comment,
        postID: String,
        userID: String,
        result: (Resource<String>) -> Unit
    )
    suspend fun getGeneralPosts( result: (Resource<List<Posts>>) -> Unit)
    suspend fun getSectionPosts(
        sections: List<SectionData>,
        result: (Resource<List<Posts>>) -> Unit
    )    suspend fun getCoursePosts(courses: List<Courses>, result: (Resource<List<Posts>>) -> Unit)
    suspend fun getPersonalPosts(
        userID: String,
        grade: String,
        result: (Resource<List<Posts>>) -> Unit
    )
    suspend fun addCommentGeneralPosts(comment: Comment, postID:String, result: (Resource<String>) -> Unit)
    suspend fun addCommentSectionPosts(comment: Comment, postID:String, section:String, dep:String, result: (Resource<String>) -> Unit)
    suspend fun addCommentCoursePosts(comment: Comment, postID:String, courseID:String, result: (Resource<String>) -> Unit)
    suspend fun addCommentPersonalPosts(comment: Comment, postID:String, userID:String, result: (Resource<String>) -> Unit)



    suspend fun getCommentGeneralPosts( postID:String,result: (Resource<List<Comment>>) -> Unit)
    suspend fun getCommentSectionPosts(postID:String, section:String,dep:String, result: (Resource<List<Comment>>) -> Unit)
    suspend fun getCommentCoursePosts(postID:String,courseID:String, result: (Resource<List<Comment>>)-> Unit)
    suspend fun getCommentPersonalPosts(postID:String,userID:String, result:(Resource<List<Comment>>) -> Unit)




    suspend fun deleteGeneralPosts(postId: String, result: (Resource<String>) -> Unit)

    suspend fun deletePersonalPosts(postId: String, userID: String, result: (Resource<String>) -> Unit)
    suspend fun deleteCoursePosts(postId: String, courseID: String, result: (Resource<String>) -> Unit)

    suspend fun deleteSectionPosts(postId: String, section: String, dep: String, result: (Resource<String>) -> Unit)
    suspend fun getSection(
        sections:List<SectionData>,
        result: (Resource<List<Section>>) -> Unit
    )
    fun getLectures(courses: List<Courses>, dep:String, result: (Resource<List<Lecture>>) -> Unit)

    suspend fun updateSectionState(
        section: Section,
        state:Boolean,
        result: (Resource<String>) -> Unit
    )
    suspend fun updateLectureState(
        lecture: Lecture,
        state:Boolean,
        result: (Resource<String>) -> Unit
    )
    suspend fun getCourseByAssistantCode( assistantCode:String,result: (Resource<List<Courses>>) -> Unit)
    suspend fun  getCourseByProfessorCode( professorCode:String,result: (Resource<List<Courses>>) -> Unit)
    suspend fun getSectionAttendance(

        section: Section,
        result: (Resource<List<Attendance>>) -> Unit
    )
    suspend fun getLectureAttendance(

        lecture: Lecture,
        result: (Resource<List<Attendance>>) -> Unit
    )
    suspend fun updateSectionAttendance(
        attendance: Attendance,
        section: Section,
        result: (Resource<String>) -> Unit
    )
    suspend fun deleteLectureAttendance(
        attendance: Attendance,
        lecture: Lecture,
        result: (Resource<String>) -> Unit
    )
    suspend fun deleteSectionAttendance(
        attendance: Attendance,
        section: Section,
        result: (Resource<String>) -> Unit
    )
    suspend fun updateLectureAttendance(
        attendance: Attendance,
        lecture: Lecture,
        result: (Resource<String>) -> Unit
    )

}