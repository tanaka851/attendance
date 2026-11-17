package com.example.attendance;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    // 部署で社員を絞り込む
    List<Employee> findByDepartmentOrderByIdAsc(String department);
}