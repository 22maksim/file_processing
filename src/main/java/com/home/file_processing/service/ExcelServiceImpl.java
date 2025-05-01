package com.home.file_processing.service;

import com.home.file_processing.exception.FileEmptyException;
import com.home.file_processing.exception.FileFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

@Service
public class ExcelServiceImpl implements ExcelService {

    @Override
    public int findNthMin(String filePath, int n, String algorithm) {
        List<Integer> numbers = readNumbersFromXlsx(filePath);

        if (!filePath.toLowerCase().endsWith(".xlsx")) {
            throw new FileFormatException("Incorrect file format. Expected .xlsx.");
        }

        if (n < 1 || n > numbers.size()) {
            throw new IllegalArgumentException("Invalid value for N. It must be between 1 and the size of the list.");
        }

        return switch (algorithm.toLowerCase()) {
            case "heap" -> findNthMinHeap(numbers, n);
            case "quick" -> {
                int[] arr = numbers.stream().mapToInt(i -> i).toArray();
                yield quickSelect(arr, 0, arr.length - 1, n - 1);
            }
            default -> throw new IllegalArgumentException("Invalid algorithm name. Use 'heap' or 'quick'.");
        };
    }

    private List<Integer> readNumbersFromXlsx(String filePath) {
        List<Integer> numbers = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(filePath);
             XSSFWorkbook workbook = new XSSFWorkbook(fis)) {

            XSSFSheet sheet = workbook.getSheetAt(0);
            if (sheet == null || sheet.getPhysicalNumberOfRows() == 0) {
                throw new FileEmptyException("File is empty or doesn't contain any data.");
            }

            for (Row row : sheet) {
                Cell cell = row.getCell(0);
                if (cell != null && cell.getCellType() == CellType.NUMERIC) {
                    numbers.add((int) cell.getNumericCellValue());
                }
            }

            if (numbers.isEmpty()) {
                throw new FileEmptyException("File is empty or doesn't contain any valid numeric data.");
            }

        } catch (IOException e) {
            throw new RuntimeException("Error reading the file: " + e.getMessage(), e);
        }

        return numbers;
    }

    private int quickSelect(int[] arr, int left, int right, int k) {
        if (left == right) return arr[left];
        int pivotIndex = partition(arr, left, right);
        if (k == pivotIndex) return arr[k];
        else if (k < pivotIndex) return quickSelect(arr, left, pivotIndex - 1, k);
        else return quickSelect(arr, pivotIndex + 1, right, k);
    }

    private int partition(int[] arr, int left, int right) {
        int pivot = arr[right];
        int i = left;
        for (int j = left; j < right; j++) {
            if (arr[j] < pivot) {
                swap(arr, i, j);
                i++;
            }
        }
        swap(arr, i, right);
        return i;
    }

    private void swap(int[] arr, int i, int j) {
        int tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }

    private int findNthMinHeap(List<Integer> numbers, int n) {
        PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Comparator.reverseOrder());

        for (int num : numbers) {
            if (maxHeap.size() < n) {
                maxHeap.offer(num);
            } else if (num < maxHeap.peek()) {
                maxHeap.poll();
                maxHeap.offer(num);
            }
        }

        if (maxHeap.isEmpty()) {
            throw new FileEmptyException("File is empty or does not contain enough valid numbers.");
        }
        return maxHeap.peek();
    }
}
