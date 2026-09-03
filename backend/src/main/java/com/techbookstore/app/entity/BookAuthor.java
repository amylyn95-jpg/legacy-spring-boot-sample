package com.techbookstore.app.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "book_authors")
public class BookAuthor {

    @EmbeddedId
    private BookAuthorId id;

    @ManyToOne
    @MapsId("bookId")
    @JoinColumn(name = "book_id")
    private Book book;

    @ManyToOne
    @MapsId("authorId")
    @JoinColumn(name = "author_id")
    private Author author;

    @Enumerated(EnumType.STRING)
    @Column(name = "author_type", length = 20)
    private AuthorType authorType;

    @Column(name = "display_order")
    private Integer displayOrder;

    public enum AuthorType {
        AUTHOR, TRANSLATOR, SUPERVISOR
    }

    // Constructors
    public BookAuthor() {}

    public BookAuthor(Book book, Author author, AuthorType authorType) {
        this.book = book;
        this.author = author;
        this.authorType = authorType;
        this.id = new BookAuthorId(book.getId(), author.getId());
    }

    // Getters and Setters
    public BookAuthorId getId() {
        return id;
    }

    public void setId(BookAuthorId id) {
        this.id = id;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public AuthorType getAuthorType() {
        return authorType;
    }

    public void setAuthorType(AuthorType authorType) {
        this.authorType = authorType;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }
}