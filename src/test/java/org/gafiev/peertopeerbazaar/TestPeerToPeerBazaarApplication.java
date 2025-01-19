package org.gafiev.peertopeerbazaar;

import org.springframework.boot.SpringApplication;

public class TestPeerToPeerBazaarApplication {

    public static void main(String[] args) {
        SpringApplication.from(PeerToPeerBazaarApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
