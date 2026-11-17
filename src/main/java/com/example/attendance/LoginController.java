package com.example.attendance;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class LoginController {

    private final EmployeeRepository employeeRepository;

    // 1. ログイン画面の表示
    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("employees", employeeRepository.findAll());
        return "login";
    }

    // 2. ログイン実行（出退勤ダッシュボード画面 / に移動）
    @PostMapping("/login")
    public String doLogin(@RequestParam("employeeId") Long employeeId, HttpSession session) {
        session.setAttribute("loginEmpId", employeeId);
        return "redirect:/"; // static/index.html が開きます
    }

    // 3. ログアウト
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // ログアウトしたらセッションを破棄
        return "redirect:/login";
    }
}