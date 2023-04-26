package com.dalcho.adme.service.Impl;

import com.dalcho.adme.common.CommonResponse;
import com.dalcho.adme.config.security.JwtTokenProvider;
import com.dalcho.adme.domain.User;
import com.dalcho.adme.dto.sign.SignInRequestDto;
import com.dalcho.adme.dto.sign.SignInResultDto;
import com.dalcho.adme.dto.sign.SignUpRequestDto;
import com.dalcho.adme.dto.sign.SignUpResultDto;
import com.dalcho.adme.exception.duplicate.UserDuplicateIdException;
import com.dalcho.adme.exception.invalid.InvalidPasswordException;
import com.dalcho.adme.exception.invalid.InvalidPatternException;
import com.dalcho.adme.exception.invalid.InvalidTokenException;
import com.dalcho.adme.exception.notfound.UserNotFoundException;
import com.dalcho.adme.repository.UserRepository;
import com.dalcho.adme.service.SignService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service("SignService")
public class SignServiceImpl implements SignService {

    public final UserRepository userRepository;
    public final JwtTokenProvider jwtTokenProvider;
    public final PasswordEncoder passwordEncoder;

    @Autowired
    public SignServiceImpl(UserRepository userRepository, JwtTokenProvider jwtTokenProvider, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
    }

    @Value("${token.admin}")
    private String ADMIN_TOKEN;

    @Override
    public SignUpResultDto signUp(SignUpRequestDto signUpRequestDto) {
        String nickname = signUpRequestDto.getNickname();
        String password = signUpRequestDto.getPassword();
        String name = signUpRequestDto.getName();
        String email = signUpRequestDto.getEmail();

        log.info("[getSignUpResult] 회원 정보 유무 확인");
        if (userRepository.existsByNickname(nickname)) {
            throw new UserDuplicateIdException();
        }
        log.info("[getSignUpResult] 회원 정보 유무 확인 완료");

        log.info("[getSignUpResult] email,password 패턴 확인");
        patternCheck("\\w+@\\w+\\.\\w+(\\.\\w+)?", email); // ex) abcd@add.com
        patternCheck("(?=.*[a-zA-Z])(?=.*\\d)(?=.*\\W).{8,20}", password); // 영문과 특수문자 숫자를 포함하며 8자 이상
        log.info("[getSignUpResult] email,password 패턴 확인 완료");

        List<String> role = Collections.singletonList("ROLE_USER"); // 변경 불가능한 요소("ROLE_USER") 생성

        if (!Objects.equals(signUpRequestDto.getAdminToken(), "")) {
            if (!signUpRequestDto.getAdminToken().equals(ADMIN_TOKEN)) {
                throw new InvalidTokenException();
            }
            role = Collections.singletonList("ROLE_ADMIN");
        }
        log.info("[getSignUpResult] 권한 확인 : " + role);

        log.info("[getSignUpResult] 회원 가입 정보 전달");
        User user = User.builder()
                .nickname(nickname)
                .name(name)
                .password(passwordEncoder.encode(password))
                .email(email)
                .roles(role)
                .build();

        User savedUser = userRepository.save(user);

        SignUpResultDto signUpResultDto = new SignInResultDto();
        log.info("[getSignUpResult] userEntity 값이 들어왔는지 확인 후 결과값 주입");

        if (!savedUser.getName().isEmpty()) {
            log.info("[getSignUpResult] 정상 처리 완료");
            setSuccessResult(signUpResultDto);
        } else {
            log.info("[getSignUpResult] 실패 처리 완료");
            setFailResult(signUpResultDto);
        }

        return signUpResultDto;
    }

    @Override
    public SignInResultDto signIn(SignInRequestDto signInRequestDto) throws RuntimeException {
        log.info("[getSignInResult] signDataHandler 로 회원 정보 요청");
        User user = userRepository.findByNickname(signInRequestDto.getNickname()).orElseThrow(() -> {
            throw new UserNotFoundException();
        });

        log.info("[getSignInResult] Id : {}", signInRequestDto.getNickname());

        log.info("[getSignInResult] 패스워드 비교 수행");
        if (!passwordEncoder.matches(signInRequestDto.getPassword(), user.getPassword())) {
            throw new InvalidPasswordException();
        }
        log.info("[getSignInResult] 패스워드 일치");

        log.info("[getSignUpResult] 권한 확인 : " + user.getRoles());
        String authority = "일반사용자";

        if (user.getRoles().equals("ROLE_ADMIN")) {
            authority = "관리자";
        }

        log.info("[getSignInResult] SignInResultDto 객체 생성");
        SignInResultDto signInResultDto = SignInResultDto.builder()
                .role_check(authority)
                .token(jwtTokenProvider.createToken(user.getNickname(), user.getRoles()))
                .build();

        log.info("[getSignInResult] SignInResultDto 객체에 값 주입");
        setSuccessResult(signInResultDto);

        return signInResultDto;
    }

    // 결과 모델에 api 요청 성공 데이터를 세팅해주는 메소드
    private void setSuccessResult(SignUpResultDto result) {
        result.setSuccess(true);
        result.setCode(CommonResponse.SUCCESS.getCode());
        result.setMsg(CommonResponse.SUCCESS.getMsg());
    }

    // 결과 모델에 api 요청 실패 데이터를 세팅해주는 메소드
    private void setFailResult(SignUpResultDto result) {
        result.setSuccess(false);
        result.setCode(CommonResponse.FAIL.getCode());
        result.setMsg(CommonResponse.FAIL.getMsg());
    }

    private void patternCheck(String regex, String checkStr) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(checkStr);
        if (!matcher.find()) {
            throw new InvalidPatternException();
        }
    }
}
