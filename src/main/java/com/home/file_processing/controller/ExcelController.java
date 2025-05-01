package com.home.file_processing.controller;

import com.home.file_processing.service.ExcelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "file processing", description = "Операции с Excel")
@RestController
@RequestMapping("/api/v1/excel")
@RequiredArgsConstructor
public class ExcelController {
    private final ExcelService excelServiceImpl;

    @Operation(
            summary = "Найти N-ое минимальное число в Excel-файле",
            description = """
            Метод принимает путь к локальному .xlsx-файлу, в котором в первом столбце указаны целые числа.
            Возвращает N-ое минимальное число (после сортировки по возрастанию).
            
            Пример: если числа [10, 3, 7, 5, 8], то при N=3 результат — 7.
            Алгоритм можно выбрать: 'quick' или 'heap'.
        """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Успешный результат"),
            @ApiResponse(responseCode = "400", description = "Неверный ввод или параметры"),
            @ApiResponse(responseCode = "500", description = "Ошибка при чтении файла")
    })
    @GetMapping("/min-number")
    public ResponseEntity<Integer> findNthMin(
            @Parameter(description = "Полный путь к локальному .xlsx файлу (например: C:/temp/numbers.xlsx)")
            @RequestParam @NotEmpty String filePath,

            @Parameter(description = "Индекс минимального числа (например, N=1 — минимальное число)")
            @RequestParam(defaultValue = "1") @Min(1) int n,

            @Parameter(description = "Алгоритм: 'quick' или 'heap'. " +
                    "По умолчанию quick. Для больших файлов рекомендую 'heap'", example = "heap")
            @RequestParam(defaultValue = "quick") String algorithm
    ) {

        int result = excelServiceImpl.findNthMin(filePath, n, algorithm);
        return ResponseEntity.ok(result);

    }
}
