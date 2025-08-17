package bootstrap.web.thymeleaf.model;

import java.util.Collection;

import lombok.*;
import jakarta.validation.constraints.*;
import jakarta.persistence.*;


@Entity
@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name =  "user", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class User {
	
	@Id
	@GeneratedValue(strategy =  GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "first_name")
	@NotBlank(message = "Name is mandatory.")
	private String firstName;
	
	@Column(name = "last_name")
	private String lastName;

	@Column(name = "name")
	private String username;
	
	@Email(message = "Invalid email address")
    @NotBlank(message = "Email is mandatory")
	private String email;
	
	@NotBlank(message = "Password cannot be black")
	private String password;
	
	@NotNull(message = "Role is mandatory")
	private UserRole role; // ROLE_USER, ROLE_ADMIN
	
	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(
			name = "users_roles",
			joinColumns = @JoinColumn(
		            name = "user_id", referencedColumnName = "id"),
			inverseJoinColumns = @JoinColumn(
				            name = "role_id", referencedColumnName = "id"))
	
	private Collection<Role> roles;


	@Override
    public String toString() {
        return "User{" +
               "id='" + id + '\'' +
               ", name='" + firstName + '\'' +
               ", email='" + email + '\'' +
               ", role='" + role + '\'' +
               '}';
    }


}
