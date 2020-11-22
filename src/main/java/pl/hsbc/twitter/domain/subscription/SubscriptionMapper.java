package pl.hsbc.twitter.domain.subscription;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.hsbc.twitter.infrastructure.mapstruct.MapperConfiguration;

import java.util.Set;

@Mapper(config = MapperConfiguration.class)
public interface SubscriptionMapper {

    SubscriptionDto entityToDto(Subscription subscription);

    @Mapping(target = "id", ignore = true)
    Subscription dtoToEntity(SubscriptionDto subscriptionDto);

    Set<SubscriptionDto> entitySetToDtoSet(Set<Subscription> subscriptions);

}
