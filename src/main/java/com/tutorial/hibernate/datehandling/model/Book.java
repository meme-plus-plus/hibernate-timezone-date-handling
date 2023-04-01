package com.tutorial.hibernate.datehandling.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.OffsetDateTime;
import java.util.Date;

@Entity
@Setter @Getter
@Table(name = "BOOK")
public class Book {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "title")
    private String title;

    @Column(name = "go_live_tsp")
    private OffsetDateTime goLiveDateTime;

    @Column(name = "completed_tsp")
    private Date completedDate;

    public String getDateStrings() {
        return " completedDate: [" + completedDate + "]" + " goLiveDateTime: [" + goLiveDateTime + "]";
    }

}
