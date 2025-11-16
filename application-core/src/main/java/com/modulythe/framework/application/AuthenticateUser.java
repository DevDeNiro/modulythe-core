package com.modulythe.framework.application;

import java.io.Serializable;
import java.util.List;

public class AuthenticateUser implements Serializable {

    //    @Serial
    private static final long serialVersionUID = 1L;

    private final String uuid;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final List<String> roles;
//    private final List<String> scopes;
//    private final String token;

//    public static Builder builder () {
//        return new Builder();
//    }

    // Authenticate the user

    public AuthenticateUser(String uuid, String firstName, String lastName, String email, List<String> roles) {
        this.uuid = uuid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.roles = roles;
    }
}