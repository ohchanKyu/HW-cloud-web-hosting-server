package kr.ac.dankook.CloudApiGateway.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.util.Arrays;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Member extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name; // User name
    private String userId; // User Id
    private String password; // User password
    private String roles; // User roles ADMIN,USER
    public List<String> getRoleList(){
        if (!this.roles.isEmpty()){
            return Arrays.asList(this.roles.split(","));
        }
        return List.of();
    }
}
