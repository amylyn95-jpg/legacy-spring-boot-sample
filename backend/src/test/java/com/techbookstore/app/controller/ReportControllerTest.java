package com.techbookstore.app.controller;

import com.techbookstore.app.dto.CustomReportRequest;
import com.techbookstore.app.dto.SalesReportDto;
import com.techbookstore.app.service.ReportService;
import com.techbookstore.app.service.AnalyticsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.techbookstore.app.config.SecurityConfig;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest({ReportController.class, com.techbookstore.app.exception.GlobalExceptionHandler.class})
@Import(SecurityConfig.class)
class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReportService reportService;

    @MockitoBean
    private AnalyticsService analyticsService;

    @MockitoBean
    private com.techbookstore.app.service.CustomReportService customReportService;

    @MockitoBean
    private com.techbookstore.app.service.BatchProcessingService batchProcessingService;

    @Autowired
    private ObjectMapper objectMapper;

    private SalesReportDto mockSalesReport;

    @BeforeEach
    void setUp() {
        mockSalesReport = new SalesReportDto();
        mockSalesReport.setStartDate(LocalDate.now().minusDays(7));
        mockSalesReport.setEndDate(LocalDate.now());
        mockSalesReport.setTotalRevenue(new BigDecimal("10000.00"));
        mockSalesReport.setTotalOrders(100);
        mockSalesReport.setAverageOrderValue(new BigDecimal("100.00"));
    }

    @Test
    void getSalesReport_ValidDates_ReturnsSuccess() throws Exception {
        // Given
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();
        when(reportService.generateSalesReport(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(mockSalesReport);

        // When & Then
        mockMvc.perform(get("/api/v1/reports/sales")
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.totalRevenue").value(10000.00))
                .andExpect(jsonPath("$.totalOrders").value(100));
    }

    @Test
    void getSalesReport_InvalidDateRange_ReturnsBadRequest() throws Exception {
        // Given
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().minusDays(7);

        // When & Then
        mockMvc.perform(get("/api/v1/reports/sales")
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getSalesTrend_ValidDates_ReturnsSuccess() throws Exception {
        // Given
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();
        when(reportService.generateSalesTrendReport(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(mockSalesReport);

        // When & Then
        mockMvc.perform(get("/api/v1/reports/sales/trend")
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getSalesRanking_WithValidParameters_ReturnsSuccess() throws Exception {
        // Given
        when(reportService.generateSalesRankingReport(anyString(), anyInt()))
                .thenReturn(mockSalesReport);

        // When & Then
        mockMvc.perform(get("/api/v1/reports/sales/ranking")
                        .param("category", "Java")
                        .param("limit", "5"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getSalesRanking_WithInvalidLimit_ReturnsBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/reports/sales/ranking")
                        .param("limit", "150")) // Over max limit of 100
                .andExpect(status().isBadRequest());
    }

    @Test
    void generateCustomReport_ValidRequest_ReturnsSuccess() throws Exception {
        // Given
        CustomReportRequest request = new CustomReportRequest();
        request.setReportType("SALES");
        request.setStartDate(LocalDate.now().minusDays(7));
        request.setEndDate(LocalDate.now());
        
        when(reportService.generateCustomReport(any(CustomReportRequest.class)))
                .thenReturn(mockSalesReport);

        // When & Then
        mockMvc.perform(post("/api/v1/reports/custom")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void generateCustomReport_InvalidRequest_ReturnsBadRequest() throws Exception {
        // Given
        CustomReportRequest request = new CustomReportRequest();
        // Missing required reportType

        // When & Then
        mockMvc.perform(post("/api/v1/reports/custom")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}