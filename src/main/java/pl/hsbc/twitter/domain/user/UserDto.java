package pl.hsbc.twitter.domain.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@RequiredArgsConstructor
public class UserDto {

    @NotBlank
    @Length(min = 2, max = 10)
    private final String userId;

}
