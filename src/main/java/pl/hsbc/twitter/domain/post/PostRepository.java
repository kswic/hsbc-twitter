package pl.hsbc.twitter.domain.post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findAllByAuthorIdOrderByCreateTimeDesc(String postAuthor);

    List<Post> findAllByAuthorIdInOrderByCreateTimeDesc(Set<String> postAuthors);

}
