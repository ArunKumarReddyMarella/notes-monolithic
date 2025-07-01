package com.enotes.monolithic.controller;

import com.enotes.monolithic.dto.TodoDto;
import com.enotes.monolithic.service.TodoService;
import com.enotes.monolithic.util.CommonUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.enotes.monolithic.util.Constants.ROLE_ADMIN_USER;
import static com.enotes.monolithic.util.Constants.ROLE_USER;

@CrossOrigin
@Tag(name = "Todo", description = "All the Todo Operation APIs")
@RestController
@RequestMapping("/api/v1/todo")
public class TodoControllerV1 {
    private static final Logger logger = LoggerFactory.getLogger(TodoControllerV1.class);

    @Autowired
    private TodoService todoService;

    @Operation(summary = "Save Todo", tags = { "Todo" }, description = "Save Todo")
    @PostMapping("/")
    @PreAuthorize(ROLE_ADMIN_USER)
    public ResponseEntity<?> saveTodo(@RequestBody TodoDto todo) throws Exception {
        logger.info("Request received to save todo: {}", todo);
        Boolean saveTodo = todoService.saveTodo(todo);
        if (saveTodo) {
            return CommonUtil.createBuildResponseMessage("Todo Saved Success", HttpStatus.CREATED);
        } else {
            return CommonUtil.createErrorResponseMessage("Todo not save", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Get Todo", tags = { "Todo" }, description = "Get Todo")
    @GetMapping("/{id}")
    @PreAuthorize(ROLE_USER)
    public ResponseEntity<?> getTodoById(@PathVariable Integer id) throws Exception {
        logger.info("Request received to get todo by id: {}", id);
        TodoDto todo = todoService.getTodoById(id);
        return CommonUtil.createBuildResponse(todo, HttpStatus.OK);
    }

    @Operation(summary = "Get All Todo By User", tags = { "Todo" }, description = "Get All Todo By User")
    @GetMapping("/list")
    @PreAuthorize(ROLE_USER)
    public ResponseEntity<?> getAllTodoByUser() throws Exception {
        logger.info("Request received to get all todo by user");
        List<TodoDto> todoList = todoService.getTodoByUser();
        if (CollectionUtils.isEmpty(todoList)) {
            return ResponseEntity.noContent().build();
        }
        return CommonUtil.createBuildResponse(todoList, HttpStatus.OK);
    }

}
