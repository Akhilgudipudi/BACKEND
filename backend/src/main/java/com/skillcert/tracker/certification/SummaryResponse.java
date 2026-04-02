package com.skillcert.tracker.certification;

import java.util.List;

public record SummaryResponse(
        int total,
        long active,
        long expiringSoon,
        long expired,
        List<Certification> upcoming,
        List<Certification> recent,
        List<CategoryCount> categories
) {
}
