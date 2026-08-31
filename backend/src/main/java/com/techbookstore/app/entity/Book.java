package com.techbookstore.app.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 13)
    private String isbn13;

    @Column(nullable = false)
    private String title;

    @Column(name = "title_en")
    private String titleEn;

    @ManyToOne
    @JoinColumn(name = "publisher_id")
    private Publisher publisher;

    @Column(name = "publication_date")
    private LocalDate publicationDate;

    private Integer edition;

    @Column(name = "list_price", precision = 10, scale = 2)
    private BigDecimal listPrice;

    @Column(name = "selling_price", precision = 10, scale = 2)
    private BigDecimal sellingPrice;

    private Integer pages;

    @Enumerated(EnumType.STRING)
    private TechLevel level;

    @Column(name = "version_info", length = 100)
    private String versionInfo;

    @Column(name = "sample_code_url", length = 500)
    private String sampleCodeUrl;

    public enum TechLevel {
        BEGINNER, INTERMEDIATE, ADVANCED
    }

    // Constructors
    public Book() {}

    public Book(String isbn13, String title) {
        this.isbn13 = isbn13;
        this.title = title;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getIsbn13() { return isbn13; }
    public void setIsbn13(String isbn13) { this.isbn13 = isbn13; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getTitleEn() { return titleEn; }
    public void setTitleEn(String titleEn) { this.titleEn = titleEn; }

    public Publisher getPublisher() { return publisher; }
    public void setPublisher(Publisher publisher) { this.publisher = publisher; }

    public LocalDate getPublicationDate() { return publicationDate; }
    public void setPublicationDate(LocalDate publicationDate) { this.publicationDate = publicationDate; }

    public Integer getEdition() { return edition; }
    public void setEdition(Integer edition) { this.edition = edition; }

    public BigDecimal getListPrice() { return listPrice; }
    public void setListPrice(BigDecimal listPrice) { this.listPrice = listPrice; }

    public BigDecimal getSellingPrice() { return sellingPrice; }
    public void setSellingPrice(BigDecimal sellingPrice) { this.sellingPrice = sellingPrice; }

    public Integer getPages() { return pages; }
    public void setPages(Integer pages) { this.pages = pages; }

    public TechLevel getLevel() { return level; }
    public void setLevel(TechLevel level) { this.level = level; }

    public String getVersionInfo() { return versionInfo; }
    public void setVersionInfo(String versionInfo) { this.versionInfo = versionInfo; }

    public String getSampleCodeUrl() { return sampleCodeUrl; }
    public void setSampleCodeUrl(String sampleCodeUrl) { this.sampleCodeUrl = sampleCodeUrl; }
}