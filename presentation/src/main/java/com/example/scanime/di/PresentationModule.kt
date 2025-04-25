package com.example.scanime.di

import com.example.scanime.ScanimeSession
import org.koin.dsl.module

val presentationModule = module{
    includes(viewModelModule)
    single{ ScanimeSession(get()) }
}