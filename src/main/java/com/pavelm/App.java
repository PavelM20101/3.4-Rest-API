package com.pavelm;

import com.pavelm.communication.Communication;
import com.pavelm.config.Config;
import com.pavelm.entity.User;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;

public class App {
    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(Config.class)){
            Communication communication = context.getBean(Communication.class);

            List<User> userList = communication.getAllUsers();

            User user = new User("James", "Brown", (byte)25);
//            communication.saveUser(user);
            communication.createUser(user);


            user.setName("Thomas");
            user.setLastName("Shelby");
//            communication.saveUser(user);
            communication.updateUser(user);

            communication.deleteUser(3L);
        }
    }
}