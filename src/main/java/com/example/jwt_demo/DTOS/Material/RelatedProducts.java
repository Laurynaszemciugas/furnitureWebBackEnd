package com.example.jwt_demo.DTOS.Material;

import com.example.jwt_demo.Enums.Category;
import com.example.jwt_demo.Enums.ProductCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RelatedProducts {


    private Long id;
    private String productName;
    private Category productCategory;
    private Long amountUsed;
    private String imageUrl;



}
//public void setImageUrl(String imageUrl) {
//    if(imageUrl == null){
//        imageUrl = "No_picture.png";
//    }
//}