package RestFulApi.TTCS.controller;

import RestFulApi.TTCS.dto.request.UserRequestDTO;
import RestFulApi.TTCS.dto.response.ResponseData;
import RestFulApi.TTCS.dto.response.ResponseError;
import RestFulApi.TTCS.dto.response.UserDetailResponse;
import RestFulApi.TTCS.service.User.UserService;
import RestFulApi.TTCS.util.Gender;
import RestFulApi.TTCS.util.UserStatus;
import RestFulApi.TTCS.util.UserType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
@Slf4j
@RestController
@RequestMapping("/user")
@Validated
@Tag(name = "User Controller")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @Operation(method = "POST", summary = "Add new user", description = "Send a request via this API to create new user")
    @PostMapping("/")
    public ResponseData<Long> addUser(@Valid @RequestBody UserRequestDTO userRequestDTO) {
        try {
            long userId  = userService.saveUser(userRequestDTO);
            return new ResponseData<>(HttpStatus.CREATED.value(), "User saved successfully" , userId);
        }catch (Exception e){
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

    @Operation(summary = "Update user", description = "Send a request via this API to update user")
    @PutMapping("/{userId}")
    public ResponseData<?> updateUser(@PathVariable @Min(1) Long userId, @Valid @RequestBody UserRequestDTO user) {
        try {
            userService.updateUser(userId , user);
            return new ResponseData<>(HttpStatus.ACCEPTED.value(), "User updated successfully");
        }catch (Exception e){
            log.info("user not found");
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

    @Operation(summary = "Change status of user", description = "Send a request via this API to change status of user")
    @PatchMapping("/{userId}")
    public ResponseData changeStatus(@PathVariable("userId") Long id, @RequestParam(required = true) UserStatus status) {
        try {
            userService.changeStatus(id , status);
            return new ResponseData(HttpStatus.ACCEPTED.value(), "User's status changed successfully");
        }catch (Exception e){
            log.info("user not found");
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

    @Operation(summary = "Delete user permanently", description = "Send a request via this API to delete user permanently")
    @DeleteMapping("/{userId}")
    public ResponseData deleteUser(@Min(1) @PathVariable("userId") int id) {
        try {
            userService.deleteUser(id);
            return new ResponseData(HttpStatus.NO_CONTENT.value(), "User deleted successfully");
        }catch (Exception e){
            log.info("user not found");
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }


    @Operation(summary = "Get user detail", description = "Send a request via this API to get user information")
    @GetMapping("/{userId}")
    public ResponseData<?> getUser(@PathVariable("userId") @Min(1) int id) {
        try {
            return new ResponseData<>(HttpStatus.OK.value(), "users" , userService.getUser(id));
        }catch (Exception e){
            log.info("user not found");
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

    @Operation(summary = "Get list of users per pageNo", description = "Send a request via this API to get user list by pageNo and pageSize")
    @GetMapping("/list")
    @ResponseStatus(HttpStatus.OK)
    public ResponseData<?> getAllUser(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String sortBy) {
        return new ResponseData<>(HttpStatus.OK.value(), "users" , userService.getAllUsersWithSortBy(pageNo , pageSize, sortBy));
    }

    @Operation(summary = "Get list of users per pageNo multiple columns", description = "Send a request via this API to get user list by pageNo and pageSize")
    @GetMapping("/list-with-multiple-columns")
    @ResponseStatus(HttpStatus.OK)
    public ResponseData<?> getAllUserMultipleColumns(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10")int pageSize,
            @RequestParam(required = false) String ...sortBy) {
        return new ResponseData<>(HttpStatus.OK.value(), "users" , userService.getAllUsersWithSortBy(pageNo , pageSize, sortBy));
    }


    @Operation(summary = "Get list of users per pageNo multiple columns and search", description = "Send a request via this API to get user list by pageNo and pageSize")
    @GetMapping("/list-with-multiple-columns-and-search")
    @ResponseStatus(HttpStatus.OK)
    public ResponseData<?> getAllUserMultipleColumnsAndSearch(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String sortBy) {
        return new ResponseData<>(HttpStatus.OK.value(), "users" , userService.getAllUserMultipleColumnsAndSearch(pageNo , pageSize, search,sortBy));
    }


    @Operation(summary = "Advance search query by criteria", description = "Send a request via this API to get user list by pageNo, pageSize and sort by multiple column")
    @GetMapping("/advance-search-with-criteria")
    public ResponseData<?> advanceSearchWithCriteria(@RequestParam(defaultValue = "0", required = false) int pageNo,
                                                     @RequestParam(defaultValue = "20", required = false) int pageSize,
                                                     @RequestParam(required = false) String sortBy,
                                                     @RequestParam(required = false) String address,
                                                     @RequestParam(defaultValue = "") String... search) {
        log.info("Request advance search query by criteria");
        return new ResponseData<>(HttpStatus.OK.value(), "users", userService.advanceSearchWithCriteria(pageNo, pageSize, sortBy , address, search));
    }


}
