package com.example.jwt_demo.controller;

import com.example.jwt_demo.Common.*;
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
import com.example.jwt_demo.Entity.OrderJoin.OrderStepsToComplete;
import com.example.jwt_demo.Entity.OrderStepsJoin.OrderStepCompletionLogs;
import com.example.jwt_demo.Enums.*;
import com.example.jwt_demo.FilterDTO.EmployeeActiveOrderFilter.EmployeeActiveOrderFilter;
import com.example.jwt_demo.FilterDTO.EmployeeAvailableOrderFilter.EmployeeAvailableOrderFilter;
import com.example.jwt_demo.FilterDTO.Order.OrderFilterHolder;
import com.example.jwt_demo.repository.*;
import com.example.jwt_demo.security.CustomUserDetails;
import com.example.jwt_demo.service.Order.*;
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



    //SERVICE
    @Autowired
    DeleteOrderService deleteOrderService;

    @Autowired
    OrderPagesGetterService orderPagesGetterService;

    @Autowired
    GetItemsOfOrdersService getItemsOfOrders;

    @Autowired
    OrderSavedService orderSavedService;

    @Autowired
    ModifyDataOrdersService modifyDataOrders;

    @Autowired
    OrderGraphDataService orderGraphDataService;


    @Autowired
    OrderRepository orderRepository;

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
    EmployeeActiveOrdersRepository employeeActiveOrdersRepository;

    @Autowired
    WorkDayRepository workDayRepository;



    @Autowired
    WorkDoneRepository workDoneRepository;


    // =====================================
    //  Delete order according to id
    // =====================================
    @GetMapping("/deleteOrder/{id}")
    public ResponseEntity<ErrorResponse> deleteOrder(@PathVariable Long id) {

        return deleteOrderService.deleteOrderAccordingToId(id);

    }



    // =====================================
    // 1 Get order pages according to filter
    // =====================================
    @PostMapping("/getAmountOfPages")
    public ResponseEntity<Long> getAmountOfPages(@RequestBody OrderFilterHolder orderFilterHolder) {

        CustomUserDetails user = common.getUserData();

        return orderPagesGetterService.orderPagePageCount(orderFilterHolder,user);
    }

    // =====================================
    // 1 Get new order pages according to filter
    // =====================================
    @GetMapping("/getNewOrderPages")
    public ResponseEntity<Long> getNewOrderPages() {

        CustomUserDetails user = common.getUserData();

        return orderPagesGetterService.newOrderPageCount(user);
    }

    @GetMapping("/getNewOrderCount")
    public ResponseEntity<Long> getNewOrderCount(){

        CustomUserDetails user = common.getUserData();

        return orderPagesGetterService.getNewOrderCount(user);

    }


    // =====================================
    // Get new orders according to filter
    // =====================================
    @PostMapping("/getAllNewOrders")
    public ResponseEntity<List<OrdersFeedData>> getAllNewOrders(@RequestBody OrderFilterHolder orderFilterHolder) {

        CustomUserDetails user = common.getUserData();

        return getItemsOfOrders.getAllNewOrders(orderFilterHolder,user);
    }

    // =====================================
    //  1 Get orders according to filter
    // =====================================
    @PostMapping("/getAllOrders")
    public ResponseEntity<List<OrdersFeedData>> getAllOrders(@RequestBody OrderFilterHolder orderFilterHolder) {

        CustomUserDetails user = common.getUserData();

        return getItemsOfOrders.getAllOrders(orderFilterHolder,user);
    }

    // get order according to id an entire order
    @GetMapping("/getOrderFromId/{id}")
    public ResponseEntity<Orders> getOrderFromId(@PathVariable Long id){
        return getItemsOfOrders.getOrderFromId(id);
    }


    @Transactional
    @PostMapping("/saveModifiedOrder")
    public ResponseEntity<ErrorResponse> saveModifiedOrder(@RequestBody Orders order){

        CustomUserDetails user = common.getUserData();

        return orderSavedService.saveModifiedOrder(order,user);

    }


    @PostMapping("/saveNewOrder")
    @Transactional
    public ResponseEntity<ErrorResponse> saveOrder(@RequestBody Orders order){

        CustomUserDetails user = common.getUserData();

        return orderSavedService.saveOrder(order,user);
    }






    @GetMapping("/getMiniStats/{from}/{to}")
    public ResponseEntity<MiniStatHolder> getOrderMiniStats(@PathVariable LocalDate from, @PathVariable LocalDate to){

        CustomUserDetails user = common.getUserData();

        return getItemsOfOrders.getOrderMiniStats(from,to,user);

    }

    @GetMapping("/getGridStuff/{id}")
    public ResponseEntity<List<NewOrderFeedData>> getOrderMiniStats(@PathVariable Long id){

        CustomUserDetails user = common.getUserData();

        return getItemsOfOrders.getGridStuff(id,user);
    }






    @GetMapping("/rejectNewOrder/{id}")
    public ResponseEntity<ErrorResponse> rejectNewOrder(@PathVariable Long id){

        CustomUserDetails user = common.getUserData();

        return modifyDataOrders.rejectNewOrder(id, user);

    }

    @GetMapping("/acceptNewOrder/{id}")
    public ResponseEntity<ErrorResponse> acceptNewOrder(@PathVariable Long id){

        CustomUserDetails user = common.getUserData();

        return modifyDataOrders.acceptNewOrder(id,user);

    }





    // ORDER REPORT PAGE CALLS
    @GetMapping("/getOrderByStatus/{fromDate}/{toDate}")
    public ResponseEntity<OrderReportPieChart> getOrderPieChartData(@PathVariable LocalDate fromDate, @PathVariable LocalDate toDate){

        CustomUserDetails user = common.getUserData();

        return orderGraphDataService.getOrderPieChartData(fromDate,toDate,user);

    }

    @GetMapping("/getOrderByLineChart/{fromDate}/{toDate}")
    public ResponseEntity<List<GraphDataDateValue>> getOrderLineChartData(@PathVariable LocalDate fromDate, @PathVariable LocalDate toDate){

        CustomUserDetails user = common.getUserData();

        return orderGraphDataService.getOrderLineChartData(fromDate,toDate,user);

    }


    @GetMapping("/getOrderMiniStatData/{fromDate}/{toDate}")
    public ResponseEntity<ReportMiniStatHolder> getOrderMiniStatData(@PathVariable LocalDate fromDate, @PathVariable LocalDate toDate){

        CustomUserDetails user = common.getUserData();

        LocalDate preFrom = fromDate.withDayOfMonth(1).minusMonths(1);

        LocalDate preTo = preFrom.plusMonths(1).minusDays(1);


        return orderGraphDataService.getOrderMiniStatData(preFrom,preTo,user);

    }

    @GetMapping("/getOrderTopConsumers/{fromDate}/{toDate}")
    public ResponseEntity< List<TopCustomerDto>> getOrderTopCustomerGrid(@PathVariable LocalDate fromDate, @PathVariable LocalDate toDate){

        CustomUserDetails user = common.getUserData();

        return orderGraphDataService.getOrderTopCustomerGrid(fromDate,toDate,user);

    }

    @GetMapping("/getRecentOrders/{fromDate}/{toDate}")
    public ResponseEntity<List<RecentOrdersReportPage>> getRecentOrderList(@PathVariable LocalDate fromDate, @PathVariable LocalDate toDate){

        CustomUserDetails user = common.getUserData();

        return orderGraphDataService.getRecentOrderList(fromDate,toDate,user);

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


    @PostMapping("/findEmployeeActiveOrders")
    public ResponseEntity<List<EmployeeActiveOrders>> findEmployeeActiveOrders(@RequestBody EmployeeActiveOrderFilter filter){


        CustomUserDetails user = common.getUserData();

        filter = providedDataChecker.defaultValueChecker(filter, EmployeeActiveOrderFilter.class);


        Long employeeId = 0L;

        if(filter.getEmpId() == null){
            employeeId = employeeRepository.employeeId(user.getId());

        }
        else{
            employeeId = filter.getEmpId();

        }

        databaseChecks.checkPriority(user,false);


        return ResponseEntity.ok(orderRepository.findEmployeeActiveOrdersLimited(employeeId,
                filter.getPromt(),
                filter.getOrderStatus(),
                filter.getPriority(),
                PageRequest.of(filter.getPage(), filter.getPageCount())));

    }

    @PostMapping("/findEmployeeActiveOrdersPages")
    public ResponseEntity<Long> findEmployeeActiveOrdersPages(@RequestBody EmployeeActiveOrderFilter filter){


        CustomUserDetails user = common.getUserData();

        filter = providedDataChecker.defaultValueChecker(filter, EmployeeActiveOrderFilter.class);


        Long employeeId = 0L;

        if(filter.getEmpId() == null){
            employeeId = employeeRepository.employeeId(user.getId());

        }
        else{
            employeeId = filter.getEmpId();

        }

        databaseChecks.checkPriority(user,false);


        return ResponseEntity.ok(orderRepository.findEmployeeActiveOrdersLimitedPages(employeeId,
                filter.getPromt(),
                filter.getOrderStatus(),
                filter.getPriority(),
                filter.getPageCount()));

    }



    // employee available order
    @PostMapping("/findEmployeeActiveOrdersNonLimited")
    public ResponseEntity<List<EmployeeOrderProjection>> findEmployeeActiveOrdersNonLimited(@RequestBody EmployeeAvailableOrderFilter employeeAvailableOrderFilter){



        CustomUserDetails user = common.getUserData();


        return getItemsOfOrders.findEmployeeActiveOrdersNonLimited(employeeAvailableOrderFilter,user);

    }

    @PostMapping("/getAmountOfPagesOnAvailableOrders")
    public ResponseEntity<Long> getAmountOfPagesOnAvailableOrders(@RequestBody EmployeeAvailableOrderFilter employeeAvailableOrderFilter) {

        CustomUserDetails user = common.getUserData();

        return getItemsOfOrders.getAmountOfPagesOnAvailableOrders(employeeAvailableOrderFilter,user);
    }







    // accept step

    @GetMapping("/acceptStep/{stepId}")
    public ResponseEntity<ErrorResponse> acceptStep(@PathVariable Long stepId){


        CustomUserDetails user = common.getUserData();


        return getItemsOfOrders.acceptStep(stepId,user);

    }


    // complete step

    @GetMapping("/completeStep/{stepId}")
    public ResponseEntity<ErrorResponse> completeStep(@PathVariable Long stepId){


        CustomUserDetails user = common.getUserData();


        return getItemsOfOrders.completeStep(stepId,user);

    }

    // update step

    @GetMapping("/updateStep/{stepId}/{newAmountCompleted}")
    public ResponseEntity<ErrorResponse> updateStep(@PathVariable Long stepId, @PathVariable Long newAmountCompleted){

        CustomUserDetails user = common.getUserData();


        return getItemsOfOrders.updateStep(stepId,newAmountCompleted,user);


    }



}
