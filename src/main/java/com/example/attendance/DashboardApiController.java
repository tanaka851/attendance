package com.example.attendance;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DashboardApiController {

    private final AttendanceRepository repository;
    private final EmployeeRepository employeeRepository;

    private Long getCurrentEmpId(HttpSession session) {
        Long empId = (Long) session.getAttribute("loginEmpId");
        return (empId != null) ? empId : 1L;
    }

    // 現在の打刻状況を取得
    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboard(HttpSession session) {
        Long empId = getCurrentEmpId(session);
        Employee emp = employeeRepository.findById(empId).orElse(new Employee());

        AttendanceRecord record = repository.findByEmployeeIdAndTargetDate(empId, LocalDate.now())
                .orElseGet(() -> AttendanceRecord.builder()
                        .employeeId(empId)
                        .targetDate(LocalDate.now())
                        .workStatus(WorkStatus.BEFORE_WORK)
                        .workLocation("オフィス") // ★「オフィス」に変更
                        .dailyReport("")
                        .build());

        // 勤務場所が未設定・空・ハイフンの場合は「オフィス」を初期値にする
        String location = record.getWorkLocation();
        if (location == null || location.isBlank() || location.equals("-") || location.equals("本社オフィス")) {
            location = "オフィス";
        }

        return ResponseEntity.ok(Map.of(
                "employeeName", emp.getName() != null ? emp.getName() : "山田太郎",
                "status", record.getWorkStatus().name(),
                "statusLabel", record.getWorkStatus().getLabel(),
                "clockInTime", record.getClockInTime() != null ? record.getClockInTime().format(DateTimeFormatter.ofPattern("HH:mm")) : "",
                "workLocation", location, // ★確実に「オフィス」が入る
                "dailyReport", record.getDailyReport() != null ? record.getDailyReport() : ""
        ));
    }

    // 打刻処理 (出勤, 退勤, 休憩)
    @PostMapping("/stamp")
    public ResponseEntity<?> stamp(@RequestBody Map<String, String> body, HttpSession session) {
        Long empId = getCurrentEmpId(session);
        String type = body.get("stampType");

        AttendanceRecord record = repository.findByEmployeeIdAndTargetDate(empId, LocalDate.now())
                .orElseGet(() -> AttendanceRecord.builder()
                        .employeeId(empId)
                        .targetDate(LocalDate.now())
                        .workStatus(WorkStatus.BEFORE_WORK)
                        .workLocation(body.getOrDefault("workLocation", "オフィス")) // ★「オフィス」に変更
                        .build());

        if (body.containsKey("workLocation")) {
            record.setWorkLocation(body.get("workLocation"));
        }

        LocalTime now = LocalTime.now();
        switch (type) {
            case "CLOCK_IN" -> {
                record.setClockInTime(now);
                record.setWorkStatus(WorkStatus.WORKING);
            }
            case "CLOCK_OUT" -> {
                record.setClockOutTime(now);
                record.setWorkStatus(WorkStatus.LEFT);
            }
            case "BREAK_START" -> record.setWorkStatus(WorkStatus.ON_BREAK);
            case "BREAK_END" -> record.setWorkStatus(WorkStatus.WORKING);
        }
        repository.save(record);
        return ResponseEntity.ok(Map.of("message", "打刻が完了しました"));
    }

    // 日報保存
    @PostMapping("/report")
    public ResponseEntity<?> saveReport(@RequestBody Map<String, String> body, HttpSession session) {
        Long empId = getCurrentEmpId(session);

        AttendanceRecord record = repository.findByEmployeeIdAndTargetDate(empId, LocalDate.now())
                .orElseGet(() -> AttendanceRecord.builder()
                        .employeeId(empId)
                        .targetDate(LocalDate.now())
                        .workStatus(WorkStatus.BEFORE_WORK)
                        .workLocation("オフィス") // ★「オフィス」に変更
                        .build());
        record.setDailyReport(body.get("report"));
        repository.save(record);
        return ResponseEntity.ok(Map.of("message", "日報を保存しました"));
    }
}