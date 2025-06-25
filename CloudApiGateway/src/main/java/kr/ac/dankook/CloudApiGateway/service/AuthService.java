package kr.ac.dankook.CloudApiGateway.service;

import kr.ac.dankook.CloudApiGateway.dto.request.LoginRequest;
import kr.ac.dankook.CloudApiGateway.dto.request.RegisterRequest;
import kr.ac.dankook.CloudApiGateway.dto.response.TokenResponse;
import kr.ac.dankook.CloudApiGateway.entity.Member;
import kr.ac.dankook.CloudApiGateway.exception.ApiErrorCode;
import kr.ac.dankook.CloudApiGateway.exception.ApiException;
import kr.ac.dankook.CloudApiGateway.jwt.JwtTokenProvider;
import kr.ac.dankook.CloudApiGateway.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional(readOnly = true)
    public boolean isExistUserIdProcess(String userId){
        return memberRepository.existsByUserId(userId);
    }

    @Transactional(readOnly = true)
    public Member findMemberByUserIdProcess(String userId){
        Optional<Member> targetMember = memberRepository.findByUserId(userId);
        if (targetMember.isPresent()) return targetMember.get();
        throw new ApiException(ApiErrorCode.MEMBER_NOT_FOUND);
    }

    @Transactional
    public void registerProcess(RegisterRequest registerRequest){

        if (isExistUserIdProcess(registerRequest.getUserId())){
            throw new ApiException(ApiErrorCode.DUPLICATE_ID);
        }
        String encodePassword = passwordEncoder.encode(registerRequest.getPassword());
        Member newMember = Member.builder()
                .name(registerRequest.getName())
                .password(encodePassword)
                .userId(registerRequest.getUserId())
                .roles("ROLE_USER")
                .build();
        memberRepository.save(newMember);
        memberRepository.flush();
    }

    @Transactional
    public TokenResponse loginProcess(LoginRequest loginRequest) {

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginRequest.getUserId(), loginRequest.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        return jwtTokenProvider.generateToken(authentication);
    }
}
