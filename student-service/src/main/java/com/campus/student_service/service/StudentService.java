package com.campus.student_service.service;

import com.campus.common.Result;
import com.campus.student_service.entity.Student;

public interface StudentService {
    Result<String> register(Student student, String code);
    Result<String> login(String username, String password, String code);
}