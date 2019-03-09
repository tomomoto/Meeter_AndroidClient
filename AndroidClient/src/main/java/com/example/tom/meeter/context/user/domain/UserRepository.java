package com.example.tom.meeter.context.user.domain;

import android.arch.lifecycle.LiveData;
import android.util.Log;

import java.util.concurrent.Executor;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import retrofit2.Response;

@Singleton
public class UserRepository {

    private static final String TAG = UserRepository.class.getCanonicalName();

    private final UserService userService;
    private final UserDao userDao;
    private final Executor executor;

    @Inject
    public UserRepository(UserService userService, UserDao userDao, Executor executor) {
        this.userService = userService;
        this.userDao = userDao;
        this.executor = executor;
    }

    public LiveData<User> getUser(String userId) {
        refreshUser(userId);
        return userDao.loadLD(userId);
    }

    private void refreshUser(final String userId) {
        executor.execute(() -> userDao.load(userId)
                .flatMap(user -> Maybe.empty(), Maybe::error, () -> Maybe.just(new Object()))
                .flatMapCompletable(ign -> Completable.fromAction(() -> {
                    Response<User> response = userService.getUser(userId).execute();
                    userDao.save(response.body());
                }))
                .doOnError(e -> Log.e(TAG, e.getMessage(), e))
                .subscribe());
    }

}