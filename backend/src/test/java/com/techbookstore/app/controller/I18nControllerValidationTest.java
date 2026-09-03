package com.techbookstore.app.controller;

import com.techbookstore.app.service.MessageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import com.techbookstore.app.config.SecurityConfig;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@WebMvcTest(I18nController.class)
@Import(SecurityConfig.class)
class I18nControllerValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MessageService messageService;

    @BeforeEach
    void setUp() {
        // Mock message service to return test values
        when(messageService.getMessage(anyString())).thenAnswer(invocation -> {
            String key = invocation.getArgument(0);
            return "test-" + key; // Return a test value for any key
        });
    }

    @Test
    void testGetMessagesEndpointExists() throws Exception {
        mockMvc.perform(get("/api/v1/i18n/messages"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    void testEnglishLanguageHeader() throws Exception {
        mockMvc.perform(get("/api/v1/i18n/messages")
                .header("Accept-Language", "en"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    void testJapaneseLanguageHeader() throws Exception {
        mockMvc.perform(get("/api/v1/i18n/messages")
                .header("Accept-Language", "ja"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    void testRequiredMessageKeysAreExposed() throws Exception {
        List<String> requiredKeys = Arrays.asList(
                // Dashboard keys
                "dashboard.title",
                "dashboard.total.books",
                "dashboard.low.stock",
                "dashboard.out.of.stock",
                "dashboard.error.occurred",
                
                // Order management keys
                "order.management",
                "order.status.pending",
                "order.status.confirmed",
                "order.action.confirm",
                "order.action.pick",
                "order.action.ship",
                "order.action.deliver",
                
                // Report keys
                "report.title",
                "report.show.button",
                "report.sales.title",
                "report.inventory.title",
                "report.customers.title",
                
                // UI keys
                "ui.detail",
                "ui.no.data",
                
                // Book level keys
                "book.level.beginner",
                "book.level.intermediate",
                "book.level.advanced",
                
                // Inventory rotation matrix keys
                "inventory.rotation.matrix",
                "inventory.rotation.quadrant.star",
                "inventory.rotation.quadrant.question",
                "inventory.rotation.quadrant.cash.cow",
                "inventory.rotation.quadrant.dog"
        );

        for (String key : requiredKeys) {
            mockMvc.perform(get("/api/v1/i18n/messages"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$['" + key + "']").exists())
                    .andExpect(jsonPath("$['" + key + "']").value(startsWith("test-")));
        }
    }

    @Test
    void testBreadcrumbMessages() throws Exception {
        mockMvc.perform(get("/api/v1/i18n/messages"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$['breadcrumb.dashboard']").exists())
                .andExpect(jsonPath("$['breadcrumb.reports']").exists());
    }

    @Test
    void testInventoryRotationTooltipMessages() throws Exception {
        List<String> tooltipKeys = Arrays.asList(
                "inventory.rotation.tooltip.turnover",
                "inventory.rotation.tooltip.last.sale",
                "inventory.rotation.tooltip.days.ago",
                "inventory.rotation.tooltip.stock",
                "inventory.rotation.tooltip.books",
                "inventory.rotation.tooltip.category"
        );

        for (String key : tooltipKeys) {
            mockMvc.perform(get("/api/v1/i18n/messages"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$['" + key + "']").exists());
        }
    }

    @Test
    void testControllerHandlesNullAcceptLanguageHeader() throws Exception {
        // Test that controller doesn't crash when Accept-Language header is null
        mockMvc.perform(get("/api/v1/i18n/messages"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    void testControllerReturnsAllExpectedMessageKeys() throws Exception {
        // Verify that the controller returns a reasonable number of message keys
        // Based on our implementation, we should have 166+ keys
        mockMvc.perform(get("/api/v1/i18n/messages"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", aMapWithSize(greaterThan(160))));
    }
}