package com.example.jwt_demo.Common.InternetScraper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Service
public class Scraper {

    HttpClient client = HttpClient.newHttpClient();

    public List<PriceResult> scraper(String text) throws IOException, InterruptedException {


        text = text.replace(" ", "+");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("https://www.kaina24.lt/s/%s/",text)))
                .GET()
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        String html = response.body();



        //System.out.println(html);

        Document document = Jsoup.parse(response.body());



        Elements element = document.select(".product-item-h");

        if (element.isEmpty()) {


            return differentLayout(html);
        }


        System.out.println("------------------------------------------------------------------------------");



        List<PriceResult> priceResultList = new ArrayList<>();

        for(Element product : element){


            String vendorName = product.select(".shop a").attr("title");
            if(vendorName.isEmpty()){
                vendorName = null;
            }

            String productName = product.select(".name a").attr("title");

            String productPrice = product.select(".price").text();

            Double priceOfProduct = Double.parseDouble(
                    productPrice
                            .replace("€", "")
                            .trim()
                            .replace(",", ".")
                            .replace("*", "")
                            .replace("nuo ", "")
            );

            String vendorUrl = product.select(".price a").attr("href");

            if(vendorUrl.isEmpty()){
                vendorUrl = product.select(".sellers-count a").attr("href");
            }

            String sellerCount = product.select(".sellers-count").text();
            sellerCount = sellerCount.isEmpty() ? "1" : sellerCount;

            Long sellerCountOfProduct = Long.parseLong(
                    sellerCount.replaceAll("[^0-9]", "")
            );


            String productPicture = product.select(".image-wrap img").attr("data-src");



            String vendorPicture = product.select(".shop img").attr("data-src");

            if(vendorPicture.isEmpty()){
                vendorPicture = null;
            }





            List<MultipleVendors> multipleVendors = null;
            if (!"1".equals(sellerCount)) {


            multipleVendors = multipleVendorPage(multipleVendors, vendorUrl);




            }


            priceResultList.add(
                    new PriceResult(vendorName,
                            productName,
                            priceOfProduct,
                            vendorUrl,
                            sellerCountOfProduct,
                            productPicture,
                            vendorPicture,
                            multipleVendors)
            );



        }

return  priceResultList;





    }

    public List<MultipleVendors> multipleVendorPage(List<MultipleVendors> multipleVendors, String vendorUrl) throws IOException, InterruptedException {


        multipleVendors = new ArrayList<>();


        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(URI.create(vendorUrl))
                .GET()
                .build();

        HttpResponse<String> response2 =
                client.send(request2, HttpResponse.BodyHandlers.ofString());

        Document document2 = Jsoup.parse(response2.body());

        Elements element2 = document2.select(".item-table-wrap[itemprop=offers] .seller-item-table");

        if(element2.isEmpty()){
            return null;
        }

        for(Element extraProducts : element2){


            String expandedVendorName = extraProducts.select(".col-1 img").attr("alt");

            String expandedProductName = extraProducts.select(".col-4 h3").first().text();

            String expandedProductPrice = extraProducts.select(".price").first().text();

            String expandedProductVendorUrl = extraProducts.select(".col-4 a").attr("href");

            String expandedProductVendorImage = extraProducts.select(".col-1 img").attr("data-src");


            Double priceExt = Double.parseDouble(
                    expandedProductPrice
                            .replace("€", "")
                            .replace(",", ".")
                            .replace("*", "")
                            .trim()
            );



            multipleVendors.add(new MultipleVendors(
                    expandedVendorName,
                    expandedProductName,
                    priceExt,
                    expandedProductVendorUrl,
                    expandedProductVendorImage)
            );



        }


        return multipleVendors;

    }



    public List<PriceResult> differentLayout(String html) throws IOException, InterruptedException {

        List<PriceResult> priceResultList = new ArrayList<>();

        //System.out.println(html);

        Document document = Jsoup.parse(html);

        Elements element = document.select(".product-item");


        for (Element product : element) {

            String productName = product.select(".product-item img").attr("alt");

            String productPrice = product.select(".price").text();

            Double priceOfProduct = Double.parseDouble(
                    productPrice
                            .replace("€", "")
                            .trim()
                            .replace(",", ".")
                            .replace("*", "")
                            .replace("nuo ", "")
            );

            String vendorUrl = product.select(".product-item a").attr("href");

            String sellerCount = product.select(".sellers-count").text();

            Long sellerCountOfProduct = Long.parseLong(
                    sellerCount.replaceAll("[^0-9]", "")
            );

            String imageUrl = product.select(".product-item img").attr("data-src");


            List<MultipleVendors> multipleVendors = new ArrayList<>();

            multipleVendors = multipleVendorPage(multipleVendors, vendorUrl);

            priceResultList.add(
                    new PriceResult(
                            null,
                            productName,
                            priceOfProduct,
                            vendorUrl,
                            sellerCountOfProduct,
                            imageUrl,
                            null,
                            multipleVendors
                    )
            );





        }

        return priceResultList;
    }


}





//Elements products = document.select(".product-item-h");
//
//List<PriceResult> results = new ArrayList<>();
//
//        for (Element product : products) {
//
//String name = product.select(".name").text();
//
//String priceText = product.select(".price").text()
//        .replace("€", "")
//        .trim()
//        .replace(",", ".")
//        .replace("nuo ", "");
//
//
//int countOfSellers;
//
//            try {
//countOfSellers = Integer.parseInt(
//        product.select(".sellers-count")
//                                .text()
//                                .replaceAll("[^0-9]", "")
//                );
//                        } catch (NumberFormatException e) {
//countOfSellers = 1;
//        }
//
//
//Double price = Double.parseDouble(priceText);
//
//String shop = product.select(".shop a").attr("title");
//
//String url = product.select(".name a").attr("href");
//
//
//
//            results.add(
//                    new PriceResult(
//                name,
//                    price,
//                    shop,
//                    url,
//                    countOfSellers
//                    )
//            );
//                    }
//
//
//                    System.out.println("===================== List ========================");
//        System.out.println(results);