package com.example.jwt_demo.service.Order;

import com.example.jwt_demo.Common.*;
import com.example.jwt_demo.DTOS.Common.MiniStatHolder;
import com.example.jwt_demo.DTOS.EmployeePage.EmployeeOrderProjection;
import com.example.jwt_demo.DTOS.Order.NewOrderFeedData;
import com.example.jwt_demo.DTOS.Order.OrderReportPieChart;
import com.example.jwt_demo.DTOS.Order.OrdersFeedData;
import com.example.jwt_demo.Entity.*;
import com.example.jwt_demo.Entity.OrderJoin.OrderStepsToComplete;
import com.example.jwt_demo.Entity.OrderStepsJoin.OrderStepCompletionLogs;
import com.example.jwt_demo.Enums.*;
import com.example.jwt_demo.FilterDTO.EmployeeAvailableOrderFilter.EmployeeAvailableOrderFilter;
import com.example.jwt_demo.FilterDTO.Order.OrderFilterHolder;
import com.example.jwt_demo.controller.Common;
import com.example.jwt_demo.repository.*;
import com.example.jwt_demo.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
public class GetItemsOfOrdersService {

    @Autowired
    Common common;

    @Autowired
    ProvidedDataChecker providedDataChecker;

    @Autowired
    DatabaseChecks databaseChecks;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    OrderStepCompletionLogsRepository orderStepCompletionLogsRepository;

    @Autowired
    OrderStepsToCompleteRepository orderStepsToCompleteRepository;


    @Autowired
    UserRepository userRepository;




    @Autowired
    WorkDoneRepository workDoneRepository;

    @Autowired
    WorkDayRepository workDayRepository;


    @Autowired
    Logic logic;


    public ResponseEntity<List<OrdersFeedData>> getAllNewOrders(OrderFilterHolder orderFilterHolder, CustomUserDetails user) {

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


    public ResponseEntity<List<OrdersFeedData>> getAllOrders(OrderFilterHolder orderFilterHolder, CustomUserDetails user) {


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

    /// ///////////////////////////////////////////////////

    // get order according to id
    public ResponseEntity<Orders> getOrderFromId(Long id){

        return ResponseEntity.ok(orderRepository.findById(id).orElseThrow());
    }

    public ResponseEntity<MiniStatHolder> getOrderMiniStats(LocalDate from,  LocalDate to, CustomUserDetails user){


        return ResponseEntity.ok(orderRepository.getOrderMiniStats(logic.dateConverter(from),logic.dateConverter(to), user.getId()));

    }

    public ResponseEntity<List<NewOrderFeedData>> getGridStuff( Long id, CustomUserDetails user){

        List<NewOrderFeedData> list = orderRepository.getNewOrderFeedData(id, user.getId());

        return ResponseEntity.ok(list);

    }



    // employee available order
    public ResponseEntity<List<EmployeeOrderProjection>> findEmployeeActiveOrdersNonLimited( EmployeeAvailableOrderFilter employeeAvailableOrderFilter, CustomUserDetails user){

        SortOrder sorting = employeeAvailableOrderFilter.getSortOrder();

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


    // get amount of pages
    public ResponseEntity<Long> getAmountOfPagesOnAvailableOrders(EmployeeAvailableOrderFilter employeeAvailableOrderFilter, CustomUserDetails user) {

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




    public ResponseEntity<ErrorResponse> acceptStep(Long stepId, CustomUserDetails user){


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

    public ResponseEntity<ErrorResponse> completeStep(Long stepId, CustomUserDetails user ){




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



        orderRepository.incrementProductsFinished(order.getId());

        return ResponseEntity.ok(new ErrorResponse("Step accepted ", Warnings.OK));

    }

    // update step

    public ResponseEntity<ErrorResponse> updateStep(Long stepId, Long newAmountCompleted, CustomUserDetails user){




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
