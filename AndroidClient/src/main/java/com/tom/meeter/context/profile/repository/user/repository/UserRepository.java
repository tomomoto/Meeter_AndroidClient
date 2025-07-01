package com.tom.meeter.context.profile.repository.user.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;

import com.tom.meeter.context.network.dto.UserDTO;
import com.tom.meeter.context.profile.repository.user.database.UserDao;
import com.tom.meeter.context.profile.repository.user.domain.User;
import com.tom.meeter.context.profile.service.ProfileService;

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

    private final ProfileService service;
    private final UserDao userDao;
    private final Executor executor;

    //@Inject
    public UserRepository(
          ProfileService service, UserDao userDao, Executor executor) {
        this.service = service;
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
                  //TODO null
                  String header = null;
                  Response<UserDTO> response = service.getUser(header, id).execute();
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
