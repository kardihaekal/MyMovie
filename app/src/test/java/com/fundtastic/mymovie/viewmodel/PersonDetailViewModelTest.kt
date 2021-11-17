package com.fundtastic.mymovie.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.fundtastic.mymovie.api.ApiUtil
import com.fundtastic.mymovie.api.PeopleService
import com.fundtastic.mymovie.models.Resource
import com.fundtastic.mymovie.models.network.PersonDetail
import com.fundtastic.mymovie.repository.PeopleRepository
import com.fundtastic.mymovie.room.PeopleDao
import com.fundtastic.mymovie.utils.MockTestUtil
import com.fundtastic.mymovie.utils.MockTestUtil.Companion.mockPerson
import com.fundtastic.mymovie.utils.MockTestUtil.Companion.mockPersonDetail
import com.fundtastic.mymovie.view.ui.details.person.PersonDetailViewModel
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class PersonDetailViewModelTest {

  private lateinit var viewModel: PersonDetailViewModel

  private lateinit var repository: PeopleRepository
  private val peopleDao = mock<PeopleDao>()
  private val service = mock<PeopleService>()

  @Rule
  @JvmField
  val instantExecutorRule = InstantTaskExecutorRule()

  @Before
  fun init() {
    repository = PeopleRepository(service, peopleDao)
    viewModel = PersonDetailViewModel(repository)
  }

  @Test
  fun loadPersonDetail() {
    val loadFromDB = mockPerson()
    whenever(peopleDao.getPerson(123)).thenReturn(loadFromDB)

    val mockResponse = mockPersonDetail()
    val call = ApiUtil.successCall(mockResponse)
    whenever(service.fetchPersonDetail(123)).thenReturn(call)

    val data = repository.loadPersonDetail(123)
    val observer = mock<Observer<Resource<PersonDetail>>>()
    data.observeForever(observer)

    viewModel.postPersonId(123)
    verify(peopleDao, times(3)).getPerson(123)
    verify(observer).onChanged(
      Resource.success(MockTestUtil.mockPersonDetail()))

    val updatedPerson = MockTestUtil.mockPerson()
    updatedPerson.personDetail = MockTestUtil.mockPersonDetail()
    verify(peopleDao).updatePerson(updatedPerson)
  }
}
