package pl.hsbc.twitter.domain.subscription;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@ApiModel(value = "SubscriptionDto", description = "Subscription object used to follow another user's posts")
@Getter
@RequiredArgsConstructor
public class SubscriptionDto {

    @NotBlank
    @Length(min = 2, max = 10)
    private final String ownerId;
    @NotBlank
    @Length(min = 2, max = 10)
    private final String followedUserId;

}
