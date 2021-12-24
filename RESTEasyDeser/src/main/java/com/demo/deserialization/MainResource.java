package com.demo.deserialization;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.*;

import static com.demo.deserialization.PasswordGenerator.generateStrongPassword;

@Path("/")
public class MainResource {
    @GET
    @Produces("text/plain")
    public String hello() {
        return "Test API!";
    }

    @POST
    @Path("/genPassword")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes({"*/*"})
    public String genPassword(Pair pair) {
        HashMap<String, String> result = new HashMap<String, String>();
        String newPasswd = generateStrongPassword(pair.getP1(), Integer.parseInt(pair.getP2()));
        return newPasswd;
    }
}