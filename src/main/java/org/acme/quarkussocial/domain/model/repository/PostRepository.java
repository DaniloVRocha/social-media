package org.acme.quarkussocial.domain.model.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.acme.quarkussocial.domain.model.domain.Post;
import org.acme.quarkussocial.domain.model.domain.User;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PostRepository implements PanacheRepository<Post> {
}
