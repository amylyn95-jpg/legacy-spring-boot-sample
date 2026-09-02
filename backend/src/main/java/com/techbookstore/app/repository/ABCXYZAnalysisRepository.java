package com.techbookstore.app.repository;

import com.techbookstore.app.entity.ABCXYZAnalysis;
import com.techbookstore.app.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ABCXYZAnalysisRepository extends JpaRepository<ABCXYZAnalysis, Long> {

    /**
     * Find latest analysis for a specific book
     */
    Optional<ABCXYZAnalysis> findTopByBookOrderByAnalysisDateDesc(Book book);

    /**
     * Find the analysis of a book for a specific date
     */
    Optional<ABCXYZAnalysis> findByBookAndAnalysisDate(Book book, LocalDate analysisDate);

    /**
     * Find analysis by date range
     */
    @Query("SELECT a FROM ABCXYZAnalysis a WHERE a.analysisDate BETWEEN :startDate AND :endDate ORDER BY a.analysisDate DESC")
    List<ABCXYZAnalysis> findByAnalysisDateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    /**
     * Find all analysis for a specific date
     */
    List<ABCXYZAnalysis> findByAnalysisDateOrderBySalesContributionDesc(LocalDate analysisDate);

    /**
     * Find by ABC category
     */
    @Query("SELECT a FROM ABCXYZAnalysis a WHERE a.abcCategory = :category AND a.analysisDate = :analysisDate")
    List<ABCXYZAnalysis> findByAbcCategoryAndAnalysisDate(@Param("category") String category, @Param("analysisDate") LocalDate analysisDate);

    /**
     * Find by XYZ category
     */
    @Query("SELECT a FROM ABCXYZAnalysis a WHERE a.xyzCategory = :category AND a.analysisDate = :analysisDate")
    List<ABCXYZAnalysis> findByXyzCategoryAndAnalysisDate(@Param("category") String category, @Param("analysisDate") LocalDate analysisDate);

    /**
     * Find by combined ABC/XYZ category
     */
    @Query("SELECT a FROM ABCXYZAnalysis a WHERE a.abcCategory = :abcCategory AND a.xyzCategory = :xyzCategory AND a.analysisDate = :analysisDate")
    List<ABCXYZAnalysis> findByCombinedCategoryAndAnalysisDate(@Param("abcCategory") String abcCategory, 
                                                              @Param("xyzCategory") String xyzCategory, 
                                                              @Param("analysisDate") LocalDate analysisDate);

    /**
     * Delete old analysis data
     */
    void deleteByAnalysisDateBefore(LocalDate cutoffDate);
}