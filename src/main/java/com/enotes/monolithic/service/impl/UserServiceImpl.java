package com.enotes.monolithic.service.impl;

import com.enotes.monolithic.dto.UserDto;
import com.enotes.monolithic.entity.Role;
import com.enotes.monolithic.entity.User;
import com.enotes.monolithic.repository.RoleRepository;
import com.enotes.monolithic.repository.UserRepository;
import com.enotes.monolithic.service.UserService;
import com.enotes.monolithic.util.Validation;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private RoleRepository roleRepo;

	@Autowired
	private Validation validation;

	@Autowired
	private ModelMapper mapper;

	@Override
	public Boolean register(UserDto userDto) {

		validation.userValidation(userDto);
		User user = mapper.map(userDto, User.class);

		setRole(userDto, user);

		User saveUser = userRepo.save(user);
		if (!ObjectUtils.isEmpty(saveUser)) {
			return true;
		}
		return false;
	}

	private void setRole(UserDto userDto, User user) {
		List<Integer> reqRoleId = userDto.getRoles().stream().map(r -> r.getId()).toList();
		List<Role> roles = roleRepo.findAllById(reqRoleId);
		user.setRoles(roles);
	}

}
