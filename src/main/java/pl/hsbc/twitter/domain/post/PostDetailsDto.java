package pl.hsbc.twitter.domain.post;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@ApiModel(value = "PostDetailsDto", description = "Post object returned with all details")
@Getter
@Setter
public class PostDetailsDto extends PostDto {

    private LocalDateTime createTime;

}
