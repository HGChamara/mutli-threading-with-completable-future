package com.multithreading.service;

import com.multithreading.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface UserService{
    CompletableFuture<List<User>> saveUser(MultipartFile file) throws Exception;
    CompletableFuture<List<User>> findAllUsers();
}
