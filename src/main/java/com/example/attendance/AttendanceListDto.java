package com.example.attendance;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AttendanceListDto {
    private String no;
    private String name;
    private String statusLabel;  // "出勤中", "退勤", "未出勤"
    private String workLocation; // "オフィス", "自宅", "-"
    private String phone;
    private String email;
    private String note;
}