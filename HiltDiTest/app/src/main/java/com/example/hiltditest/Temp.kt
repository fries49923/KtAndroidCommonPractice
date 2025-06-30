package com.example.hiltditest

//import dagger.Module
//import dagger.Provides
//import dagger.hilt.InstallIn
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import javax.inject.Inject
//
//abstract class MainViewModelBase : ViewModel() {
//    protected val _data = MutableStateFlow(0)
//    val data: StateFlow<Int> = _data.asStateFlow()
//
//    abstract fun increment()
//}
//
//@HiltViewModel
//class MainViewModelA @Inject constructor() : MainViewModelBase() {
//    override fun increment() {
//        _data.value += 1
//    }
//}
//
//@HiltViewModel
//class MainViewModelB @Inject constructor() : MainViewModelBase() {
//    override fun increment() {
//        _data.value += 2
//    }
//}
//
//// fail
//@Module
//@InstallIn(ViewModelComponent::class)
//object ViewModelModule {
//
//    @Provides
//    fun provideMainViewModel() : MainViewModelBase = MainViewModelA()
//}