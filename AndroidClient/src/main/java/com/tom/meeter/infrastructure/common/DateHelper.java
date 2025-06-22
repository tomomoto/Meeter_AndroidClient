package com.tom.meeter.infrastructure.common;

import static com.tom.meeter.infrastructure.common.CommonHelper.EMPTY_STR;
import static com.tom.meeter.infrastructure.common.CommonHelper.UI_DATE_TIME_FORMAT;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.util.Log;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.Locale;

public final class DateHelper {

    private static final String TAG = DateHelper.class.getCanonicalName();
    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private DateHelper() {
    }

    public static String getAgeFromDateOld(String date) {
        if (date == null) {
            return "";
        }

        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        try {
            dob.setTime(FORMAT.parse(date));
        } catch (ParseException e) {
            Log.e(TAG, e.getLocalizedMessage(), e);
            return null;
        }

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }

        return String.valueOf(age);
    }


    public static String getAgeFromDate(LocalDate birthDate) {
        if (birthDate == null) {
            return EMPTY_STR;
        }
        try {
            int age = Period.between(birthDate, LocalDate.now()).getYears();
            return String.valueOf(age);
        } catch (DateTimeParseException e) {
            Log.e(TAG, "Error while parsing the date [" + birthDate + "] : " + e.getMessage(), e);
            return null;
        }
    }


    // Метод для отображения DatePickerDialog
    public static void showDatePickerDialog(Context ctx, final EditText targetEditText) {
        // Получаем текущую дату
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Создаем и показываем DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(ctx,
              (view, selectedYear, selectedMonth, selectedDay) -> {
                  // Устанавливаем выбранную дату в EditText
                  String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                  targetEditText.setText(selectedDate);
              }, year, month, day);

        // Показываем диалог
        datePickerDialog.show();
    }

    // Метод для отображения Material DatePicker
    public static void showMaterialDatePicker(
          AppCompatActivity activity, final EditText targetEditText) {
        // Создаём constraints (ограничения для выбора даты)
        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();
        Calendar calendar = Calendar.getInstance();
        constraintsBuilder.setValidator(DateValidatorPointForward.from(calendar.getTimeInMillis()));

        // Создаем Material DatePicker
        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
        builder.setCalendarConstraints(constraintsBuilder.build());
        builder.setTitleText("Select Date");

        MaterialDatePicker<Long> datePicker = builder.build();

        // Устанавливаем слушатель на выбор даты
        datePicker.addOnPositiveButtonClickListener(selection -> {
            // Форматируем выбранную дату
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.setTimeInMillis(selection);
            String selectedDateString = selectedDate.get(Calendar.DAY_OF_MONTH) + "/" +
                  (selectedDate.get(Calendar.MONTH) + 1) + "/" +
                  selectedDate.get(Calendar.YEAR);

            // Устанавливаем выбранную дату в поле
            targetEditText.setText(selectedDateString);
        });

        // Показываем диалог
        datePicker.show(activity.getSupportFragmentManager(), datePicker.toString());
    }

    public static void showDateTimePickerOld(Context ctx, EditText target) {
        final Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(
              ctx,
              (view, year, month, dayOfMonth) -> {
                  calendar.set(Calendar.YEAR, year);
                  calendar.set(Calendar.MONTH, month);
                  calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                  TimePickerDialog timePickerDialog = new TimePickerDialog(
                        ctx,
                        (timeView, hourOfDay, minute) -> {
                            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            calendar.set(Calendar.MINUTE, minute);

                            SimpleDateFormat sdf = new SimpleDateFormat(
                                  "yyyy-MM-dd HH:mm", Locale.getDefault());
                            String formatted = sdf.format(calendar.getTime());
                            target.setText(formatted);
                        },
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        true
                  );

                  timePickerDialog.show();
              },
              calendar.get(Calendar.YEAR),
              calendar.get(Calendar.MONTH),
              calendar.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.show();
    }

    public static void showDateTimePicker(Context ctx, EditText target) {
        LocalDate nowDate = LocalDate.now();
        LocalTime nowTime = LocalTime.now();

        DatePickerDialog datePickerDialog = new DatePickerDialog(
              ctx,
              (view, year, month, dayOfMonth) -> {
                  LocalDate selectedDate = LocalDate.of(year, month + 1, dayOfMonth);

                  TimePickerDialog timePickerDialog = new TimePickerDialog(
                        ctx,
                        (timeView, hourOfDay, minute) -> {
                            LocalTime selectedTime = LocalTime.of(hourOfDay, minute);
                            LocalDateTime dateTime = LocalDateTime.of(selectedDate, selectedTime);

                            String formatted = dateTime.format(UI_DATE_TIME_FORMAT);
                            target.setText(formatted);
                        },
                        nowTime.getHour(),
                        nowTime.getMinute(),
                        true
                  );

                  timePickerDialog.show();
              },
              nowDate.getYear(),
              nowDate.getMonthValue() - 1,
              nowDate.getDayOfMonth()
        );

        datePickerDialog.show();
    }
}
