package src.service.user.model;

import org.springframework.security.core.GrantedAuthority;


public enum UserRole implements GrantedAuthority {
    ADMIN("Admin"),
    EMPLOYEE("Employee"),
    CUSTOMER("Customer"),
    DEVELOPER("Developer");
    private final String label;

    UserRole(String label) {
        this.label = label;
    }

    @Override
    public String getAuthority() {
        return "ROLE_" + name();
    }

    public String getLabel() {
        return name();
    }
}
