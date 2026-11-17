package com.example.attendance;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "employees")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String employeeNo;   // "01", "02" など
    private String name;         // "山田太郎" など
    private String department;   // "開発第1部"
    private String phone;        // 電話番号
    private String email;        // メールアドレス
    private String note;         // 備考/特記事項
}