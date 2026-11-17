package com.example.attendance;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class AttendanceListController {

    private final EmployeeRepository employeeRepository;
    private final AttendanceRepository attendanceRepository;

    private static final Long DEFAULT_LOGIN_EMP_ID = 1L; // ログイン中: 山田太郎 (ID: 1)

    // 一覧画面の表示
    @GetMapping("/attendance/list")
    public String showList(Model model) {
        String currentDept = "開発第1部";
        LocalDate today = LocalDate.now();

        // 部署の社員を取得
        List<Employee> employees = employeeRepository.findByDepartmentOrderByIdAsc(currentDept);

        List<AttendanceListDto> dtoList = new ArrayList<>();
        int countWorking = 0;
        int countLeft = 0;
        int countBefore = 0;

        for (Employee emp : employees) {
            // 社員ごとの本日の打刻レコードを取得
            var recordOpt = attendanceRepository.findByEmployeeIdAndTargetDate(emp.getId(), today);

            WorkStatus status = recordOpt.map(AttendanceRecord::getWorkStatus).orElse(WorkStatus.BEFORE_WORK);
            String location = recordOpt.map(AttendanceRecord::getWorkLocation).orElse("-");

            // 人数の集計
            if (status == WorkStatus.WORKING) countWorking++;
            else if (status == WorkStatus.LEFT) countLeft++;
            else countBefore++;

            // 備考（自分の場合は "(自分)"、特記事項があればそれを表示）
            String note = emp.getId().equals(DEFAULT_LOGIN_EMP_ID) 
                    ? "(自分)" 
                    : (emp.getNote() != null && !emp.getNote().isBlank() ? emp.getNote() : "-");

            dtoList.add(AttendanceListDto.builder()
                    .no(emp.getEmployeeNo())
                    .name(emp.getName())
                    .statusLabel(status.getLabel())
                    .workLocation(location != null && !location.isBlank() ? location : "-")
                    .phone(emp.getPhone())
                    .email(emp.getEmail())
                    .note(note)
                    .build());
        }

        // ログイン中の社員情報（連絡先変更フォーム用）
        Employee myEmployee = employeeRepository.findById(DEFAULT_LOGIN_EMP_ID).orElse(new Employee());

        model.addAttribute("loginUser", myEmployee.getName());
        model.addAttribute("department", currentDept);
        model.addAttribute("targetDate", today);
        model.addAttribute("myEmployee", myEmployee);
        model.addAttribute("attendanceList", dtoList);
        model.addAttribute("countTotal", dtoList.size());
        model.addAttribute("countWorking", countWorking);
        model.addAttribute("countLeft", countLeft);
        model.addAttribute("countBefore", countBefore);

        return "attendance_list";
    }

    // 連絡先更新処理
    @PostMapping("/attendance/list/update-contact")
    public String updateContact(@RequestParam("phone") String phone,
                                @RequestParam("email") String email,
                                @RequestParam("note") String note,
                                RedirectAttributes redirectAttributes) {

        employeeRepository.findById(DEFAULT_LOGIN_EMP_ID).ifPresent(emp -> {
            emp.setPhone(phone);
            emp.setEmail(email);
            emp.setNote(note);
            employeeRepository.save(emp);
        });

        redirectAttributes.addFlashAttribute("successMessage", "連絡先情報を更新しました。");
        return "redirect:/attendance/list";
    }
}