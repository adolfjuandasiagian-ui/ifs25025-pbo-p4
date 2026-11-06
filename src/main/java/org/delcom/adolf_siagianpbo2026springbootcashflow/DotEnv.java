package org.delcom.adolf_siagianpbo2026springbootcashflow;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Memuat berkas .env ke system properties sebelum Spring Boot dijalankan,
 * sehingga placeholder seperti ${APP_PORT:8080} pada application.yaml dapat terisi.
 * Nilai yang sudah ada di system property / environment tidak ditimpa.
 */
public final class DotEnv {

    private DotEnv() {
    }

    /** Memuat .env dari direktori kerja; diabaikan jika berkas tidak ada. */
    public static void load() throws IOException {
        load(Path.of(".env"));
    }

    public static void load(Path file) throws IOException {
        if (!Files.exists(file)) {
            return;
        }
        List<String> lines = Files.readAllLines(file);
        for (String raw : lines) {
            String line = raw.trim();
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }
            int idx = line.indexOf('=');
            if (idx <= 0) {
                continue;
            }
            String key = line.substring(0, idx).trim();
            String value = stripQuotes(line.substring(idx + 1).trim());
            if (System.getProperty(key) == null && System.getenv(key) == null) {
                System.setProperty(key, value);
            }
        }
    }

    private static String stripQuotes(String value) {
        if (value.length() >= 2) {
            char first = value.charAt(0);
            char last = value.charAt(value.length() - 1);
            if ((first == '"' && last == '"') || (first == '\'' && last == '\'')) {
                return value.substring(1, value.length() - 1);
            }
        }
        return value;
    }
}
