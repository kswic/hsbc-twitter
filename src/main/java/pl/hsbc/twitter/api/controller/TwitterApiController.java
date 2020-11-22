package pl.hsbc.twitter.api.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.hsbc.twitter.api.exception.ApiException;
import pl.hsbc.twitter.domain.post.PostDetailsDto;
import pl.hsbc.twitter.domain.post.PostDto;
import pl.hsbc.twitter.domain.post.PostService;
import pl.hsbc.twitter.domain.subscription.SubscriptionDto;
import pl.hsbc.twitter.domain.subscription.SubscriptionService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Api(tags = TwitterApiController.API_TAG_TWITTER)
@RestController
@RequestMapping("api")
public class TwitterApiController {

    public static final String API_TAG_TWITTER = "Twitter endpoints";
    public static final String FIELD_ERRORS_DELIMITER = ", \n";
    public static final String FIELD_ERROR_FORMAT = "%s: %s";

    private final PostService postService;
    private final SubscriptionService subscriptionService;

    public TwitterApiController(PostService postService, SubscriptionService subscriptionService) {
        this.postService = postService;
        this.subscriptionService = subscriptionService;
    }

    @ApiOperation(value = "Allow post new user's message.",
            notes = "User ID of message author and message content should be provided in form of JSON object. " +
                    "There is a limit for message length - 140 characters.",
            response = SubscriptionDto.class)
    @PostMapping("post")
    public ResponseEntity<PostDto> postMessage(@ApiParam(value = "Posted message JSON object", required = true)
                                               @Valid @RequestBody PostDto postDto, BindingResult bindingResult) {
        if (bindingResult.hasFieldErrors()) {
            throw new ApiException(retrieveBindingErrorMessages(bindingResult), HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok(postService.addPost(postDto));
    }

    @ApiOperation(value = "Creates subscription to follow messages of other user.",
            notes = "User ID of subscription owner and followed users must be provided in form of JSON object.",
            response = SubscriptionDto.class)
    @PostMapping("follow")
    public ResponseEntity<SubscriptionDto> follow(@ApiParam(value = "Subscription JSON object", required = true)
                                                  @Valid @RequestBody SubscriptionDto subscriptionDto,
                                                  BindingResult bindingResult) {
        if (bindingResult.hasFieldErrors()) {
            throw new ApiException(retrieveBindingErrorMessages(bindingResult), HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok(subscriptionService.subscribe(subscriptionDto));
    }

    @ApiOperation(value = "Prints all messages created by user in chronological order, newest first.",
            notes = "User ID should be provided to get results.",
            response = PostDetailsDto.class,
            responseContainer = "List")
    @GetMapping("wall/{userId}")
    public List<PostDetailsDto> wall(@ApiParam(value = "ID of the user", required = true) @PathVariable String userId) {
        return postService.getAllUserPosts(userId);
    }

    @ApiOperation(value = "Prints all messages created followed users in chronological order, newest first.",
            notes = "User ID should be provided to get results.",
            response = PostDetailsDto.class,
            responseContainer = "List")
    @GetMapping("timeline/{userId}")
    public List<PostDetailsDto> timeline(@ApiParam(value = "ID of the user", required = true) @PathVariable String userId) {
        return postService.getSubscribedPosts(userId);
    }

    private String retrieveBindingErrorMessages(BindingResult bindingResult) {
        return bindingResult.getFieldErrors()
                .stream()
                .map(fieldError -> String.format(FIELD_ERROR_FORMAT, fieldError.getField(), fieldError.getDefaultMessage()))
                .sorted()
                .collect(Collectors.joining(FIELD_ERRORS_DELIMITER));
    }

}
