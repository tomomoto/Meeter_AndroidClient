package com.example.tom.meeter.context.user.repository;

import static com.example.tom.meeter.infrastructure.common.Constants.getAuthHeader;

import android.arch.lifecycle.LiveData;
import android.util.Log;

import com.example.tom.meeter.context.user.database.UserDao;
import com.example.tom.meeter.context.user.domain.User;
import com.example.tom.meeter.context.user.service.UserService;

import java.io.IOException;
import java.util.concurrent.Executor;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import retrofit2.Response;

@Singleton
public class UserRepository {

    private static final String TAG = UserRepository.class.getCanonicalName();
    private static final Object MARKER = new Object();

    private final UserService userService;
    private final UserDao userDao;
    private final Executor executor;

    @Inject
    public UserRepository(UserService userService, UserDao userDao, Executor executor) {
        this.userService = userService;
        this.userDao = userDao;
        this.executor = executor;
    }

    public LiveData<User> getUserLiveData(String id) {
        refreshUser(id);
        return userDao.loadLD(id);
    }

    private void refreshUser(String id) {
        executor.execute(() -> userDao.load(id)
              .flatMap(user -> Maybe.empty(), Maybe::error, () -> Maybe.just(MARKER))
              .flatMapCompletable(ign -> Completable.fromAction(() -> {
                  Response<User> response = userService.getUser(id).execute();
                  if (response.isSuccessful()) {
                      userDao.save(response.body());
                  } else {
                      Log.d(TAG, "Response is not succeed.");
                  }
              }))
              .doOnError(e -> Log.e(TAG, e.getMessage(), e))
              .subscribe());
    }

    private void refreshUserWithHeader(String token) {
        executor.execute(
              () -> {
                  Response<User> response = null;
                  try {
                      response = userService.getProfile(getAuthHeader(token))
                            .execute();
                  } catch (IOException e) {
                      throw new RuntimeException(e);
                  }
                  userDao.save(response.body());
              });
    }

}