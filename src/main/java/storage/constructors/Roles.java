package storage.constructors;

import java.util.Objects;

public class Roles {
    private long idRole;
    private String role;

    public Roles(long idRole, String role) {
        this.idRole = idRole;
        this.role = role;
    }
    public long getIdRole() {
        return idRole;
    }

    public void setIdRole(long idRole) {
        this.idRole = idRole;
    }

    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "Roles{" +
                "idRole=" + idRole +
                ", role='" + role + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Roles)) return false;
        Roles roles = (Roles) o;
        return idRole == roles.idRole && role.equals(roles.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idRole, role);
    }
}
