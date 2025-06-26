package com.enotes.monolithic.service.impl;

import com.enotes.monolithic.dto.TodoDto;
import com.enotes.monolithic.entity.Todo;
import com.enotes.monolithic.enums.TodoStatus;
import com.enotes.monolithic.exception.ResourceNotFoundException;
import com.enotes.monolithic.repository.TodoRepository;
import com.enotes.monolithic.service.TodoService;
import com.enotes.monolithic.util.CommonUtil;
import com.enotes.monolithic.util.Validation;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Service
public class TodoServiceImpl implements TodoService {
	private static final Logger logger = LoggerFactory.getLogger(TodoServiceImpl.class);

	@Autowired
	private TodoRepository todoRepo;

	@Autowired
	private ModelMapper mapper;

	@Autowired
	private Validation validation;

	@Override
	public Boolean saveTodo(TodoDto todoDto) throws Exception {
		// validate todo status
		validation.todoValidation(todoDto);

		Todo todo = mapper.map(todoDto, Todo.class);
		todo.setStatusId(todoDto.getStatus().getId());
		Todo saveTodo = todoRepo.save(todo);
		if (!ObjectUtils.isEmpty(saveTodo)) {
			return true;
		}
		return false;
	}

	@Override
	public TodoDto getTodoById(Integer id) throws Exception {
		Todo todo = todoRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Todo Not Found ! id invalid"));
		TodoDto todoDto = mapper.map(todo, TodoDto.class);
		setStatus(todoDto,todo);
		return todoDto;
	}

	private void setStatus(TodoDto todoDto, Todo todo) {
		
		for(TodoStatus st:TodoStatus.values())
		{
			if(st.getId().equals(todo.getStatusId()))
			{
				TodoDto.StatusDto statusDto= TodoDto.StatusDto.builder()
						.id(st.getId())
						.name(st.getName())
						.build();
				todoDto.setStatus(statusDto);
				logger.info("Todo Status set successfully for todo : {}", todoDto);
			}
		}
		
	}

	@Override
	public List<TodoDto> getTodoByUser() {
		Integer userId = CommonUtil.getLoggedInUser().getId();
		List<Todo> todos = todoRepo.findByCreatedBy(userId);
		return todos.stream().map(td -> mapper.map(td, TodoDto.class)).toList();
	}

}
