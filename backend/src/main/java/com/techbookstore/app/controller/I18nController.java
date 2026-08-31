package com.techbookstore.app.controller;

import com.techbookstore.app.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * REST API for internationalization support.
 */
@RestController
@RequestMapping("/api/v1/i18n")
@CrossOrigin(origins = "${app.cors.allowed-origins:http://localhost:3000}")
public class I18nController {
    
    private final MessageService messageService;
    
    @Autowired
    public I18nController(MessageService messageService) {
        this.messageService = messageService;
    }
    
    /**
     * Get messages for current locale.
     */
    @GetMapping("/messages")
    public Map<String, String> getMessages(HttpServletRequest request) {
        // Set locale based on Accept-Language header
        String acceptLanguage = request.getHeader("Accept-Language");
        Locale locale = Locale.JAPANESE; // default
        if (acceptLanguage != null && acceptLanguage.startsWith("en")) {
            locale = Locale.ENGLISH;
        }
        LocaleContextHolder.setLocale(locale);
        
        Map<String, String> messages = new HashMap<>();
        
        // Common labels
        messages.put("app.title", messageService.getMessage("app.title"));
        messages.put("app.welcome", messageService.getMessage("app.welcome"));
        messages.put("app.language", messageService.getMessage("app.language"));
        
        // Menu items
        messages.put("menu.dashboard", messageService.getMessage("menu.dashboard"));
        messages.put("menu.books", messageService.getMessage("menu.books"));
        messages.put("menu.orders", messageService.getMessage("menu.orders"));
        messages.put("menu.customers", messageService.getMessage("menu.customers"));
        messages.put("menu.reports", messageService.getMessage("menu.reports"));
        
        // Inventory management
        messages.put("inventory.title", messageService.getMessage("inventory.title"));
        messages.put("inventory.book.title", messageService.getMessage("inventory.book.title"));
        messages.put("inventory.available.stock", messageService.getMessage("inventory.available.stock"));
        messages.put("inventory.status", messageService.getMessage("inventory.status"));
        messages.put("inventory.location", messageService.getMessage("inventory.location"));
        messages.put("inventory.actions", messageService.getMessage("inventory.actions"));
        messages.put("inventory.receive", messageService.getMessage("inventory.receive"));
        messages.put("inventory.sell", messageService.getMessage("inventory.sell"));
        messages.put("inventory.status.outofstock", messageService.getMessage("inventory.status.outofstock"));
        messages.put("inventory.status.lowstock", messageService.getMessage("inventory.status.lowstock"));
        messages.put("inventory.status.instock", messageService.getMessage("inventory.status.instock"));
        messages.put("inventory.location.store", messageService.getMessage("inventory.location.store"));
        messages.put("inventory.location.warehouse", messageService.getMessage("inventory.location.warehouse"));
        
        // Customer management
        messages.put("customer.basic.info", messageService.getMessage("customer.basic.info"));
        messages.put("customer.email", messageService.getMessage("customer.email"));
        messages.put("customer.phone", messageService.getMessage("customer.phone"));
        messages.put("customer.birthdate", messageService.getMessage("customer.birthdate"));
        messages.put("customer.gender", messageService.getMessage("customer.gender"));
        messages.put("customer.gender.male", messageService.getMessage("customer.gender.male"));
        messages.put("customer.gender.female", messageService.getMessage("customer.gender.female"));
        messages.put("customer.gender.other", messageService.getMessage("customer.gender.other"));
        messages.put("customer.occupation.info", messageService.getMessage("customer.occupation.info"));
        messages.put("customer.occupation", messageService.getMessage("customer.occupation"));
        messages.put("customer.company", messageService.getMessage("customer.company"));
        messages.put("customer.department", messageService.getMessage("customer.department"));
        
        // Book management
        messages.put("book.title", messageService.getMessage("book.title"));
        messages.put("book.title.english", messageService.getMessage("book.title.english"));
        messages.put("book.publication.date", messageService.getMessage("book.publication.date"));
        messages.put("book.edition", messageService.getMessage("book.edition"));
        messages.put("book.list.price", messageService.getMessage("book.list.price"));
        messages.put("book.selling.price", messageService.getMessage("book.selling.price"));
        messages.put("book.version.info", messageService.getMessage("book.version.info"));
        messages.put("book.sample.code.url", messageService.getMessage("book.sample.code.url"));
        messages.put("book.detail.dialog.title", messageService.getMessage("book.detail.dialog.title"));
        messages.put("book.level.beginner", messageService.getMessage("book.level.beginner"));
        messages.put("book.level.intermediate", messageService.getMessage("book.level.intermediate"));
        messages.put("book.level.advanced", messageService.getMessage("book.level.advanced"));
        
        // Receive stock dialog
        messages.put("receive.dialog.title", messageService.getMessage("receive.dialog.title"));
        messages.put("receive.quantity", messageService.getMessage("receive.quantity"));
        messages.put("receive.location", messageService.getMessage("receive.location"));
        messages.put("receive.reason", messageService.getMessage("receive.reason"));
        messages.put("receive.delivery.note", messageService.getMessage("receive.delivery.note"));
        messages.put("receive.operation.success", messageService.getMessage("receive.operation.success"));
        
        // Forms
        messages.put("form.submit", messageService.getMessage("form.submit"));
        messages.put("form.cancel", messageService.getMessage("form.cancel"));
        messages.put("form.save", messageService.getMessage("form.save"));
        
        // Error messages
        messages.put("error.data.load.failed", messageService.getMessage("error.data.load.failed"));
        messages.put("error.server.connection", messageService.getMessage("error.server.connection"));
        messages.put("error.details", messageService.getMessage("error.details"));
        
        // Common actions
        messages.put("action.retry", messageService.getMessage("action.retry"));
        messages.put("actions", messageService.getMessage("actions"));
        
        // UI Labels
        messages.put("ui.close", messageService.getMessage("ui.close"));
        messages.put("ui.edit", messageService.getMessage("ui.edit"));
        messages.put("ui.detail", messageService.getMessage("ui.detail"));
        messages.put("ui.search", messageService.getMessage("ui.search"));
        messages.put("ui.search.keyword", messageService.getMessage("ui.search.keyword"));
        messages.put("ui.search.placeholder", messageService.getMessage("ui.search.placeholder"));
        messages.put("ui.page.title.books", messageService.getMessage("ui.page.title.books"));
        messages.put("ui.tab.basic.info", messageService.getMessage("ui.tab.basic.info"));
        messages.put("ui.tab.related.info", messageService.getMessage("ui.tab.related.info"));
        messages.put("ui.tab.inventory.info", messageService.getMessage("ui.tab.inventory.info"));
        messages.put("ui.field.title", messageService.getMessage("ui.field.title"));
        messages.put("ui.field.isbn13", messageService.getMessage("ui.field.isbn13"));
        messages.put("ui.field.publisher", messageService.getMessage("ui.field.publisher"));
        messages.put("ui.field.publication.date", messageService.getMessage("ui.field.publication.date"));
        messages.put("ui.field.edition", messageService.getMessage("ui.field.edition"));
        messages.put("ui.field.pages", messageService.getMessage("ui.field.pages"));
        messages.put("ui.field.level", messageService.getMessage("ui.field.level"));
        messages.put("ui.field.list.price", messageService.getMessage("ui.field.list.price"));
        messages.put("ui.field.selling.price", messageService.getMessage("ui.field.selling.price"));
        messages.put("ui.table.header.id", messageService.getMessage("ui.table.header.id"));
        messages.put("ui.table.header.title", messageService.getMessage("ui.table.header.title"));
        messages.put("ui.table.header.publisher", messageService.getMessage("ui.table.header.publisher"));
        messages.put("ui.table.header.publication.date", messageService.getMessage("ui.table.header.publication.date"));
        messages.put("ui.table.header.list.price", messageService.getMessage("ui.table.header.list.price"));
        messages.put("ui.table.header.level", messageService.getMessage("ui.table.header.level"));
        messages.put("ui.reload", messageService.getMessage("ui.reload"));
        
        // Reports
        messages.put("report.customers.under.development", messageService.getMessage("report.customers.under.development"));
        
        // Version Info and Description
        messages.put("book.version.info.label", messageService.getMessage("book.version.info.label"));
        messages.put("book.sample.code.url.label", messageService.getMessage("book.sample.code.url.label"));
        messages.put("book.description.label", messageService.getMessage("book.description.label"));
        
        // Future Development Messages
        messages.put("future.author.info", messageService.getMessage("future.author.info"));
        messages.put("future.author.info.description", messageService.getMessage("future.author.info.description"));
        messages.put("future.author.info.future", messageService.getMessage("future.author.info.future"));
        messages.put("future.author.profile", messageService.getMessage("future.author.profile"));
        messages.put("future.author.other.books", messageService.getMessage("future.author.other.books"));
        messages.put("future.author.experience", messageService.getMessage("future.author.experience"));
        
        messages.put("future.tech.category", messageService.getMessage("future.tech.category"));
        messages.put("future.tech.category.description", messageService.getMessage("future.tech.category.description"));
        messages.put("future.tech.category.future", messageService.getMessage("future.tech.category.future"));
        messages.put("future.tech.category.fields", messageService.getMessage("future.tech.category.fields"));
        messages.put("future.tech.category.keywords", messageService.getMessage("future.tech.category.keywords"));
        messages.put("future.tech.category.related", messageService.getMessage("future.tech.category.related"));
        
        messages.put("future.inventory.status", messageService.getMessage("future.inventory.status"));
        messages.put("future.inventory.status.description", messageService.getMessage("future.inventory.status.description"));
        messages.put("future.inventory.status.future", messageService.getMessage("future.inventory.status.future"));
        messages.put("future.inventory.store.stock", messageService.getMessage("future.inventory.store.stock"));
        messages.put("future.inventory.warehouse.stock", messageService.getMessage("future.inventory.warehouse.stock"));
        messages.put("future.inventory.total.stock", messageService.getMessage("future.inventory.total.stock"));
        messages.put("future.inventory.reserved.orders", messageService.getMessage("future.inventory.reserved.orders"));
        messages.put("future.inventory.planned.orders", messageService.getMessage("future.inventory.planned.orders"));
        messages.put("future.inventory.alerts", messageService.getMessage("future.inventory.alerts"));
        
        messages.put("future.sales.performance", messageService.getMessage("future.sales.performance"));
        messages.put("future.sales.performance.description", messageService.getMessage("future.sales.performance.description"));
        messages.put("future.sales.performance.future", messageService.getMessage("future.sales.performance.future"));
        messages.put("future.sales.monthly.quantity", messageService.getMessage("future.sales.monthly.quantity"));
        messages.put("future.sales.trend.graph", messageService.getMessage("future.sales.trend.graph"));
        messages.put("future.sales.popularity.ranking", messageService.getMessage("future.sales.popularity.ranking"));
        
        // UI Field Suffixes
        messages.put("ui.field.suffix.pages", messageService.getMessage("ui.field.suffix.pages"));
        
        // Reports Page Messages
        messages.put("report.title", messageService.getMessage("report.title"));
        messages.put("report.description", messageService.getMessage("report.description"));
        messages.put("report.show.button", messageService.getMessage("report.show.button"));
        messages.put("report.usage.title", messageService.getMessage("report.usage.title"));
        messages.put("report.usage.update", messageService.getMessage("report.usage.update"));
        messages.put("report.usage.processing", messageService.getMessage("report.usage.processing"));
        messages.put("report.usage.export", messageService.getMessage("report.usage.export"));
        
        // Report Types
        messages.put("report.sales.title", messageService.getMessage("report.sales.title"));
        messages.put("report.sales.description", messageService.getMessage("report.sales.description"));
        messages.put("report.sales.analysis.title", messageService.getMessage("report.sales.analysis.title"));
        messages.put("report.sales.analysis.description", messageService.getMessage("report.sales.analysis.description"));
        messages.put("report.inventory.title", messageService.getMessage("report.inventory.title"));
        messages.put("report.inventory.description", messageService.getMessage("report.inventory.description"));
        messages.put("report.inventory.analysis.title", messageService.getMessage("report.inventory.analysis.title"));
        messages.put("report.inventory.analysis.description", messageService.getMessage("report.inventory.analysis.description"));
        messages.put("report.customers.title", messageService.getMessage("report.customers.title"));
        messages.put("report.customers.description", messageService.getMessage("report.customers.description"));
        messages.put("report.tech.trends.title", messageService.getMessage("report.tech.trends.title"));
        messages.put("report.tech.trends.description", messageService.getMessage("report.tech.trends.description"));
        messages.put("report.dashboard.title", messageService.getMessage("report.dashboard.title"));
        messages.put("report.dashboard.description", messageService.getMessage("report.dashboard.description"));
        
        // Breadcrumbs
        messages.put("breadcrumb.dashboard", messageService.getMessage("breadcrumb.dashboard"));
        messages.put("breadcrumb.reports", messageService.getMessage("breadcrumb.reports"));
        
        // Dashboard
        messages.put("dashboard.title", messageService.getMessage("dashboard.title"));
        messages.put("dashboard.total.books", messageService.getMessage("dashboard.total.books"));
        messages.put("dashboard.low.stock", messageService.getMessage("dashboard.low.stock"));
        messages.put("dashboard.out.of.stock", messageService.getMessage("dashboard.out.of.stock"));
        messages.put("dashboard.recent.books", messageService.getMessage("dashboard.recent.books"));
        messages.put("dashboard.no.recent.books", messageService.getMessage("dashboard.no.recent.books"));
        messages.put("dashboard.inventory.alerts", messageService.getMessage("dashboard.inventory.alerts"));
        messages.put("dashboard.no.alerts", messageService.getMessage("dashboard.no.alerts"));
        messages.put("dashboard.no.alerts.description", messageService.getMessage("dashboard.no.alerts.description"));
        messages.put("dashboard.error.occurred", messageService.getMessage("dashboard.error.occurred"));
        
        // Order Management
        messages.put("order.management", messageService.getMessage("order.management"));
        messages.put("order.status.pending", messageService.getMessage("order.status.pending"));
        messages.put("order.status.confirmed", messageService.getMessage("order.status.confirmed"));
        messages.put("order.status.picking", messageService.getMessage("order.status.picking"));
        messages.put("order.status.shipped", messageService.getMessage("order.status.shipped"));
        messages.put("order.status.delivered", messageService.getMessage("order.status.delivered"));
        messages.put("order.status.cancelled", messageService.getMessage("order.status.cancelled"));
        messages.put("order.type.walk.in", messageService.getMessage("order.type.walk.in"));
        messages.put("order.type.online", messageService.getMessage("order.type.online"));
        messages.put("order.type.phone", messageService.getMessage("order.type.phone"));
        messages.put("order.action.confirm", messageService.getMessage("order.action.confirm"));
        messages.put("order.action.pick", messageService.getMessage("order.action.pick"));
        messages.put("order.action.ship", messageService.getMessage("order.action.ship"));
        messages.put("order.action.deliver", messageService.getMessage("order.action.deliver"));
        messages.put("order.details", messageService.getMessage("order.details"));
        
        // Inventory Rotation Matrix
        messages.put("inventory.rotation.matrix", messageService.getMessage("inventory.rotation.matrix"));
        messages.put("inventory.rotation.matrix.analysis", messageService.getMessage("inventory.rotation.matrix.analysis"));
        
        // Additional UI messages
        messages.put("ui.detail", messageService.getMessage("ui.detail"));
        messages.put("ui.no.data", messageService.getMessage("ui.no.data"));
        
        // Book levels
        messages.put("book.level.beginner", messageService.getMessage("book.level.beginner"));
        messages.put("book.level.intermediate", messageService.getMessage("book.level.intermediate"));
        messages.put("book.level.advanced", messageService.getMessage("book.level.advanced"));
        
        // Inventory Rotation Matrix Quadrants
        messages.put("inventory.rotation.quadrant.star", messageService.getMessage("inventory.rotation.quadrant.star"));
        messages.put("inventory.rotation.quadrant.star.description", messageService.getMessage("inventory.rotation.quadrant.star.description"));
        messages.put("inventory.rotation.quadrant.question", messageService.getMessage("inventory.rotation.quadrant.question"));
        messages.put("inventory.rotation.quadrant.question.description", messageService.getMessage("inventory.rotation.quadrant.question.description"));
        messages.put("inventory.rotation.quadrant.cash.cow", messageService.getMessage("inventory.rotation.quadrant.cash.cow"));
        messages.put("inventory.rotation.quadrant.cash.cow.description", messageService.getMessage("inventory.rotation.quadrant.cash.cow.description"));
        messages.put("inventory.rotation.quadrant.dog", messageService.getMessage("inventory.rotation.quadrant.dog"));
        messages.put("inventory.rotation.quadrant.dog.description", messageService.getMessage("inventory.rotation.quadrant.dog.description"));
        
        // Inventory Rotation Tooltip
        messages.put("inventory.rotation.tooltip.turnover", messageService.getMessage("inventory.rotation.tooltip.turnover"));
        messages.put("inventory.rotation.tooltip.last.sale", messageService.getMessage("inventory.rotation.tooltip.last.sale"));
        messages.put("inventory.rotation.tooltip.days.ago", messageService.getMessage("inventory.rotation.tooltip.days.ago"));
        messages.put("inventory.rotation.tooltip.stock", messageService.getMessage("inventory.rotation.tooltip.stock"));
        messages.put("inventory.rotation.tooltip.books", messageService.getMessage("inventory.rotation.tooltip.books"));
        messages.put("inventory.rotation.tooltip.category", messageService.getMessage("inventory.rotation.tooltip.category"));
        
        return messages;
    }
}