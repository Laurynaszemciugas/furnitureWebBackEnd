package com.example.jwt_demo.Common;

import com.example.jwt_demo.Entity.Orders;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GmailHTML {






    public String buildGmailCode(String code) {

        if (code == null || !code.matches("\\d{6}")) {
            throw new IllegalArgumentException("Invalid verification code");
        }

        StringBuilder message = new StringBuilder();

        message.append("""
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Password Reset</title>
            </head>

            <body style="margin:0; padding:0; background-color:#f4f7fb;
                         font-family:Arial,Helvetica,sans-serif;">

            <table role="presentation" width="100%" cellspacing="0" cellpadding="0"
                   style="background-color:#f4f7fb; padding:40px 12px;">
                <tr>
                    <td align="center">

                        <table role="presentation" width="100%" cellspacing="0" cellpadding="0"
                               style="max-width:480px; background:#ffffff;
                                      border-radius:12px; overflow:hidden;
                                      border:1px solid #e5eaf2;">

                            <!-- Blue accent -->
                            <tr>
                                <td style="height:5px; background:#1a73e8; font-size:0;">
                                    &nbsp;
                                </td>
                            </tr>

                            <!-- Header -->
                            <tr>
                                <td style="padding:32px 32px 16px; text-align:center;">

                                    <div style="font-size:28px; font-weight:bold;
                                                color:#1a73e8; letter-spacing:-1px;">
                                        Account Security
                                    </div>

                                    <h1 style="margin:24px 0 12px; font-size:24px;
                                               line-height:1.3; color:#202124;
                                               font-weight:600;">
                                        Reset your password
                                    </h1>

                                    <p style="margin:0; font-size:15px;
                                              line-height:1.7; color:#5f6368;">
                                        We received a request to reset your password.
                                        Use the verification code below to continue.
                                    </p>

                                </td>
                            </tr>

                            <!-- Verification code -->
                            <tr>
                                <td style="padding:20px 32px 28px; text-align:center;">

                                    <div style="margin-bottom:12px; font-size:12px;
                                                font-weight:bold; color:#5f6368;
                                                letter-spacing:1.5px;
                                                text-transform:uppercase;">
                                        Your verification code
                                    </div>

                                    <div style="display:inline-block; padding:18px 28px;
                                                background:#e8f0fe;
                                                border:1px solid #d2e3fc;
                                                border-radius:8px;
                                                color:#1967d2; font-size:32px;
                                                font-weight:bold; letter-spacing:8px;">
                                        """)
                .append(code)
                .append("""
                                    </div>

                                    <p style="margin:20px 0 0; font-size:14px;
                                              line-height:1.6; color:#5f6368;">
                                        Enter this code on the password reset page.
                                    </p>

                                </td>
                            </tr>

                            <!-- Security notice -->
                            <tr>
                                <td style="padding:0 32px 28px;">

                                    <div style="padding:16px; background:#f8fafd;
                                                border-radius:8px;
                                                border:1px solid #edf0f5;">

                                        <p style="margin:0; font-size:13px;
                                                  line-height:1.7; color:#5f6368;">
                                            <strong style="color:#202124;">
                                                Didn't request a reset?
                                            </strong>
                                            You can safely ignore this email.
                                            Never share your verification code
                                            with anyone.
                                        </p>

                                    </div>

                                </td>
                            </tr>

                            <!-- Footer -->
                            <tr>
                                <td style="padding:20px 24px; text-align:center;
                                           background:#f8fafd;
                                           border-top:1px solid #edf0f5;">

                                    <p style="margin:0; font-size:12px;
                                              line-height:1.6; color:#80868b;">
                                        This is an automated message.
                                        Please do not reply.
                                    </p>

                                </td>
                            </tr>

                        </table>

                    </td>
                </tr>
            </table>

            </body>
            </html>
            """);

        return message.toString();
    }





    public String buildPriorityEmail(List<Orders> orders) {

        StringBuilder message = new StringBuilder();


        // ============================================================
        // HEADER
        // ============================================================

        message.append("""
            <!DOCTYPE html>
            <html>

            <head>
                <meta charset="UTF-8">
                <title>Orders Requiring Attention</title>
            </head>

            <body style="
                margin: 0;
                padding: 0;
                background-color: #f4f6f8;
                font-family: Arial, Helvetica, sans-serif;
                color: #202124;
            ">

            <div style="
                max-width: 850px;
                margin: 30px auto;
                background-color: #ffffff;
                border-radius: 12px;
                overflow: hidden;
                box-shadow: 0 2px 12px rgba(0,0,0,0.08);
            ">

                <!-- HEADER -->

                <div style="
                    background-color: #2275F3;
                    color: #ffffff;
                    padding: 25px 30px;
                ">

                    <h1 style="
                        margin: 0;
                        font-size: 24px;
                    ">
                        Furniture Management System
                    </h1>

                    <p style="
                        margin: 8px 0 0;
                        font-size: 14px;
                        opacity: 0.9;
                    ">
                        Order attention notification
                    </p>

                </div>


                <!-- CONTENT -->

                <div style="padding: 30px;">

                    <!-- WARNING -->

                    <div style="
                        background-color: #fff7e6;
                        border-left: 5px solid #f59e0b;
                        border-radius: 6px;
                        padding: 16px 18px;
                        margin-bottom: 28px;
                    ">

                        <div style="
                            font-size: 18px;
                            font-weight: bold;
                            color: #92400e;
                        ">
                            ⚠ Orders require attention
                        </div>

                        <p style="
                            margin: 7px 0 0;
                            color: #78350f;
                            font-size: 14px;
                        ">
                            The system found
                            <strong>%d</strong>
                            orders with high priority or overdue status.
                        </p>

                    </div>
            """.formatted(orders.size()));


        // ============================================================
        // ORDERS
        // ============================================================

        for (Orders order : orders) {

            message.append("""
        
        <!-- ORDER -->

        <div style="
            border: 1px solid #e1e5ea;
            border-radius: 10px;
            margin-bottom: 25px;
            overflow: hidden;
        ">

            <!-- ORDER HEADER -->

            <div style="
                background-color: #f8fafc;
                padding: 18px 20px;
                border-bottom: 1px solid #e1e5ea;
            ">

                <div style="
                    font-size: 20px;
                    font-weight: bold;
                    color: #1f2937;
                ">
                    Order #%d
                </div>


                <div style="
                    margin-top: 10px;
                    font-size: 14px;
                    color: #6b7280;
                ">

                    Customer:

                    <strong style="color: #374151;">
                        %s
                    </strong>

                    <br>

                    Email:

                    <span style="color: #374151;">
                        %s
                    </span>

                    <br>

                    Priority:

                    <strong style="
                        color: #dc2626;
                    ">
                        %s
                    </strong>

                    <br>

                    Estimated due date:

                    <strong style="
                        color: #374151;
                    ">
                        %s
                    </strong>

                </div>

            </div>


            <!-- ORDER BODY -->

            <div style="padding: 20px;">

                <h3 style="
                    margin: 0 0 12px 0;
                    font-size: 16px;
                    color: #1f2937;
                ">
                    Products
                </h3>


                <table style="
                    width: 100%%;
                    border-collapse: collapse;
                    font-size: 14px;
                ">

                    <thead>

                        <tr style="
                            background-color: #f1f5f9;
                            color: #475569;
                        ">

                            <th style="
                                padding: 11px;
                                text-align: left;
                                border-bottom: 1px solid #e2e8f0;
                            ">
                                Product
                            </th>

                            <th style="
                                padding: 11px;
                                text-align: center;
                                border-bottom: 1px solid #e2e8f0;
                            ">
                                Amount
                            </th>

                            <th style="
                                padding: 11px;
                                text-align: right;
                                border-bottom: 1px solid #e2e8f0;
                            ">
                                Material cost
                            </th>

                            <th style="
                                padding: 11px;
                                text-align: right;
                                border-bottom: 1px solid #e2e8f0;
                            ">
                                Total
                            </th>

                        </tr>

                    </thead>

                    <tbody>
""".formatted(
                    order.getId(),
                    order.getOrderCreatedByName(),
                    order.getOrderCreatedByGmail(),
                    order.getPriority(),
                    order.getEstimatedDueDate()
            ));


            // ========================================================
            // PRODUCTS
            // ========================================================

            for (var product : order.getProductsData()) {

                message.append("""
                            
                            <tr>

                                <td style="
                                    padding: 11px;
                                    border-bottom: 1px solid #e5e7eb;
                                    color: #1f2937;
                                ">
                                    <strong>%s</strong>
                                </td>

                                <td style="
                                    padding: 11px;
                                    text-align: center;
                                    border-bottom: 1px solid #e5e7eb;
                                    color: #374151;
                                ">
                                    %d
                                </td>

                                <td style="
                                    padding: 11px;
                                    text-align: right;
                                    border-bottom: 1px solid #e5e7eb;
                                    color: #374151;
                                ">
                                    %.2f €
                                </td>

                                <td style="
                                    padding: 11px;
                                    text-align: right;
                                    border-bottom: 1px solid #e5e7eb;
                                    color: #374151;
                                ">
                                    %.2f €
                                </td>

                            </tr>
                    """.formatted(
                        product.getProduct().getProductName(),
                        product.getAmountOfProduct(),
                        product.getProduct().getMaterialCost(),
                        product.getCost()
                ));
            }


            // ========================================================
            // EMPLOYEES
            // ========================================================

            message.append("""
                        
                        </tbody>

                    </table>


                    <!-- EMPLOYEES -->

                    <h3 style="
                        margin: 25px 0 12px 0;
                        font-size: 16px;
                        color: #1f2937;
                    ">
                        Assigned employees
                    </h3>


                    <div style="
                        background-color: #f8fafc;
                        border-radius: 7px;
                        padding: 12px 15px;
                    ">
                """);


            if (order.getEmployees() != null &&
                    !order.getEmployees().isEmpty()) {

                for (var employee : order.getEmployees()) {

                    message.append("""
                            
                            <div style="
                                padding: 5px 0;
                                font-size: 14px;
                                color: #374151;
                            ">
                                👤 %s
                            </div>
                    """.formatted(
                            employee.getEmployee().getFullName()
                    ));
                }

            } else {

                message.append("""
                            
                            <div style="
                                font-size: 14px;
                                color: #6b7280;
                            ">
                                No employees assigned
                            </div>
                    """);
            }


            // ========================================================
            // CLOSE ORDER
            // ========================================================

            message.append("""
                        
                    </div>

                </div>
            """);
        }


        // ============================================================
        // FOOTER
        // ============================================================

        message.append("""
            
                </div>


                <!-- FOOTER -->

                <div style="
                    background-color: #f8fafc;
                    border-top: 1px solid #e5e7eb;
                    padding: 20px 30px;
                    text-align: center;
                    color: #6b7280;
                    font-size: 12px;
                ">

                    This is an automated message from the
                    <strong>Furniture Management System</strong>.

                    <br><br>

                    Please review the listed orders in the system.

                </div>

            </div>

            </body>
            </html>
            """);


        return message.toString();
    }



}
