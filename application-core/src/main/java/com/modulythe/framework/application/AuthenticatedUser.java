package com.modulythe.framework.application;

import java.util.List;

// Cette class pourrait être utilisée pour stocker les informations de l'utilisateur authentifié
// A mapper et récupérer d'un token JWT par exemple
// TODO: Voir pour l'usage du SDK : https://connect2id.com/products/nimbus-jose-jwt
public class AuthenticatedUser {
    private final String uuid;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final List<String> roles;

    public AuthenticatedUser(String uuid, String firstName, String lastName, String email, List<String> roles) {
        this.uuid = uuid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.roles = roles;
    }

    public String getUuid() {
        return uuid;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public List<String> getRoles() {
        return roles;
    }
}