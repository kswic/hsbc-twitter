package pl.hsbc.twitter.domain.subscription;

import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.hsbc.twitter.domain.user.User;
import pl.hsbc.twitter.domain.user.UserDto;
import pl.hsbc.twitter.domain.user.UserRepository;
import pl.hsbc.twitter.domain.user.UserService;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class SubscriptionService {

    private final SubscriptionMapper subscriptionMapper;
    private final SubscriptionRepository subscriptionRepository;
    private final UserService userService;
    private final UserRepository userRepository;

    public SubscriptionService(SubscriptionMapper subscriptionMapper, SubscriptionRepository subscriptionRepository,
                               UserService userService, UserRepository userRepository) {
        this.subscriptionMapper = subscriptionMapper;
        this.subscriptionRepository = subscriptionRepository;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @Transactional
    public SubscriptionDto subscribe(SubscriptionDto subscriptionDto) {
        Subscription subscription = subscriptionMapper.dtoToEntity(subscriptionDto);
        boolean exists = subscriptionRepository.exists(Example.of(subscription));

        if (exists) {
            throw new IllegalArgumentException("Such subscription already exists");
        }

        if (!userRepository.existsById(subscriptionDto.getOwnerId())) {
            userService.addUser(new UserDto(subscriptionDto.getOwnerId()));
        }

        subscriptionRepository.save(subscription);

        return subscriptionDto;
    }

    public Set<SubscriptionDto> getUserSubscriptions(String userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            return new HashSet<>();
        }

        return subscriptionMapper.entitySetToDtoSet(optionalUser.get().getSubscriptions());
    }

}
