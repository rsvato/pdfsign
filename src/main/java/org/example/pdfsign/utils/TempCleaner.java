package org.example.pdfsign.utils;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileFilter;
import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class TempCleaner {
    private static final Logger logger = LoggerFactory.getLogger(TempCleaner.class);
    @Value("${pdfsign.tempDir}")
    private String tempDir;
    @Value("${pdfsign.maxAge}")
    private Duration maxAge;
    private final ScheduledExecutorService service = Executors.newScheduledThreadPool(1);

    @PostConstruct
    public void init() {
        service.scheduleAtFixedRate(this::cleanOldFiles, 30, 1000, TimeUnit.MILLISECONDS);
    }

    @PreDestroy
    public void destroy() {
        service.shutdown();
    }

    private void cleanOldFiles() {
        long now = System.currentTimeMillis();
        File tempDirectory = new File(tempDir);
        File[] pdfs = tempDirectory.listFiles(pathname -> pathname.getName().endsWith("pdf"));
        if (pdfs != null) {
            for (File pdf : pdfs) {
                deleteIfNeeded(now, pdf);
            }
        }
    }

    private void deleteIfNeeded(long now, File pdf) {
        if (pdf.exists()) {
            long modified = pdf.lastModified();
            if (now - modified >= maxAge.toMillis()) {
                logger.info("Trying to delete {}", pdf.getAbsolutePath());
                boolean delete = pdf.delete();
                if (!delete) {
                    logger.warn("Failed to delete {}", pdf.getAbsolutePath());
                }
            }
        }
    }
}
