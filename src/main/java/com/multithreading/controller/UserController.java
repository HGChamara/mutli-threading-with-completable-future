package com.multithreading.controller;

import com.multithreading.entity.User;
import com.multithreading.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, produces = "application/json")
    public ResponseEntity saveUsers(@RequestParam(value="files")MultipartFile[] files) throws Exception {
        for(MultipartFile file : files) {
            userService.saveUser(file);
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping(produces = "application/json")
    public CompletableFuture<ResponseEntity> findAllUsers() {
        return userService.findAllUsers().thenApply(ResponseEntity::ok);
    }

    @GetMapping(path = "/thread", produces = "application/json")
    public ResponseEntity findAllUsersAsync() {

        CompletableFuture<List<User>> users1 = userService.findAllUsers();
        CompletableFuture<List<User>> users2 = userService.findAllUsers();
        CompletableFuture<List<User>> users3 = userService.findAllUsers();
        CompletableFuture.allOf(users1, users2, users3).join();

        try {
            List<User> combinedUsers = new ArrayList<>();
            combinedUsers.addAll(users1.get());
            combinedUsers.addAll(users2.get());
            combinedUsers.addAll(users3.get());

            return ResponseEntity.ok(combinedUsers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
