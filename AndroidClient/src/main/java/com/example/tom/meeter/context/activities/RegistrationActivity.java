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

  private static final String TAG = ProfileActivity.class.getCanonicalName();
  private static final String EMPTY_TEXT = "";
  private static final int CAMERA_REQUEST_CODE = 0;
  private static final int GALLERY_REQUEST_CODE = 1;

  @BindView(R.id.imageViewId)
  ImageView userPhoto;

  @BindView(R.id.nameEditTextId)
  TextView nameEditText;

  @BindView(R.id.surnameEditTextId)
  TextView surnameEditText;

  @BindView(R.id.radioGroupId)
  RadioGroup radioGroup;

  @BindView(R.id.registrationLoginId)
  TextView registrationLogin;

  @BindView(R.id.registrationPasswordId)
  TextView registrationPassword;

  @BindView(R.id.registrationRepeatPasswordId)
  TextView registrationRepeatPassword;

  @BindView(R.id.matchesId)
  TextView passwordsMatches;

  @BindView(R.id.registrationInfoId)
  TextView registrationInfo;

  @BindView(R.id.nextRegistrationId)
  Button nextRegistration;

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

  @OnClick(R.id.cameraButtonId)
  public void cameraClickHandler(Button btn) {
    Intent takePicture = new Intent(ACTION_IMAGE_CAPTURE);
    startActivityForResult(takePicture, CAMERA_REQUEST_CODE);
  }

  @OnClick(R.id.galleryButtonId)
  public void galleryClickHandler(Button btn) {
    Intent pickPhoto = new Intent(ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    startActivityForResult(pickPhoto, GALLERY_REQUEST_CODE);
  }

  @OnTextChanged(
      value = {R.id.registrationPasswordId, R.id.registrationRepeatPasswordId},
      callback = AFTER_TEXT_CHANGED
  )
  public void passwordsChangedHandler(Editable text) {
    CharSequence rpText = registrationPassword.getText();
    CharSequence rrpText = registrationRepeatPassword.getText();

    if (rpText == null || EMPTY_TEXT.equals(rpText.toString()) ||
        rrpText == null || EMPTY_TEXT.equals(rrpText.toString())) {
      passwordsMatches.setText(getString(R.string.enter_your_password));
      nextRegistration.setEnabled(false);
      return;
    }
    boolean matches = rpText.toString().equals(rrpText.toString());
    passwordsMatches.setText(matches ? "Matches" : "Doesn't matches");
    nextRegistration.setEnabled(matches);
  }

  @OnClick(R.id.nextRegistrationId)
  public void registerClickHandler(Button btn) {
    int checkedRadioButtonId = radioGroup.getCheckedRadioButtonId();

    String gender = null;
    switch (checkedRadioButtonId) {
      case R.id.radioButtonMale:
        gender = "male";
        break;
      case R.id.radioButtonFemale:
        gender = "female";
      default:
        break;
    }

    EventBus.getDefault().post(new RegistrationAttempt(
        nameEditText.getText().toString(),
        surnameEditText.getText().toString(),
        gender,
        registrationLogin.getText().toString(),
        registrationPassword.getText().toString(),
        registrationInfo.getText().toString(),
        null
    ));
  }

  protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
    super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
    switch (requestCode) {
      case CAMERA_REQUEST_CODE:
      case GALLERY_REQUEST_CODE:
        if (resultCode == RESULT_OK) {
          userPhoto.setImageURI(imageReturnedIntent.getData());
        }
      default:
        break;
    }
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onMessageEvent(RegistrationSuccess ev) {
    Log.d(TAG, ev.toString());
    Intent intent = new Intent(RegistrationActivity.this, ProfileActivity.class);
    intent.putExtra(USER_ID_KEY, ev.getUserId());
    startActivity(intent);
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
