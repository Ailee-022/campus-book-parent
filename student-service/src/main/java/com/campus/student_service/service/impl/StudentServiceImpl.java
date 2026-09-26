package com.campus.student_service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.campus.common.JwtUtil;
import com.campus.common.Result;
import com.campus.student_service.entity.Student;
import com.campus.student_service.mapper.StudentMapper;
import com.campus.student_service.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.hutool.crypto.digest.BCrypt;

@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentMapper studentMapper;

    @Override
    public Result<String> register(Student student, String code) {
        if (code == null || code.isEmpty()) return Result.error("验证码不能为空");
        QueryWrapper<Student> wrapper = new QueryWrapper<>();
        wrapper.eq("username", student.getUsername());
        if (studentMapper.selectOne(wrapper) != null) return Result.error("用户名已存在");

        // 核心：BCrypt 加密密码！
        student.setPassword(BCrypt.hashpw(student.getPassword()));
        student.setStatus(1);
        studentMapper.insert(student);
        return Result.success("注册成功");
    }

    @Override
    public Result<String> login(String username, String password, String code) {
        if (code == null || code.isEmpty()) return Result.error("验证码不能为空");
        QueryWrapper<Student> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        Student student = studentMapper.selectOne(wrapper);
        if (student == null) return Result.error("用户不存在");

        // 核心：BCrypt 校验明文和密文
        if (!BCrypt.checkpw(password, student.getPassword())) return Result.error("密码错误");

        String token = JwtUtil.createToken(student.getId(), student.getUsername());
        return Result.success(token);
    }
}