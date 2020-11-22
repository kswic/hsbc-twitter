package pl.hsbc.twitter.domain.user;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.hsbc.twitter.infrastructure.mapstruct.MapperConfiguration;

@Mapper(config = MapperConfiguration.class)
public interface UserMapper {

    @Mapping(target = "id", source = "userId")
    @Mapping(target = "subscriptions", ignore = true)
    User dtoToEntity(UserDto userDto);

}
