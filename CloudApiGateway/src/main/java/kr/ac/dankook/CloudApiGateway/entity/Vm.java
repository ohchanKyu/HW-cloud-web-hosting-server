package kr.ac.dankook.CloudApiGateway.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Vm extends BaseEntity{

    @Id
    private String id;

    @OneToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;
}
