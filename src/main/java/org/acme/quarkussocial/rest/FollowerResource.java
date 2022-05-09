package org.acme.quarkussocial.rest;

import org.acme.quarkussocial.domain.model.domain.Follower;
import org.acme.quarkussocial.domain.model.domain.User;
import org.acme.quarkussocial.domain.model.repository.FollowerRepository;
import org.acme.quarkussocial.domain.model.repository.UserRepository;
import org.acme.quarkussocial.dto.FollowerRequest;
import org.acme.quarkussocial.dto.FollowerResponse;
import org.acme.quarkussocial.dto.FollowersPerUserResponse;
import org.jboss.resteasy.reactive.RestResponse;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

@Path("/users/{userId}/followers")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class FollowerResource {

    private UserRepository userRepository;
    private FollowerRepository followerRepository;

    @Inject
    public FollowerResource(UserRepository userRepository, FollowerRepository followerRepository) {
        this.userRepository = userRepository;
        this.followerRepository = followerRepository;
        this.userRepository = userRepository;
    }
    
    @PUT
    @Transactional
    public Response followUser(@PathParam("userId") Long userId, FollowerRequest request){
        User user = userRepository.findById(userId);

        if(userId == request.getFollowerId()){
            return Response.status(Response.Status.CONFLICT).entity("Você não pode seguir você mesmo").build();
        }

        if(user == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        User follower = userRepository.findById(request.getFollowerId());
        Boolean follows = followerRepository.follows(user, follower);
        if(!follows){
            Follower entity = new Follower();
            followerRepository.follows(user, follower);
            entity.setUser(user);
            entity.setFollower(follower);

            followerRepository.persist(entity);
        }
        return Response.status(RestResponse.Status.NO_CONTENT).build();
    }

    @GET
    public Response listFollowers(@PathParam("userId") Long userId){
        User user = userRepository.findById(userId);
        if(user == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        List<Follower> byUser = followerRepository.findByUser(userId);

        FollowersPerUserResponse response = new FollowersPerUserResponse();
        response.setFollowersCount(byUser.size());
        List<FollowerResponse> collect = byUser.stream().map(FollowerResponse::new).collect(Collectors.toList());
        response.setContent(collect);

        return Response.ok(response).build();
    }

    @DELETE
    @Transactional
    public Response unfollowUser(@PathParam("userId") Long userId, @QueryParam("followerId") Long followerId){
        User user = userRepository.findById(userId);
        if(user == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        followerRepository.deleteByFollowerAndUser(followerId, userId);

        return Response.status(Response.Status.NO_CONTENT).build();
    }
}
