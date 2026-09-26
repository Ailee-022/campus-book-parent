package com.campus.student_service.controller;

import com.campus.common.Result;
import com.campus.student_service.entity.Student;
import com.campus.student_service.mapper.StudentMapper;
import com.campus.student_service.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/student")
public class StudentController {

    @Autowired private StudentMapper studentMapper;
    @Autowired private StudentService studentService;

    @GetMapping("/list")
    public List<Student> list() { return studentMapper.selectList(null); }

    @PostMapping("/register")
    public Result<String> register(@RequestBody Student student, @RequestParam String code) {
        return studentService.register(student, code);
    }

    @PostMapping("/login")
    public Result<String> login(@RequestParam String username, @RequestParam String password, @RequestParam String code) {
        return studentService.login(username, password, code);
    }
}