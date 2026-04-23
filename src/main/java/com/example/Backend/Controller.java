package com.example.Backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class Controller {

    @GetMapping("/id")
    public String getString() {
        return "sadfghasdfghjklölkjhgfdsdfghjklkjhgfdsdfghj";
    }
    @Autowired
    private JdbcTemplate jdbcTemplate; // Ett enkelt verktyg för att köra SQL

    @GetMapping("/db-test")
    public String testDbConnection() {
        try {
            // Vi kör en superenkel SQL-fråga som bara returnerar siffran 1
            Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            if (result != null && result == 1) {
                return "Databas-anslutningen fungerar! Svar från Supabase: " + result;
            } else {
                return "Kunde ansluta, men fick konstigt svar från databasen.";
            }
        } catch (Exception e) {
            return "Databas-fel: " + e.getMessage();
        }
    }

}
