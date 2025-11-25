package com.example.event_manager.service;

import com.example.event_manager.model.Event;
import com.example.event_manager.repository.EventRepository;
import com.opencsv.CSVReader;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class ImportService {

    @Autowired
    private EventRepository eventRepository;

    public ResponseEntity<String> importCsv(MultipartFile file) {
        List<Event> events = new ArrayList<>();
        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
             CSVReader csvReader = new CSVReader(reader)) {

            String[] line;
            int lineNumber = 0;
            while ((line = csvReader.readNext()) != null) {
                lineNumber++;
                // Skip header if present
                if (lineNumber == 1 && line[0].equalsIgnoreCase("name")) {
                    continue;
                }
                Event event = new Event();
                event.setName(line[0]);
                event.setStartDate(line[1]);
                event.setEndDate(line[2]);
                event.setLongitude(line[3].isEmpty() ? null : Double.parseDouble(line[3]));
                event.setLatitude(line[4].isEmpty() ? null : Double.parseDouble(line[4]));
                event.setPlaceId(line[5].isEmpty() ? null : Long.parseLong(line[5]));
                event.setLocationName(line.length > 6 ? line[6] : null);
                events.add(event);
            }
            eventRepository.saveAll(events);
            return ResponseEntity.ok("CSV import successful! Imported " + events.size() + " events.");
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("CSV import failed: " + e.getMessage());
        }
    }

    public ResponseEntity<String> importExcel(MultipartFile file) {
        List<Event> events = new ArrayList<>();
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            int rowNumber = 0;
            for (Row row : sheet) {
                if (rowNumber == 0) {
                    rowNumber++;
                    continue; // skip header
                }
                Event event = new Event();
                event.setName(getCellStringValue(row.getCell(0)));
                event.setStartDate(getCellStringValue(row.getCell(1)));
                event.setEndDate(getCellStringValue(row.getCell(2)));
                event.setLongitude(getCellDoubleValue(row.getCell(3)));
                event.setLatitude(getCellDoubleValue(row.getCell(4)));
                event.setPlaceId(getCellLongValue(row.getCell(5)));
                event.setLocationName(getCellStringValue(row.getCell(6)));
                events.add(event);
                rowNumber++;
            }
            eventRepository.saveAll(events);
            return ResponseEntity.ok("Excel import successful! Imported " + events.size() + " events.");
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("Excel import failed: " + e.getMessage());
        }
    }

    // Helper: Converts both numeric and string cells to String
    private String getCellStringValue(Cell cell) {
        if (cell == null) return null;
        if (cell.getCellType() == CellType.STRING) return cell.getStringCellValue();
        if (cell.getCellType() == CellType.NUMERIC) {
            double val = cell.getNumericCellValue();
            // For dates, you might want to format, but for now treat like integer if possible
            if (val == (long) val) {
                return String.valueOf((long) val);
            } else {
                return String.valueOf(val);
            }
        }
        return cell.toString();
    }

    // Helper: Converts cell to Double if possible
    private Double getCellDoubleValue(Cell cell) {
        if (cell == null) return null;
        if (cell.getCellType() == CellType.NUMERIC) return cell.getNumericCellValue();
        try {
            return Double.valueOf(cell.toString());
        } catch (Exception e) {
            return null;
        }
    }

    // Helper: Converts cell to Long if possible
    private Long getCellLongValue(Cell cell) {
        Double d = getCellDoubleValue(cell);
        if (d == null) return null;
        return d.longValue();
    }
}
