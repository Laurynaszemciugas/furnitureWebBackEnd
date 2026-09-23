package com.example.jwt_demo.repository;

import com.example.jwt_demo.DTOS.User.AccountOverview;
import com.example.jwt_demo.DTOS.User.Appearance;
import com.example.jwt_demo.DTOS.User.PersonalPrefrences;
import com.example.jwt_demo.DTOS.User.ProfileInformation;
import com.example.jwt_demo.Entity.User;
import com.example.jwt_demo.Entity.UserSettings;
import com.example.jwt_demo.Enums.OrderProcessing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByGmail(String username);
    boolean existsByGmail(String username);



    @Query(value = """

            SELECT u.id
            FROM bpfurniture.users u
            
            Join bpfurniture.user_settings us ON us.user_id = u.id

            Where us.order_processing = :orderProcessing


""" ,  nativeQuery = true)
    List<Long> getIdsOfTheOrderProcessing(String orderProcessing);




    @Query("""
SELECT
    u.googleId
   

FROM User u

 WHERE u.gmail = :gmail
  
""")
    String findGoogleId(String gmail);



    @Query("""
SELECT new com.example.jwt_demo.DTOS.User.ProfileInformation(
    u.fullName,
    u.gmail,
    u.role,
    u.phoneNumber,
    u.bio,
    u.imageUrl
)
FROM User u

 WHERE u.id = :userId
  
""")
    ProfileInformation getProfileInfo(Long userId);



    @Query("""
SELECT new com.example.jwt_demo.DTOS.User.AccountOverview(
    u.created,
    u.gmail,
    u.verification,
    u.bannedTill,
    u.lastLogin,
    u.ip
)
 FROM User u

 WHERE u.id = :userId
  
""")
    AccountOverview getAccountOverview(Long userId);

    @Query("""
SELECT new com.example.jwt_demo.DTOS.User.PersonalPrefrences(
    s.dateFormat,
    s.timeZone,
    s.language,
    s.receiveGmail
)
FROM UserSettings s
WHERE s.user.id = :userId
""")
    PersonalPrefrences getPersonalPrefrences(Long userId);


    @Query("""
    SELECT s
    FROM UserSettings s
    WHERE s.user.id = :userId
""")
    UserSettings getUserSettings(Long userId);


    @Query("""
    SELECT new com.example.jwt_demo.DTOS.User.Appearance(
    
    s.theme,
    s.accent,
    s.sidebarSize
    
    )
    FROM UserSettings s
    WHERE s.user.id = :userId
""")
    Appearance getAppearance(Long userId);









}