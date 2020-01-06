/*
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.lym.wanandroid_kotlin.mvvm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.lym.wanandroid_kotlin.data.repository.IndexRepository
import org.lym.wanandroid_kotlin.data.repository.Repository
import org.lym.wanandroid_kotlin.data.repository.SearchRepository
import org.lym.wanandroid_kotlin.data.repository.UserRepository
import org.lym.wanandroid_kotlin.mvvm.viewmodel.HistoryViewModel
import org.lym.wanandroid_kotlin.mvvm.viewmodel.IndexViewModel
import org.lym.wanandroid_kotlin.mvvm.viewmodel.SearchResultViewModel
import org.lym.wanandroid_kotlin.mvvm.viewmodel.UserViewModel

/**
 * Factory for all ViewModels.
 */
@Suppress("UNCHECKED_CAST")
class ViewModelFactory constructor(
    private val repository: Repository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>) =
        with(modelClass) {
            when {
                isAssignableFrom(IndexViewModel::class.java) ->
                    IndexViewModel(repository as IndexRepository)
                isAssignableFrom(UserViewModel::class.java) ->
                    UserViewModel(repository as UserRepository)
                isAssignableFrom(HistoryViewModel::class.java) ->
                    HistoryViewModel(repository as SearchRepository)
                isAssignableFrom(SearchResultViewModel::class.java) ->
                    SearchResultViewModel(repository as SearchRepository)
                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}
