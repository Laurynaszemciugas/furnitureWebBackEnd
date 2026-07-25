package com.example.jwt_demo.Common;

import com.example.jwt_demo.Entity.Materials;
import com.example.jwt_demo.Entity.Orders;
import com.example.jwt_demo.Entity.StockMovement;
import com.example.jwt_demo.Entity.User;
import com.example.jwt_demo.Enums.Type;
import com.example.jwt_demo.repository.MaterialRepository;
import com.example.jwt_demo.repository.OrderRepository;
import com.example.jwt_demo.repository.StockMovementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class Logic {

    @Autowired
    StockMovementRepository stockMovementRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    MaterialRepository materialRepository;

    public LocalDateTime dateConverter(LocalDate givenDate){


        if (givenDate == null || givenDate.equals(LocalDate.of(1000, 12, 12))) {
            return null;
        }

            String fromInput = String.format("%s %s", givenDate, "13:42:46.614631");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");

            return LocalDateTime.parse(fromInput, formatter);

    }


    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }



    public void materialMovementTracker(Long userId,Long orderId, Long materialId, Long stockWas, Long stockNew ){

        Orders orders = orderRepository.findById(orderId).orElseThrow();
        Materials materials = materialRepository.findById(materialId).orElseThrow();


        StockMovement stockMovement = new StockMovement();
        stockMovement.setMaterials(materials);
        stockMovement.setOrders(orders);


        Long diffrence = Math.abs(stockWas - stockNew);



        if(stockNew > stockWas){

            stockMovement.setType(Type.IN);
            stockMovement.setAmountTakeAdd(Math.abs(stockWas - stockNew));
            stockMovement.setBalance(stockWas + diffrence);

        }
        if(stockNew < stockWas){

            stockMovement.setType(Type.OUT);
            stockMovement.setAmountTakeAdd(Math.abs(stockWas - stockNew));
            stockMovement.setBalance(Math.abs(stockWas - diffrence));
        }

        stockMovementRepository.save(stockMovement);


    }


}
