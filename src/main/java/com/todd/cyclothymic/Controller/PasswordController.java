package com.todd.cyclothymic.Controller;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.jdbc.core.JdbcTemplate;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class PasswordController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostMapping("/check-password")
    public ResponseEntity<Map<String, Object>> checkPassword(@RequestBody Map<String, String> request) {
        System.out.println("Richiesta ricevuta: " + request);
        
        String password = request.get("password");
        if (password == null) {
            return ResponseEntity.badRequest().body(Map.of("success", false));
        }

        try {
            String sql = "SELECT password FROM admin LIMIT 1";
            String storedPassword = jdbcTemplate.queryForObject(sql, String.class);
            
            boolean isValid = password.equals(storedPassword);
            System.out.println("Password confronto: " + isValid);
            
            return ResponseEntity.ok(Map.of("success", isValid));
            
        } catch (Exception e) {
            System.out.println("Errore: " + e.getMessage());
            return ResponseEntity.ok(Map.of("success", false));
        }
    }
}