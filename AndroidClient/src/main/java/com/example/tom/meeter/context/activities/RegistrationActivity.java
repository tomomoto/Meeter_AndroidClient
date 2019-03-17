package com.example.tom.meeter.context.activities;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.tom.meeter.R;
import com.example.tom.meeter.context.network.domain.RegistrationAttempt;
import com.example.tom.meeter.context.network.domain.RegistrationFailed;
import com.example.tom.meeter.context.network.domain.RegistrationSuccess;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

import static android.content.Intent.ACTION_PICK;
import static android.provider.MediaStore.ACTION_IMAGE_CAPTURE;
import static butterknife.OnTextChanged.Callback.AFTER_TEXT_CHANGED;
import static com.example.tom.meeter.infrastructure.common.Constants.USER_ID_KEY;

/**
 * Created by Tom on 01.12.2016.
 */
public class RegistrationActivity extends AppCompatActivity {

  private static final String TAG = RegistrationActivity.class.getCanonicalName();
  private static final String EMPTY_TEXT = "";
  private static final int CAMERA_REQUEST_CODE = 0;
  private static final int GALLERY_REQUEST_CODE = 1;

  /**
   * Resolves gender based on gender radio button value.
   */
  private static String resolveGender(int radio) {
    switch (radio) {
      case R.id.regRadioBtnMale:
        return "male";
      case R.id.regRadioBtnFemale:
        return "female";
      default:
        throw new IllegalArgumentException("#args - radio: " + radio);
    }
  }

  @BindView(R.id.regImageView)
  ImageView userImage;

  @BindView(R.id.regNameEditText)
  EditText name;

  @BindView(R.id.regSurnameEditText)
  EditText surname;

  @BindView(R.id.regGenderRadioGroup)
  RadioGroup gender;

  @BindView(R.id.regLoginEditText)
  EditText login;

  @BindView(R.id.regPasswordEditText)
  EditText password;

  @BindView(R.id.regRepeatPasswordEditText)
  EditText repeatPassword;

  @BindView(R.id.regMatchesEditText)
  TextView passwordsMatches;

  @BindView(R.id.regInfoEditText)
  EditText info;

  @BindView(R.id.regRegisterBtn)
  Button register;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.registration_activity);
    ButterKnife.bind(this);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  protected void onStart() {
    super.onStart();
    EventBus.getDefault().register(this);
    Log.d(TAG, "Event bus registered...");
  }

  @Override
  protected void onStop() {
    super.onStop();
    EventBus.getDefault().unregister(this);
    Log.d(TAG, "Event bus unregistered...");
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();

    if (id == R.id.action_settings) {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  @OnClick(R.id.registration_camera_btn_id)
  public void cameraClickHandler(Button btn) {
    startActivityForResult(new Intent(ACTION_IMAGE_CAPTURE), CAMERA_REQUEST_CODE);
  }

  @OnClick(R.id.registration_gallery_btn_id)
  public void galleryClickHandler(Button btn) {
    startActivityForResult(new Intent(ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI), GALLERY_REQUEST_CODE);
  }

  @OnTextChanged(value = {R.id.regPasswordEditText, R.id.regRepeatPasswordEditText}, callback = AFTER_TEXT_CHANGED)
  public void passwordsChangedHandler(Editable text) {
    CharSequence pass = password.getText();
    CharSequence repeatPass = repeatPassword.getText();

    if (pass == null || EMPTY_TEXT.equals(pass.toString())
        || repeatPass == null || EMPTY_TEXT.equals(repeatPass.toString())) {
      passwordsMatches.setText(getString(R.string.enter_your_password));
      register.setEnabled(false);
      return;
    }
    boolean matches = pass.toString().equals(repeatPass.toString());
    passwordsMatches.setText(matches ? getString(R.string.matches) : getString(R.string.does_not_match));
    register.setEnabled(matches);
  }

  @OnClick(R.id.regRegisterBtn)
  public void registerClickHandler(Button btn) {
    EventBus.getDefault().post(new RegistrationAttempt(name.getText().toString(), surname.getText().toString(),
        resolveGender(gender.getCheckedRadioButtonId()), login.getText().toString(), password.getText().toString(),
        info.getText().toString(), null//TODO: set datebirth here.
    ));
  }

  protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
    super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
    switch (requestCode) {
      case CAMERA_REQUEST_CODE:
      case GALLERY_REQUEST_CODE:
        if (resultCode == RESULT_OK) {
          userImage.setImageURI(imageReturnedIntent.getData());
        }
      default:
        break;
    }
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onMessageEvent(RegistrationSuccess ev) {
    Log.d(TAG, ev.toString());
    startActivity(new Intent(RegistrationActivity.this, ProfileActivity.class)
        .putExtra(USER_ID_KEY, ev.getUserId()));
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onMessageEvent(RegistrationFailed ev) {
    Log.d(TAG, ev.toString());
    new AlertDialog.Builder(RegistrationActivity.this)
        .setTitle(getString(R.string.registration_failure))
        .setMessage(getString(R.string.wrong_credentials))
        .setNegativeButton(getString(R.string.ok), (dialog, id) -> dialog.cancel())
        .create()
        .show();
  }
}
