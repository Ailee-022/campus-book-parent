package com.campus.student_service.controller;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.campus.common.JwtUtil;
import com.campus.common.Result;
import com.campus.student_service.entity.Admin;
import com.campus.student_service.entity.Student;
import com.campus.student_service.mapper.AdminMapper;
import com.campus.student_service.mapper.StudentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired private AdminMapper adminMapper;
    @Autowired private StudentMapper studentMapper;

    // 管理员登录
    @PostMapping("/login")
    public Result<String> login(@RequestParam String username, @RequestParam String password) {
        QueryWrapper<Admin> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        Admin admin = adminMapper.selectOne(wrapper);

        if (admin == null || !BCrypt.checkpw(password, admin.getPassword())) {
            return Result.error("账号或密码错误");
        }
        String token = JwtUtil.createToken(admin.getId(), admin.getUsername());
        return Result.success(token);
    }

    // 管理员查看所有学生（考核要求：管理全部模块）
    @GetMapping("/student/list")
    public Result<List<Student>> studentList() {
        return Result.success(studentMapper.selectList(null));
    }

    // 管理员删除学生
    @DeleteMapping("/student/{id}")
    public Result<String> deleteStudent(@PathVariable Long id) {
        studentMapper.deleteById(id);
        return Result.success("删除成功");
    }
}