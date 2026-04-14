package cl.duocuc.biblioteca.bff;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class BibliotecaBffApplication {

    public static void main(String[] args) {
        SpringApplication.run(BibliotecaBffApplication.class, args);
    }
}
