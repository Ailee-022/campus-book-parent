package com.campus.book_service.service.impl; // 注意这里路径变了

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.book_service.entity.Book;
import com.campus.book_service.mapper.BookMapper;
import com.campus.book_service.service.BookService;
import org.springframework.stereotype.Service;

@Service
public class BookServiceImpl extends ServiceImpl<BookMapper, Book> implements BookService {
}