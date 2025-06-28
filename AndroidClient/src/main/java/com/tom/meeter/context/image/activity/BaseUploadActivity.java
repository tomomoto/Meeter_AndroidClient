package com.tom.meeter.context.image.activity;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.tom.meeter.App;
import com.tom.meeter.context.auth.infrastructure.AuthHelper;
import com.tom.meeter.context.image.service.ImageService;
import com.tom.meeter.databinding.ActivityUploadImageBinding;
import com.tom.meeter.infrastructure.http.HttpErrorLogger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.inject.Inject;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public abstract class BaseUploadActivity extends AppCompatActivity {
    protected static final int REQUEST_PICK_IMAGE = 100;
    public static final String PHOTO_PATH_RESULT = "photoPath";
    @Inject
    ImageService imageService;
    private ActivityUploadImageBinding binding;
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private AccountManager accountManager;

    protected Uri selectedImageUri;

    protected abstract String getUploadUrl();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityUploadImageBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        ((App) getApplication()).getComponent().inject(this);

        accountManager = AccountManager.get(this);

        pickImageLauncher = registerForActivityResult(
              new ActivityResultContracts.StartActivityForResult(),
              result -> {
                  if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                      Uri selectedImageUri = result.getData().getData();
                      binding.imagePreview.setImageURI(selectedImageUri);
                      binding.btnUploadImage.setEnabled(true);
                      this.selectedImageUri = selectedImageUri;
                  }
              }
        );

        binding.btnSelectImage.setOnClickListener(v -> selectImage());
        binding.btnUploadImage.setOnClickListener(v -> uploadImage(this, selectedImageUri));
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageLauncher.launch(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            binding.imagePreview.setImageURI(selectedImageUri);
            binding.btnUploadImage.setEnabled(true);
        }
    }

    public void uploadImage(Context context, Uri uri) {
        MultipartBody.Part imagePart;
        try {
            imagePart = createImagePartFromUri(context, uri);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Ошибка при обработке изображения", Toast.LENGTH_SHORT).show();
            return;
        }

        String auth = AuthHelper.getAuthHeader(accountManager);
        // Пример вызова API
        imageService.uploadImage(auth, getUploadUrl(), imagePart).enqueue(
              new HttpErrorLogger<>(this) {
                  @Override
                  public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                      if (response.isSuccessful() && response.body() != null) {
                          try {
                              String imagePath = response.body().string();
                              handleUploadedImagePath(imagePath);
                          } catch (IOException e) {
                              e.printStackTrace();
                              Toast.makeText(context, "Ошибка при чтении ответа", Toast.LENGTH_SHORT).show();
                          }
                      }
                  }
              });
    }

    private void handleUploadedImagePath(String imagePath) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(PHOTO_PATH_RESULT, imagePath);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    public static MultipartBody.Part createImagePartFromUri(Context context, Uri uri) throws IOException {
        ContentResolver contentResolver = context.getContentResolver();

        // 1. Получаем MIME-тип
        String mimeType = contentResolver.getType(uri);
        if (mimeType == null) {
            mimeType = "image/jpeg"; // по умолчанию
        }

        // 2. Получаем расширение из MIME-типа
        String extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType);
        if (extension == null) {
            extension = "jpg"; // fallback
        }

        // 3. Получаем имя файла
        String fileName = getFileName(context, uri);
        if (fileName == null || !fileName.contains(".")) {
            fileName = "upload." + extension;
        }

        // 4. Чтение содержимого файла
        InputStream inputStream = contentResolver.openInputStream(uri);
        if (inputStream == null) throw new IOException("Failed to open input stream");

        byte[] bytes = readBytes(inputStream);

        // 5. Создаём RequestBody и MultipartBody.Part
        RequestBody requestBody = RequestBody.create(bytes, MediaType.parse(mimeType));
        return MultipartBody.Part.createFormData("file", fileName, requestBody);
    }

    public static String getFileName(Context context, Uri uri) {
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        if (cursor != null) {
            int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            if (cursor.moveToFirst()) {
                String name = cursor.getString(nameIndex);
                cursor.close();
                return name;
            }
            cursor.close();
        }
        return "file.jpg";
    }

    public static byte[] readBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[8192]; // 8 KB буфер
        int nRead;
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        return buffer.toByteArray();
    }
}
