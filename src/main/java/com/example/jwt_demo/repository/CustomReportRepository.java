package com.example.jwt_demo.repository;

import com.example.jwt_demo.DTOS.CustomReport.CustomReportFeed;
import com.example.jwt_demo.Entity.CreateReport.Report;
import com.example.jwt_demo.Entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CustomReportRepository extends JpaRepository<Report,Long> {


@Query("""

    SELECT new com.example.jwt_demo.DTOS.CustomReport.CustomReportFeed(r.id, r.reportName,r.dashboardWidget,r.reportColor, r.description, r.created)
     FROM Report r

""")
    List<CustomReportFeed> customRepostFeedList();


}
