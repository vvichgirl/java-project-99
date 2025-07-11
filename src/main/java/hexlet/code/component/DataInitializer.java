package hexlet.code.component;

import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.service.CustomUserDetailsService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@AllArgsConstructor
public class DataInitializer implements ApplicationRunner {

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final CustomUserDetailsService userService;

    @Autowired
    private final TaskStatusRepository statusRepository;

    private static final Set<String> SLUGS = Set.of("draft", "to_review", "to_be_fixed", "to_publish", "published");

    @Override
    public void run(ApplicationArguments args) throws Exception {
        var email = "hexlet@example.com";
        if (userRepository.findByEmail(email).isEmpty()) {
            var user = new User();
            user.setEmail(email);
            user.setPasswordDigest("qwerty");
            userService.createUser(user);
        }

        for (String slug : SLUGS) {
            var status = new TaskStatus();
            status.setSlug(slug);
            status.setName(slug);
            statusRepository.save(status);
        }
    }
}
