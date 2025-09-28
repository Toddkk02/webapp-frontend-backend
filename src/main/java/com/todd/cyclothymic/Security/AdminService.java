package com.todd.cyclothymic.Security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public boolean checkPasswordSimple(String rawPassword) {
        try {
            String sql = "SELECT password FROM admin LIMIT 1";
            String storedPassword = jdbcTemplate.queryForObject(sql, String.class);
            
            if (storedPassword == null) {
                System.out.println("❌ Nessuna password trovata nel DB");
                return false;
            }
            
            System.out.println("🔍 Password DB: " + storedPassword);
            System.out.println("🔍 Password input: " + rawPassword);
            
            boolean result = rawPassword.equals(storedPassword);
            System.out.println("🔍 Confronto: " + result);
            
            return result;
            
        } catch (Exception e) {
            System.out.println("💥 Errore DB: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}