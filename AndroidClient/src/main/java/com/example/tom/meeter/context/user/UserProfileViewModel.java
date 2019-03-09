package com.example.tom.meeter.context.user;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.example.tom.meeter.context.user.domain.User;
import com.example.tom.meeter.context.user.domain.UserRepository;

import javax.inject.Inject;

public class UserProfileViewModel extends ViewModel {

    private String userId;
    private LiveData<User> user;

    private UserRepository userRepository;

    @Inject
    public UserProfileViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void init(String userId) {
        if (user != null) {
            return;
        }
        user = userRepository.getUser(Integer.valueOf(userId));
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public LiveData<User> getUser() {
        return user;
    }

}
