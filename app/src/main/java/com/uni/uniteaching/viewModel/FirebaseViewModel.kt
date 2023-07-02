package com.uni.uniteaching.viewModel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uni.uniteaching.data.FirebaseRepo
import com.uni.uniteaching.data.Resource
import com.uni.uniteaching.classes.*
import com.uni.uniteaching.classes.user.UserStudent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FirebaseViewModel @Inject constructor(
    private val  repository: FirebaseRepo
): ViewModel() {

    private val _deletePost = MutableStateFlow<Resource<String>?>(null)
    val deletePost = _deletePost.asStateFlow()
    fun deletePostPersonal(postId: String, userId: String) = viewModelScope.launch {
        _deletePost.value = Resource.Loading
        repository.deletePersonalPosts(postId, userId) {
            _deletePost.value = it

        }
    }

    fun deletePostCourse(postId: String, courseId: String) = viewModelScope.launch {
        _deletePost.value = Resource.Loading
        repository.deleteCoursePosts(postId, courseId) {
            _deletePost.value = it

        }
    }

    fun deletePostSection(postId: String, dep: String, section: String) = viewModelScope.launch {
        _deletePost.value = Resource.Loading
        repository.deleteSectionPosts(postId, section, dep) {
            _deletePost.value = it

        }
    }

    fun deletePostGeneral(postId: String) = viewModelScope.launch {
        _deletePost.value = Resource.Loading
        repository.deleteGeneralPosts(postId) {
            _deletePost.value = it

        }
    }


    //-----------------------------------------------------------section data-------------------------------------------------------------//
    private val _getSectionData = MutableStateFlow<Resource<List<SectionData>>?>(null)
    val getSectionData = _getSectionData.asStateFlow()

    fun getSectionData(assistantID:String) = viewModelScope.launch {
        _getSectionData.value = Resource.Loading
        repository.getSectionData(assistantID) {
            _getSectionData.value = it

        }
    }
    //-----------------------------------------------------------courses data-------------------------------------------------------------//

    //-----------------------------------------------------------courses data-------------------------------------------------------------//

    private val _getCourse = MutableStateFlow<Resource<List<Courses>>?>(null)
    val getCourse = _getCourse.asStateFlow()

    fun getCoursesByProfessorID(professorID:String) = viewModelScope.launch {
        _getCourse.value = Resource.Loading
        repository.getCourseByProfessorCode(professorID) {
            _getCourse.value = it

        }
    }
    fun getCoursesByAssistantID(assistantID:String) = viewModelScope.launch {
        _getCourse.value = Resource.Loading
        repository.getCourseByAssistantCode(assistantID) {
            _getCourse.value = it

        }
    }
    //-----------------------------------------------------------courses data-------------------------------------------------------------//

    //-----------------------------------------------------------post-------------------------------------------------------------//

    private val _addPost = MutableStateFlow<Resource<String>?>(null)
    val addPost = _addPost.asStateFlow()

    fun addPostPersonal(posts: Posts, userId: String) = viewModelScope.launch {
        _addPost.value = Resource.Loading
        repository.addPersonalPosts(posts, userId) {
            _addPost.value = it

        }
    }

    fun addPostGeneral(posts: Posts) = viewModelScope.launch {
        _addPost.value = Resource.Loading
        repository.addGeneralPosts(posts) {
            _addPost.value = it

        }
    }

    fun addPostSection(posts: Posts, section: String, dep: String) = viewModelScope.launch {
        _addPost.value = Resource.Loading
        repository.addSectionPosts(posts, section, dep) {
            _addPost.value = it

        }
    }

    fun addPostCourse(posts: Posts, courseID: String) = viewModelScope.launch {
        _addPost.value = Resource.Loading
        repository.addCoursePosts(posts, courseID) {
            _addPost.value = it

        }
    }
    //-----------------------------------------------------------post-------------------------------------------------------------//


    //-----------------------------------------------------------comment-------------------------------------------------------------//
    private val _addCommentGeneral = MutableStateFlow<Resource<String>?>(null)
    val addCommentGeneral = _addCommentGeneral.asStateFlow()
    fun addCommentsPersonal(comment: Comment, postID: String, userId: String) =
        viewModelScope.launch {
            _addCommentGeneral.value = Resource.Loading
            repository.addCommentPersonalPosts(comment, postID, userId) {
                _addCommentGeneral.value = it

            }
        }

    fun addCommentsGeneral(comment: Comment, postID: String) = viewModelScope.launch {

        _addCommentGeneral.value = Resource.Loading
        repository.addCommentGeneralPosts(comment, postID) {
            _addCommentGeneral.value = it

        }
    }

    fun addCommentsSection(comment: Comment, postID: String, section: String, dep: String) =
        viewModelScope.launch {

            _addCommentGeneral.value = Resource.Loading
            repository.addCommentSectionPosts(comment, postID, section, dep) {
                _addCommentGeneral.value = it

            }
        }

    fun addCommentsCourse(comment: Comment, postID: String, courseID: String) =
        viewModelScope.launch {

            _addCommentGeneral.value = Resource.Loading
            repository.addCommentCoursePosts(comment, postID, courseID) {
                _addCommentGeneral.value = it

            }
        }


    fun updateCommentsPersonal(comment: Comment, postID: String, userId: String) =
        viewModelScope.launch {
            _addCommentGeneral.value = Resource.Loading
            repository.updateCommentPersonalPosts(comment, postID, userId) {
                _addCommentGeneral.value = it
            }
        }

    fun updateCommentsGeneral(comment: Comment, postID: String) = viewModelScope.launch {
        _addCommentGeneral.value = Resource.Loading
        repository.updateCommentGeneralPosts(comment, postID) {
            _addCommentGeneral.value = it
        }
    }

    fun updateCommentsSection(comment: Comment, postID: String, section: String, dep: String) =
        viewModelScope.launch {
            _addCommentGeneral.value = Resource.Loading
            repository.updateCommentSectionPosts(comment, postID, section, dep) {
                _addCommentGeneral.value = it
            }
        }

    fun updateCommentsCourse(comment: Comment, postID: String, courseID: String) =
        viewModelScope.launch {
            _addCommentGeneral.value = Resource.Loading
            repository.updateCommentCoursePosts(comment, postID, courseID) {
                _addCommentGeneral.value = it
            }
        }

    fun deleteCommentsPersonal(comment: Comment, postID: String, userId: String) =
        viewModelScope.launch {
            _addCommentGeneral.value = Resource.Loading
            repository.deleteCommentPersonalPosts(comment, postID, userId) {
                _addCommentGeneral.value = it
            }
        }

    fun deleteCommentsGeneral(comment: Comment, postID: String) = viewModelScope.launch {
        _addCommentGeneral.value = Resource.Loading
        repository.deleteCommentGeneralPosts(comment, postID) {
            _addCommentGeneral.value = it
        }
    }

    fun deleteCommentsSection(comment: Comment, postID: String, section: String, dep: String) =
        viewModelScope.launch {
            _addCommentGeneral.value = Resource.Loading
            repository.deleteCommentSectionPosts(comment, postID, section, dep) {
                _addCommentGeneral.value = it
            }
        }

    fun deleteCommentsCourse(comment: Comment, postID: String, courseID: String) =
        viewModelScope.launch {
            _addCommentGeneral.value = Resource.Loading
            repository.deleteCommentCoursePosts(comment, postID, courseID) {
                _addCommentGeneral.value = it
            }
        }


    private val _getCommentGeneral = MutableStateFlow<Resource<List<Comment>>?>(null)
    val getCommentGeneral = _getCommentGeneral.asStateFlow()
    fun getCommentsPersonal(postID: String, userId: String) = viewModelScope.launch {

        _getCommentGeneral.value = Resource.Loading
        repository.getCommentPersonalPosts(postID, userId) {
            _getCommentGeneral.value = it

        }
    }

    fun getCommentsGeneral(postID: String) = viewModelScope.launch {

        _getCommentGeneral.value = Resource.Loading
        repository.getCommentGeneralPosts(postID) {
            _getCommentGeneral.value = it

        }
    }

    fun getCommentsSection(postID: String, section: String, dep: String) = viewModelScope.launch {

        _getCommentGeneral.value = Resource.Loading
        repository.getCommentSectionPosts(postID, section, dep) {
            _getCommentGeneral.value = it

        }
    }

    fun getCommentsCourse(postID: String, courseID: String) = viewModelScope.launch {

        _getCommentGeneral.value = Resource.Loading
        repository.getCommentCoursePosts(postID, courseID) {
            _getCommentGeneral.value = it

        }
    }


//-----------------------------------------------------------comment-------------------------------------------------------------//


    private val _getSection = MutableStateFlow<Resource<List<Section>>?>(Resource.Loading)
    val getSection = _getSection.asStateFlow()
    private val _getLecture = MutableStateFlow<Resource<List<Lecture>>?>(null)
    val getLecture = _getLecture.asStateFlow()

    fun getLecture(courses: List<Courses>, dep: String) {
        repository.getLectures(courses, dep) {
            _getLecture.value = it
        }
    }

    fun getSection(sections:List<SectionData>) = viewModelScope.launch {
        _getSection.value = Resource.Loading
        repository.getSection(sections) {
            _getSection.value = it
        }
    }


    //-----------------------------------------------------------schedule-------------------------------------------------------------//


// -------------------------------------------------------- student data -------------------------------------------------------//

    private val _searchStudentByID = MutableStateFlow<Resource<UserStudent>?>(Resource.Loading)
    val searchStudentByID = _searchStudentByID.asStateFlow()
    fun searchStudentByID(grade: String, code: String) = viewModelScope.launch {
        _searchStudentByID.value = Resource.Loading
        repository.searchStudentByID(grade, code) {
            _searchStudentByID.value = it
        }
    }

    private val _searchStudentAll = MutableStateFlow<Resource<List<UserStudent>>?>(Resource.Loading)
    val searchStudentAll = _searchStudentAll.asStateFlow()
    fun searchStudentAll(grade: String) = viewModelScope.launch {
        _searchStudentAll.value = Resource.Loading
        repository.searchStudentAll(grade) {
            _searchStudentAll.value = it
        }
    }

    private val _searchStudentByDepartment =
        MutableStateFlow<Resource<List<UserStudent>>?>(Resource.Loading)
    val searchStudentByDepartment = _searchStudentByDepartment.asStateFlow()
    fun searchStudentByDepartment(grade: String, dep: String) = viewModelScope.launch {
        _searchStudentAll.value = Resource.Loading
        repository.searchStudentByDepartment(grade, dep) {
            _searchStudentAll.value = it
        }
    }

    private val _searchStudentBySection =
        MutableStateFlow<Resource<List<UserStudent>>?>(Resource.Loading)
    val searchStudentBySection = _searchStudentBySection.asStateFlow()
    fun searchStudentBySection(grade: String, dep: String, section: String) =
        viewModelScope.launch {
            _searchStudentAll.value = Resource.Loading
            repository.searchStudentBySection(grade, section, dep) {
                _searchStudentAll.value = it
            }
        }


// -------------------------------------------------------- student data -------------------------------------------------------//


    private val _getCourses = MutableStateFlow<Resource<List<Courses>>?>(null)
    val getCourses = _getCourses.asStateFlow()
    private val _getProfessor = MutableStateFlow<Resource<List<Professor>>?>(null)
    val getProfessor = _getProfessor.asStateFlow()
    private val _getAssistant = MutableStateFlow<Resource<List<Assistant>>?>(null)
    val getAssistant = _getAssistant.asStateFlow()

    //-----------------------------------------------------------------------------------------------------


    private val _getPosts = MutableStateFlow<Resource<List<Posts>>?>(null)
    val getPosts = _getPosts.asStateFlow()

    private val _getPostsCourses = MutableStateFlow<Resource<List<Posts>>?>(null)
    val getPostsCourses = _getPostsCourses.asStateFlow()

    private val _getSectionPost = MutableStateFlow<Resource<List<Posts>>?>(null)
    val getSectionPost = _getSectionPost.asStateFlow()

    fun getSectionPost(sections:List<SectionData>) = viewModelScope.launch {
        _getSectionPost.value = Resource.Loading
        repository.getSectionPosts(sections) {
            _getSectionPost.value = it
        }
    }
    fun getPostsGeneral() = viewModelScope.launch {
        _getPosts.value = Resource.Loading
        repository.getGeneralPosts() {
            _getPosts.value = it
        }
    }

    fun getPostsCourse(courses: List<Courses>) = viewModelScope.launch {
        _getPostsCourses.value = Resource.Loading
        repository.getCoursePosts(courses) {
            _getPostsCourses.value = it
        }
    }
//--------------------------------- attendance --------------------------------------------
    private val _getAttendance = MutableStateFlow<Resource<List<Attendance>>?>(null)
    val getAttendance = _getAttendance.asStateFlow()

    fun getSectionAttendance(section:Section) = viewModelScope.launch {
        _getAttendance.value = Resource.Loading
        repository.getSectionAttendance(section) {
            _getAttendance.value = it

        }
    }
    fun getLectureAttendance(lecture: Lecture) = viewModelScope.launch {
        _getAttendance.value = Resource.Loading
        repository.getLectureAttendance(lecture) {
            _getAttendance.value = it

        }
    }

    private val _addAttendance = MutableStateFlow<Resource<String>?>(null)
    val addAttendance = _addAttendance.asStateFlow()

    fun addSectionAttendance(section:Section,attendance: Attendance) = viewModelScope.launch {
        _addAttendance.value = Resource.Loading
        repository.updateSectionAttendance(attendance,section) {
            _addAttendance.value = it
        }
    }
    fun addLectureAttendance(lecture:Lecture,attendance: Attendance) = viewModelScope.launch {
        _addAttendance.value = Resource.Loading
        repository.updateLectureAttendance(attendance,lecture) {
            _addAttendance.value = it
        }
    }


    private val _deleteAttendance = MutableStateFlow<Resource<String>?>(null)
    val deleteAttendance = _deleteAttendance.asStateFlow()

    fun deleteSectionAttendance(section:Section,attendance: Attendance) = viewModelScope.launch {
        _deleteAttendance.value = Resource.Loading
        repository.deleteSectionAttendance(attendance,section) {
            _deleteAttendance.value = it
        }
    }
    fun deleteLectureAttendance(lecture:Lecture,attendance: Attendance) = viewModelScope.launch {
        _deleteAttendance.value = Resource.Loading
        repository.deleteLectureAttendance(attendance,lecture) {
            _deleteAttendance.value = it
        }
    }

}

