package com.techbookstore.app.exception;

/**
 * Exception thrown when a tech category is not found.
 */
public class TechCategoryNotFoundException extends RuntimeException {
    
    public TechCategoryNotFoundException(String categoryCode) {
        super("Tech category not found: " + categoryCode);
    }
}
