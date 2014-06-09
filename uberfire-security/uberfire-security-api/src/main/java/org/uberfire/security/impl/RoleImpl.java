package org.uberfire.security.impl;

import java.io.Serializable;

import org.uberfire.security.Role;

public class RoleImpl implements Role,
                                 Serializable {

    private static final long serialVersionUID = 8713460024436782774L;

    private String name;

    public RoleImpl() {
    }

    public RoleImpl( String name ) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        RoleImpl role = (RoleImpl) o;

        if (!name.equals(role.name)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
