package com.example.jwt_demo.Common;

import com.example.jwt_demo.Entity.ActionTracker;
import com.example.jwt_demo.Entity.User;
import com.example.jwt_demo.Enums.ActionDesciptionEnum;
import com.example.jwt_demo.Enums.ActionTrackerEnum;
import com.example.jwt_demo.repository.ActionTrackerRepository;
import com.example.jwt_demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ActionMaker {

    @Autowired
    ActionTrackerRepository actionTrackerRepository;

    @Autowired
    UserRepository userRepository;

    public void makeAction(String actionName, Long user, Long whoMadeAction, ActionTrackerEnum actionType, ActionDesciptionEnum actionDesc){

        ActionTracker action = new ActionTracker();
        action.setActionName(actionName);
        action.setUser(userRepository.findById(user).orElseThrow());
        action.setWhoMadeIt(whoMadeAction == null ? userRepository.findById(user).orElseThrow() : userRepository.findById(whoMadeAction).orElseThrow());
        action.setTypeOfActionRecorded(actionType);
        action.setAction(actionDesc);

        User user1 = userRepository.findById(user).orElseThrow();
        action.setName(user1.getName());


        actionTrackerRepository.save(action);

    }

}
