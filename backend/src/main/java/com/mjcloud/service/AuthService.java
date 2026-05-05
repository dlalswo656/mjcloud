package com.mjcloud.service;

import com.mjcloud.dto.AuthDto;
import com.mjcloud.entity.User;
import com.mjcloud.repository.UserRepository;
import com.mjcloud.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthDto.TokenResponse signup(AuthDto.SignupRequest request) {
        // 아이디 중복 체크
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("이미 사용 중인 아이디입니다.");
        }
        // 아이디 형식 체크 (4~20자 영문/숫자)
        if (!request.getUsername().matches("^[a-zA-Z0-9]{4,20}$")) {
            throw new RuntimeException("아이디는 4~20자 영문/숫자만 가능합니다.");
        }
        // 비밀번호 길이 체크
        if (request.getPassword().length() < 6) {
            throw new RuntimeException("비밀번호는 6자 이상이어야 합니다.");
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();
        userRepository.save(user);

        String token = jwtTokenProvider.generateToken(user.getUsername());
        return new AuthDto.TokenResponse(token, user.getUsername(), user.getId());
    }

    public AuthDto.TokenResponse login(AuthDto.LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("아이디 또는 비밀번호가 올바르지 않습니다."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("아이디 또는 비밀번호가 올바르지 않습니다.");
        }

        String token = jwtTokenProvider.generateToken(user.getUsername());
        return new AuthDto.TokenResponse(token, user.getUsername(), user.getId());
    }
}
