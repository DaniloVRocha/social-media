package org.acme.quarkussocial.rest;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Sort;
import org.acme.quarkussocial.domain.model.domain.Post;
import org.acme.quarkussocial.domain.model.domain.User;
import org.acme.quarkussocial.domain.model.repository.FollowerRepository;
import org.acme.quarkussocial.domain.model.repository.PostRepository;
import org.acme.quarkussocial.domain.model.repository.UserRepository;
import org.acme.quarkussocial.dto.CreatePostRequest;
import org.acme.quarkussocial.dto.PostResponse;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

@Path("/users/{userId}/posts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PostResource {

    private UserRepository userRepository;
    private PostRepository postRepository;
    private FollowerRepository followerRepository;

    @Inject
    public PostResource(UserRepository userRepository, PostRepository postRepository, FollowerRepository followerRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.followerRepository = followerRepository;
    }

    @POST
    @Transactional
    public Response savePost(@PathParam("userId") Long userId, CreatePostRequest request){

        User user = userRepository.findById(userId);
        if(user == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        Post post = new Post();
        post.setText(request.getText());
        post.setUser(user);

        postRepository.persist(post);
        return Response.status(Response.Status.CREATED).build();
    }

    @GET
    public Response listPost(@PathParam("userId") Long userId,@HeaderParam("followerId") Long followerId){
        User user = userRepository.findById(userId);
        if(user == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        if(followerId == null){
            return Response.status(Response.Status.BAD_REQUEST).entity("Você esqueceu de enviar o followerId").build();
        }

        User follower = userRepository.findById(followerId);
        if(follower == null){
            return Response.status(Response.Status.BAD_REQUEST).entity("followerId Inexistente").build();
        }
        boolean follows = followerRepository.follows(user, follower);
        if(!follows){
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("Você não consegue ver esses posts, pois não segue o usuario").build();
        }

        PanacheQuery<Post> query = postRepository.find("user", Sort.by("dateTime", Sort.Direction.Descending), user);
        var list = query.list();
        List<PostResponse> response = list.stream().map(post -> PostResponse.fromEntity(post)).collect(Collectors.toList());
        return Response.ok(response).build();
    }
}
