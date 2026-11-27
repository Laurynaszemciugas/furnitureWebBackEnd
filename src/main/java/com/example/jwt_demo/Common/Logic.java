package com.example.jwt_demo.Common;

import com.example.jwt_demo.Entity.*;
import com.example.jwt_demo.Enums.ImageLogic;
import com.example.jwt_demo.Enums.NotificationTypes;
import com.example.jwt_demo.Enums.Type;
import com.example.jwt_demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class Logic {

    @Autowired
    StockMovementRepository stockMovementRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    MaterialRepository materialRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    NotificationRepository notificationRepository;

    public LocalDateTime dateConverter(LocalDate givenDate) {

        if (givenDate == null || givenDate.equals(LocalDate.of(1000, 12, 12))) {
            return null;
        }

        return givenDate.atStartOfDay();
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

    public void addNewNotification(String image, String notificationName, String message, String highlightedText, NotificationTypes type, Long itemId, Long sender, Long receiver){


        User senderUser = null;

        if(sender != null) {
            senderUser = userRepository.findById(sender).orElseThrow();
        }

        User receiverUser = null;

        if(receiver != null) {
            receiverUser = userRepository.findById(receiver).orElseThrow();
        }

        Notifications notification = new Notifications();
        notification.setNotificationImage(image);
        notification.setNotificationName(notificationName);
        notification.setMessage(message);
        notification.setMessageHighLightedText(highlightedText);
        notification.setNotificationType(type);
        notification.setItemId(itemId);
        notification.setSender(senderUser);
        notification.setReceiver(receiverUser);

        notification.setReadedNotification(false);

        notificationRepository.save(notification);

    }



    public String extractMainImage(List<ProductImageData> productImage, List<MaterialImageData> materialImage){

        String mainImage = "No_picture.png";

        if(productImage == null){
            for(var s : materialImage){
                if(s.getImageLogic().equals(ImageLogic.Main)){
                    mainImage = s.getImageUrl();
                }
            }
        }
        else{

            for(var s : productImage){
                if(s.getImageLogic().equals(ImageLogic.Main)){
                    mainImage = s.getImageUrl();
                }
            }

        }

        return mainImage;

    }


}
