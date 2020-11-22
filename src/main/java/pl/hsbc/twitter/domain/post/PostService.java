package pl.hsbc.twitter.domain.post;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.hsbc.twitter.domain.subscription.SubscriptionDto;
import pl.hsbc.twitter.domain.subscription.SubscriptionService;
import pl.hsbc.twitter.domain.user.UserDto;
import pl.hsbc.twitter.domain.user.UserService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class PostService {

    private final PostMapper postMapper;
    private final PostRepository postRepository;
    private final UserService userService;
    private final SubscriptionService subscriptionService;

    public PostService(PostMapper postMapper, PostRepository postRepository, UserService userService,
                       SubscriptionService subscriptionService) {
        this.postMapper = postMapper;
        this.postRepository = postRepository;
        this.userService = userService;
        this.subscriptionService = subscriptionService;
    }

    @Transactional
    public PostDetailsDto addPost(PostDto postDto) {
        userService.addUser(new UserDto(postDto.getAuthorId()));

        Post savedPost = postRepository.save(postMapper.dtoToEntity(postDto));

        return postMapper.entityToDetailsDto(savedPost);
    }

    public List<PostDetailsDto> getAllUserPosts(String userId) {
        return postMapper.entityListToDtoList(postRepository.findAllByAuthorIdOrderByCreateTimeDesc(userId));
    }

    public List<PostDetailsDto> getSubscribedPosts(String userId) {
        Set<String> followedUsers = subscriptionService.getUserSubscriptions(userId)
                .stream()
                .map(SubscriptionDto::getFollowedUserId)
                .collect(Collectors.toSet());

        return postMapper.entityListToDtoList(postRepository.findAllByAuthorIdInOrderByCreateTimeDesc(followedUsers));
    }
}
