package com.fundtastic.mymovie.view.ui.details.movie

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.fundtastic.mymovie.models.Keyword
import com.fundtastic.mymovie.models.Resource
import com.fundtastic.mymovie.models.Review
import com.fundtastic.mymovie.models.Video
import com.fundtastic.mymovie.repository.MovieRepository
import com.fundtastic.mymovie.utils.AbsentLiveData
import javax.inject.Inject
import timber.log.Timber

class MovieDetailViewModel @Inject constructor(
  private val repository: MovieRepository
) : ViewModel() {

  private val movieIdLiveData: MutableLiveData<Int> = MutableLiveData()
  val keywordListLiveData: LiveData<Resource<List<Keyword>>>
  val videoListLiveData: LiveData<Resource<List<Video>>>
  val reviewListLiveData: LiveData<Resource<List<Review>>>

  init {
    Timber.d("Injection MovieDetailViewModel")

    this.keywordListLiveData = movieIdLiveData.switchMap {
      movieIdLiveData.value?.let {
        repository.loadKeywordList(it)
      } ?: AbsentLiveData.create()
    }

    this.videoListLiveData = movieIdLiveData.switchMap {
      movieIdLiveData.value?.let {
        repository.loadVideoList(it)
      } ?: AbsentLiveData.create()
    }

    this.reviewListLiveData = movieIdLiveData.switchMap {
      movieIdLiveData.value?.let {
        repository.loadReviewsList(it)
      } ?: AbsentLiveData.create()
    }
  }

  fun postMovieId(id: Int) = movieIdLiveData.postValue(id)
}
