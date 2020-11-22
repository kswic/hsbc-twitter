package pl.hsbc.twitter.api.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import pl.hsbc.twitter.domain.post.PostDetailsDto;
import pl.hsbc.twitter.domain.post.PostDto;
import pl.hsbc.twitter.domain.subscription.SubscriptionDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:/clear_data.sql")
class TwitterApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldPostMessage() throws Exception {
        // given
        PostDto postDto = new PostDto("user1", "message1");

        // when
        MvcResult mvcResult = mockMvc.perform(post("/api/post")
                .content(objectMapper.writeValueAsString(postDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andReturn();

        // then
        PostDetailsDto response = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), PostDetailsDto.class);

        assertThat(response).isNotNull();
        assertThat(response.getAuthorId()).isEqualTo("user1");
        assertThat(response.getMessage()).isEqualTo("message1");
        assertThat(response.getCreateTime()).isNotNull();
        assertThat(response.getCreateTime()).isBefore(LocalDateTime.now());
    }

    @ParameterizedTest
    @MethodSource("provideArgsForFailedMessagePosting")
    void shouldFailWhilePostingMessage(String authorId, String message, String responseMessage) throws Exception {
        // given
        PostDto postDto = new PostDto(authorId, message);

        // when
        MvcResult mvcResult = mockMvc.perform(post("/api/post")
                .content(objectMapper.writeValueAsString(postDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400))
                .andReturn();

        // then
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(response).isNotNull();
        assertThat(response).isEqualTo(responseMessage);
    }

    private static Stream<Arguments> provideArgsForFailedMessagePosting() {
        return Stream.of(
                Arguments.of("", "message1", "authorId: length must be between 2 and 10, \nauthorId: must not be empty"),
                Arguments.of("user1", "aaa<script>bbb", "message: may have unsafe html content"),
                Arguments.of("user1", "a", "message: length must be between 2 and 140")
        );
    }

    @Test
    void shouldSubscribe() throws Exception {
        // given
        PostDto post1Dto = new PostDto("user1", "message1");
        PostDto post2Dto = new PostDto("user1", "message2");
        SubscriptionDto subscriptionDto = new SubscriptionDto("user2", "user1");

        // when
        mockMvc.perform(post("/api/post")
                .content(objectMapper.writeValueAsString(post1Dto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200));
        mockMvc.perform(post("/api/post")
                .content(objectMapper.writeValueAsString(post2Dto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200));

        MvcResult mvcResult = mockMvc.perform(post("/api/follow")
                .content(objectMapper.writeValueAsString(subscriptionDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andReturn();

        // then
        SubscriptionDto response = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), SubscriptionDto.class);

        assertThat(response).isNotNull();
        assertThat(response.getOwnerId()).isEqualTo("user2");
        assertThat(response.getFollowedUserId()).isEqualTo("user1");
    }

    @Test
    void shouldBlocksSecondSubscription() throws Exception {
        // given
        PostDto post1Dto = new PostDto("user1", "message1");
        SubscriptionDto subscriptionDto = new SubscriptionDto("user2", "user1");

        // when
        mockMvc.perform(post("/api/post")
                .content(objectMapper.writeValueAsString(post1Dto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200));
        mockMvc.perform(post("/api/follow")
                .content(objectMapper.writeValueAsString(subscriptionDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200));

        MvcResult mvcResult = mockMvc.perform(post("/api/follow")
                .content(objectMapper.writeValueAsString(subscriptionDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(500))
                .andReturn();

        // then
        String response = mvcResult.getResponse().getContentAsString();

        assertThat(response).isNotNull();
        assertThat(response).isNotEmpty();
        assertThat(response).isEqualTo("Such subscription already exists");
    }

    @Test
    void shouldResponseWithUserPostsWall() throws Exception {
        // given
        PostDto post1Dto = new PostDto("user1", "message1");
        PostDto post2Dto = new PostDto("user1", "message2");

        // when
        mockMvc.perform(post("/api/post")
                .content(objectMapper.writeValueAsString(post1Dto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200));
        mockMvc.perform(post("/api/post")
                .content(objectMapper.writeValueAsString(post2Dto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200));

        MvcResult mvcResult = mockMvc.perform(get("/api/wall/user1"))
                .andExpect(status().is(200))
                .andReturn();

        // then
        List<PostDetailsDto> response = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {
        });

        assertThat(response).isNotNull();
        assertThat(response).isNotEmpty();
        assertThat(response).hasSize(2);
        assertThat(response.get(0).getCreateTime()).isAfter(response.get(1).getCreateTime());
        assertThat(response.get(0).getAuthorId()).isEqualTo("user1");
        assertThat(response.get(0).getMessage()).isEqualTo("message2");
        assertThat(response.get(1).getAuthorId()).isEqualTo("user1");
        assertThat(response.get(1).getMessage()).isEqualTo("message1");
    }

    @Test
    void shouldResponseWithUserTimelineOfFollowedPorts() throws Exception {
        // given
        PostDto post1Dto = new PostDto("user1", "user1 message1");
        PostDto post2Dto = new PostDto("user2", "user2 message1");
        PostDto post3Dto = new PostDto("user1", "user1 message2");
        SubscriptionDto subscription1Dto = new SubscriptionDto("user3", "user1");
        SubscriptionDto subscription2Dto = new SubscriptionDto("user3", "user2");

        // when
        mockMvc.perform(post("/api/post")
                .content(objectMapper.writeValueAsString(post1Dto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200));
        mockMvc.perform(post("/api/post")
                .content(objectMapper.writeValueAsString(post2Dto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200));
        mockMvc.perform(post("/api/post")
                .content(objectMapper.writeValueAsString(post3Dto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200));

        mockMvc.perform(post("/api/follow")
                .content(objectMapper.writeValueAsString(subscription1Dto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200));
        mockMvc.perform(post("/api/follow")
                .content(objectMapper.writeValueAsString(subscription2Dto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200));

        MvcResult mvcResult = mockMvc.perform(get("/api/timeline/user3"))
                .andExpect(status().is(200))
                .andReturn();

        // then
        List<PostDetailsDto> response = objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
                new TypeReference<>() {});

        assertThat(response).isNotNull();
        assertThat(response).isNotEmpty();
        assertThat(response).hasSize(3);
        assertThat(response.get(0).getCreateTime()).isAfter(response.get(1).getCreateTime());
        assertThat(response.get(1).getCreateTime()).isAfter(response.get(2).getCreateTime());
        assertThat(response.get(0).getAuthorId()).isEqualTo("user1");
        assertThat(response.get(0).getMessage()).isEqualTo("user1 message2");
        assertThat(response.get(1).getAuthorId()).isEqualTo("user2");
        assertThat(response.get(1).getMessage()).isEqualTo("user2 message1");
        assertThat(response.get(2).getAuthorId()).isEqualTo("user1");
        assertThat(response.get(2).getMessage()).isEqualTo("user1 message1");
    }

}