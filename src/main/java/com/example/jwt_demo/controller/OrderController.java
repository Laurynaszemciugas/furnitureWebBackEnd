package com.example.jwt_demo.controller;

import com.example.jwt_demo.Common.*;
import com.example.jwt_demo.DTOS.Common.GraphDataDateValue;
import com.example.jwt_demo.DTOS.Common.MiniStatHolder;
import com.example.jwt_demo.DTOS.Common.ReportMiniStatHolder;
import com.example.jwt_demo.DTOS.DashBoard.ActivityFeedModel;
import com.example.jwt_demo.DTOS.DashBoard.DashBoardMonthlyOrdersCompleted;
import com.example.jwt_demo.DTOS.Order.*;
import com.example.jwt_demo.Entity.*;
import com.example.jwt_demo.Entity.EmployeeJoin.OrderEmployees;
import com.example.jwt_demo.Entity.OrderJoin.OrderProducts;
import com.example.jwt_demo.Entity.ProductJoin.ProductMaterials;
import com.example.jwt_demo.Enums.*;
import com.example.jwt_demo.FilterDTO.Order.OrderFilterHolder;
import com.example.jwt_demo.GlobalExseptions.Exseptions.ValidationException;
import com.example.jwt_demo.repository.*;
import com.example.jwt_demo.security.CustomUserDetails;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    Logic logic;

    @Autowired
    ProvidedDataChecker providedDataChecker;

    @Autowired
    DatabaseChecks databaseChecks;

    @Autowired
    Common common;

    @Autowired
    ActionMaker actionMaker;


    @Autowired
    ActionTrackerRepository actionTrackerRepository;

    Map<Long,Integer> countTheTimesAccordingToUser = new HashMap<>();

    @PostMapping("/getAllOrders")
    public ResponseEntity<List<OrdersFeedData>> getAllOrders(@RequestBody OrderFilterHolder orderFilterHolder) {

        CustomUserDetails user = common.getUserData();

        orderFilterHolder = providedDataChecker.defaultValueChecker(orderFilterHolder, OrderFilterHolder.class);

        return ResponseEntity.ok(
                orderRepository.getOrderData(
                        orderFilterHolder.getOrderStatusChoice(),
                        orderFilterHolder.getPriceFromChoice(),
                        orderFilterHolder.getPriceToChoice(),
                        logic.dateConverter(orderFilterHolder.getDateFromChoice()),
                        logic.dateConverter(orderFilterHolder.getDateToChoice()),
                        orderFilterHolder.getAmountOfProductsChoice(),
                        orderFilterHolder.getPromptChoice(),
                        orderFilterHolder.getEmployee(),
                        orderFilterHolder.getProducts(),
                        orderFilterHolder.getOrderActiveInactive(),
                        PageRequest.of(orderFilterHolder.getPage(), orderFilterHolder.getPageCount()),
                        user.getId()
                )
        );
    }


    @PostMapping("/getAllNewOrders")
    public ResponseEntity<List<OrdersFeedData>> getAllNewOrders(@RequestBody OrderFilterHolder orderFilterHolder) {

        CustomUserDetails user = common.getUserData();

        orderFilterHolder.setOrderStatusChoice(OrderStatus.NEW);
        orderFilterHolder = providedDataChecker.defaultValueChecker(orderFilterHolder, OrderFilterHolder.class);


        return ResponseEntity.ok(
                orderRepository.getNewOrders(
                        orderFilterHolder.getOrderStatusChoice(),
                        orderFilterHolder.getPriceFromChoice(),
                        orderFilterHolder.getPriceToChoice(),
                        logic.dateConverter(orderFilterHolder.getDateFromChoice()),
                        logic.dateConverter(orderFilterHolder.getDateToChoice()),
                        orderFilterHolder.getAmountOfProductsChoice(),
                        orderFilterHolder.getPromptChoice(),
                        orderFilterHolder.getEmployee(),
                        orderFilterHolder.getProducts(),
                        orderFilterHolder.getOrderActiveInactive(),
                        PageRequest.of(orderFilterHolder.getPage(), orderFilterHolder.getPageCount()),
                        user.getId()
                )
        );
    }


    @PostMapping("/getAmountOfPages")
    public ResponseEntity<Long> getAmountOfPages(@RequestBody OrderFilterHolder orderFilterHolder) {

        CustomUserDetails user = common.getUserData();


        orderFilterHolder = providedDataChecker.defaultValueChecker(orderFilterHolder, OrderFilterHolder.class);


        Long count = orderRepository.getNumberOfOrderPages(
                orderFilterHolder.getOrderStatusChoice(),
                orderFilterHolder.getPriceFromChoice(),
                orderFilterHolder.getPriceToChoice(),
                logic.dateConverter(orderFilterHolder.getDateFromChoice()),
                logic.dateConverter(orderFilterHolder.getDateToChoice()),
                orderFilterHolder.getAmountOfProductsChoice(),
                orderFilterHolder.getPromptChoice(),
                Double.valueOf(orderFilterHolder.getPageCount()),
                user.getId()

        );






        return ResponseEntity.ok(
                count
        );
    }



    @GetMapping("/deleteOrder/{id}")
    public ResponseEntity<ErrorResponse> deleteOrder(@PathVariable Long id) {

       Orders orders = orderRepository.findById(id).orElseThrow();


       try{

           orderRepository.delete(orders);

           return ResponseEntity.ok(new ErrorResponse("Deleted successfully",Warnings.OK));

       } catch (Exception e) {
           orders.setActiveInactive(ActiveInactive.INACTIVE);
           orderRepository.save(orders);
           return ResponseEntity.ok(new ErrorResponse("Order was set to Inactive",Warnings.OK));
       }



    }



    @GetMapping("/getOrderFromId/{id}")
    public ResponseEntity<Orders> getOrderFromId(@PathVariable Long id){

        return ResponseEntity.ok(orderRepository.findById(id).orElseThrow());
    }

    public Orders copyOrder(Orders original) {

        Orders copy = new Orders();

        copy.setId(original.getId());
        copy.setBillingAddress(original.getBillingAddress());
        copy.setTotalPrice(original.getTotalPrice());
        copy.setOrderNote(original.getOrderNote());
        copy.setOrderStatus(original.getOrderStatus());
        copy.setEstimatedDueDate(original.getEstimatedDueDate());
        copy.setPayMethod(original.getPayMethod());
        copy.setPayStatus(original.getPayStatus());


        // Copy products
        List<OrderProducts> copiedProducts = original.getProductsData()
                .stream()
                .map(oldProduct -> {

                    OrderProducts newProduct = new OrderProducts();

                    newProduct.setId(oldProduct.getId());
                    newProduct.setAmountOfProduct(oldProduct.getAmountOfProduct());
                    newProduct.setCost(oldProduct.getCost());


                    Product productCopy = new Product();
                    productCopy.setStockCalculatedManually(oldProduct.getProduct().isStockCalculatedManually());
                    productCopy.setId(oldProduct.getProduct().getId());
                    productCopy.setProductName(oldProduct.getProduct().getProductName());

                    List<ProductMaterials> copiedMaterials =
                            oldProduct.getProduct().getMaterials()
                                    .stream()
                                    .map(oldMaterial -> {

                                        ProductMaterials newMaterial = new ProductMaterials();
                                        newMaterial.setAmountUsed(oldMaterial.getAmountUsed());



                                        newMaterial.setId(oldMaterial.getId());


                                        Materials materialCopy = new Materials();

                                        materialCopy.setId(
                                                oldMaterial.getMaterials().getId()
                                        );

                                        materialCopy.setMaterialName(
                                                oldMaterial.getMaterials().getMaterialName()
                                        );

                                        materialCopy.setInStock(
                                                oldMaterial.getMaterials().getInStock()
                                        );




                                        newMaterial.setMaterials(materialCopy);

                                        return newMaterial;

                                    })
                                    .toList();


                    productCopy.setMaterials(copiedMaterials);

                    newProduct.setProduct(productCopy);

                    return newProduct;

                })
                .toList();


        copy.setProductsData(copiedProducts);


        // Copy employees
        List<OrderEmployees> copiedEmployees =
                original.getEmployees()
                        .stream()
                        .map(oldEmployee -> {

                            OrderEmployees newEmployee = new OrderEmployees();

                            newEmployee.setId(oldEmployee.getId());
                            newEmployee.setEmployee(oldEmployee.getEmployee());

                            return newEmployee;

                        })
                        .toList();


        copy.setEmployees(copiedEmployees);


        return copy;
    }

    @Transactional
    @PostMapping("/saveModifiedOrder")
    public ResponseEntity<ErrorResponse> saveModifiedOrder(@RequestBody Orders order){

        CustomUserDetails user = common.getUserData();

        Orders sameExistingOrder = orderRepository.findById(order.getId()).orElseThrow();
        Orders nonModified = copyOrder(sameExistingOrder);

        sameExistingOrder.getProductsData().clear();
        sameExistingOrder.getEmployees().clear();



        if(order.getBillingAddress().isEmpty() || order.getBillingAddress() == null){
            throw  new ValidationException("Address is required", Warnings.ERROR);
        }

        sameExistingOrder.setBillingAddress(order.getBillingAddress());



        double totalPrice = 0.0;


        if(order.getProductsData().isEmpty() || order.getProductsData() == null){
            throw  new ValidationException("Existing order cannot be without products ", Warnings.ERROR);
        }

        List<OrderProducts> products = new ArrayList<>();
        for(var s : order.getProductsData()) {
            Long productId = s.getProduct().getId();
            Product existingProduct = productRepository.findById(productId).orElseThrow();

            if (s.getAmountOfProduct() <= 0 || s.getAmountOfProduct() >= 100) {
                throw  new ValidationException("Product quantity can only be from 1 to 99", Warnings.ERROR);
            }
            totalPrice += existingProduct.getPrice() * s.getAmountOfProduct();
            OrderProducts orderProducts = new OrderProducts();
            orderProducts.setProduct(existingProduct);
            orderProducts.setOrder(sameExistingOrder);
            orderProducts.setCost(materialCost(s.getProduct().getId(), s.getAmountOfProduct()));
            orderProducts.setAmountOfProduct(s.getAmountOfProduct());
            products.add(orderProducts);
        }

        sameExistingOrder.getProductsData().addAll(products);

        if(order.getEmployees().isEmpty() || order.getEmployees() == null){
            throw  new ValidationException("Existing order cannot be without employees ", Warnings.ERROR);
        }
        for(var s : order.getEmployees()){
            Long employeeId = s.getEmployee().getId();



            Employee existingEmployee = employeeRepository.findById(employeeId).orElseThrow();


            OrderEmployees orderEmployees = new OrderEmployees();
            orderEmployees.setOrder(sameExistingOrder);
            orderEmployees.setEmployee(existingEmployee);

            sameExistingOrder.getEmployees().add(orderEmployees);

        }

            sameExistingOrder.setTotalPrice(totalPrice);
            sameExistingOrder.setOrderNote(order.getOrderNote());
            sameExistingOrder.setOrderStatus(order.getOrderStatus());
            sameExistingOrder.setEstimatedDueDate(order.getEstimatedDueDate());
            sameExistingOrder.setPayMethod(order.getPayMethod());
            sameExistingOrder.setPayStatus(order.getPayStatus());









            databaseChecks.checkModifiedOrders(sameExistingOrder.getId(),nonModified);
        databaseChecks.calculateProductsStock(null,false);
        databaseChecks.calculateMaterialsStock(order.getId());
        orderRepository.save(sameExistingOrder);

        actionMaker.makeAction(String.format("ORD-%d %s",order.getId(), "was modified and saved successfully"),user.getId(),null,ActionTrackerEnum.USER, ActionDesciptionEnum.Order_Updated);


        orderRepository.incrementProductsFinished(order.getId());

        return ResponseEntity.ok(new ErrorResponse(String.format("ORD-%d %s",order.getId(), "was modified and saved successfully"),Warnings.OK));
    }


    public Double materialCost(Long productId, Long amountTaken){

        Product product = productRepository.findById(productId).orElseThrow();


        return product.getPrice() * amountTaken;

    }

    @PostMapping("/saveNewOrder")
    public ResponseEntity<ErrorResponse> saveNewOrder(@RequestBody Orders order){

        CustomUserDetails user = common.getUserData();

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
        newOrder.setUser(userRepository.findById(user.getId()).orElseThrow());



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


                    products.add(orderProducts);
                }
            newOrder.setProductsData(products);
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


        // get creator which is admin in this case
        User creator = userRepository.findById(user.getId()).orElseThrow();
        // if buyer not found then system cant pinpoint to whom it is needed not big deal it will be null
        User buyer = userRepository.findByGmail(order.getOrderCreatedByGmail());
        newOrder.setUser(creator);
        newOrder.setOrderPlacedBy(buyer);
        if(buyer == null){
            int times = 1;
                if (!countTheTimesAccordingToUser.isEmpty() && countTheTimesAccordingToUser.get(newOrder.getId()).equals(1)) {
                    countTheTimesAccordingToUser.remove(newOrder.getId());

                    orderRepository.save(newOrder);
                    return ResponseEntity.ok(new ErrorResponse(String.format("Order [ORD-%d] was created successfully", newOrder.getId()), Warnings.OK));
            }
            countTheTimesAccordingToUser.put(newOrder.getId(),times);
            throw new ValidationException(order.getOrderCreatedByGmail() + " is not found this is not nessasary (PRESS AGAIN TO CONFIRM) ", Warnings.WARNING);
        }




        orderRepository.save(newOrder);


        databaseChecks.calculateProductsStock(1L,false);


        databaseChecks.calculateMaterialsStock(newOrder.getId());

        actionMaker.makeAction(String.format("Order [ORD-%d] was created successfully",newOrder.getId()),user.getId(),null,ActionTrackerEnum.USER, ActionDesciptionEnum.Order_Created);

        return ResponseEntity.ok(new ErrorResponse(String.format("Order [ORD-%d] was created successfully",newOrder.getId()),Warnings.OK));

    }


    @GetMapping("/getMiniStats/{from}/{to}")
    public ResponseEntity<MiniStatHolder> getOrderMiniStats(@PathVariable LocalDate from, @PathVariable LocalDate to){

        CustomUserDetails user = common.getUserData();

        return ResponseEntity.ok(orderRepository.getOrderMiniStats(logic.dateConverter(from),logic.dateConverter(to), user.getId()));

    }


    @GetMapping("/getNewOrderCount")
    public ResponseEntity<Long> getOrderMiniStats(){

        CustomUserDetails user = common.getUserData();

        return ResponseEntity.ok(orderRepository.findNewOrdersCount(user.getId()));

    }

    @GetMapping("/getGridStuff/{id}")
    public ResponseEntity<List<NewOrderFeedData>> getOrderMiniStats(@PathVariable Long id){

        CustomUserDetails user = common.getUserData();

        List<NewOrderFeedData> list = orderRepository.getNewOrderFeedData(id, user.getId());

        return ResponseEntity.ok(list);

    }

    @GetMapping("/rejectNewOrder/{id}")
    public ResponseEntity<ErrorResponse> rejectNewOrder(@PathVariable Long id){

        CustomUserDetails user = common.getUserData();

        Orders newOrder = orderRepository.findById(id).orElseThrow();
        newOrder.setOrderStatus(OrderStatus.CANCELLED);

        orderRepository.save(newOrder);

        actionMaker.makeAction(String.format("Order [ORD-%d] was rejected successfully",newOrder.getId()),user.getId(),null,ActionTrackerEnum.USER, ActionDesciptionEnum.Order_Status_Change);



        return ResponseEntity.ok(new ErrorResponse("Changed successfully to cancelled", Warnings.OK));

    }

    @GetMapping("/acceptNewOrder/{id}")
    public ResponseEntity<ErrorResponse> acceptNewOrder(@PathVariable Long id){


        CustomUserDetails user = common.getUserData();

        Orders newOrder = orderRepository.findById(id).orElseThrow();

        for(var ord : newOrder.getProductsData()){

            Long amountTaken = ord.getAmountOfProduct();
            Long amountAvailable = ord.getProduct().getStockQuantity();

            if(amountTaken > amountAvailable){
                newOrder.setOrderStatus(OrderStatus.LACK_OF_SUPPLY);
                newOrder.setServerNote("Order not possible will be automatically changed to Pending when supply exists");

                actionMaker.makeAction(String.format("Order [ORD-%d] Changed successfully to Lack of supply",newOrder.getId()),user.getId(),null,ActionTrackerEnum.SYSTEM, ActionDesciptionEnum.Order_Status_Change);

            }

            else{
                newOrder.setOrderStatus(OrderStatus.Pending);
            }



        }

        orderRepository.save(newOrder);

        actionMaker.makeAction(String.format("Order [ORD-%d] Changed successfully to Pending",newOrder.getId()),user.getId(),null,ActionTrackerEnum.USER, ActionDesciptionEnum.Order_Status_Change);



        return ResponseEntity.ok(new ErrorResponse("Changed successfully to Pending", Warnings.OK));

    }

    // ORDER REPORT PAGE CALLS


    @GetMapping("/getOrderByStatus/{fromDate}/{toDate}")
    public ResponseEntity<OrderReportPieChart> getOrderPieChartData(@PathVariable LocalDate fromDate, @PathVariable LocalDate toDate){

        System.out.println(fromDate);

        CustomUserDetails user = common.getUserData();

        return ResponseEntity.ok(orderRepository.orderReportPieChart(logic.dateConverter(fromDate),logic.dateConverter(toDate), user.getId()));

    }

    @GetMapping("/getOrderByLineChart/{fromDate}/{toDate}")
    public ResponseEntity<List<GraphDataDateValue>> getOrderLineChartData(@PathVariable LocalDate fromDate, @PathVariable LocalDate toDate){


        CustomUserDetails user = common.getUserData();

        return ResponseEntity.ok(orderRepository.orderReportLineBar(logic.dateConverter(fromDate),logic.dateConverter(toDate), user.getId()));

    }


    @GetMapping("/getOrderMiniStatData/{fromDate}/{toDate}")
    public ResponseEntity<ReportMiniStatHolder> getOrderMiniStatData(@PathVariable LocalDate fromDate, @PathVariable LocalDate toDate){

        CustomUserDetails user = common.getUserData();

        LocalDate preFrom = fromDate.withDayOfMonth(1).minusMonths(1);

        LocalDate preTo = preFrom.plusMonths(1).minusDays(1);


        return ResponseEntity.ok(orderRepository.getOrderMiniStats(logic.dateConverter(fromDate),logic.dateConverter(toDate),logic.dateConverter(preFrom),logic.dateConverter(preTo), user.getId()));

    }

    @GetMapping("/getOrderTopConsumers/{fromDate}/{toDate}")
    public ResponseEntity< List<TopCustomerDto>> getOrderTopCustomerGrid(@PathVariable LocalDate fromDate, @PathVariable LocalDate toDate){

        CustomUserDetails user = common.getUserData();

        return ResponseEntity.ok(orderRepository.topCustomerList(logic.dateConverter(fromDate), logic.dateConverter(toDate),PageRequest.of(0,5), user.getId()));

    }

    @GetMapping("/getRecentOrders/{fromDate}/{toDate}")
    public ResponseEntity<List<RecentOrdersReportPage>> getRecentOrderList(@PathVariable LocalDate fromDate, @PathVariable LocalDate toDate){

        CustomUserDetails user = common.getUserData();

        return ResponseEntity.ok(orderRepository.recentOrderReportPage(logic.dateConverter(fromDate), logic.dateConverter(toDate),PageRequest.of(0,5), user.getId()));

    }

// dashboard



    @GetMapping("/getDashboardOrderMini/{fromDate}/{toDate}")
    public ResponseEntity<DashBoardMonthlyOrdersCompleted> getDashboardOrderMini(@PathVariable LocalDate fromDate, @PathVariable LocalDate toDate){

        CustomUserDetails user = common.getUserData();

        LocalDate preFrom = fromDate.withDayOfMonth(1).minusMonths(1);

        LocalDate preTo = preFrom.plusMonths(1).minusDays(1);

        return ResponseEntity.ok(orderRepository.getOrderDashboadrMini(logic.dateConverter(fromDate),logic.dateConverter(toDate),logic.dateConverter(preFrom),logic.dateConverter(preTo), user.getId()));

    }


    @GetMapping("/getGraphDashboard/{fromDate}/{toDate}")
    public ResponseEntity<List<GraphDataDateValue>> getGraphDashboard(@PathVariable LocalDate fromDate, @PathVariable LocalDate toDate){

        CustomUserDetails user = common.getUserData();

        return ResponseEntity.ok(orderRepository.getGraphForDashBoard(logic.dateConverter(fromDate),logic.dateConverter(toDate), user.getId()));

    }

    @GetMapping("/getActionTracker")
    public ResponseEntity<List<ActivityFeedModel>> getActionTracker(){

        CustomUserDetails user = common.getUserData();

        return ResponseEntity.ok(orderRepository.getActionTracker(user.getId(),PageRequest.of(0,5)));

    }








}
