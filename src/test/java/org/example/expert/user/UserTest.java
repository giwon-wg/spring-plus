package org.example.expert.user;

import com.example.domain.user.entity.User;
import com.example.domain.user.enums.UserRole;
import com.example.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;

@SpringBootTest
@ActiveProfiles("test")
public class UserTest {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private EntityManager em;

	@Test
	@Commit
	@Transactional
	void signupMillion() {
		for (int i = 0; i < 1000000; i++) {
			User user = new User("testUser" + i + "@test.com", "test1", UserRole.USER, "nickname_" + i);
			em.persist(user);

			if (i % 1000 == 0) {
				em.flush();
				em.clear();
				System.out.println("1천개 저장");
			}
		}
	}

}
