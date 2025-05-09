package org.example.expert.domain.manager.service;

import org.example.expert.domain.manager.entity.Log;
import org.example.expert.domain.manager.repository.LogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LogService {

	private final LogRepository logRepository;

	public LogService(LogRepository logRepository) {
		this.logRepository = logRepository;
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void saveLog(String action, String details) {
		Log log = new Log();
		log.setAction(action);
		log.setDetails(details);
		logRepository.save(log);
	}
}
