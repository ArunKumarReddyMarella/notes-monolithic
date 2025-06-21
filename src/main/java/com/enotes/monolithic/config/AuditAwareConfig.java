package com.enotes.monolithic.config;

import com.enotes.monolithic.entity.User;
import com.enotes.monolithic.util.CommonUtil;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

public class AuditAwareConfig implements AuditorAware<Integer> {

	@Override
	public Optional<Integer> getCurrentAuditor() {
		User loggedInUser = CommonUtil.getLoggedInUser();
		return Optional.ofNullable(loggedInUser.getId());

	}

}
