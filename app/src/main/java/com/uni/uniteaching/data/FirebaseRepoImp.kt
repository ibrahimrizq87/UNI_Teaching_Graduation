package com.uni.uniteaching.data



import android.util.Log
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.FirebaseFirestore
import com.uni.uniteaching.data.di.FireStoreTable
import com.uni.uniteaching.data.di.PermissionsRequired
import com.uni.uniteaching.data.di.PostType
import com.uni.uniteaching.classes.*
import com.uni.uniteaching.classes.user.UserStudent
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class FirebaseRepoImp@Inject constructor(
   private val database:FirebaseFirestore

): FirebaseRepo {


    override suspend fun deleteGeneralPosts(postId: String, result: (Resource<String>) -> Unit) {
        val document = database.collection(FireStoreTable.post).document(postId)
        document.delete()
            .addOnSuccessListener {
                result.invoke(
                    Resource.Success("deleted successfully")
                )
            }
            .addOnFailureListener {
                result.invoke(
                    Resource.Failure(
                        it.localizedMessage
                    )
                )
            }
    }

    override suspend fun deletePersonalPosts(
        postId: String,
        userID: String,
        result: (Resource<String>) -> Unit
    ) {
        val document = database.collection(PostType.personal_posts).document(userID)
            .collection(FireStoreTable.post)

            .document(postId)
        document.delete()
            .addOnSuccessListener {
                result.invoke(
                    Resource.Success("deleted successfully")
                )
            }
            .addOnFailureListener {
                result.invoke(
                    Resource.Failure(
                        it.localizedMessage
                    )
                )
            }
    }

    override suspend fun deleteCoursePosts(
        postId: String,
        courseID: String,
        result: (Resource<String>) -> Unit
    ) {
        val document = database.collection(FireStoreTable.courses).document(courseID)
            .collection(FireStoreTable.post).document(postId)

        document.delete()
            .addOnSuccessListener {
                result.invoke(
                    Resource.Success("deleted successfully")
                )
            }
            .addOnFailureListener {
                result.invoke(
                    Resource.Failure(
                        it.localizedMessage
                    )
                )
            }
    }

    override suspend fun deleteSectionPosts(
        postId: String,
        section: String,
        dep: String,
        result: (Resource<String>) -> Unit
    ) {
        val document = database.collection(PostType.section_posts).document(dep).collection(section)
            .document(postId)
        document.delete()
            .addOnSuccessListener {
                result.invoke(
                    Resource.Success("deleted successfully")
                )
            }
            .addOnFailureListener {
                result.invoke(
                    Resource.Failure(
                        it.localizedMessage
                    )
                )
            }
    }

    override suspend fun getSectionData(assistantCode: String, result: (Resource<List<SectionData>>) -> Unit) {
        val listOfPosts = arrayListOf<SectionData>()

            val docRef =
                database.collection(FireStoreTable.assistant_sections).document(assistantCode)
                    .collection(FireStoreTable.sections)
            docRef.get()
                .addOnSuccessListener { documents ->
                for (document in documents) {
                    val section = document.toObject(SectionData::class.java)
                    listOfPosts.add(section)
                }
                    result.invoke(
                        Resource.Success(listOfPosts)
                    )

            }
                .addOnFailureListener { exception ->
                    result.invoke(
                        Resource.Failure(
                            exception.localizedMessage
                        )
                    )

                }


    }
    override suspend fun searchStudentByID( grade:String,code:String,result: (Resource<UserStudent>) -> Unit) {
        val docRef = database.collection(grade)
            .whereEqualTo("code", code)
        docRef.get()
            .addOnSuccessListener {
                lateinit var  student:UserStudent
                student= UserStudent()
                for(rec in it){

                    student =  rec.toObject(UserStudent::class.java)

                }
                result.invoke(
                    Resource.Success(student)
                )
            }
            .addOnFailureListener{
                result.invoke(
                    Resource.Failure(
                        it.localizedMessage
                    )
                )
            }

    }
    override suspend fun searchStudentBySection( grade:String,section:String,department:String,result: (Resource<List<UserStudent>>) -> Unit) {
        val docRef = database.collection(grade)
            .whereEqualTo("section", section)
            .whereEqualTo("department", department)
        docRef.get()
            .addOnSuccessListener {
                val listOfStudents= arrayListOf<UserStudent>()
                for(rec in it){
                    val student =  rec.toObject(UserStudent::class.java)
                    listOfStudents.add(student)
                }
                result.invoke(
                    Resource.Success(listOfStudents)
                )
            }
            .addOnFailureListener{
                result.invoke(
                    Resource.Failure(
                        it.localizedMessage
                    )
                )
            }

    }
    override suspend fun searchStudentByDepartment( grade:String,department: String,result: (Resource<List<UserStudent>>) -> Unit) {
        val docRef = database.collection(grade)
            .whereEqualTo("department", department)
        docRef.get()
            .addOnSuccessListener {
                val listOfStudents= arrayListOf<UserStudent>()
                for(rec in it){
                    val student =  rec.toObject(UserStudent::class.java)
                    listOfStudents.add(student)
                }
                result.invoke(
                    Resource.Success(listOfStudents)
                )
            }
            .addOnFailureListener{
                result.invoke(
                    Resource.Failure(
                        it.localizedMessage
                    )
                )
            }

    }
    override suspend fun searchStudentAll( grade:String,result: (Resource<List<UserStudent>>) -> Unit) {
        val docRef = database.collection(grade)

        docRef.get()
            .addOnSuccessListener {
                val listOfStudents= arrayListOf<UserStudent>()
                for(rec in it){
                    val student =  rec.toObject(UserStudent::class.java)
                    listOfStudents.add(student)
                }
                result.invoke(
                    Resource.Success(listOfStudents)
                )
            }
            .addOnFailureListener{
                result.invoke(
                    Resource.Failure(
                        it.localizedMessage
                    )
                )
            }

    }
// ----------------------------------------------- search for student --------------------------------------------------------//


    // -------------------------------------------------------- add post -------------------------------------------------------//
    override suspend fun addGeneralPosts(posts: Posts, result: (Resource<String>) -> Unit) {
        val document = database.collection(FireStoreTable.post).document()
        posts.postID = document.id
        document.set(posts)
            .addOnSuccessListener {
                result.invoke(
                    Resource.Success(document.id)
                )
            }
            .addOnFailureListener {
                result.invoke(
                    Resource.Failure(
                        it.localizedMessage
                    )
                )
            }
    }

    override suspend fun addPersonalPosts(
        posts: Posts,
        userID: String,
        result: (Resource<String>) -> Unit
    ) {
        val document = database.collection(PostType.personal_posts).document(userID)
            .collection(FireStoreTable.post).document()
        posts.postID = document.id
        document.set(posts)
            .addOnSuccessListener {
                result.invoke(
                    Resource.Success(document.id)
                )
            }
            .addOnFailureListener {
                result.invoke(
                    Resource.Failure(
                        it.localizedMessage
                    )
                )
            }
    }

    override suspend fun addCoursePosts(
        posts: Posts,
        courseID: String,
        result: (Resource<String>) -> Unit
    ) {
        val document = database.collection(FireStoreTable.courses).document(courseID)
            .collection(FireStoreTable.post).document()
        posts.postID = document.id
        document.set(posts)
            .addOnSuccessListener {
                result.invoke(
                    Resource.Success(document.id)
                )
            }
            .addOnFailureListener {
                result.invoke(
                    Resource.Failure(
                        it.localizedMessage
                    )
                )
            }
    }

    override suspend fun addSectionPosts(
        posts: Posts,
        section: String,
        dep: String,
        result: (Resource<String>) -> Unit
    ) {
        val document =
            database.collection(PostType.section_posts).document(dep).collection(section).document()
        posts.postID = document.id
        document.set(posts)
            .addOnSuccessListener {
                result.invoke(
                    Resource.Success(document.id)
                )
            }
            .addOnFailureListener {
                result.invoke(
                    Resource.Failure(
                        it.localizedMessage
                    )
                )
            }
    }
// -------------------------------------------------------- add post -------------------------------------------------------//




    // -------------------------------------------------------- get post -------------------------------------------------------//
    override suspend fun getGeneralPosts(result: (Resource<List<Posts>>) -> Unit) {
        val docRef = database.collection(FireStoreTable.post)
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                result.invoke(Resource.Failure(e.toString()))
                return@addSnapshotListener
            }

            val listOfPosts = arrayListOf<Posts>()
            for (rec in snapshot!!) {
                val post = rec.toObject(Posts::class.java)
                listOfPosts.add(post)
            }
            result.invoke(Resource.Success(listOfPosts))
        }
    }

    override suspend fun getSectionPosts(
        sections: List<SectionData>,
        result: (Resource<List<Posts>>) -> Unit
    ) {
        val listOfPosts = arrayListOf<Posts>()
        for(section in sections){
        val document = database.collection(PostType.section_posts).document(section.dep).collection(section.sectionName)
        document.addSnapshotListener { snapshot, e ->
            if (e != null) {
                result.invoke(Resource.Failure(e.toString()))
                return@addSnapshotListener
            }


            for (rec in snapshot!!) {
                val post = rec.toObject(Posts::class.java)

                listOfPosts.add(post)
            }

        }
        }
        result.invoke(Resource.Success(listOfPosts))
    }

    override suspend fun getCoursePosts(
        courses: List<Courses>,
        result: (Resource<List<Posts>>) -> Unit
    ) {
        val listOfPosts = arrayListOf<Posts>()
        for (course in courses) {
            val document =
                database.collection(FireStoreTable.courses).document(course.courseCode).collection(
                    FireStoreTable.post
                )

            document.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    result.invoke(Resource.Failure(e.toString()))
                    return@addSnapshotListener
                }
                for (rec in snapshot!!) {
                    val post = rec.toObject(Posts::class.java)
                    listOfPosts.add(post)
                }
            }
        }
        result.invoke(Resource.Success(listOfPosts))
    }

    override suspend fun getPersonalPosts(
        userID: String,
        grade: String,
        result: (Resource<List<Posts>>) -> Unit
    ) {

        val docRef = database.collection(grade)
            .whereEqualTo("code", userID)
        docRef.get()
            .addOnSuccessListener {
                lateinit var student: UserStudent
                for (rec in it) {
                    student = rec.toObject(UserStudent::class.java)
                }
                val document = database.collection(PostType.personal_posts).document(student.userId)
                    .collection(
                        FireStoreTable.post
                    )

                document.addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        result.invoke(Resource.Failure(e.toString()))
                        return@addSnapshotListener
                    }

                    val listOfPosts = arrayListOf<Posts>()
                    for (rec in snapshot!!) {
                        val post = rec.toObject(Posts::class.java)
                        listOfPosts.add(post)
                    }
                    result.invoke(Resource.Success(listOfPosts))
                }

            }
            .addOnFailureListener {
                result.invoke(
                    Resource.Failure(
                        it.localizedMessage
                    )
                )
            }


    }

    // -------------------------------------------------------- update comment -------------------------------------------------------//
    override suspend fun updateCommentGeneralPosts(comment: Comment, postID: String, result: (Resource<String>) -> Unit) {
        val document=database.collection(FireStoreTable.post).document(postID).collection(
            FireStoreTable.comment).document(comment.commentID)
        document.set(comment)
            .addOnSuccessListener {
                result.invoke(
                    Resource.Success("comment updated successfully")
                )
            }
            .addOnFailureListener{
                result.invoke(
                    Resource.Failure(
                        it.localizedMessage
                    )
                )
            }
    }
    override suspend fun updateCommentSectionPosts(comment: Comment, postID: String, section: String, dep: String, result: (Resource<String>) -> Unit) {

        val document=database.collection(PostType.section_posts).document(dep).collection(section)
            .document(postID).collection(FireStoreTable.comment).document(comment.commentID)

        document.set(comment)
            .addOnSuccessListener {
                result.invoke(
                    Resource.Success("comment updated successfully")
                )
            }
            .addOnFailureListener{
                result.invoke(
                    Resource.Failure(
                        it.localizedMessage
                    )
                )
            }

    }
    override suspend fun updateCommentCoursePosts(comment: Comment, postID: String, courseID: String, result: (Resource<String>) -> Unit) {
        val document=database.collection(FireStoreTable.courses).document(courseID).collection(
            FireStoreTable.post)
            .document(postID).collection(FireStoreTable.comment).document(comment.commentID)
        document.set(comment)
            .addOnSuccessListener {
                result.invoke(
                    Resource.Success("comment updated successfully")
                )
            }
            .addOnFailureListener{
                result.invoke(
                    Resource.Failure(
                        it.localizedMessage
                    )
                )
            }
    }
    override suspend fun updateCommentPersonalPosts(comment: Comment, postID: String, userID: String, result: (Resource<String>) -> Unit) {
        val document=database.collection(PostType.personal_posts).document(userID).collection(
            FireStoreTable.post)
            .document(postID).collection(FireStoreTable.comment).document(comment.commentID)

        document.set(comment)
            .addOnSuccessListener {
                result.invoke(
                    Resource.Success("comment updated successfully")
                )
            }
            .addOnFailureListener{
                result.invoke(
                    Resource.Failure(
                        it.localizedMessage
                    )
                )
            }
    }
// -------------------------------------------------------- update comment -------------------------------------------------------//


    // -------------------------------------------------------- delete comment -------------------------------------------------------//
    override suspend fun deleteCommentGeneralPosts(comment: Comment, postID: String, result: (Resource<String>) -> Unit) {
        val document=database.collection(FireStoreTable.post).document(postID).collection(
            FireStoreTable.comment).document(comment.commentID)
        document.delete()
            .addOnSuccessListener {
                result.invoke(
                    Resource.Success("comment deleted successfully")
                )
            }
            .addOnFailureListener{
                result.invoke(
                    Resource.Failure(
                        it.localizedMessage
                    )
                )
            }
    }
    override suspend fun deleteCommentSectionPosts(comment: Comment, postID: String, section: String, dep: String, result: (Resource<String>) -> Unit) {

        val document=database.collection(PostType.section_posts).document(dep).collection(section)
            .document(postID).collection(FireStoreTable.comment).document(comment.commentID)

        document.delete()
            .addOnSuccessListener {
                result.invoke(
                    Resource.Success("comment deleted successfully")
                )
            }
            .addOnFailureListener{
                result.invoke(
                    Resource.Failure(
                        it.localizedMessage
                    )
                )
            }

    }
    override suspend fun deleteCommentCoursePosts(comment: Comment, postID: String, courseID: String, result: (Resource<String>) -> Unit) {
        val document=database.collection(FireStoreTable.courses).document(courseID).collection(
            FireStoreTable.post)
            .document(postID).collection(FireStoreTable.comment).document(comment.commentID)
        document.delete()
            .addOnSuccessListener {
                result.invoke(
                    Resource.Success("comment deleted successfully")
                )
            }
            .addOnFailureListener{
                result.invoke(
                    Resource.Failure(
                        it.localizedMessage
                    )
                )
            }
    }
    override suspend fun deleteCommentPersonalPosts(comment: Comment, postID: String, userID: String, result: (Resource<String>) -> Unit) {
        val document=database.collection(PostType.personal_posts).document(userID).collection(
            FireStoreTable.post)
            .document(postID).collection(FireStoreTable.comment).document(comment.commentID)

        document.delete()
            .addOnSuccessListener {
                result.invoke(
                    Resource.Success("comment deleted successfully")
                )
            }
            .addOnFailureListener{
                result.invoke(
                    Resource.Failure(
                        it.localizedMessage
                    )
                )
            }
    }
// -------------------------------------------------------- delete comment -------------------------------------------------------//



    // -------------------------------------------------------- add comment -------------------------------------------------------//
    override suspend fun addCommentGeneralPosts(comment: Comment, postID: String, result: (Resource<String>) -> Unit) {
        val document=database.collection(FireStoreTable.post).document(postID).collection(
            FireStoreTable.comment)
        comment.commentID=document.id
        document.add(comment)
            .addOnSuccessListener {
                result.invoke(
                    Resource.Success("comment added successfully")
                )
            }
            .addOnFailureListener{
                result.invoke(
                    Resource.Failure(
                        it.localizedMessage
                    )
                )
            }
    }
    override suspend fun addCommentSectionPosts(comment: Comment, postID: String, section: String, dep: String, result: (Resource<String>) -> Unit) {

        val document=database.collection(PostType.section_posts).document(dep).collection(section)
            .document(postID).collection(FireStoreTable.comment)
        comment.commentID=document.id
        document.add(comment)
            .addOnSuccessListener {
                result.invoke(
                    Resource.Success("comment added successfully")
                )
            }
            .addOnFailureListener{
                result.invoke(
                    Resource.Failure(
                        it.localizedMessage
                    )
                )
            }

    }
    override suspend fun addCommentCoursePosts(comment: Comment, postID: String, courseID: String, result: (Resource<String>) -> Unit) {
        val document=database.collection(FireStoreTable.courses).document(courseID).collection(
            FireStoreTable.post)
            .document(postID).collection(FireStoreTable.comment)
        comment.commentID=document.id
        document.add(comment)
            .addOnSuccessListener {
                result.invoke(
                    Resource.Success("comment added successfully")
                )
            }
            .addOnFailureListener{
                result.invoke(
                    Resource.Failure(
                        it.localizedMessage
                    )
                )
            }
    }
    override suspend fun addCommentPersonalPosts(comment: Comment, postID: String, userID: String, result: (Resource<String>) -> Unit) {
        val document=database.collection(PostType.personal_posts).document(userID).collection(
            FireStoreTable.post)
            .document(postID).collection(FireStoreTable.comment)
        comment.commentID=document.id
        document.add(comment)
            .addOnSuccessListener {
                result.invoke(
                    Resource.Success("comment added successfully")
                )
            }
            .addOnFailureListener{
                result.invoke(
                    Resource.Failure(
                        it.localizedMessage
                    )
                )
            }
    }
// -------------------------------------------------------- add comment -------------------------------------------------------//


    // -------------------------------------------------------- get comment -------------------------------------------------------//
    override suspend fun getCommentGeneralPosts(postID: String, result: (Resource<List<Comment>>) -> Unit) {
        val document=database.collection(FireStoreTable.post)
            .document(postID).collection(FireStoreTable.comment)

        document.addSnapshotListener { snapshot, e ->
            if (e != null) {
                result.invoke(Resource.Failure(e.toString()))
                return@addSnapshotListener
            }

            val listOfPosts= arrayListOf<Comment>()
            for (rec in snapshot!!){
                val post = rec.toObject(Comment::class.java)
                listOfPosts.add(post)
            }
            result.invoke(Resource.Success(listOfPosts))
        }

    }
    override suspend fun getCommentSectionPosts(postID: String, section: String, dep: String, result: (Resource<List<Comment>>) -> Unit) {
        val document=database.collection(PostType.section_posts).document(dep).collection(section)
            .document(postID).collection(FireStoreTable.comment)
        document.addSnapshotListener { snapshot, e ->
            if (e != null) {
                result.invoke(Resource.Failure(e.toString()))
                return@addSnapshotListener
            }

            val listOfPosts= arrayListOf<Comment>()
            for (rec in snapshot!!){
                val post = rec.toObject(Comment::class.java)
                listOfPosts.add(post)
            }
            result.invoke(Resource.Success(listOfPosts))
        }
    }
    override suspend fun getCommentCoursePosts(postID: String, courseID: String, result: (Resource<List<Comment>>) -> Unit) {
        val document=database.collection(FireStoreTable.courses).document(courseID).collection(
            FireStoreTable.post)
            .document(postID).collection(FireStoreTable.comment)
        document.addSnapshotListener { snapshot, e ->
            if (e != null) {
                result.invoke(Resource.Failure(e.toString()))
                return@addSnapshotListener
            }

            val listOfPosts= arrayListOf<Comment>()
            for (rec in snapshot!!){
                val post = rec.toObject(Comment::class.java)
                listOfPosts.add(post)
            }
            result.invoke(Resource.Success(listOfPosts))
        }


    }
    override suspend fun getCommentPersonalPosts(postID: String, userID: String, result: (Resource<List<Comment>>) -> Unit) {
        val document=database.collection(PostType.personal_posts).document(userID).collection(
            FireStoreTable.post)
            .document(postID).collection(FireStoreTable.comment)
        document.addSnapshotListener { snapshot, e ->
            if (e != null) {
                result.invoke(Resource.Failure(e.toString()))
                return@addSnapshotListener
            }

            val listOfPosts= arrayListOf<Comment>()
            for (rec in snapshot!!){
                val post = rec.toObject(Comment::class.java)
                listOfPosts.add(post)
            }
            result.invoke(Resource.Success(listOfPosts))
        }
    }
// -------------------------------------------------------- get comment -------------------------------------------------------//



    // -------------------------------------------------------- get teaching data -------------------------------------------------------//

// for teaching




    override suspend fun getLectureAttendance(

        lecture: Lecture,
        result: (Resource<List<Attendance>>) -> Unit
    ) {
        val document = database.collection(FireStoreTable.courses).document(lecture.courseCode)
            .collection(FireStoreTable.lectures)
            .document(lecture.dep)
            .collection(lecture.dep)
            .document(lecture.lectureId)
            .collection(FireStoreTable.attendance)


        document.get()
            .addOnSuccessListener {
                val listOfStudents = arrayListOf<Attendance>()
                for (rec in it) {
                    val student = rec.toObject(Attendance::class.java)
                    listOfStudents.add(student)
                }
                result.invoke(
                    Resource.Success(listOfStudents)
                )
            }
            .addOnFailureListener {
                result.invoke(
                    Resource.Failure(
                        it.localizedMessage
                    )
                )
            }
    }


    override suspend fun getSectionAttendance(

        section: Section,
        result: (Resource<List<Attendance>>) -> Unit
    ) {
        val document = database.collection(FireStoreTable.courses).document(section.courseCode)
            .collection(FireStoreTable.sections)
            .document(section.dep)
            .collection(section.section)
            .document(section.sectionId)
            .collection(FireStoreTable.attendance)
        document.get()
            .addOnSuccessListener {
                val listOfStudents = arrayListOf<Attendance>()
                for (rec in it) {
                    val student = rec.toObject(Attendance::class.java)
                    listOfStudents.add(student)
                }
                result.invoke(
                    Resource.Success(listOfStudents)
                )
            }
            .addOnFailureListener {
                result.invoke(
                    Resource.Failure(
                        it.localizedMessage
                    )
                )
            }
    }
    override suspend fun getCourseByProfessorCode( professorCode:String,result: (Resource<List<Courses>>) -> Unit) {
        val docRef = database.collection(FireStoreTable.courses)
            .whereEqualTo("professor", professorCode)
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                result.invoke(Resource.Failure(e.toString()))
                return@addSnapshotListener
            }

            val listOfPosts= arrayListOf<Courses>()
            for (rec in snapshot!!){
                val post = rec.toObject(Courses::class.java)
                listOfPosts.add(post)
            }
            result.invoke(Resource.Success(listOfPosts))
        }
    }

    override suspend fun updateSectionAttendance(
        attendance: Attendance,
        section: Section,
        result: (Resource<String>) -> Unit
    ) {
        val document = database.collection(FireStoreTable.courses)
            .document(section.courseCode)
            .collection(FireStoreTable.sections)
            .document(section.dep)
            .collection(section.section).document(section.sectionId).collection(FireStoreTable.attendance).document()
        attendance.attendanceID=document.id
        document.set(attendance)
            .addOnSuccessListener {
                result.invoke(
                    Resource.Success("attendance added successfully")
                )
            }
            .addOnFailureListener {
                result.invoke(
                    Resource.Failure(
                        it.localizedMessage
                    )
                )
            }
    }

    override suspend fun updateLectureAttendance(
        attendance: Attendance,
        lecture: Lecture,
        result: (Resource<String>) -> Unit
    ) {
        val document = database.collection(FireStoreTable.courses).document(lecture.courseCode)
            .collection(FireStoreTable.lectures)
            .document(lecture.dep)
            .collection(lecture.dep)
            .document(lecture.lectureId)
            .collection(FireStoreTable.attendance).document()
        attendance.attendanceID=document.id

        document.set(attendance)
            .addOnSuccessListener {
                result.invoke(
                    Resource.Success("attendance added successfully")
                )
            }
            .addOnFailureListener {
                result.invoke(
                    Resource.Failure(
                        it.localizedMessage
                    )
                )
            }
    }
    override suspend fun deleteSectionAttendance(
        attendance: Attendance,
        section: Section,
        result: (Resource<String>) -> Unit
    ) {
        val document = database.collection(FireStoreTable.courses)
            .document(section.courseCode)
            .collection(FireStoreTable.sections)
            .document(section.dep)
            .collection(section.section).document(section.sectionId).collection(FireStoreTable.attendance).document(attendance.attendanceID)
        document.delete()
            .addOnSuccessListener {
                result.invoke(
                    Resource.Success("attendance deleted successfully")
                )
            }
            .addOnFailureListener {
                result.invoke(
                    Resource.Failure(
                        it.localizedMessage
                    )
                )
            }
    }

    override suspend fun deleteLectureAttendance(
        attendance: Attendance,
        lecture: Lecture,
        result: (Resource<String>) -> Unit
    ) {
        val document = database.collection(FireStoreTable.courses).document(lecture.courseCode)
            .collection(FireStoreTable.lectures)
            .document(lecture.dep)
            .collection(lecture.dep)
            .document(lecture.lectureId)
            .collection(FireStoreTable.attendance).document(attendance.attendanceID)
        document.delete()
            .addOnSuccessListener {
                result.invoke(
                    Resource.Success("attendance deleted successfully")
                )
            }
            .addOnFailureListener {
                result.invoke(
                    Resource.Failure(
                        it.localizedMessage
                    )
                )
            }
    }
    override suspend fun getCourseByAssistantCode( assistantCode:String,result: (Resource<List<Courses>>) -> Unit) {
        val docRef = database.collection(FireStoreTable.courses)
            .whereEqualTo("learningAssistant", assistantCode)
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                result.invoke(Resource.Failure(e.toString()))
                return@addSnapshotListener
            }

            val listOfPosts= arrayListOf<Courses>()
            for (rec in snapshot!!){

                val post = rec.toObject(Courses::class.java)
                listOfPosts.add(post)
            }
            result.invoke(Resource.Success(listOfPosts))
        }
    }



    override suspend fun getSection(
      sections:List<SectionData>,
        result: (Resource<List<Section>>) -> Unit
    ) {
        val listOfPosts = arrayListOf<Section>()
        for (section in sections) {
            val docRef =
                database.collection(FireStoreTable.courses)
                    .document(section.courseCode)
                    .collection(FireStoreTable.sections)
                    .document(section.dep)
                    .collection(section.sectionName)
            docRef.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    result.invoke(Resource.Failure(e.toString()))
                    return@addSnapshotListener
                }
                for (rec in snapshot!!) {
                    val post = rec.toObject(Section::class.java)
                    listOfPosts.add(post)
                }

            }
        }
        result.invoke(Resource.Success(listOfPosts))

    }

    override  fun getLectures(courses: List<Courses>, dep:String, result: (Resource<List<Lecture>>) -> Unit) {
        val listOfPosts = arrayListOf<Lecture>()
        for (course in courses) {
            val docRef = database.
            collection(FireStoreTable.courses).
            document(course.courseCode)
                .collection(FireStoreTable.lectures)
                .document(dep).
                collection(dep)
            docRef.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    result.invoke(Resource.Failure(e.toString()))
                    return@addSnapshotListener
                }
                for (rec in snapshot!!) {
                    val post = rec.toObject(Lecture::class.java)
                    listOfPosts.add(post)
                }

            }
        }
        result.invoke(Resource.Success(listOfPosts))
    }




    override suspend fun updateLectureState(
        lecture: Lecture,
        state:Boolean,

        result: (Resource<String>) -> Unit
    ) {
        val document = database.collection(FireStoreTable.courses).document(lecture.courseCode)
            .collection(FireStoreTable.lectures).document(lecture.dep).collection(lecture.dep)
            .document(lecture.lectureId)
        document.update("hasRunning",state)
            .addOnSuccessListener {
                result.invoke(
                    Resource.Success("lectures updated successfully")
                )
            }
            .addOnFailureListener {
                result.invoke(
                    Resource.Failure(
                        it.localizedMessage
                    )
                )
            }

    }




    override suspend fun updateSectionState(
        section: Section,
        state:Boolean,
        result: (Resource<String>) -> Unit
    ) {
        val document = database.collection(FireStoreTable.courses)
            .document(section.courseCode)
            .collection(FireStoreTable.sections)
            .document(section.dep)
            .collection(section.section)
            .document(section.sectionId)

        document.update("hasRunning", state)
            .addOnSuccessListener {
                result.invoke(
                    Resource.Success("sections updated successfully")
                )
            }
            .addOnFailureListener {
                result.invoke(
                    Resource.Failure(
                        it.localizedMessage
                    )
                )
            }
    }

}
