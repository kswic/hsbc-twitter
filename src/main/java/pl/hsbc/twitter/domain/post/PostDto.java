package pl.hsbc.twitter.domain.post;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.SafeHtml;

import javax.validation.constraints.NotEmpty;

@ApiModel(value = "PostDto", description = "Post object used to create new post request")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostDto {

    @NotEmpty
    @Length(min = 2, max = 10)
    private String authorId;

    @NotEmpty
    @SafeHtml
    @Length(min = 2, max = 140)
    private String message;

}
