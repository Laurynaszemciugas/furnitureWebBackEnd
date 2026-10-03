package com.example.jwt_demo.service.Order;

import com.example.jwt_demo.Common.Logic;
import com.example.jwt_demo.DTOS.Common.GraphDataDateValue;
import com.example.jwt_demo.DTOS.Common.ReportMiniStatHolder;
import com.example.jwt_demo.DTOS.Order.OrderReportPieChart;
import com.example.jwt_demo.DTOS.Order.RecentOrdersReportPage;
import com.example.jwt_demo.DTOS.Order.TopCustomerDto;
import com.example.jwt_demo.repository.OrderRepository;
import com.example.jwt_demo.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDate;
import java.util.List;

@Service
public class OrderGraphDataService {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    Logic logic;



    public ResponseEntity<OrderReportPieChart> getOrderPieChartData(LocalDate fromDate, LocalDate toDate, CustomUserDetails user){


        return ResponseEntity.ok(orderRepository.orderReportPieChart(logic.dateConverter(fromDate),logic.dateConverter(toDate), user.getId()));

    }

    public ResponseEntity<List<GraphDataDateValue>> getOrderLineChartData(LocalDate fromDate, LocalDate toDate, CustomUserDetails user){


        return ResponseEntity.ok(orderRepository.orderReportLineBar(logic.dateConverter(fromDate),logic.dateConverter(toDate), user.getId()));

    }

    public ResponseEntity<ReportMiniStatHolder> getOrderMiniStatData(LocalDate fromDate,  LocalDate toDate, CustomUserDetails user){


        LocalDate preFrom = fromDate.withDayOfMonth(1).minusMonths(1);

        LocalDate preTo = preFrom.plusMonths(1).minusDays(1);


        return ResponseEntity.ok(orderRepository.getOrderMiniStats(logic.dateConverter(fromDate),logic.dateConverter(toDate),logic.dateConverter(preFrom),logic.dateConverter(preTo), user.getId()));

    }

    public ResponseEntity< List<TopCustomerDto>> getOrderTopCustomerGrid(LocalDate fromDate,  LocalDate toDate, CustomUserDetails user){


        return ResponseEntity.ok(orderRepository.topCustomerList(logic.dateConverter(fromDate), logic.dateConverter(toDate), PageRequest.of(0,5), user.getId()));

    }

    public ResponseEntity<List<RecentOrdersReportPage>> getRecentOrderList(LocalDate fromDate,  LocalDate toDate, CustomUserDetails user){


        return ResponseEntity.ok(orderRepository.recentOrderReportPage(logic.dateConverter(fromDate), logic.dateConverter(toDate),PageRequest.of(0,5), user.getId()));

    }



}
