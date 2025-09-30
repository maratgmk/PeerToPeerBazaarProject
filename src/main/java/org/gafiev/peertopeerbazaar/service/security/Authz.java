package org.gafiev.peertopeerbazaar.service.security;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gafiev.peertopeerbazaar.entity.user.Role;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component("authz")
@RequiredArgsConstructor
@Slf4j
public class Authz {
    private static final Set<String> SELF_ROLES = Set.of(Role.USER, Role.AUTHOR, Role.SELLER, Role.BUYER).stream()
            .map(role -> "ROLE_" + role.name())
            .collect(Collectors.toSet());

    public boolean isSelf(@Nullable Long id, Authentication authentication) {
        if(id == null){
            log.info("isSelf = false");
            return false;
        }
        boolean isNotAllowedRole = authentication.getAuthorities().stream()
                .noneMatch(auth -> SELF_ROLES.contains(auth.getAuthority()));
        if (isNotAllowedRole) {
            log.info("isSelf = false");
            return false;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof Jwt jwt) {
            Object claim = jwt.getClaim("uid");
            if (claim instanceof Number number) {
                boolean isSame = number.longValue() == id;
                log.info("isSelf = " + (isSame));
                return isSame;
            }
            if (claim instanceof String str) {
                boolean isSame = Long.parseLong(str) == id;
                log.info("isSelf = " + (isSame));
                return isSame;
            }
        }
        log.info("isSelf = false");
        return false;
    }
}
