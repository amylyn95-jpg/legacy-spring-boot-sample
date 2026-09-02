package com.techbookstore.app.service;

import com.techbookstore.app.entity.ABCXYZAnalysis;
import com.techbookstore.app.entity.Book;
import com.techbookstore.app.entity.Inventory;
import com.techbookstore.app.repository.ABCXYZAnalysisRepository;
import com.techbookstore.app.repository.BookRepository;
import com.techbookstore.app.repository.InventoryRepository;
import com.techbookstore.app.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for ABC/XYZ Analysis implementation
 * ABC分析（売上貢献度別分類）とXYZ分析（需要変動性別分類）の実装
 */
@Service
@Transactional
public class ABCXYZAnalysisService {

    private static final Logger logger = LoggerFactory.getLogger(ABCXYZAnalysisService.class);

    private final ABCXYZAnalysisRepository abcxyzRepository;
    private final BookRepository bookRepository;
    private final InventoryRepository inventoryRepository;
    private final OrderRepository orderRepository;

    // ABC分析の閾値 (A: 20%, B: 60%, C: 20%)
    private static final double ABC_A_THRESHOLD = 0.20;
    private static final double ABC_B_THRESHOLD = 0.80;

    // XYZ分析の変動係数閾値 (X: <0.5, Y: 0.5-1.0, Z: >1.0)
    private static final double XYZ_X_THRESHOLD = 0.5;
    private static final double XYZ_Y_THRESHOLD = 1.0;
    
    // Analysis period constants
    private static final int ANALYSIS_PERIOD_MONTHS = 12;
    private static final BigDecimal MINIMUM_SALES_THRESHOLD = BigDecimal.valueOf(100);

    public ABCXYZAnalysisService(ABCXYZAnalysisRepository abcxyzRepository,
                                BookRepository bookRepository,
                                InventoryRepository inventoryRepository,
                                OrderRepository orderRepository) {
        this.abcxyzRepository = abcxyzRepository;
        this.bookRepository = bookRepository;
        this.inventoryRepository = inventoryRepository;
        this.orderRepository = orderRepository;
    }

    /**
     * Perform comprehensive ABC/XYZ analysis for all books
     * 全書籍に対してABC/XYZ分析を実行
     */
    public List<ABCXYZAnalysis> performAnalysis(LocalDate analysisDate) {
        if (analysisDate == null) {
            throw new IllegalArgumentException("Analysis date cannot be null");
        }
        
        logger.info("Starting ABC/XYZ analysis for date: {}", analysisDate);

        try {
            // Get all books with inventory
            List<Book> books = bookRepository.findAll();
            
            if (books.isEmpty()) {
                logger.warn("No books found for ABC/XYZ analysis");
                return new ArrayList<>();
            }
            
            // Calculate sales contribution for ABC analysis
            Map<Long, BigDecimal> salesContributions = calculateSalesContributions(books, analysisDate);
            
            // Calculate demand variability for XYZ analysis
            Map<Long, BigDecimal> demandVariabilities = calculateDemandVariabilities(books, analysisDate);
            
            // Perform ABC classification
            Map<Long, String> abcClassifications = performAbcAnalysis(salesContributions);
            
            // Perform XYZ classification
            Map<Long, String> xyzClassifications = performXyzAnalysis(demandVariabilities);
            
            // Create and save analysis results
            List<ABCXYZAnalysis> results = new ArrayList<>();
            
            for (Book book : books) {
                if (salesContributions.containsKey(book.getId())) {
                    try {
                        ABCXYZAnalysis analysis = abcxyzRepository
                            .findByBookAndAnalysisDate(book, analysisDate)
                            .orElse(null);

                        if (analysis == null) {
                            analysis = new ABCXYZAnalysis(
                                book,
                                abcClassifications.get(book.getId()),
                                xyzClassifications.get(book.getId()),
                                salesContributions.get(book.getId()),
                                demandVariabilities.get(book.getId()),
                                analysisDate
                            );
                        } else {
                            analysis.updateClassification(
                                abcClassifications.get(book.getId()),
                                xyzClassifications.get(book.getId()),
                                salesContributions.get(book.getId()),
                                demandVariabilities.get(book.getId())
                            );
                        }
                        
                        results.add(abcxyzRepository.save(analysis));
                    } catch (Exception e) {
                        logger.error("Failed to save ABC/XYZ analysis for book ID: {}", book.getId(), e);
                    }
                }
            }
            
            logger.info("Completed ABC/XYZ analysis for {} books", results.size());
            return results;
            
        } catch (Exception e) {
            logger.error("Failed to perform ABC/XYZ analysis for date: {}", analysisDate, e);
            throw new RuntimeException("ABC/XYZ analysis failed", e);
        }
    }

    /**
     * Calculate sales contribution for each book (for ABC analysis)
     * 各書籍の売上貢献度を計算（ABC分析用）
     */
    private Map<Long, BigDecimal> calculateSalesContributions(List<Book> books, LocalDate analysisDate) {
        Map<Long, BigDecimal> contributions = new HashMap<>();
        
        // Calculate total sales for the analysis period (last 12 months)
        LocalDate startDate = analysisDate.minusMonths(ANALYSIS_PERIOD_MONTHS);
        
        for (Book book : books) {
            try {
                // Mock implementation - in real system, calculate from order data
                BigDecimal bookSales = calculateBookSalesValue(book.getId(), startDate, analysisDate);
                
                // Only include books with sales above minimum threshold
                if (bookSales.compareTo(MINIMUM_SALES_THRESHOLD) >= 0) {
                    contributions.put(book.getId(), bookSales);
                }
            } catch (Exception e) {
                logger.warn("Failed to calculate sales contribution for book ID: {}", book.getId(), e);
                // Continue with other books
            }
        }
        
        // Calculate total sales
        BigDecimal totalSales = contributions.values().stream()
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Convert to percentage contributions
        if (totalSales.compareTo(BigDecimal.ZERO) > 0) {
            contributions.replaceAll((bookId, sales) -> 
                sales.divide(totalSales, 4, RoundingMode.HALF_UP)
                     .multiply(BigDecimal.valueOf(100)));
        } else {
            logger.warn("Total sales is zero for analysis period");
        }
        
        return contributions;
    }

    /**
     * Calculate demand variability for each book (for XYZ analysis)
     * 各書籍の需要変動性を計算（XYZ分析用）
     */
    private Map<Long, BigDecimal> calculateDemandVariabilities(List<Book> books, LocalDate analysisDate) {
        Map<Long, BigDecimal> variabilities = new HashMap<>();
        
        LocalDate startDate = analysisDate.minusMonths(12);
        
        for (Book book : books) {
            List<Integer> monthlyDemands = calculateMonthlyDemands(book.getId(), startDate, analysisDate);
            BigDecimal coefficientOfVariation = calculateCoefficientOfVariation(monthlyDemands);
            variabilities.put(book.getId(), coefficientOfVariation);
        }
        
        return variabilities;
    }

    /**
     * Perform ABC classification based on sales contributions
     * 売上貢献度に基づくABC分類を実行
     */
    private Map<Long, String> performAbcAnalysis(Map<Long, BigDecimal> salesContributions) {
        Map<Long, String> classifications = new HashMap<>();
        
        // Sort books by sales contribution (descending)
        List<Map.Entry<Long, BigDecimal>> sortedEntries = salesContributions.entrySet().stream()
            .sorted(Map.Entry.<Long, BigDecimal>comparingByValue().reversed())
            .collect(Collectors.toList());
        
        BigDecimal cumulativePercentage = BigDecimal.ZERO;
        
        for (Map.Entry<Long, BigDecimal> entry : sortedEntries) {
            cumulativePercentage = cumulativePercentage.add(entry.getValue());
            double cumulative = cumulativePercentage.doubleValue();
            
            String classification;
            if (cumulative <= ABC_A_THRESHOLD * 100) {
                classification = "A";
            } else if (cumulative <= ABC_B_THRESHOLD * 100) {
                classification = "B";
            } else {
                classification = "C";
            }
            
            classifications.put(entry.getKey(), classification);
        }
        
        return classifications;
    }

    /**
     * Perform XYZ classification based on demand variability
     * 需要変動性に基づくXYZ分類を実行
     */
    private Map<Long, String> performXyzAnalysis(Map<Long, BigDecimal> demandVariabilities) {
        Map<Long, String> classifications = new HashMap<>();
        
        for (Map.Entry<Long, BigDecimal> entry : demandVariabilities.entrySet()) {
            double variability = entry.getValue().doubleValue();
            
            String classification;
            if (variability < XYZ_X_THRESHOLD) {
                classification = "X";  // Stable demand
            } else if (variability < XYZ_Y_THRESHOLD) {
                classification = "Y";  // Variable demand
            } else {
                classification = "Z";  // Irregular demand
            }
            
            classifications.put(entry.getKey(), classification);
        }
        
        return classifications;
    }

    /**
     * Calculate book sales value for a period
     * 期間の書籍売上額を計算
     */
    private BigDecimal calculateBookSalesValue(Long bookId, LocalDate startDate, LocalDate endDate) {
        // Mock implementation - replace with actual order data query
        Random random = new Random(bookId);
        double salesValue = 10000 + random.nextDouble() * 50000; // Random sales between 10k-60k
        return BigDecimal.valueOf(salesValue).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calculate monthly demands for coefficient of variation
     * 変動係数計算用の月次需要を計算
     */
    private List<Integer> calculateMonthlyDemands(Long bookId, LocalDate startDate, LocalDate endDate) {
        // Mock implementation - replace with actual demand data
        List<Integer> demands = new ArrayList<>();
        Random random = new Random(bookId);
        
        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            int baseDemand = 10 + random.nextInt(20); // Base demand 10-30
            int variability = random.nextInt(10) - 5; // Variability -5 to +5
            demands.add(Math.max(0, baseDemand + variability));
            current = current.plusMonths(1);
        }
        
        return demands;
    }

    /**
     * Calculate coefficient of variation
     * 変動係数を計算
     */
    private BigDecimal calculateCoefficientOfVariation(List<Integer> values) {
        if (values.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        double mean = values.stream().mapToInt(Integer::intValue).average().orElse(0.0);
        
        if (mean == 0) {
            return BigDecimal.ZERO;
        }
        
        double variance = values.stream()
            .mapToDouble(v -> Math.pow(v - mean, 2))
            .average()
            .orElse(0.0);
        
        double standardDeviation = Math.sqrt(variance);
        double coefficientOfVariation = standardDeviation / mean;
        
        return BigDecimal.valueOf(coefficientOfVariation).setScale(4, RoundingMode.HALF_UP);
    }

    /**
     * Get latest analysis results
     * 最新の分析結果を取得
     */
    @Transactional(readOnly = true)
    public List<ABCXYZAnalysis> getLatestAnalysis() {
        LocalDate latestDate = LocalDate.now();
        return abcxyzRepository.findByAnalysisDateOrderBySalesContributionDesc(latestDate);
    }

    /**
     * Get analysis by category combinations
     * カテゴリ組み合わせ別の分析結果を取得
     */
    @Transactional(readOnly = true)
    public Map<String, List<ABCXYZAnalysis>> getAnalysisByCategory(LocalDate analysisDate) {
        Map<String, List<ABCXYZAnalysis>> categoryResults = new HashMap<>();
        
        String[] abcCategories = {"A", "B", "C"};
        String[] xyzCategories = {"X", "Y", "Z"};
        
        for (String abc : abcCategories) {
            for (String xyz : xyzCategories) {
                String combinedCategory = abc + xyz;
                List<ABCXYZAnalysis> results = abcxyzRepository
                    .findByCombinedCategoryAndAnalysisDate(abc, xyz, analysisDate);
                categoryResults.put(combinedCategory, results);
            }
        }
        
        return categoryResults;
    }
}