package com.hospital.manpower.util;

import java.time.LocalDateTime;

public final class DateTimeUtil {

    private DateTimeUtil() {
    }

    public static LocalDateTime now() {
        return LocalDateTime.now();
    }
}
