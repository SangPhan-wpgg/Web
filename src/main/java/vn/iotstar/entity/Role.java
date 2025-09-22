package vn.iotstar.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "roles")
@NamedQuery(name = "Role.findAll", query = "SELECT r FROM Role r")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "role_name", columnDefinition = "NVARCHAR(255)")
    private String roleName;

    // Getter Setter
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getRoleName() {
        return roleName;
    }
    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}
