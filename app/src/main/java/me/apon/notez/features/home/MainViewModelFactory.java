/*
 * Copyright (C) 2017 The Android Open Source Project
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

package me.apon.notez.features.home;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import me.apon.notez.data.database.AppDatabase;
import me.apon.notez.data.database.dao.AccountDao;
import me.apon.notez.features.user.UserViewModel;


/**
 * Factory for ViewModels
 */
public class MainViewModelFactory implements ViewModelProvider.Factory {

    private final AppDatabase appDatabase;

    public MainViewModelFactory(AppDatabase appDatabase) {
        this.appDatabase = appDatabase;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MainViewModel.class)) {
            return (T) new MainViewModel(appDatabase);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
