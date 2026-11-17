package com.example.attendance;

public enum WorkStatus {
    BEFORE_WORK("未出勤"),
    WORKING("勤務中"),
    ON_BREAK("休憩中"),
    LEFT("退勤済");

    private final String label;
    WorkStatus(String label) { this.label = label; }
    public String getLabel() { return label; }
}