package pl.hsbc.twitter.domain.post;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.hsbc.twitter.infrastructure.mapstruct.MapperConfiguration;

import java.util.List;

@Mapper(config = MapperConfiguration.class)
public interface PostMapper {

    PostDetailsDto entityToDetailsDto(Post post);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    Post dtoToEntity(PostDto postDto);

    List<PostDetailsDto> entityListToDtoList(List<Post> posts);

}
