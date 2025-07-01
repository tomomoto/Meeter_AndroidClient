package com.tom.meeter.infrastructure.common;

import static com.tom.meeter.infrastructure.common.Globals.getServerPath;

import android.app.Application;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;

import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class RetrofitBuilder {

    private static final JavaTimeModule jtm = new JavaTimeModule();

    private static final Jdk8Module jdk8m = new Jdk8Module();
    //.addModule(new Jdk8Module().configureReadAbsentAsNull(false))

    private RetrofitBuilder() {
    }

    public static Retrofit createDefaultBuilder(Application app) {
        return createBuilder(app, Collections.singletonList(jtm));
    }

    public static Retrofit createBuilder(
          Application app, List<Module> modules) {
        JsonMapper.Builder builder = JsonMapper.builder();
        for (Module m : modules) {
            builder.addModule(m);
        }
        return new Retrofit.Builder()
              .baseUrl(getServerPath(app))
              .addConverterFactory(JacksonConverterFactory.create(
                    builder.build()
                          .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                          .setTimeZone(TimeZone.getDefault())))
              .build();
    }

    public static Retrofit createBuilder(Application app) {
        return createBuilder(app, Arrays.asList(jtm, jdk8m));
    }
}
