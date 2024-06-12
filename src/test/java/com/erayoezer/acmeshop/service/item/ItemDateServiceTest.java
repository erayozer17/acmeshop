package com.erayoezer.acmeshop.service.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ItemDateServiceTest {

    private ItemDateService itemDateService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        itemDateService = new ItemDateService();
    }

    @Test
    public void testGetDateRepresentation() throws ParseException {
        SimpleDateFormat OUTPUT_FORMAT = new SimpleDateFormat("dd MMMM yyyy");
        String dateString = "25 December 2022";
        Date date = new SimpleDateFormat("dd MMMM yyyy").parse(dateString);

        String result = itemDateService.getDateRepresentation(date);

        assertThat(result).isEqualTo(dateString);
    }

    @Test
    public void testSetWinterDateFromString() throws ParseException {
        String nextAt = "25 Dec 2024";
        String everydayAt = "08:00";
        String timeZone = "Europe/Berlin";
        String expectedResult = "2024-12-25 07:00:00.0";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
        Timestamp result = itemDateService.setDateFromString(nextAt, everydayAt, timeZone);
        String actualResult = dateFormat.format(new Date(result.getTime()));

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testSetSummerDateFromString() throws ParseException {
        String nextAt = "25 Jun 2024";
        String everydayAt = "08:00";
        String timeZone = "Europe/Berlin";
        String expectedResult = "2024-06-25 06:00:00.0";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
        Timestamp result = itemDateService.setDateFromString(nextAt, everydayAt, timeZone);
        String actualResult = dateFormat.format(new Date(result.getTime()));

        assertEquals(expectedResult, actualResult);
    }
}
