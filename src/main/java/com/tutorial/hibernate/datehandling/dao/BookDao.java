package com.tutorial.hibernate.datehandling.dao;

import com.tutorial.hibernate.datehandling.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface
BookDao extends JpaRepository<Book, String> {
    public Book findByTitle(String title);
}
