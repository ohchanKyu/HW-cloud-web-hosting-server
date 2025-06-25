package kr.ac.dankook.CloudApiGateway.config.principal;

import kr.ac.dankook.CloudApiGateway.entity.Member;
import kr.ac.dankook.CloudApiGateway.exception.ApiErrorCode;
import kr.ac.dankook.CloudApiGateway.exception.ApiException;
import kr.ac.dankook.CloudApiGateway.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PrincipalDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Member> memberEntity = memberRepository.findByUserId(username);
        return memberEntity.map(PrincipalDetails::new)
                .orElseThrow(() -> new ApiException(
                        ApiErrorCode.MEMBER_NOT_FOUND
                ));
    }
}
