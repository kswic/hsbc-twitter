package pl.hsbc.twitter.domain.user;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.data.domain.Persistable;
import pl.hsbc.twitter.domain.subscription.Subscription;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.util.Set;

@Entity
@Getter
@Setter
public class User implements Persistable<String> {

    @Id
    private String id;
    @OneToMany
    @Fetch(FetchMode.SUBSELECT)
    @JoinColumn(name = "ownerId")
    private Set<Subscription> subscriptions;

    @Override
    public boolean isNew() {
        return getId() == null;
    }

}
