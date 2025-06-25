package kr.ac.dankook.CloudApiGateway.repository;


import kr.ac.dankook.CloudApiGateway.entity.Member;
import kr.ac.dankook.CloudApiGateway.entity.Vm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VmRepository extends JpaRepository<Vm,Long> {
    Optional<Vm> findByMember(Member member);
}
