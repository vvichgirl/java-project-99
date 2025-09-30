package hexlet.code.component;

import hexlet.code.model.Label;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.service.CustomUserDetailsService;
import lombok.AllArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@AllArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;

    private final CustomUserDetailsService userService;

    private final TaskStatusRepository statusRepository;

    private final LabelRepository labelRepository;

    private static final Set<String> SLUGS = Set.of("draft", "to_review", "to_be_fixed", "to_publish", "published");
    private static final Set<String> LABEL_NAMES = Set.of("feature", "bug");


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
            if (statusRepository.findBySlug(slug).isEmpty()) {
                var status = new TaskStatus();
                status.setSlug(slug);
                status.setName(slug);
                statusRepository.save(status);
            }
        }

        for (String name : LABEL_NAMES) {
            if (labelRepository.findByName(name).isEmpty()) {
                var label = new Label();
                label.setName(name);
                labelRepository.save(label);
            }
        }
    }
}
