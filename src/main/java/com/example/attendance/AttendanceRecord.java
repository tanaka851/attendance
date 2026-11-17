package com.example.attendance;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "attendance_records")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AttendanceRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long employeeId;
    private LocalDate targetDate;

    @Enumerated(EnumType.STRING)
    private WorkStatus workStatus;

    private LocalTime clockInTime;
    private LocalTime clockOutTime;
    private String workLocation;
    
    @Column(columnDefinition = "TEXT")
    private String dailyReport;
}