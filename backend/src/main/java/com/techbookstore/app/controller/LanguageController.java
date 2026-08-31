package com.techbookstore.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.support.RequestContextUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Locale;

/**
 * Controller for handling language switching.
 */
@Controller
public class LanguageController {
    
    @GetMapping("/language/switch")
    public String switchLanguage(@RequestParam String lang, 
                                HttpServletRequest request,
                                HttpServletResponse response) {
        LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
        if (localeResolver != null) {
            Locale locale = "en".equals(lang) ? Locale.ENGLISH : Locale.JAPANESE;
            localeResolver.setLocale(request, response, locale);
        }
        
        // Redirect to referer or home
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/");
    }
}