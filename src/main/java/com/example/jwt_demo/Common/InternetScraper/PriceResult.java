package com.example.jwt_demo.Common.InternetScraper;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PriceResult {

    private String vendorName;
    private String productName;
    private Double price;
    private String vendorUrl;
    private Long sellerCount;
    private String imageUrl;
    private String vendorImageUrl;
    private List<MultipleVendors> multipleVendors;

    @Override
    public String toString() {
        return "PriceResult {\n" +
                "  vendorName='" + vendorName + "',\n" +
                "  productName='" + productName + "',\n" +
                "  price=" + price + ",\n" +
                "  vendorUrl='" + vendorUrl + "',\n" +
                "  sellerCount=" + sellerCount + ",\n" +
                "  imageUrl='" + imageUrl + "',\n" +
                "  vendorImageUrl='" + vendorImageUrl + "',\n" +
                "  multipleVendors=" + multipleVendors + "\n" +
                "}"
                +
                "\n"+ "\n";
    }


}
