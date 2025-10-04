package ru.practicum.shareit.user;


import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/users")
@Validated
public class UserController {

    private final UserClient userClient;


    // POST http://localhost:9090/users
    @PostMapping
    public ResponseEntity<Object> createUser(@RequestBody @Valid UserCreateDto dto) {
        log.info("Gateway: POST /users — create user {}", dto);
        return userClient.createUser(dto);
    }


    // GET http://localhost:9090/users/{userId}
    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUser(@PathVariable @Positive Long userId) {
        log.info("Gateway: GET /users/{} — get user", userId);
        return userClient.getUser(userId);
    }


    // GET http://localhost:9090/users
    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("Gateway: GET /users — get all users");
        return userClient.getAllUsers();
    }

    // PATCH http://localhost:9090/users/{userId}
    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable @Positive Long userId,
                                             @RequestBody UserUpdateDto dto) {
        log.info("Gateway: PATCH /users/{} — update user payload={}", userId, dto);
        return userClient.updateUser(userId, dto);
    }

    // DELETE http://localhost:9090/users/{userId}
    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable @Positive Long userId) {
        log.info("Gateway: DELETE /users/{} — delete user", userId);
        return userClient.deleteUser(userId);
    }




}
