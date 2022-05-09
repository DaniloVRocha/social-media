package org.acme.quarkussocial.rest;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import org.acme.quarkussocial.domain.model.domain.User;
import org.acme.quarkussocial.domain.model.repository.UserRepository;
import org.acme.quarkussocial.dto.CreateUserRequest;
import org.acme.quarkussocial.dto.ResponseError;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Set;

@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    private UserRepository userRepository;
    private Validator validator;

    @Inject
    public UserResource(UserRepository userRepository, Validator validator) {
        this.userRepository = userRepository;
        this.validator = validator;
    }

    @POST
    @Transactional
    public Response createUser(CreateUserRequest userRequest){
        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(userRequest);

        if(!violations.isEmpty()){
            return ResponseError.createFromValidation(violations).withStatusCode(ResponseError.UNPROCESSABLE_ENTITY_STATUS);
        }

        User user = new User();
        user.setName(userRequest.getName());
        user.setAge(userRequest.getAge());
        userRepository.persist(user);
        return Response.status(Response.Status.CREATED).entity(userRequest).build();
    }

    @GET
    public Response listAllUsers(){
        PanacheQuery<User> query = userRepository.findAll();
        return Response.ok(query.list()).build();
    }

    @DELETE
    @Path("{id}")
    @Transactional
    public Response deleteUser(@PathParam("id") Long id){
        User user = userRepository.findById(id);
        if(user != null){
            userRepository.delete(user);
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @PUT
    @Path("{id}")
    @Transactional
    public Response updateUser(@PathParam("id") Long id, CreateUserRequest userRequest){
        User user = userRepository.findById(id);
        if(user != null){
            user.setName(userRequest.getName());
            user.setAge(userRequest.getAge());
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }


}
