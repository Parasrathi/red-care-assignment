package com.red.care.task.util;

import com.red.care.task.exception.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class QueryBuilder {
    private static final String LANGUAGE_FIELD = "language:";
    private static final String CREATED_FIELD_GREATER_THAN_EQUAL = "created:>=";
    private static final String FORKS_FIELD_GREATER_THAN_EQUAL = "forks:>";
    private static final String STARS_FIELD_GREATER_THAN_EQUAL = "stars:>";
    private static final int ZERO = 0;

    public static String buildQueryParameter(final String language, final Date createdFrom) {
        try {
            final LocalDate createdlocalDate = createdFrom.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();

            final List<String> queryParameters = new ArrayList<>();

            if (language != null && !language.isBlank()) {
                queryParameters.add(LANGUAGE_FIELD + language);
            }
            if (createdlocalDate != null) {
                queryParameters.add(CREATED_FIELD_GREATER_THAN_EQUAL + createdlocalDate);
            }

            //Assumption to filter out lowest score repositories
            queryParameters.add(FORKS_FIELD_GREATER_THAN_EQUAL + ZERO);
            queryParameters.add(STARS_FIELD_GREATER_THAN_EQUAL + ZERO);

            return String.join(" ", queryParameters);
        } catch (Exception ex) {
            log.error("Failed to build Query Parameter", ex);
            throw new ApiException("Query Builder Failed");
        }
    }
}
