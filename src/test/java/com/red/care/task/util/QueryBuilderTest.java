package com.red.care.task.util;

import com.red.care.task.exception.ApiException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class QueryBuilderTest {
    
    @Test
    public void happyPath() throws Exception {
        Date date = new SimpleDateFormat("dd-MM-yyyy").parse("21-11-2025");
        String query = QueryBuilder.buildQueryParameter("java", date);
        assert (query.equals("language:java created:>=2025-11-21 forks:>0 stars:>0"));
    }

    @Test
    public void createdFromDateNull_throwsException() throws Exception {
        assertThrows(ApiException.class, () ->
                QueryBuilder.buildQueryParameter("java", null));
    }
}
