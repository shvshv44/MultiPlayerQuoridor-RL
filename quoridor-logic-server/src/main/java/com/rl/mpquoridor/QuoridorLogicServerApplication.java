package com.rl.mpquoridor;

import com.rl.mpquoridor.models.Game;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class QuoridorLogicServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(QuoridorLogicServerApplication.class, args);
		new Game();
	}

}
