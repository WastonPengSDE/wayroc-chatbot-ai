package com.wayroc.wayrocchatbot.utils;

import org.apache.poi.ss.usermodel.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ExcelUtils {

    public static void main(String[] args) throws Exception {
        // 本地测试：把文件放到 resources 里或改成你的真实路径
        String path = "src/main/resources/test_excel.xlsx";
        try (FileInputStream fis = new FileInputStream(path)) {
            String csv = excelToCsv(fis);
            System.out.println("===== CSV OUTPUT =====");
            System.out.println(csv);
        }
    }

    /**
     * Controller 用：上传的 Excel 转成 CSV 字符串
     */
    public static String excelToCsv(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return "";
        }
        try (InputStream is = file.getInputStream()) {
            return excelToCsv(is);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 核心逻辑：InputStream -> CSV
     * 逻辑和你之前 EasyExcel 那个类似：
     * - 只读第一个 sheet
     * - 一行一行读
     * - 过滤掉空字符串
     * - 用逗号拼接
     */
    public static String excelToCsv(InputStream is) {
        StringBuilder sb = new StringBuilder();

        try (Workbook workbook = WorkbookFactory.create(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            DataFormatter formatter = new DataFormatter();

            for (Row row : sheet) {
                if (row == null) {
                    continue;
                }

                List<String> cells = new ArrayList<>();
                short lastCell = row.getLastCellNum();

                for (int i = 0; i < lastCell; i++) {
                    Cell cell = row.getCell(i);
                    String val = formatter.formatCellValue(cell);
                    if (val != null && !val.isEmpty()) {
                        cells.add(val);
                    }
                }

                // 整行是空就跳过
                if (cells.isEmpty()) {
                    continue;
                }

                sb.append(String.join(",", cells)).append("\n");
            }
        } catch (Exception e) {
            return "";
        }

        return sb.toString();
    }
}
