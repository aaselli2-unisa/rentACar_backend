package src.controller.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import src.controller.TResponse;
import src.controller.user.request.UpdatePasswordRequest;
import src.controller.user.response.UserResponse;
import src.service.user.UserService;

import java.util.List;

import static src.controller.user.LogConstant.*;

@RestController
@Slf4j
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<TResponse<Page<UserResponse>>> getAllUsers(Pageable pageable) {
        log.info(GETTING_ALL_USERS);
        Page<UserResponse> users = normalizePageForSerialization(userService.getAll(pageable));
        log.info(RETRIEVED_ALL_USERS, users.getTotalElements());
        return new ResponseEntity<>(TResponse.<Page<UserResponse>>tResponseBuilder()
                .response(users)
                .build(), HttpStatus.OK
        );
    }

    @GetMapping(params = "isDeleted")
    public ResponseEntity<TResponse<List<UserResponse>>> getAllByDeletedState(
            @RequestParam(value = "isDeleted", required = false) boolean isDeleted) {
        log.info(GETTING_USERS_BY_DELETED_STATE, isDeleted);
        List<UserResponse> users = userService.getAllByDeletedState(isDeleted);
        log.info(RETRIEVED_USERS_BY_DELETED_STATE, users.size());
        return new ResponseEntity<>(TResponse.<List<UserResponse>>tResponseBuilder()
                .response(users)
                .build(), HttpStatus.OK
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<TResponse<UserResponse>> getById(@PathVariable int id) {
        log.info(GETTING_USER_BY_ID, id);
        UserResponse user = userService.getById(id);
        log.info(RETRIEVED_USER_BY_ID, id);
        return new ResponseEntity<>(TResponse.<UserResponse>tResponseBuilder()
                .response(user)
                .build(), HttpStatus.OK
        );
    }

    @GetMapping("/count/{isDeleted}")
    public ResponseEntity<TResponse<Integer>> getCountByDeletedState(@PathVariable boolean isDeleted) {
        log.info(GETTING_USER_COUNT_BY_DELETED_STATE, isDeleted);
        int count = userService.getCountByDeletedState(isDeleted);
        log.info(RETRIEVED_USER_COUNT_BY_DELETED_STATE, count);
        return new ResponseEntity<>(TResponse.<Integer>tResponseBuilder()
                .response(count)
                .build(), HttpStatus.OK
        );
    }

    // Security patch V01: password moved from query parameter to request body so it
    // is never written to server access logs, browser history, or Referer headers.
    @PutMapping("/updatePassword")
    public ResponseEntity<TResponse<Void>> updatePassword(
            @Valid @RequestBody UpdatePasswordRequest request) {
        log.info(UPDATING_USER_PASSWORD, request.getId());
        userService.updatePassword(request.getId(), request.getPassword());
        log.info(USER_PASSWORD_UPDATED, request.getId());
        return new ResponseEntity<>(TResponse.<Void>tResponseBuilder()
                .build(), HttpStatus.NO_CONTENT
        );
    }

    @PutMapping("/block/{id}")
    public ResponseEntity<TResponse<Void>> blockUser(@PathVariable int id) {
        log.info(BLOCKING_USER, id);
        userService.blockUser(id);
        log.info(USER_BLOCKED, id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private Page<UserResponse> normalizePageForSerialization(Page<UserResponse> users) {
        if (users == null) {
            return Page.empty(PageRequest.of(0, 1));
        }
        if (users.getPageable().isPaged()) {
            return users;
        }

        int safePageSize = Math.max(users.getNumberOfElements(), 1);
        return new PageImpl<>(users.getContent(), PageRequest.of(0, safePageSize), users.getTotalElements());
    }
}
