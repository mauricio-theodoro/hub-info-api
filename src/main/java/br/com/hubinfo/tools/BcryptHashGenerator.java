package br.com.hubinfo.tools;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BcryptHashGenerator {
    public static void main(String[] args) {
        String raw = (args != null && args.length > 0) ? args[0] : "Admin@123";
        var enc = new BCryptPasswordEncoder(12);
        System.out.println(enc.encode(raw));
    }
}
