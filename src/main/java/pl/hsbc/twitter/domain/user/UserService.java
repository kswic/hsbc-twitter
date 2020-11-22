package pl.hsbc.twitter.domain.user;

import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;

    public UserService(UserMapper userMapper, UserRepository userRepository) {
        this.userMapper = userMapper;
        this.userRepository = userRepository;
    }

    public void addUser(UserDto userDto) {
        userRepository.save(userMapper.dtoToEntity(userDto));
    }

}
