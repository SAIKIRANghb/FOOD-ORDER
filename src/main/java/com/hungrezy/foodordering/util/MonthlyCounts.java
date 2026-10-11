package com.hungrezy.foodordering.util;

import com.hungrezy.foodordering.entity.BaseEntity;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Builds the [{month: "January", <key>: count}, ...] shape (all 12 months, zero-filled)
 * that the React admin graphs consume from /api/user/count and /api/restaurant/count.
 */
public final class MonthlyCounts {
    private static final String[] MONTHS = {
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
    };

    private MonthlyCounts() {
    }

    public static List<Map<String, Object>> of(List<? extends BaseEntity> entities, String key) {
        Map<String, Long> counts = new LinkedHashMap<>();
        for (String month : MONTHS) {
            counts.put(month, 0L);
        }
        for (BaseEntity entity : entities) {
            Instant createdAt = entity.getCreatedAt();
            if (createdAt == null) {
                continue;
            }
            String month = createdAt.atZone(ZoneOffset.UTC)
                    .getMonth()
                    .getDisplayName(TextStyle.FULL, Locale.ENGLISH);
            counts.merge(month, 1L, Long::sum);
        }
        List<Map<String, Object>> result = new ArrayList<>(12);
        for (String month : MONTHS) {
            Map<String, Object> row = new LinkedHashMap<>(2);
            row.put("month", month);
            row.put(key, counts.get(month));
            result.add(row);
        }
        return result;
    }
}
