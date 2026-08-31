package com.techbookstore.app.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "book_categories")
public class BookCategory {

    @EmbeddedId
    private BookCategoryId id;

    @ManyToOne
    @MapsId("bookId")
    @JoinColumn(name = "book_id")
    private Book book;

    @ManyToOne
    @MapsId("categoryId")
    @JoinColumn(name = "category_id")
    private TechCategory category;

    @Column(name = "is_primary")
    private Boolean isPrimary = false;

    // Constructors
    public BookCategory() {}

    public BookCategory(Book book, TechCategory category, Boolean isPrimary) {
        this.book = book;
        this.category = category;
        this.isPrimary = isPrimary;
        this.id = new BookCategoryId(book.getId(), category.getId());
    }

    // Getters and Setters
    public BookCategoryId getId() {
        return id;
    }

    public void setId(BookCategoryId id) {
        this.id = id;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public TechCategory getCategory() {
        return category;
    }

    public void setCategory(TechCategory category) {
        this.category = category;
    }

    public Boolean getIsPrimary() {
        return isPrimary;
    }

    public void setIsPrimary(Boolean isPrimary) {
        this.isPrimary = isPrimary;
    }
}