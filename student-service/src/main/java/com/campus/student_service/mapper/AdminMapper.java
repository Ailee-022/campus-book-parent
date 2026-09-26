package com.campus.student_service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.student_service.entity.Admin;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AdminMapper extends BaseMapper<Admin> {
}