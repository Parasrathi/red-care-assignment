package com.red.care.task.util;

import com.red.care.task.exception.ApiException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class PopularityScoreCalculatorTest {

    @Autowired
    private PopularityScoreCalculator popularityScoreCalculator;

    @Test
    public void happyPath() throws ParseException {
        Date date = new SimpleDateFormat("dd-MM-yyyy").parse("21-11-2025");
        double score = popularityScoreCalculator.calculatePopularityScore(10,10, date);
        assert (score >= 0);
    }

    @Test
    public void updatedAtNull_throwsException() throws ApiException {
        assertThrows(ApiException.class, () ->
                popularityScoreCalculator.calculatePopularityScore(10,10, null));
    }
}
