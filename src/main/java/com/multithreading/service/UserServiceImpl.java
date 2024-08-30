package com.multithreading.service;

import com.multithreading.entity.User;
import com.multithreading.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Async
    @Override
    public CompletableFuture<List<User>> saveUser(MultipartFile file) throws Exception {
        long startTime = System.currentTimeMillis();
        List<User> users = parseCSVFile(file);
        logger.info("Saving users list of {} ",users.size()," "+Thread.currentThread().getName());
        users = userRepository.saveAll(users);
        long endTime = System.currentTimeMillis();
        logger.info("Total time {}", (endTime-startTime));
        return CompletableFuture.completedFuture(users);
    }

    @Async
    @Override
    public CompletableFuture<List<User>> findAllUsers() {
        List<User> users = userRepository.findAll();
        logger.info("Get users list of {} ",users.size()," "+Thread.currentThread().getName());
        return CompletableFuture.completedFuture(users);
    }

    private List<User> parseCSVFile(final MultipartFile file) throws Exception {
        final List<User> users = new ArrayList<>();
        try {
            try(final BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
                String line;
                while((line = br.readLine()) != null) {
                    final String[] data = line.split(",");
                    final User user = new User();
                    user.setName(data[0]);
                    user.setEmail(data[1]);
                    user.setGender(data[2]);
                    users.add(user);
                }
                return users;
            }

        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new Exception("Failed to parse the CSV ",e);
        }
    }
}
