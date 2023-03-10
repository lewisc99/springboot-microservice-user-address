package com.lewis.user.controller;


import com.lewis.user.config.rabbitmq.rabbitmqConstraints;
import com.lewis.user.models.User;
import com.lewis.user.models.dto.UserUpdateDto;
import com.lewis.user.models.events.UserUpdatedEvent;
import com.lewis.user.service.UserService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RefreshScope
@RequestMapping("api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RabbitTemplate rabbitTemplate;



    @GetMapping
    public ResponseEntity<List<User>> get()
    {
       List<User> users =  userService.getAll();

       if (users == null)
       {
           return ResponseEntity.badRequest().build();
       }

       return ResponseEntity.ok(users);
    }


    @GetMapping("/{id}")
    public ResponseEntity<User> getById(@PathVariable Integer id)
    {
        Optional<User> user = userService.getById(id);

        if (user.isEmpty())
        {
            return ResponseEntity.status(404).build();
        }

        return ResponseEntity.ok(user.get());
    }


    @PutMapping("{id}")
    public ResponseEntity<UserUpdateDto> update(@PathVariable Integer id, @RequestBody UserUpdateDto updateDto)
    {

        Optional<User> user = userService.getById(id);

        if (user.isEmpty())
        {
            return ResponseEntity.notFound().build();
        }

        userService.Update(user.get(), updateDto );

        updateDto.setId(user.get().getId());
        updateDto.setDoc(user.get().getDoc());

        UserUpdatedEvent userUpdatedEvent = new UserUpdatedEvent(id,updateDto.getName());
        rabbitTemplate.convertAndSend(rabbitmqConstraints.USER_EXCHANGE_NAME,rabbitmqConstraints.USER_ROUTING_KEY,userUpdatedEvent);

       return ResponseEntity.status(200).body(updateDto);

    }
}
