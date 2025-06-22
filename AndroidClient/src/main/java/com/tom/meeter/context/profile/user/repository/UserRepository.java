package com.tom.meeter.context.profile.user.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;

import com.tom.meeter.context.network.dto.UserDTO;
import com.tom.meeter.context.profile.user.database.UserDao;
import com.tom.meeter.context.profile.user.domain.User;
import com.tom.meeter.context.user.service.UserService;

import java.time.LocalDate;
import java.util.concurrent.Executor;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import retrofit2.Response;

//@AppScope
@Deprecated
public class UserRepository {

    private static final String TAG = UserRepository.class.getCanonicalName();
    private static final Object MARKER = new Object();

    private final UserService userService;
    private final UserDao userDao;
    private final Executor executor;

    //@Inject
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
                  Response<UserDTO> response = userService.getUser(id).execute();
                  if (response.isSuccessful()) {
                      UserDTO body = response.body();
                      LocalDate birthday = body.getBirthday();
                      userDao.save(new User(
                            body.getId(), body.getName(), body.getGender().getValue(),
                            body.getSurname(), body.getInfo(),
                            birthday != null ? birthday.toString() : null));
                  } else {
                      Log.d(TAG, "Response is not succeed.");
                  }
              }))
              .doOnError(e -> Log.e(TAG, e.getMessage(), e))
              .subscribe());
    }
}
