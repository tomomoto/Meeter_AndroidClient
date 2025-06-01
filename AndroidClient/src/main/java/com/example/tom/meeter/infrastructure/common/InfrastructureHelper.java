package com.example.tom.meeter.infrastructure.common;

import android.os.Bundle;

public class InfrastructureHelper {

    private InfrastructureHelper() {
    }

    public static Bundle createBundle(String key, String value) {
        Bundle result = new Bundle();
        result.putString(key, value);
        return result;
    }
}
