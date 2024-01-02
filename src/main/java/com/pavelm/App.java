package com.pavelm;

import com.pavelm.communication.Communication;
import com.pavelm.config.Config;
import com.pavelm.entity.User;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.http.HttpMethod;

import java.util.List;

public class App {
    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(Config.class)) {
            Communication communication = context.getBean(Communication.class);

            List<User> userList = communication.getAllUsers();
            System.out.println("All users: " + userList);


            User user = new User(3L, "James", "Brown", (byte) 25);
            communication.createUser(user);
            System.out.println("User created: " + user);
            String info2 = communication.performActionAndGetInfo(HttpMethod.POST, user);

            user.setName("Thomas");
            user.setLastName("Shelby");
            communication.updateUser(user);
            System.out.println("User updated: " + user);
            String info3 = communication.performActionAndGetInfo(HttpMethod.PUT, user);

            communication.deleteUser(3L);
            System.out.println("User deleted");
            String info4 = communication.performActionAndGetInfo(HttpMethod.DELETE, 3L);


            System.out.println(info2);
            System.out.println(info3);
            System.out.println(info4);
        }
    }
}
