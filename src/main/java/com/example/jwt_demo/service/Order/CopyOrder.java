package com.example.jwt_demo.service.Order;

import com.example.jwt_demo.Entity.EmployeeJoin.OrderEmployees;
import com.example.jwt_demo.Entity.Materials;
import com.example.jwt_demo.Entity.OrderJoin.OrderProducts;
import com.example.jwt_demo.Entity.OrderJoin.OrderStepsToComplete;
import com.example.jwt_demo.Entity.Orders;
import com.example.jwt_demo.Entity.Product;
import com.example.jwt_demo.Entity.ProductJoin.ProductMaterials;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CopyOrder {

    public Orders copyOrder(Orders original) {

        Orders copy = new Orders();

        copy.setId(original.getId());
        copy.setBillingAddress(original.getBillingAddress());
        copy.setTotalPrice(original.getTotalPrice());
        copy.setOrderNote(original.getOrderNote());
        copy.setOrderStatus(original.getOrderStatus());
        copy.setEstimatedDueDate(original.getEstimatedDueDate());
        copy.setPayMethod(original.getPayMethod());
        copy.setPayStatus(original.getPayStatus());





        // Copy products
        List<OrderProducts> copiedProducts = original.getProductsData()
                .stream()
                .map(oldProduct -> {

                    OrderProducts newProduct = new OrderProducts();

                    newProduct.setId(oldProduct.getId());
                    newProduct.setAmountOfProduct(oldProduct.getAmountOfProduct());
                    newProduct.setCost(oldProduct.getCost());



                    Product productCopy = new Product();
                    productCopy.setStockCalculatedManually(oldProduct.getProduct().isStockCalculatedManually());
                    productCopy.setId(oldProduct.getProduct().getId());
                    productCopy.setProductName(oldProduct.getProduct().getProductName());

                    List<ProductMaterials> copiedMaterials =
                            oldProduct.getProduct().getMaterials()
                                    .stream()
                                    .map(oldMaterial -> {

                                        ProductMaterials newMaterial = new ProductMaterials();
                                        newMaterial.setAmountUsed(oldMaterial.getAmountUsed());



                                        newMaterial.setId(oldMaterial.getId());


                                        Materials materialCopy = new Materials();

                                        materialCopy.setId(
                                                oldMaterial.getMaterials().getId()
                                        );

                                        materialCopy.setMaterialName(
                                                oldMaterial.getMaterials().getMaterialName()
                                        );

                                        materialCopy.setInStock(
                                                oldMaterial.getMaterials().getInStock()
                                        );







                                        newMaterial.setMaterials(materialCopy);

                                        return newMaterial;

                                    })
                                    .toList();


                    productCopy.setMaterials(copiedMaterials);

                    newProduct.setProduct(productCopy);

                    return newProduct;

                })
                .toList();


        copy.setProductsData(copiedProducts);


        // Copy employees
        List<OrderEmployees> copiedEmployees =
                original.getEmployees()
                        .stream()
                        .map(oldEmployee -> {

                            OrderEmployees newEmployee = new OrderEmployees();

                            newEmployee.setId(oldEmployee.getId());
                            newEmployee.setEmployee(oldEmployee.getEmployee());

                            return newEmployee;

                        })
                        .toList();


        copy.setEmployees(copiedEmployees);


        return copy;
    }

}


//333333333333333333333333333333
//
//
//
//        package com.example.jwt_demo.service.Order;
//
//import com.example.jwt_demo.Entity.EmployeeJoin.OrderEmployees;
//import com.example.jwt_demo.Entity.Materials;
//import com.example.jwt_demo.Entity.OrderJoin.OrderProducts;
//import com.example.jwt_demo.Entity.OrderJoin.OrderStepsToComplete;
//import com.example.jwt_demo.Entity.Orders;
//import com.example.jwt_demo.Entity.Product;
//import com.example.jwt_demo.Entity.ProductJoin.ProductMaterials;
//import org.springframework.stereotype.Service;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Service
//public class CopyOrder {
//
//    public Orders copyOrder(Orders original) {
//
//        Orders copy = new Orders();
//
//        // ==========================
//        // ORDER
//        // ==========================
//
//        // Existing order -> keep ID
//        copy.setId(original.getId());
//
//        copy.setBillingAddress(original.getBillingAddress());
//        copy.setTotalPrice(original.getTotalPrice());
//        copy.setOrderNote(original.getOrderNote());
//        copy.setOrderStatus(original.getOrderStatus());
//        copy.setEstimatedDueDate(original.getEstimatedDueDate());
//        copy.setPayMethod(original.getPayMethod());
//        copy.setPayStatus(original.getPayStatus());
//
//
//        // ==========================
//        // PRODUCTS
//        // ==========================
//
//        List<OrderProducts> copiedProducts = new ArrayList<>();
//
//        if (original.getProductsData() != null) {
//
//            for (OrderProducts oldProduct : original.getProductsData()) {
//
//                OrderProducts newProduct = new OrderProducts();
//
//                // VERY IMPORTANT
//                // Existing OrderProducts -> keep ID
//                newProduct.setId(oldProduct.getId());
//
//                newProduct.setAmountOfProduct(
//                        oldProduct.getAmountOfProduct()
//                );
//
//                newProduct.setCost(
//                        oldProduct.getCost()
//                );
//
//                // ==========================
//                // PRODUCT
//                // ==========================
//
//                Product oldCatalogProduct = oldProduct.getProduct();
//
//                if (oldCatalogProduct != null) {
//
//                    Product productCopy = new Product();
//
//                    productCopy.setId(
//                            oldCatalogProduct.getId()
//                    );
//
//                    productCopy.setProductName(
//                            oldCatalogProduct.getProductName()
//                    );
//
//                    productCopy.setStockCalculatedManually(
//                            oldCatalogProduct.isStockCalculatedManually()
//                    );
//
//
//                    // ==========================
//                    // MATERIALS
//                    // ==========================
//
//                    List<ProductMaterials> copiedMaterials =
//                            new ArrayList<>();
//
//                    if (oldCatalogProduct.getMaterials() != null) {
//
//                        for (ProductMaterials oldMaterial :
//                                oldCatalogProduct.getMaterials()) {
//
//                            ProductMaterials newMaterial =
//                                    new ProductMaterials();
//
//                            newMaterial.setId(
//                                    oldMaterial.getId()
//                            );
//
//                            newMaterial.setAmountUsed(
//                                    oldMaterial.getAmountUsed()
//                            );
//
//                            if (oldMaterial.getMaterials() != null) {
//
//                                Materials materialCopy =
//                                        new Materials();
//
//                                materialCopy.setId(
//                                        oldMaterial
//                                                .getMaterials()
//                                                .getId()
//                                );
//
//                                materialCopy.setMaterialName(
//                                        oldMaterial
//                                                .getMaterials()
//                                                .getMaterialName()
//                                );
//
//                                materialCopy.setInStock(
//                                        oldMaterial
//                                                .getMaterials()
//                                                .getInStock()
//                                );
//
//                                newMaterial.setMaterials(
//                                        materialCopy
//                                );
//                            }
//
//                            copiedMaterials.add(newMaterial);
//                        }
//                    }
//
//                    productCopy.setMaterials(
//                            copiedMaterials
//                    );
//
//                    newProduct.setProduct(
//                            productCopy
//                    );
//                }
//
//
//                // ==========================
//                // ORDER STEPS
//                // ==========================
//
//                List<OrderStepsToComplete> copiedSteps =
//                        new ArrayList<>();
//
//                if (oldProduct.getOrderSteps() != null) {
//
//                    for (OrderStepsToComplete oldStep :
//                            oldProduct.getOrderSteps()) {
//
//                        OrderStepsToComplete newStep =
//                                new OrderStepsToComplete();
//
//                        // VERY IMPORTANT
//                        // Existing step -> keep ID
//                        newStep.setId(
//                                oldStep.getId()
//                        );
//
//                        newStep.setStepRealId(
//                                oldStep.getStepRealId()
//                        );
//
//                        newStep.setStepId(
//                                oldStep.getStepId()
//                        );
//
//                        newStep.setStepName(
//                                oldStep.getStepName()
//                        );
//
//                        newStep.setStepDescription(
//                                oldStep.getStepDescription()
//                        );
//
//                        newStep.setStepsNeeded(
//                                oldStep.getStepsNeeded()
//                        );
//
//                        newStep.setStepsCompleted(
//                                oldStep.getStepsCompleted()
//                        );
//
//                        newStep.setEmployee(
//                                oldStep.getEmployee()
//                        );
//
//                        newStep.setProductFinishStepStatus(
//                                oldStep.getProductFinishStepStatus()
//                        );
//
//                        newStep.setCreated(
//                                oldStep.getCreated()
//                        );
//
//                        // Maintain relationship
//                        newStep.setOrderProducts(
//                                newProduct
//                        );
//
//                        // IMPORTANT:
//                        // Do NOT copy completion logs here.
//                        //
//                        // They belong to the existing step and
//                        // should not be recreated as new entities.
//
//                        copiedSteps.add(newStep);
//                    }
//                }
//
//                newProduct.setOrderSteps(
//                        copiedSteps
//                );
//
//                copiedProducts.add(
//                        newProduct
//                );
//            }
//        }
//
//        copy.setProductsData(
//                copiedProducts
//        );
//
//
//        // ==========================
//        // EMPLOYEES
//        // ==========================
//
//        List<OrderEmployees> copiedEmployees =
//                new ArrayList<>();
//
//        if (original.getEmployees() != null) {
//
//            for (OrderEmployees oldEmployee :
//                    original.getEmployees()) {
//
//                OrderEmployees newEmployee =
//                        new OrderEmployees();
//
//                newEmployee.setId(
//                        oldEmployee.getId()
//                );
//
//                newEmployee.setEmployee(
//                        oldEmployee.getEmployee()
//                );
//
//                copiedEmployees.add(
//                        newEmployee
//                );
//            }
//        }
//
//        copy.setEmployees(
//                copiedEmployees
//        );
//
//
//        return copy;
//    }
//}