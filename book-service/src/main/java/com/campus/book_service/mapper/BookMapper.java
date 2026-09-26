package com.campus.book_service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.book_service.entity.Book;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface BookMapper extends BaseMapper<Book> {

    // 乐观锁扣减库存：只有当库存足够 且 版本号没变时，才扣减成功
    @Update("UPDATE tb_book SET stock = stock - #{quantity}, version = version + 1 " +
            "WHERE id = #{bookId} AND stock >= #{quantity} AND version = #{version}")
    int deductStock(@Param("bookId") Long bookId,
                    @Param("quantity") Integer quantity,
                    @Param("version") Integer version);
}