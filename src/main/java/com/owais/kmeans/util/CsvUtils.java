package com.owais.kmeans.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.owais.kmeans.model.Product;

public class CsvUtils {
	public static InputStream convertExcelToCSV(InputStream excelInput) throws IOException {
    	// Convrt Excel to CSV
        Workbook workbook = new XSSFWorkbook(excelInput);
        Sheet sheet = workbook.getSheetAt(0);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));

        for (Row row : sheet) {
            List<String> values = new ArrayList<>();
            for (Cell cell : row) {
                cell.setCellType(CellType.STRING);
                
                // If this line is missing, the method usually breaks the CSV format. 
                values.add(cell.getStringCellValue().replace(",", " "));
            }
            writer.write(String.join(",", values));
            writer.newLine();
        }

        writer.flush();
        return new ByteArrayInputStream(outputStream.toByteArray());
    }

    public  static List<Product> parseCSV(InputStream inputStream) throws IOException {
        List<Product> productList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {

            Map<String, Integer> headerMap = csvParser.getHeaderMap();
            // Validate column names
            if (!headerMap.keySet().containsAll(List.of("prod_name", "product_type_name", "colour_name"))) {
                throw new IllegalArgumentException("CSV must contain headers: prod_name, product_type_name, colour_name");
            }
            
            for (CSVRecord record : csvParser) {
                String prodName = record.get("prod_name").trim();
                String productType = record.get("product_type_name").trim();
                String colour = record.get("colour_name").trim();

                productList.add(new Product(prodName, productType, colour));
            }
        }

        return productList;
    }
}
