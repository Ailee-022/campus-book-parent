package com.campus.book_service.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.book_service.entity.Book;
import com.campus.book_service.mapper.BookMapper;
import com.campus.book_service.service.BookService;
import com.campus.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/book")
public class BookController {

    @Autowired
    private BookService bookService;

    // 核心修复：注入 BookMapper，用于调用自定义的扣减库存方法
    @Autowired
    private BookMapper bookMapper;

    // 1. 发布图书
    @PostMapping("/add")
    public Result<String> add(@RequestBody Book book) {
        book.setStatus(1);
        bookService.save(book);
        return Result.success("发布成功");
    }

    // 2. 修改图书
    @PutMapping("/update")
    public Result<String> update(@RequestBody Book book) {
        bookService.updateById(book);
        return Result.success("修改成功");
    }

    // 3. 删除图书
    @DeleteMapping("/delete/{id}")
    public Result<String> delete(@PathVariable Long id) {
        bookService.removeById(id);
        return Result.success("删除成功");
    }

    // 4. 分页查询 + 模糊搜索
    @GetMapping("/list")
    public Result<Page<Book>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword) {

        Page<Book> pageParam = new Page<>(page, size);
        QueryWrapper<Book> wrapper = new QueryWrapper<>();

        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like("title", keyword).or().like("author", keyword);
        }
        wrapper.eq("status", 1); // 只查上架的书

        Page<Book> result = bookService.page(pageParam, wrapper);
        return Result.success(result);
    }

    // 5. 供订单服务调用：乐观锁扣减库存
    @PostMapping("/deduct")
    public Result<String> deduct(@RequestParam Long bookId, @RequestParam Integer quantity, @RequestParam Integer version) {
        int rows = bookMapper.deductStock(bookId, quantity, version);
        if (rows > 0) {
            return Result.success("扣减成功");
        }
        return Result.error("扣减失败，库存不足或版本冲突");
    }
}