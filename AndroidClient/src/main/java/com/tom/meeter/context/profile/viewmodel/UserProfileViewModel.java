package com.tom.meeter.context.profile.viewmodel;

import static com.tom.meeter.infrastructure.common.InfrastructureHelper.logMethod;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.tom.meeter.context.profile.repository.user.domain.User;
import com.tom.meeter.context.profile.repository.user.repository.UserRepository;

@Deprecated
public class UserProfileViewModel extends ViewModel {

    private static final String TAG = UserProfileViewModel.class.getCanonicalName();

    private String userId;
    private LiveData<User> userLiveData;

    private final UserRepository userRepository;

    //@Inject
    public UserProfileViewModel(UserRepository userRepository) {
        logMethod(TAG, this);
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

    @Override
    protected void onCleared() {
        logMethod(TAG, this);
        super.onCleared();
    }
}
