package com.example.attendance;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final EmployeeRepository employeeRepository;
    private final AttendanceRepository attendanceRepository;

    @Override
    @Transactional // データの整合性とID取得を確実にする
    public void run(String... args) {
        try {
            if (employeeRepository.count() > 0) {
                return; // 既にデータがあればスキップ
            }

            LocalDate today = LocalDate.now();

            // 1. 社員データの登録
            Employee emp1 = employeeRepository.save(Employee.builder()
                    .employeeNo("01").name("山田太郎").department("開発第1部")
                    .phone("090-1234-5678").email("taro@example.com").note("").build());

            Employee emp2 = employeeRepository.save(Employee.builder()
                    .employeeNo("02").name("鈴木一郎").department("開発第1部")
                    .phone("080-3333-4444").email("suzuki@example.com").note("午前中外出").build());

            Employee emp3 = employeeRepository.save(Employee.builder()
                    .employeeNo("03").name("佐藤花子").department("開発第1部")
                    .phone("090-5555-6666").email("sato@example.com").note("").build());

            Employee emp4 = employeeRepository.save(Employee.builder()
                    .employeeNo("04").name("田中次郎").department("開発第1部")
                    .phone("090-7777-8888").email("tanaka@example.com").note("").build());

            // 2. 本日の打刻・勤務データ登録
            attendanceRepository.save(AttendanceRecord.builder()
                    .employeeId(emp1.getId()).targetDate(today)
                    .workStatus(WorkStatus.BEFORE_WORK).workLocation("-").build());

            attendanceRepository.save(AttendanceRecord.builder()
                    .employeeId(emp2.getId()).targetDate(today)
                    .workStatus(WorkStatus.BEFORE_WORK).workLocation("-").build());

            attendanceRepository.save(AttendanceRecord.builder()
                    .employeeId(emp3.getId()).targetDate(today)
                    .workStatus(WorkStatus.BEFORE_WORK).workLocation("-").build());

            attendanceRepository.save(AttendanceRecord.builder()
                    .employeeId(emp4.getId()).targetDate(today)
                    .workStatus(WorkStatus.BEFORE_WORK).workLocation("-").build());

            log.info("初期データの登録が正常に完了しました。");

        } catch (Exception e) {
            // エラーが起きてもサーバーを停止させず、原因ログを出力する
            log.error("初期データ登録中にエラーが発生しました: {}", e.getMessage(), e);
        }
    }
}