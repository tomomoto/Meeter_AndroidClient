package com.example.tom.meeter.context.user;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.example.tom.meeter.context.user.domain.User;
import com.example.tom.meeter.context.user.repository.UserRepository;

import javax.inject.Inject;

public class UserProfileViewModel extends ViewModel {

    private String userId;
    private LiveData<User> userLiveData;

    private final UserRepository userRepository;

    @Inject
    public UserProfileViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void init(String userId) {
        this.userId = userId;
        if (userLiveData == null) {
            userLiveData = userRepository.getUserLiveData(userId);
        }
    }

    public String getUserId() {
        return userId;
    }

    public LiveData<User> getUserLiveData() {
        return userLiveData;
    }

}
