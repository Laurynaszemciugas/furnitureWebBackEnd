package com.example.jwt_demo.repository;

import com.example.jwt_demo.Entity.WorkDay;
import com.example.jwt_demo.Entity.WorkDone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface WorkDoneRepository extends JpaRepository<WorkDone,Long> {



    @Query(value = """

    SELECT * FROM bpfurniture.work_done wd where wd.work_day_id = :workDayId ;


""", nativeQuery = true)
    List<WorkDone> allInfoAboutSpecificWorkDay(Long workDayId);





}
