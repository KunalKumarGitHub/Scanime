package com.example.scanime.di

import com.example.scanime.ui.feature.login.LoginViewModel
import com.example.scanime.ui.feature.home.HomeViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module{
    viewModel{
        LoginViewModel(get())
    }
    viewModel{
        HomeViewModel(get(), get())
    }
}