package org.gafiev.peertopeerbazaar.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@OpenAPIDefinition(
        info = @Info(
                title = "PeerToPeerBazaar",
                description = "",
                version = "1.0.0",
                contact = @Contact(
                        name = "Marat Gafiev",
                        email = "marat.gafiev109@gmail.com",
                        url = "https://github.com/maratgmk"
                )
        )
)
@SecurityScheme(name = "JWT",type = SecuritySchemeType.HTTP,bearerFormat = "JWT",scheme = "bearer")
public class OpenApiConfig {

}
