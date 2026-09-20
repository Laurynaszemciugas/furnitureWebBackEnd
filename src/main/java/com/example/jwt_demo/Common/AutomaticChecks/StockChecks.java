package com.example.jwt_demo.Common.AutomaticChecks;

import com.example.jwt_demo.Common.DatabaseChecks;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StockChecks {

    DatabaseChecks databaseChecks;

    public StockChecks(DatabaseChecks databaseChecks) {
        this.databaseChecks = databaseChecks;
    }

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void checkOrderPriority(){

        databaseChecks.checkPriority(null,true);


    }


}
