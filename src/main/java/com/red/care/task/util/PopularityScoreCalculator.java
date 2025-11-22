package com.red.care.task.util;

import com.red.care.task.exception.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Component
@Slf4j
public class PopularityScoreCalculator {

    final static double STAR_WEIGHT = 0.5;
    final static double FORK_WEIGHT = 0.3;
    final static double RECENCY_WEIGHT = 0.2;

    public double calculatePopularityScore(final long starCount, final long forkCount, Date updatedAt) {
        // 1. Normalize star and fork counts logarithmically
        try {
            double starScore = Math.log1p(starCount) / Math.log1p(100_000);  // 100k stars as max
            double forkScore = Math.log1p(forkCount) / Math.log1p(20_000);   // 20k forks as max

            // 2. Compute recency (exponential decay - recently updates score higher)
            long daysOld = ChronoUnit.DAYS.between(
                    updatedAt.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                    LocalDate.now()
            );
            double recencyScore = Math.exp(-daysOld / 180.0); // half-life ≈ 125 days

            // 3. Weighted sum (already bounded 0–1)
            double total = (STAR_WEIGHT * starScore)
                    + (FORK_WEIGHT * forkScore)
                    + (RECENCY_WEIGHT * recencyScore);

            // 4. Round to 4 decimal places
            return Math.round(total * 10_000.0) / 10_000.0;
        } catch (Exception ex) {
            log.error("Failed to calculate popularity score", ex);
            throw new ApiException("Popularity Score Calculator Failed");
        }
    }
}
