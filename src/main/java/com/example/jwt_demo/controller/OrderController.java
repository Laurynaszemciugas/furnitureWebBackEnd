package com.example.jwt_demo.controller;

import com.example.jwt_demo.Common.*;
import com.example.jwt_demo.Common.OrderAcceptance.MostOrders;
import com.example.jwt_demo.Common.OrderAcceptance.OrderAcceptingModels;
import com.example.jwt_demo.DTOS.Common.GraphDataDateValue;
import com.example.jwt_demo.DTOS.Common.MiniStatHolder;
import com.example.jwt_demo.DTOS.Common.ReportMiniStatHolder;
import com.example.jwt_demo.DTOS.DashBoard.ActivityFeedModel;
import com.example.jwt_demo.DTOS.DashBoard.DashBoardMonthlyOrdersCompleted;
import com.example.jwt_demo.DTOS.EmployeePage.EmployeeOrderProjection;
import com.example.jwt_demo.DTOS.Order.*;
import com.example.jwt_demo.Entity.*;
import com.example.jwt_demo.Entity.EmployeeJoin.EmployeeActiveOrders;
import com.example.jwt_demo.Entity.EmployeeJoin.OrderEmployees;
import com.example.jwt_demo.Entity.OrderJoin.OrderProducts;
import com.example.jwt_demo.Entity.OrderJoin.OrderStepsToComplete;
import com.example.jwt_demo.Entity.OrderStepsJoin.OrderStepCompletionLogs;
import com.example.jwt_demo.Entity.ProductJoin.ProductMaterials;
import com.example.jwt_demo.Enums.*;
import com.example.jwt_demo.FilterDTO.EmployeeAvailableOrderFilter.EmployeeAvailableOrderFilter;
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
import java.util.*;

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
    EmployeeActiveOrdersRepository employeeActiveOrdersRepository;

    @Autowired
    OrderStepsToCompleteRepository orderStepsToCompleteRepository;


    @Autowired
    OrderStepCompletionLogsRepository orderStepCompletionLogsRepository;

    @Autowired
    WorkDayRepository workDayRepository;

    @Autowired
    OrderAcceptingModels orderAcceptingModels;

    @Autowired
    WorkDoneRepository workDoneRepository;


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

        databaseChecks.checkPriority(user,false);


        return ResponseEntity.ok(
                orderRepository.getNewOrders(

                        user.getId(),
                        PageRequest.of(orderFilterHolder.getPage(),orderFilterHolder.getPageCount())
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
                orderFilterHolder.getPromptChoice(),
                orderFilterHolder.getEmployee(),
                orderFilterHolder.getProducts(),
                orderFilterHolder.getOrderActiveInactive(),
                orderFilterHolder.getPageCount(),
                user.getId()

        );




        return ResponseEntity.ok(
                count
        );
    }


    @GetMapping("/getNewOrderPages")
    public ResponseEntity<Long> getNewOrderPages() {

        CustomUserDetails user = common.getUserData();





        Long count = orderRepository.getNewOrderTotalPages(
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




        if(order.getBillingAddress().isEmpty() || order.getBillingAddress() == null){
            throw  new ValidationException("Address is required", Warnings.ERROR);
        }

        sameExistingOrder.setBillingAddress(order.getBillingAddress());



        double totalPrice = 0.0;


        if(order.getProductsData().isEmpty() || order.getProductsData() == null){
            throw  new ValidationException("Existing order cannot be without products ", Warnings.ERROR);
        }

        List<OrderProducts> products = new ArrayList<>();
        sameExistingOrder.getProductsData().clear();
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
            orderProducts.setCost(orderAcceptingModels.materialCost(s.getProduct().getId(), s.getAmountOfProduct()));
            orderProducts.setAmountOfProduct(s.getAmountOfProduct());
            products.add(orderProducts);
        }

        sameExistingOrder.getProductsData().addAll(products);

        if(order.getEmployees().isEmpty() || order.getEmployees() == null){
            throw  new ValidationException("Existing order cannot be without employees ", Warnings.ERROR);
        }

        sameExistingOrder.getEmployees().clear();
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

        databaseChecks.calculateMaterialsStock(order.getId());
        databaseChecks.checkModifiedOrders(sameExistingOrder.getId(),nonModified);
        databaseChecks.calculateProductsStock(null,false);

        orderRepository.save(sameExistingOrder);

        actionMaker.makeAction(String.format("ORD-%d %s",order.getId(), "was modified and saved successfully"),user.getId(),null,ActionTrackerEnum.USER, ActionDesciptionEnum.Order_Updated);


        orderRepository.incrementProductsFinished(order.getId());

        return ResponseEntity.ok(new ErrorResponse(String.format("ORD-%d %s",order.getId(), "was modified and saved successfully"),Warnings.OK));
    }





    @PostMapping("/saveNewOrder")
    @Transactional
    public ResponseEntity<ErrorResponse> saveOrder(@RequestBody Orders order){


        System.out.println("saving new way");

        CustomUserDetails userData = common.getUserData();

        User user = userRepository.findById(userData.getId()).orElseThrow();

        User actualUser = userRepository.findById(user.getId()).orElseThrow();

        if(actualUser.getRole().equals(Role.ANONYMOUS)){

        }
        else if (actualUser.getRole().equals(Role.USER)){

            OrderProcessing orderProcessing = actualUser.getUserSettingsList().getOrderProcessing();


            switch (orderProcessing){
                case FIRST_COME_FIRST_SERVE -> {
                    orderAcceptingModels.saveNewOrder(order,user);
                }
                case MAXIMIZE_ORDER -> {
                    Orders saveOrder = orderAcceptingModels.putInfoIntoNewOrder(order,user);
                    orderRepository.save(saveOrder);
                }

            }


        }

        return ResponseEntity.ok(new ErrorResponse(String.format("Order created",order.getId()),Warnings.OK));

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


        if(newOrder.getOrderStatus().equals(OrderStatus.AWAITING_CONFIRMATION)){
            newOrder.setOrderStatus(OrderStatus.Pending);
            orderRepository.save(newOrder);

            actionMaker.makeAction(String.format("Order [ORD-%d] Changed successfully to Pending",newOrder.getId()),user.getId(),null,ActionTrackerEnum.USER, ActionDesciptionEnum.Order_Status_Change);
        }

        else{
            return ResponseEntity.ok(new ErrorResponse("Order cannot be accepted due to lack of supply", Warnings.ERROR));
        }





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



    // employee page


    @GetMapping("/getEmployeeOrderProjection")
    public ResponseEntity<List<EmployeeOrderProjection>> getEmployeeOrderProjection(){

        CustomUserDetails user = common.getUserData();

        Long employee = employeeRepository.employeeId(user.getId());

        databaseChecks.checkPriority(user,false);

        EmployeeAvailableOrderFilter filter = new EmployeeAvailableOrderFilter();

        filter = providedDataChecker.defaultValueChecker(filter, EmployeeAvailableOrderFilter.class);

        return ResponseEntity.ok(orderRepository.findOrdersForEmployeeLimited(employee,
                filter.getPromt(),
                filter.getOrderStatus(),
                filter.getPriority(),
                PageRequest.of(0,3)));

    }

    @GetMapping("/findHowManyItemsAreAvailable")
    public ResponseEntity<Long> findHowManyItemsAreAvailable(){


        CustomUserDetails user = common.getUserData();

        Long employee = employeeRepository.employeeId(user.getId());


        return ResponseEntity.ok(orderRepository.findHowManyItemsAreAvailable(employee));

    }

    @GetMapping("/findHowManyItemsAreActive")
    public ResponseEntity<Long> findHowManyItemsAreActive(){


        CustomUserDetails user = common.getUserData();

        Long employee = employeeRepository.employeeId(user.getId());



        return ResponseEntity.ok(orderRepository.findHowManyItemsAreActive(employee));

    }



    @GetMapping("/acceptOrderEmployee/{orderId}")
    public ResponseEntity<ErrorResponse> acceptOrderEmployee(@PathVariable Long orderId){





        CustomUserDetails user = common.getUserData();

        Long employeeId = employeeRepository.employeeId(user.getId());


        // check if employee has work day


        if(workDayRepository.doesEmployeeAlreadyStartedWork(employeeId) == 0){
            return ResponseEntity.ok(new ErrorResponse("Please start the work day before accepting new orders or continuing", Warnings.WARNING));
        }



        Orders orders = orderRepository.findById(orderId).orElseThrow();

        Employee employee = employeeRepository.findById(employeeId).orElseThrow();

        EmployeeActiveOrders employeeActiveOrders = new EmployeeActiveOrders();
        employeeActiveOrders.setOrder(orders);
        employeeActiveOrders.setEmployee(employee);


        employeeActiveOrdersRepository.save(employeeActiveOrders);


        // save the work done

        WorkDay workDay = workDayRepository.getWorkDayInfo(employeeId);


        User admin = userRepository.findById(employee.getUser().getId()).orElseThrow();

        WorkDone workDone = new WorkDone();
        workDone.setWorkDay(workDay);
        workDone.setWhatWasDone(String.format(" order - #%d %s",orderId,"was accepted"));
        workDone.setOrder(orders);
        workDone.setEmployee(employeeRepository.findById(employeeId).orElseThrow());
        workDay.setUser(admin);

        workDoneRepository.save(workDone);



        if(orders.getOrderStatus().equals(OrderStatus.Pending)) {

            orders.setOrderStatus(OrderStatus.In_Progress);

            orderRepository.save(orders);
        }


        return ResponseEntity.ok(new ErrorResponse(String.format(" order - #%d %s",orderId,"was accepted"), Warnings.OK));

    }


    @GetMapping("/findEmployeeActiveOrders")
    public ResponseEntity<List<EmployeeActiveOrders>> findEmployeeActiveOrders(){


        CustomUserDetails user = common.getUserData();


        Long employeeId = employeeRepository.employeeId(user.getId());

        databaseChecks.checkPriority(user,false);


        return ResponseEntity.ok(orderRepository.findEmployeeActiveOrdersLimited(employeeId,PageRequest.of(0,5)));

    }


    // employee available order
    @PostMapping("/findEmployeeActiveOrdersNonLimited")
    public ResponseEntity<List<EmployeeOrderProjection>> findEmployeeActiveOrdersNonLimited(@RequestBody EmployeeAvailableOrderFilter employeeAvailableOrderFilter){

        SortOrder sorting = employeeAvailableOrderFilter.getSortOrder();

        CustomUserDetails user = common.getUserData();
        Long employeeId = employeeRepository.employeeId(user.getId());
        employeeAvailableOrderFilter = providedDataChecker.defaultValueChecker(employeeAvailableOrderFilter, EmployeeAvailableOrderFilter.class);

        databaseChecks.checkPriority(user,false);


        List<EmployeeOrderProjection> answer = orderRepository.findOrdersForEmployeeLimited(employeeId,
                employeeAvailableOrderFilter.getPromt(),
                employeeAvailableOrderFilter.getOrderStatus(),
                employeeAvailableOrderFilter.getPriority(),
                PageRequest.of(employeeAvailableOrderFilter.getPage(),employeeAvailableOrderFilter.getPageCount()));


        // sorting
        if(sorting.equals(SortOrder.OLDEST)){
            answer.sort(
                    Comparator.comparing(EmployeeOrderProjection::getCreated)
            );
        }
        else if(sorting.equals(SortOrder.NEWEST)){
            answer.sort(
                    Comparator.comparing(EmployeeOrderProjection::getCreated).reversed()
            );
        }


        return ResponseEntity.ok(answer);

    }

    @PostMapping("/getAmountOfPagesOnAvailableOrders")
    public ResponseEntity<Long> getAmountOfPagesOnAvailableOrders(@RequestBody EmployeeAvailableOrderFilter employeeAvailableOrderFilter) {

        CustomUserDetails user = common.getUserData();
        Long employeeId = employeeRepository.employeeId(user.getId());

        employeeAvailableOrderFilter = providedDataChecker.defaultValueChecker(employeeAvailableOrderFilter, EmployeeAvailableOrderFilter.class);



        Long count = orderRepository.getAmountOfPagesOnAvailableOrders(
                employeeId,
                employeeAvailableOrderFilter.getPromt(),
                employeeAvailableOrderFilter.getOrderStatus(),
                employeeAvailableOrderFilter.getPriority(),
                employeeAvailableOrderFilter.getPageCount());


        return ResponseEntity.ok(
                count
        );
    }







    // accept step

    @GetMapping("/acceptStep/{stepId}")
    public ResponseEntity<ErrorResponse> acceptStep(@PathVariable Long stepId){


        CustomUserDetails user = common.getUserData();


        // check if employee has work day



        Long employeeId = employeeRepository.employeeId(user.getId());

        if(workDayRepository.doesEmployeeAlreadyStartedWork(employeeId) == 0){
            return ResponseEntity.ok(new ErrorResponse("Please start the work day before accepting new orders or continuing", Warnings.WARNING));
        }


        User actualUser = userRepository.findById(user.getId()).orElseThrow();

        OrderStepsToComplete orderStepsToComplete = orderStepsToCompleteRepository.findById(stepId).orElseThrow();


        if(orderStepsToComplete.getEmployee() !=null){
            return ResponseEntity.ok(new ErrorResponse("Step is already taken you can help to complete it ", Warnings.OK));
        }

        orderStepsToComplete.setEmployee(actualUser);
        orderStepsToComplete.setProductFinishStepStatus(ProductFinishStepStatus.IN_PROGRESS);
        orderStepsToComplete.setCreated(LocalDateTime.now());

        Long orderId = orderStepsToComplete.getOrderProducts().getOrder().getId();

        Orders order = orderRepository.findById(orderId).orElseThrow();

        orderStepsToCompleteRepository.save(orderStepsToComplete);


        OrderStepCompletionLogs orderStepCompletionLogs = new OrderStepCompletionLogs();
        orderStepCompletionLogs.setEmployee(actualUser);
        orderStepCompletionLogs.setOrderStepsToComplete(orderStepsToComplete);
        orderStepCompletionLogs.setThingThatWasDone(String.format("%d %s %s",orderStepsToComplete.getStepId(),orderStepsToComplete.getStepName(),"was accepted"));

        orderStepCompletionLogsRepository.save(orderStepCompletionLogs);


        // save the work done

        WorkDay workDay = workDayRepository.getWorkDayInfo(employeeId);


        Employee employee = employeeRepository.findById(employeeId).orElseThrow();
        User admin = userRepository.findById(employee.getUser().getId()).orElseThrow();

        WorkDone workDone = new WorkDone();
        workDone.setWorkDay(workDay);
        workDone.setWhatWasDone(String.format("%d %s %s",orderStepsToComplete.getStepId(),orderStepsToComplete.getStepName(),"was accepted"));
        workDone.setOrder(order);
        workDone.setOrderStepsToComplete(orderStepsToComplete);
        workDone.setEmployee(employeeRepository.findById(employeeId).orElseThrow());
        workDay.setUser(admin);

        workDoneRepository.save(workDone);






        return ResponseEntity.ok(new ErrorResponse(String.format("%d %s %s",orderStepsToComplete.getStepId(),orderStepsToComplete.getStepName(),"was accepted"), Warnings.OK));

    }


    // complete step

    @GetMapping("/completeStep/{stepId}")
    public ResponseEntity<ErrorResponse> completeStep(@PathVariable Long stepId){


        CustomUserDetails user = common.getUserData();


        // check if employee has work day

        Long employeeId = employeeRepository.employeeId(user.getId());

        if(workDayRepository.doesEmployeeAlreadyStartedWork(employeeId) == 0){
            return ResponseEntity.ok(new ErrorResponse("Please start the work day before accepting new orders or continuing", Warnings.WARNING));
        }


        User actualUser = userRepository.findById(user.getId()).orElseThrow();

        OrderStepsToComplete orderStepsToComplete = orderStepsToCompleteRepository.findById(stepId).orElseThrow();

        orderStepsToComplete.setProductFinishStepStatus(ProductFinishStepStatus.FINISHED);
        orderStepsToComplete.setEmployee(actualUser);
        orderStepsToComplete.setCreated(LocalDateTime.now());

        Long orderId = orderStepsToComplete.getOrderProducts().getOrder().getId();

        Orders order = orderRepository.findById(orderId).orElseThrow();


        orderStepsToCompleteRepository.save(orderStepsToComplete);


        OrderStepCompletionLogs orderStepCompletionLogs = new OrderStepCompletionLogs();
        orderStepCompletionLogs.setEmployee(actualUser);
        orderStepCompletionLogs.setOrderStepsToComplete(orderStepsToComplete);
        orderStepCompletionLogs.setThingThatWasDone(String.format("order - #%d  step -  %s %s",order.getId(),orderStepsToComplete.getStepName(),"Was completed"));

        orderStepCompletionLogsRepository.save(orderStepCompletionLogs);


        // save the work done

        WorkDay workDay = workDayRepository.getWorkDayInfo(employeeId);


        Employee employee = employeeRepository.findById(employeeId).orElseThrow();
        User admin = userRepository.findById(employee.getUser().getId()).orElseThrow();

        WorkDone workDone = new WorkDone();
        workDone.setWorkDay(workDay);
        workDone.setWhatWasDone(String.format("order - #%d  step -  %s %s",order.getId(),orderStepsToComplete.getStepName(),"Was completed"));
        workDone.setOrder(order);
        workDone.setOrderStepsToComplete(orderStepsToComplete);
        workDone.setEmployee(employeeRepository.findById(employeeId).orElseThrow());
        workDay.setUser(admin);

        workDoneRepository.save(workDone);


        boolean canBeSetAsFinished = true;
        for(var s : order.getProductsData()){



            for(var steps : s.getOrderSteps()){

                if (!steps.getProductFinishStepStatus().equals(ProductFinishStepStatus.FINISHED)){
                    canBeSetAsFinished = false;
                    break;
                }

            }
        }

        if(canBeSetAsFinished){
            order.setOrderStatus(OrderStatus.Finished);
            databaseChecks.orderFinishedDeductReserve(order.getId());
        }
        else {
            order.setOrderStatus(OrderStatus.Pending);
        }

        orderRepository.save(order);





        return ResponseEntity.ok(new ErrorResponse("Step accepted ", Warnings.OK));

    }

    // update step

    @GetMapping("/updateStep/{stepId}/{newAmountCompleted}")
    public ResponseEntity<ErrorResponse> updateStep(@PathVariable Long stepId, @PathVariable Long newAmountCompleted){

        CustomUserDetails user = common.getUserData();



        // check if employee has work day

        Long employeeId = employeeRepository.employeeId(user.getId());


        if(workDayRepository.doesEmployeeAlreadyStartedWork(employeeId) == 0){
            return ResponseEntity.ok(new ErrorResponse("Please start the work day before accepting new orders or continuing", Warnings.WARNING));
        }


        User actualUser = userRepository.findById(user.getId()).orElseThrow();

        OrderStepsToComplete orderStepsToComplete = orderStepsToCompleteRepository.findById(stepId).orElseThrow();

        Long orderId = orderStepsToComplete.getOrderProducts().getOrder().getId();

        Orders order = orderRepository.findById(orderId).orElseThrow();


        OrderStepCompletionLogs orderStepCompletionLogs = new OrderStepCompletionLogs();
        orderStepCompletionLogs.setEmployee(actualUser);
        orderStepCompletionLogs.setOrderStepsToComplete(orderStepsToComplete);
        orderStepCompletionLogs.setThingThatWasDone(String.format("%s %s [%d] %s [%d]", orderStepsToComplete.getStepName(),"was modified completed steps was ",orderStepsToComplete.getStepsCompleted(),"new value", newAmountCompleted));

        orderStepCompletionLogsRepository.save(orderStepCompletionLogs);

        // save the work done

        WorkDay workDay = workDayRepository.getWorkDayInfo(employeeId);


        Employee employee = employeeRepository.findById(employeeId).orElseThrow();
        User admin = userRepository.findById(employee.getUser().getId()).orElseThrow();

        WorkDone workDone = new WorkDone();
        workDone.setWorkDay(workDay);
        workDone.setWhatWasDone(String.format("%s %s [%d] %s [%d]", orderStepsToComplete.getStepName(),"was modified completed steps was ",orderStepsToComplete.getStepsCompleted(),"new value", newAmountCompleted));
        workDone.setOrder(order);
        workDone.setOrderStepsToComplete(orderStepsToComplete);
        workDone.setEmployee(employeeRepository.findById(employeeId).orElseThrow());
        workDay.setUser(admin);

        workDoneRepository.save(workDone);



// set value after its saved
        orderStepsToComplete.setStepsCompleted(newAmountCompleted);

        orderStepsToCompleteRepository.save(orderStepsToComplete);


        boolean canBeSetAsFinished = true;
        for(var s : order.getProductsData()){



            for(var steps : s.getOrderSteps()){

                if (!steps.getProductFinishStepStatus().equals(ProductFinishStepStatus.FINISHED)){
                    canBeSetAsFinished = false;
                    break;
                }

            }
        }

        if(canBeSetAsFinished){
            order.setOrderStatus(OrderStatus.Finished);
            databaseChecks.orderFinishedDeductReserve(order.getId());
        }
        else {
            order.setOrderStatus(OrderStatus.Pending);
        }

        orderRepository.save(order);


        return ResponseEntity.ok(new ErrorResponse(String.format("%d %s",orderStepsToComplete.getStepId(),"was modified successfully"), Warnings.OK));

    }



}
