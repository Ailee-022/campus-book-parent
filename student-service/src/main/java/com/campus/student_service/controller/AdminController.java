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
@CrossOrigin("*")
public class AdminController {

    @Autowired
    private AdminMapper adminMapper;
    @Autowired
    private StudentMapper studentMapper;

    // 1. 管理员登录
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

    // 2. 管理员查看所有学生（查）
    @GetMapping("/student/list")
    public Result<List<Student>> studentList() {
        return Result.success(studentMapper.selectList(null));
    }

    // 3. 管理员新增学生（增）
    @PostMapping("/student/add")
    public Result<String> addStudent(@RequestBody Student student) {
        // 密码必须加密
        student.setPassword(BCrypt.hashpw(student.getPassword()));
        student.setStatus(1);
        studentMapper.insert(student);
        return Result.success("管理员新增学生成功");
    }

    // 4. 管理员修改学生信息（改）
    @PutMapping("/student/update")
    public Result<String> updateStudent(@RequestBody Student student) {
        // 根据 ID 修改，如果传了密码，最好也校验一下，这里为了演示直接更新
        studentMapper.updateById(student);
        return Result.success("管理员修改学生信息成功");
    }

    // 5. 管理员删除学生（删）
    @DeleteMapping("/student/{id}")
    public Result<String> deleteStudent(@PathVariable Long id) {
        studentMapper.deleteById(id);
        return Result.success("删除成功");
    }
}