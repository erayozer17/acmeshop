package com.erayoezer.acmeshop.service.item;

import com.erayoezer.acmeshop.model.Topic;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

@Service
public class ItemDateService {

    private static final SimpleDateFormat OUTPUT_FORMAT = new SimpleDateFormat("dd MMMM yyyy");
    public static final DateTimeFormatter DB_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    public String getDateRepresentation(Date nextAt) {
        return OUTPUT_FORMAT.format(nextAt);
    }

    public Timestamp setDateFromString(String nextAt, String everydayAt, String timeZone) throws ParseException {
        ZoneId zoneId = ZoneId.of(timeZone);
        LocalDateTime startDate = getDateForGivenDate(everydayAt, nextAt);
        ZonedDateTime zonedDateTime = startDate.atZone(zoneId);
        ZonedDateTime zonedDateTimeInGMT = zonedDateTime.withZoneSameInstant(ZoneId.of("GMT"));
        String formattedDate = zonedDateTimeInGMT.format(DB_DATE_FORMAT);
        return Timestamp.valueOf(formattedDate);
    }

    public Timestamp setDateFromString(LocalDateTime startDate, String timeZone) {
        ZoneId zoneId = ZoneId.of(timeZone);
        ZonedDateTime zonedDateTime = startDate.atZone(zoneId);
        ZonedDateTime zonedDateTimeInGMT = zonedDateTime.withZoneSameInstant(ZoneId.of("GMT"));
        String formattedDate = zonedDateTimeInGMT.format(DB_DATE_FORMAT);
        return Timestamp.valueOf(formattedDate);
    }

    private static LocalDateTime getDateForGivenDate(String everydayAt, String startDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");
        LocalDateTime processedStartDate = null;
        try {
            processedStartDate = LocalDateTime.parse(startDate, formatter);
        } catch (DateTimeParseException ex) {
            startDate += " 00:00";
            processedStartDate = LocalDateTime.parse(startDate, formatter);
        }
        if (everydayAt.isEmpty()) {
            everydayAt = "8:00";
        }
        String[] hourAndMinutes = everydayAt.split(":");
        return processedStartDate
                .withHour(
                        Integer.parseInt(hourAndMinutes[0])
                )
                .withMinute(
                        Integer.parseInt(hourAndMinutes[1])
                )
                .withSecond(0)
                .withNano(0);
    }

    public LocalDateTime getDateForGivenDate(Topic topic, String startDate) {
        return getDateForGivenDate(topic.getEverydayAt(), startDate);
    }
}
