package com.campus.student_service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.student_service.entity.Student; // 手动写死这行！
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StudentMapper extends BaseMapper<Student> {
}