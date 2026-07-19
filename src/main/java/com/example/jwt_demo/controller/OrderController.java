package com.example.jwt_demo.controller;

import com.example.jwt_demo.Common.DatabaseChecks;
import com.example.jwt_demo.Common.ErrorResponse;
import com.example.jwt_demo.Common.Logic;
import com.example.jwt_demo.Common.ProvidedDataChecker;
import com.example.jwt_demo.DTOS.Common.GraphDataDateValue;
import com.example.jwt_demo.DTOS.Common.MiniStatHolder;
import com.example.jwt_demo.DTOS.Common.ReportMiniStatHolder;
import com.example.jwt_demo.DTOS.Order.*;
import com.example.jwt_demo.Entity.Employee;
import com.example.jwt_demo.Entity.EmployeeJoin.OrderEmployees;
import com.example.jwt_demo.Entity.OrderJoin.OrderProducts;
import com.example.jwt_demo.Entity.Orders;
import com.example.jwt_demo.Entity.Product;
import com.example.jwt_demo.Entity.User;
import com.example.jwt_demo.Enums.OrderStatus;
import com.example.jwt_demo.Enums.Warnings;
import com.example.jwt_demo.FilterDTO.Order.OrderFilterHolder;
import com.example.jwt_demo.GlobalExseptions.Exseptions.ValidationException;
import com.example.jwt_demo.repository.EmployeeRepository;
import com.example.jwt_demo.repository.OrderRepository;
import com.example.jwt_demo.repository.ProductRepository;
import com.example.jwt_demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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

    Map<Long,Integer> countTheTimesAccordingToUser = new HashMap<>();

    @PostMapping("/getAllOrders")
    public ResponseEntity<List<OrdersFeedData>> getAllOrders(@RequestBody OrderFilterHolder orderFilterHolder) {


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
                        PageRequest.of(orderFilterHolder.getPage(), orderFilterHolder.getPageCount())
                )
        );
    }


    @PostMapping("/getAllNewOrders")
    public ResponseEntity<List<OrdersFeedData>> getAllNewOrders(@RequestBody OrderFilterHolder orderFilterHolder) {


        orderFilterHolder.setOrderStatusChoice(OrderStatus.NEW);
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
                        PageRequest.of(orderFilterHolder.getPage(), orderFilterHolder.getPageCount())
                )
        );
    }


    @PostMapping("/getAmountOfPages")
    public ResponseEntity<Long> getAmountOfPages(@RequestBody OrderFilterHolder orderFilterHolder) {




        orderFilterHolder = providedDataChecker.defaultValueChecker(orderFilterHolder, OrderFilterHolder.class);


        Long count = orderRepository.getNumberOfOrderPages(
                orderFilterHolder.getOrderStatusChoice(),
                orderFilterHolder.getPriceFromChoice(),
                orderFilterHolder.getPriceToChoice(),
                logic.dateConverter(orderFilterHolder.getDateFromChoice()),
                logic.dateConverter(orderFilterHolder.getDateToChoice()),
                orderFilterHolder.getAmountOfProductsChoice(),
                orderFilterHolder.getPromptChoice(),
                Double.valueOf(orderFilterHolder.getPageCount())
        );






        return ResponseEntity.ok(
                count
        );
    }





    @GetMapping("/getOrderFromId/{id}")
    public ResponseEntity<Orders> getOrderFromId(@PathVariable Long id){
        return ResponseEntity.ok(orderRepository.findById(id).orElseThrow());
    }

    @PostMapping("/saveModifiedOrder")
    public ResponseEntity<ErrorResponse> saveModifiedOrder(@RequestBody Orders order){


        Orders sameExistingOrder = orderRepository.findById(order.getId()).orElseThrow();

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

        for(var s : order.getProductsData()) {
            Long productId = s.getProduct().getId();
            Product existingProduct = productRepository.findById(productId).orElseThrow();

            if (s.getAmountOfProduct() <= 0 || s.getAmountOfProduct() >= 100) {
                throw  new ValidationException("Product quantity can only be from 1 to 99", Warnings.ERROR);
            }
            totalPrice += existingProduct.getPrice() * s.getAmountOfProduct();
            OrderProducts orderProducts = new OrderProducts();

            System.out.println(s.getAmountOfProduct());

            orderProducts.setAmountOfProduct(s.getAmountOfProduct());
            orderProducts.setOrder(sameExistingOrder);
            orderProducts.setProduct(existingProduct);
            orderProducts.setCost(existingProduct.getPrice()); // calculate plus tax stuff


            sameExistingOrder.getProductsData().add(orderProducts);
        }


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







        orderRepository.save(sameExistingOrder);


        return ResponseEntity.ok(new ErrorResponse(String.format("ORD-%d %s",order.getId(), "was modified and saved successfully"),Warnings.OK));
    }



    @PostMapping("/saveNewOrder")
    public ResponseEntity<ErrorResponse> saveNewOrder(@RequestBody Orders order){


        // checks if there is any null or is empty values
        providedDataChecker.checkEmptyValue(order, Orders.class);

        Orders newOrder = new Orders();

        newOrder.setOrderNote(order.getOrderNote());
        newOrder.setOrderStatus(order.getOrderStatus());
        newOrder.setPayMethod(order.getPayMethod());
        newOrder.setPayStatus(order.getPayStatus());
        newOrder.setBillingAddress(order.getBillingAddress());
        newOrder.setPhoneNumber(order.getPhoneNumber());
        newOrder.setEstimatedDueDate(order.getEstimatedDueDate());
        newOrder.setOrderCreatedByGmail(order.getOrderCreatedByGmail());
        newOrder.setOrderCreatedByName(order.getOrderCreatedByName());
        newOrder.setCreatedDate(LocalDate.now());



        if(order.getProductsData() == null || order.getProductsData().isEmpty()){
            throw new ValidationException("No products are added please add products to continue", Warnings.ERROR);
        }
        else{

            Double totalPrice = 0.0;
            for(var s : order.getProductsData()){
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
                    orderProducts.setCost(totalPrice);
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
        User creator = userRepository.findById(1l).orElseThrow();
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



        return ResponseEntity.ok(new ErrorResponse(String.format("Order [ORD-%d] was created successfully",newOrder.getId()),Warnings.OK));

    }


    @GetMapping("/getMiniStats/{from}/{to}")
    public ResponseEntity<MiniStatHolder> getOrderMiniStats(@PathVariable LocalDate from, @PathVariable LocalDate to){


        return ResponseEntity.ok(orderRepository.getOrderMiniStats(logic.dateConverter(from),logic.dateConverter(to)));

    }


    @GetMapping("/getNewOrderCount")
    public ResponseEntity<Long> getOrderMiniStats(){


        return ResponseEntity.ok(orderRepository.findNewOrdersCount());

    }

    @GetMapping("/getGridStuff/{id}")
    public ResponseEntity<List<NewOrderFeedData>> getOrderMiniStats(@PathVariable Long id){

        List<NewOrderFeedData> list = orderRepository.getNewOrderFeedData(id);

        return ResponseEntity.ok(list);

    }

    @GetMapping("/rejectNewOrder/{id}")
    public ResponseEntity<ErrorResponse> rejectNewOrder(@PathVariable Long id){

        Orders newOrder = orderRepository.findById(id).orElseThrow();
        newOrder.setOrderStatus(OrderStatus.CANCELLED);

        orderRepository.save(newOrder);

        return ResponseEntity.ok(new ErrorResponse("Changed successfully to cancelled", Warnings.OK));

    }

    @GetMapping("/acceptNewOrder/{id}")
    public ResponseEntity<ErrorResponse> acceptNewOrder(@PathVariable Long id){

        Orders newOrder = orderRepository.findById(id).orElseThrow();

        for(var ord : newOrder.getProductsData()){

            Long amountTaken = ord.getAmountOfProduct();
            Long amountAvailable = ord.getProduct().getStockQuantity();

            if(amountTaken > amountAvailable){
                newOrder.setOrderStatus(OrderStatus.LACK_OF_SUPPLY);
                newOrder.setServerNote("Order not possible will be automatically changed to Pending when supply exists");
            }

            else{
                newOrder.setOrderStatus(OrderStatus.Pending);
            }



        }

        orderRepository.save(newOrder);

        return ResponseEntity.ok(new ErrorResponse("Changed successfully to Pending", Warnings.OK));

    }

    // ORDER REPORT PAGE CALLS


    @GetMapping("/getOrderByStatus/{fromDate}/{toDate}")
    public ResponseEntity<OrderReportPieChart> getOrderPieChartData(@PathVariable LocalDate fromDate, @PathVariable LocalDate toDate){

        System.out.println(fromDate);

        return ResponseEntity.ok(orderRepository.orderReportPieChart(logic.dateConverter(fromDate),logic.dateConverter(toDate)));

    }

    @GetMapping("/getOrderByLineChart/{fromDate}/{toDate}")
    public ResponseEntity<List<GraphDataDateValue>> getOrderLineChartData(@PathVariable LocalDate fromDate, @PathVariable LocalDate toDate){


        System.out.println("line bar");


        System.out.println(fromDate + "   "   + toDate);


        return ResponseEntity.ok(orderRepository.orderReportLineBar(logic.dateConverter(fromDate),logic.dateConverter(toDate)));

    }


    @GetMapping("/getOrderMiniStatData/{fromDate}/{toDate}")
    public ResponseEntity<ReportMiniStatHolder> getOrderMiniStatData(@PathVariable LocalDate fromDate, @PathVariable LocalDate toDate){

        LocalDate preFrom = fromDate.withDayOfMonth(1).minusMonths(1);

        LocalDate preTo = preFrom.plusMonths(1).minusDays(1);


        return ResponseEntity.ok(orderRepository.getOrderMiniStats(logic.dateConverter(fromDate),logic.dateConverter(toDate),logic.dateConverter(preFrom),logic.dateConverter(preTo)));

    }

    @GetMapping("/getOrderTopConsumers/{fromDate}/{toDate}")
    public ResponseEntity< List<TopCustomerDto>> getOrderTopCustomerGrid(@PathVariable LocalDate fromDate, @PathVariable LocalDate toDate){

        return ResponseEntity.ok(orderRepository.topCustomerList(logic.dateConverter(fromDate), logic.dateConverter(toDate),PageRequest.of(0,5)));

    }

    @GetMapping("/getRecentOrders/{fromDate}/{toDate}")
    public ResponseEntity<List<RecentOrdersReportPage>> getRecentOrderList(@PathVariable LocalDate fromDate, @PathVariable LocalDate toDate){

        return ResponseEntity.ok(orderRepository.recentOrderReportPage(logic.dateConverter(fromDate), logic.dateConverter(toDate),PageRequest.of(0,5)));

    }











}
