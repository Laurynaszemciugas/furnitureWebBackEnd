package com.example.jwt_demo.Common.OrderAcceptance;

import com.example.jwt_demo.Common.ActionMaker;
import com.example.jwt_demo.Common.DatabaseChecks;
import com.example.jwt_demo.Common.ErrorResponse;
import com.example.jwt_demo.Common.ProvidedDataChecker;
import com.example.jwt_demo.Entity.*;
import com.example.jwt_demo.Entity.EmployeeJoin.OrderEmployees;
import com.example.jwt_demo.Entity.OrderJoin.OrderProducts;
import com.example.jwt_demo.Entity.OrderJoin.OrderStepsToComplete;
import com.example.jwt_demo.Enums.*;
import com.example.jwt_demo.GlobalExseptions.Exseptions.ValidationException;
import com.example.jwt_demo.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class OrderAcceptingModels {

    @Autowired
    DatabaseChecks databaseChecks;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    MaterialRepository materialRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    ActionMaker actionMaker;

    @Autowired
    ProvidedDataChecker providedDataChecker;

    Map<Long,Integer> countTheTimesAccordingToUser = new HashMap<>();



    // ===============================================================
    // First come first serve algorithm
    // ===============================================================

    public ResponseEntity<ErrorResponse> saveNewOrder(Orders order, User whoMakesThisOrder){






        Orders newOrder = putInfoIntoNewOrder(order,whoMakesThisOrder);






        // get creator which is admin in this case
        User creator = userRepository.findById(whoMakesThisOrder.getId()).orElseThrow();
        // if buyer not found then system cant pinpoint to whom it is needed not big deal it will be null
        User buyer = userRepository.findByGmail(order.getOrderCreatedByGmail());
        newOrder.setUser(creator);
        newOrder.setOrderPlacedBy(buyer);
        if(buyer == null){
            int times = 1;
            if (!countTheTimesAccordingToUser.isEmpty() && countTheTimesAccordingToUser.get(newOrder.getId()).equals(1)) {
                countTheTimesAccordingToUser.remove(newOrder.getId());

                orderRepository.save(newOrder);
                databaseChecks.calculateMaterialsStock(newOrder.getId());
                databaseChecks.checkNewAddedOrder(newOrder.getId(),false);
                databaseChecks.addReserveFromCreatedOrder(newOrder.getId());
                databaseChecks.calculateProductsStock(null, false);


                return ResponseEntity.ok(new ErrorResponse(String.format("Order [ORD-%d] was created successfully", newOrder.getId()), Warnings.OK));
            }
            countTheTimesAccordingToUser.put(newOrder.getId(),times);
            throw new ValidationException(order.getOrderCreatedByGmail() + " is not found this is not necessary (PRESS AGAIN TO CONFIRM) ", Warnings.WARNING);
        }












        actionMaker.makeAction(String.format("Order [ORD-%d] was created successfully",newOrder.getId()),whoMakesThisOrder.getId(),null, ActionTrackerEnum.USER, ActionDesciptionEnum.Order_Created);


        return ResponseEntity.ok(new ErrorResponse(String.format("Order [ORD-%d] was created successfully and awaiting further actions",newOrder.getId()),Warnings.OK));
    }


    // ===============================================================
    // Timed maximize order thingy
    // ===============================================================

    @Scheduled(cron = "0 */60 * * * MON-FRI")
    @Transactional
    public void startTheMaximeOrder(){

        List<Long> users = userRepository.getIdsOfTheOrderProcessing(OrderProcessing.MAXIMIZE_ORDER.toString());



        if(users.isEmpty()){
            return;
        }

        for(var us : users){

            List<Orders> newOrders = orderRepository.getAllNewOrder(us);

            List<Materials> allMaterials = materialRepository.getAllMaterials(us);

            acceptOrdersAsManyAsPossible(newOrders,allMaterials);

        }
    }

    // ===============================================================
    // accept as many orders algorithm
    // ===============================================================
    @Transactional
    public void acceptOrdersAsManyAsPossible( List<Orders> newOrders, List<Materials> allMaterials) {


        List<Long> acceptedOrders = new ArrayList<>();
        List<Long> bannedOrders = new ArrayList<>();



        boolean stillPossible = true;

        while (stillPossible) {


            System.out.println("");
            System.out.println("");
            System.out.println("");


            List<MostOrders> orderCalculation = new ArrayList<>();


            for (var orders : newOrders) {

                Long orderId = orders.getId();
                Double percentageNeeded = 0.0;


                //skip banned orders
                boolean skip = bannedOrders.contains(orderId);

                if (skip) {
                    continue;
                }


                for (var productData : orders.getProductsData()) {

                    Product product = productRepository.findById(productData.getProduct().getId()).orElseThrow();

                    Long productAmountNeeded = productData.getAmountOfProduct();

                    for (var materials : product.getMaterials()) {

                        Long materialId = materials.getMaterials().getId();

                        Long materialNeeded = materials.getAmountUsed();

                        Long totalMaterialNeeded = productAmountNeeded * materialNeeded;

                        for (var s : allMaterials) {
                            if (s.getId().equals(materialId)) {

                                Long availableMaterial = s.getInStock();

                                // remove the order if its taking more or equal items
                                if (totalMaterialNeeded >= availableMaterial) {
                                    bannedOrders.add(orderId);
                                    break;
                                } else {

                                    System.out.println(orderId);
                                    System.out.println("Material " + materials.getMaterials().getMaterialName());
                                    System.out.println("Total material neeeded " + totalMaterialNeeded);
                                    System.out.println("Total material exists " + availableMaterial);
                                    System.out.println("Percentage taken " + ((double) totalMaterialNeeded / (double) availableMaterial) * 100);
                                    System.out.println("---------------------------------------------");

                                    percentageNeeded += ((double) totalMaterialNeeded / (double) availableMaterial) * 100;
                                }

                            }
                        }


                    }

                    orderCalculation.add(new MostOrders(orders, percentageNeeded));
                }

            }


            System.out.println("===========Totals================================");
            System.out.println("===========Totals================================");
            System.out.println("===========Totals================================");

            orderCalculation.sort(Comparator.comparing(MostOrders::getPercentage));


            for (var s : orderCalculation) {
                System.out.println(s.getOrders().getId());
                System.out.println(s.getPercentage());
            }


            MostOrders acceptedOrder;

            try {
                acceptedOrder = orderCalculation.get(0);
            } catch (Exception e) {

                System.out.println("No more orders present");

                break;
            }

            boolean canOrderBeAccepted = true;

            for (var productData : acceptedOrder.getOrders().getProductsData()) {


                Product product = productRepository.findById(productData.getProduct().getId()).orElseThrow();

                Long productAmountNeeded = productData.getAmountOfProduct();

                for (var material : product.getMaterials()) {


                    Materials inTheDb = new Materials();

                    for (var materialInDb : allMaterials) {
                        if (material.getMaterials().getId().equals(materialInDb.getId())) {
                            inTheDb = materialInDb;
                        }
                    }

                    Long materialNeeded = material.getAmountUsed();

                    Long totalMaterialNeeded = productAmountNeeded * materialNeeded;


                    Long materialStock = inTheDb.getInStock();

                    Long newStock = materialStock - totalMaterialNeeded;

                    if (newStock < 0) {

                        canOrderBeAccepted = false;

                    }

                }


            }

            if (canOrderBeAccepted) {
                acceptedOrders.add(acceptedOrder.getOrders().getId());
                bannedOrders.add(acceptedOrder.getOrders().getId());
            } else {
                Orders order = orderRepository.findById(acceptedOrder.getOrders().getId()).orElseThrow();
                order.setOrderStatus(OrderStatus.LACK_OF_SUPPLY);
                orderRepository.save(order);
                bannedOrders.add(acceptedOrder.getOrders().getId());
            }




        }

        System.out.println("===========Acepting order one smalest and recount================================");
        for (var accepted : acceptedOrders) {
            System.out.println(accepted);

            Orders order = orderRepository.findById(accepted).orElseThrow();
            order.setOrderStatus(OrderStatus.AWAITING_CONFIRMATION);


            for (OrderProducts orderProduct : order.getProductsData()) {

                if (orderProduct.getOrderSteps() == null ||
                        orderProduct.getOrderSteps().isEmpty()) {

                         order.setProductsData(stepCreator(order, order));

                }
            }


            orderRepository.save(order);

            databaseChecks.calculateMaterialsStock(order.getId());
            databaseChecks.checkNewAddedOrder(order.getId(),false);
            databaseChecks.addReserveFromCreatedOrder(order.getId());
            databaseChecks.calculateProductsStock(null, false);



        }

    }


    // ===============================================================
    // Create order bones of it
    // ===============================================================

    public Orders putInfoIntoNewOrder(Orders order, User whoMakesThisOrder){


        // checks if there is any null or is empty values
        providedDataChecker.checkEmptyValue(order, Orders.class);

        Orders newOrder = new Orders();

        newOrder.setOrderNote(order.getOrderNote());
        newOrder.setOrderStatus(OrderStatus.NEW);
        newOrder.setActiveInactive(ActiveInactive.ACTIVE);
        newOrder.setPayMethod(order.getPayMethod());
        newOrder.setPayStatus(order.getPayStatus());
        newOrder.setBillingAddress(order.getBillingAddress());
        newOrder.setPhoneNumber(order.getPhoneNumber());
        newOrder.setEstimatedDueDate(order.getEstimatedDueDate());
        newOrder.setOrderCreatedByGmail(order.getOrderCreatedByGmail());
        newOrder.setOrderCreatedByName(order.getOrderCreatedByName());
        newOrder.setCreatedDate(LocalDate.now());
        newOrder.setUser(userRepository.findById(whoMakesThisOrder.getId()).orElseThrow());



        if(order.getProductsData() == null || order.getProductsData().isEmpty()){
            throw new ValidationException("No products are added please add products to continue", Warnings.ERROR);
        }
        else{

            Double totalPrice = 0.0;
            for(var s : order.getProductsData()){
                totalPrice = 0.0;
                Product product = productRepository.findById(s.getProduct().getId()).orElseThrow();
                totalPrice+= s.getAmountOfProduct()* product.getPrice();
            }
            newOrder.setTotalPrice(totalPrice);


        }







        if(order.getEmployees() == null || order.getEmployees().isEmpty()){
            throw new ValidationException("No employees are selected", Warnings.ERROR);
        }
        else{
            List<OrderEmployees> employees = new ArrayList<>();
            for(var s : order.getEmployees()){

                if(s.getEmployee().getId() == null){
                    throw new ValidationException("Employee doesnt have an id", Warnings.FATAL_ERROR);
                }
                Employee employee = employeeRepository.findById(s.getEmployee().getId()).orElseThrow(()-> new ValidationException("Employee not found", Warnings.ERROR));

                OrderEmployees orderEmployees = new OrderEmployees();
                orderEmployees.setEmployee(employee);
                orderEmployees.setOrder(newOrder);


                employees.add(orderEmployees);
            }
            newOrder.setEmployees(employees);
        }

        // steps



        newOrder.setProductsData(stepCreator(order,newOrder));


        return newOrder;

    }


    // ===============================================================
    // Create steps always add package at the end
    // ===============================================================

    public List<OrderProducts> stepCreator(Orders order, Orders newOrder){


        List<OrderProducts> products = new ArrayList<>();
        for(var s : order.getProductsData()){



            if(s.getProduct().getId() == null){
                throw new ValidationException("Product doesnt have an id", Warnings.FATAL_ERROR);
            }
            Product product = productRepository.findById(s.getProduct().getId()).orElseThrow(()-> new ValidationException("Product not found", Warnings.ERROR));

            if (s.getAmountOfProduct() <= 0 || s.getAmountOfProduct() >= 100) {
                throw  new ValidationException("Product quantity can only be from 1 to 99", Warnings.ERROR);
            }
//                if(product.getStockQuantity() < s.getAmountOfProduct()){
//                    throw new ValidationException(String.format("Order is not possible due to [%s] having less stock that is needed to fill the order | AVAILABLE STOCK %d | NEEDED STOCK %d",product.getProductName(),product.getStockQuantity(),s.getAmountOfProduct()), Warnings.ERROR);
//                }


            OrderProducts orderProducts = new OrderProducts();
            orderProducts.setProduct(product);
            orderProducts.setOrder(newOrder);
            orderProducts.setCost(materialCost(s.getProduct().getId(), s.getAmountOfProduct()));
            orderProducts.setAmountOfProduct(s.getAmountOfProduct());



            List<OrderStepsToComplete> orderSteps = new ArrayList<>();

            Long sizeOfTheSteps = Long.valueOf(product.getSteps().size());
            Long i = 0L;

            for (var step : product.getSteps()) {



                OrderStepsToComplete orderStep = new OrderStepsToComplete();


                if(product.isStockCalculatedManually()){
                    orderStep.setProductFinishStepStatus(
                            ProductFinishStepStatus.NOT_STARTED
                    );

                    orderStep.setStepsNeeded(orderProducts.getAmountOfProduct());
                    orderStep.setStepsCompleted(0L);

                    orderStep.setStepId(1L);
                    orderStep.setStepName("Package the product");
                    orderStep.setStepDescription("Package the product using the styro foam bubble rap");

                    orderStep.setOrderProducts(orderProducts);

                    orderSteps.add(orderStep);
                    break;
                }
                else {


                    orderStep.setProductFinishStepStatus(
                            ProductFinishStepStatus.NOT_STARTED
                    );

                    orderStep.setStepsNeeded(orderProducts.getAmountOfProduct());
                    orderStep.setStepsCompleted(0L);

                    orderStep.setStepRealId(step.getId());
                    orderStep.setStepId(step.getStepId());
                    orderStep.setStepName(step.getStepName());
                    orderStep.setStepDescription(step.getStepDescription());

                    orderStep.setOrderProducts(orderProducts);

                    orderSteps.add(orderStep);
                }

                i++;

                if(i.equals(sizeOfTheSteps)){

                    OrderStepsToComplete packageStep = new OrderStepsToComplete();

                    packageStep.setProductFinishStepStatus(
                            ProductFinishStepStatus.NOT_STARTED
                    );

                    packageStep.setStepsNeeded(orderProducts.getAmountOfProduct());
                    packageStep.setStepsCompleted(0L);

                    packageStep.setStepId(step.getStepId()+1);
                    packageStep.setStepName("Package the product");
                    packageStep.setStepDescription("Package the product using the styro foam bubble rap");

                    packageStep.setOrderProducts(orderProducts);

                    orderSteps.add(packageStep);
                }

            }

            orderProducts.setOrderSteps(orderSteps);




            products.add(orderProducts);
        }



        return products;

    }



    // ===============================================================
    // Calculate material cost according to product
    // ===============================================================
    public Double materialCost(Long productId, Long amountTaken){

        Product product = productRepository.findById(productId).orElseThrow();


        return product.getPrice() * amountTaken;

    }



}
