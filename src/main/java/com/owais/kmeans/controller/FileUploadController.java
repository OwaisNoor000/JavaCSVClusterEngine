package com.owais.kmeans.controller;

import com.owais.kmeans.model.Product;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.owais.kmeans.util.CsvUtils;
import com.owais.kmeans.service.ClusteringService;
import javax.inject.Inject;

import java.io.*;
import java.util.*;

@RestController
@RequestMapping("/api")
public class FileUploadController {
	
	@Inject
	ClusteringService clusteringService;
	
	@PostMapping("/upload")
	public ResponseEntity<?> handleFileUpload(@RequestParam("file") MultipartFile file) throws Exception {
	    String contentType = file.getContentType();
	    InputStream inputStream;

	    if (contentType != null && (contentType.contains("excel") || file.getOriginalFilename().endsWith(".xlsx"))) {
	        inputStream = CsvUtils.convertExcelToCSV(file.getInputStream());
	    } else if (file.getOriginalFilename().endsWith(".csv")) {
	        inputStream = file.getInputStream();
	    } else {
	        return ResponseEntity.badRequest().body("Only CSV or Excel files are accepted.");
	    }

	    List<Product> products = CsvUtils.parseCSV(inputStream);

	    // Call clustering service that returns data ready for CSV
	    List<Map<String, Object>> clusteredData = clusteringService.cluster(products);

	    // Generate CSV in-memory from clusteredData
	    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	    CsvUtils.writeToCSV(clusteredData, outputStream);

	    byte[] csvBytes = outputStream.toByteArray();

	    return ResponseEntity.ok()
	            .header("Content-Disposition", "attachment; filename=clustered_products.csv")
	            .contentType(MediaType.parseMediaType("text/csv"))
	            .body(csvBytes);
	}


    
}
