/*
 * Copyright (c) 2017.  Alneqat Ltd (https://alneqat.com). All Rights Reserved
 */

package com.clanconnect.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.*;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "users")
public class User implements UserDetails
{

    private static final long serialVersionUID = 1L;

    protected ObjectId companyId;

    @Id
    protected ObjectId id;
    protected String firstName;
    protected String lastName;
    protected String mobile;
    protected String phone;
    protected String backupPhone;
    protected String email;
    @Indexed(unique = true)
    protected String username;
    protected String password;
    protected boolean enabled;
    protected boolean accountNonExpired;
    protected boolean accountNonLocked;
    protected Collection<Authority> authorities;
    protected boolean isCredentialsNonExpired;
    protected Date activationDate;
    protected ObjectId image;
    protected UserType type;
    @Version
    private long version;
    @CreatedBy
    private String createdBy;
    @CreatedDate
    private Date createdDate;
    @LastModifiedBy
    private String lastModifiedBy;
    @LastModifiedDate
    private Date lastModifiedDate;

    public void addAuthority(Authority authority) {
        if (this.authorities == null) {
            this.authorities = new ArrayList<>();
        }
        this.authorities.add(authority);
    }

    @Override
    public String getUsername()
    {
        return this.username;
    }

    @JsonIgnore
    @Override
    public String getPassword()
    {
        return this.password;
    }


    @Override
    public boolean isEnabled()
    {
        return this.enabled;
    }

    @Override
    public boolean isAccountNonExpired()
    {
        return this.accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked()
    {
        return this.accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired()
    {
        return isCredentialsNonExpired;
    }

    public void removeAuthority(Authority authority) {
        if (this.authorities == null) {
            return;
        }
        this.authorities.remove(authority);
    }

    public enum UserType {
        SYSTEM_USER, SUPER_ADMIN, EMPLOYEE, CUSTOMER
    }

}
