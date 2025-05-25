package com.owais.kmeans.controller;

import com.owais.kmeans.model.Product;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.owais.kmeans.util.CsvUtils;

import java.io.*;
import java.util.*;

@RestController
@RequestMapping("/api")
public class FileUploadController {

	
    @PostMapping("/upload")
    public ResponseEntity<?> handleFileUpload(@RequestParam("file") MultipartFile file) throws Exception {
        String contentType = file.getContentType();
        InputStream inputStream;

        
        // Always use CSV
        // XLSX is converted to CSV
        // No Other file types accepted
        if (contentType != null && (contentType.contains("excel") || file.getOriginalFilename().endsWith(".xlsx"))) {
            
            inputStream = CsvUtils.convertExcelToCSV(file.getInputStream());
        } else if (file.getOriginalFilename().endsWith(".csv")) {
            inputStream = file.getInputStream();
        } else {
            return ResponseEntity.badRequest().body("Only CSV or Excel files are accepted.");
        }

        
        List<Product> products = CsvUtils.parseCSV(inputStream);
        return ResponseEntity.ok("File uploaded and parsed successfully. Total products: " + products.size());
    }

    
}
