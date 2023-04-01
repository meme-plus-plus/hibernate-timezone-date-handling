package com.tutorial.hibernate.datehandling;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.tutorial.hibernate.datehandling.dao.BookDao;
import com.tutorial.hibernate.datehandling.model.Book;
import org.hibernate.SessionFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Description;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

@RunWith(SpringJUnit4ClassRunner.class)
@EnableJpaRepositories
@ContextConfiguration
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class,
        DbUnitTestExecutionListener.class })
@DataJpaTest
@DatabaseSetup("classpath:data.xml")
public class DateHandlingTest {

    @Autowired
    private BookDao bookdao;

    @Autowired
    private JdbcTemplate template;

    @Autowired
    private SessionFactory session;

    @Test
    @Description("How Dates are translated")
    //Re-run with spring.jpa.properties.hibernate.jdbc.time_zone=UTC commented
    public void viewBookDates() {
        Book book =  bookdao.findById("1").get();

        //toString forms of Date/OffsetDateTime
        System.out.println(book.getDateStrings()); //Date - no offset Info (but still in utc) | offsetDateTime has offset info in context.
    }

    @Test
    @Description("Date Formatting without setting timezone")
    public void dateFormattingWithoutSettingTimezone() {
        Book book =  bookdao.findById("1").get();

        //ISO string translations without explicitly stating timeZone:
        System.out.println("completedDate: [" + getIsoStringFromDate(book.getCompletedDate(), false) + "]");
        System.out.println("goLiveDate:    [" + getIsoFromOffsetDate(book.getGoLiveDateTime(), false) + "]");
    }

    @Test
    @Description("Date Formatting with setting timezone") // <-- GOOD
    public void dateFormattingWithSettingTimezone() {
        Book book =  bookdao.findById("1").get();

        //ISO string translations without explicitly stating timeZone:
        System.out.println("completedDate: [" + getIsoStringFromDate(book.getCompletedDate(), true) + "]");
        System.out.println("goLiveDate:    [" + getIsoFromOffsetDate(book.getGoLiveDateTime(), true) + "]");
    }

    @Test
    @Description("Example of dates causing you some heart burn.")
    public void incorrectDateTranslations() throws JsonProcessingException {
//        Book book =  bookdao.findById("1").get();
        bookdao.findByTitle("The Confused");
        bookdao.findByTitle("The Confused");


        //Example - you have a web service, and you want to intercept how jackson serializes dates to provide a more clear view for your users.
        SimpleModule offsetDateTimeModule = new SimpleModule();
        offsetDateTimeModule.addSerializer(OffsetDateTime.class, new JsonSerializer<OffsetDateTime>() {
            @Override
            public void serialize(OffsetDateTime offsetDateTime, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
                jsonGenerator.writeString(getIsoFromOffsetDate(offsetDateTime, true)); // <-- NO timeZone set.
            }
        });

        ObjectMapper jacksonMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .registerModule(offsetDateTimeModule);

        System.out.println(jacksonMapper.writeValueAsString(bookdao.findById("1").get()));
    }


    private String getIsoStringFromDate(Date date, Boolean setUTCOnFormat) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"); // Quoted "Z" to indicate UTC, no timezone offset on dates.
        if(setUTCOnFormat) df.setTimeZone(TimeZone.getTimeZone("UTC"));
        return df.format(date);
    }

    private String getIsoFromOffsetDate(OffsetDateTime date, Boolean setUTCOnFormat) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
        if(setUTCOnFormat) df = df.withZone(ZoneOffset.UTC);
        return df.format(date);
    }
}
