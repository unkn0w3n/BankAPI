package routes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;
import controller.AccountController;
import controller.CardController;
import controller.PaymentController;
import model.Account;
import model.Payment;
import utilits.HttpHelper;
import utilits.Properties;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ApiPayments {
    ObjectMapper objectMapper = new ObjectMapper();

    ApiPayments(){
    }

    public void process(HttpServer server){
        server.createContext("/api/payments", (exchange -> {
            exchange.getResponseHeaders().set("Content-Type", "application/json;charset=utf-8");
            String requestUrl = exchange.getRequestURI().toString();
            System.out.println("[!] URL:"+requestUrl);
            switch (exchange.getRequestMethod()) {
                case "GET":
                    //[:GET] [/api/payments/(\d+)]. Get payment info.
                    if(requestUrl.matches("/api/payments/\\d+$")){
                        int paymentId = 0;
                        Matcher m = Pattern.compile("/api/payments/(\\d+)$").matcher(requestUrl);
                        while(m.find()){
                            paymentId = m.group(1).length()>0 ? Integer.parseInt(m.group(1)) : 0;
                        }
                        if(paymentId == 0){
                            HttpHelper.sendHttpResponse(exchange, "PaymentId not set in GET",400);
                        } else {
                            String jsonObjString = null;
                            try {
                                PaymentController paymentController = new PaymentController();
                                jsonObjString = paymentController.getPaymentInfoById(paymentId);
                            } catch (SQLException ex) {
                                ex.printStackTrace();
                            }
                            HttpHelper.sendHttpResponse(exchange, "json", jsonObjString,200);
                            System.out.println("[+] GET "+requestUrl+". show payment Information");
                        }
                    }

                    //[:GET] [/api/payments/(\d+)]. Get payment all payment of Account
                    if(requestUrl.matches("/api/payments/account/\\d+$")){
                        String accNumber = "";
                        Matcher m = Pattern.compile("/api/payments/account/(\\d{20})$").matcher(requestUrl);
                        while(m.find()){
                            accNumber = m.group(1).length()>0 ? m.group(1) : "";
                        }
                        if(accNumber.length() == 0){
                            HttpHelper.sendHttpResponse(exchange, "error","Account number not set correctly. Must be 20 digits.",400);
                            break;
                        } else {
                            //out data
                            String payments = "";
                            try {
                                PaymentController paymentController = new PaymentController();
                                payments = paymentController.getAccountPaymentsInfoByAccNum(accNumber);
                                if (payments.matches("error")) {
                                    HttpHelper.sendHttpResponse(exchange, "error", payments, 400);
                                    break;
                                }
                            } catch (SQLException ex) {
                                ex.printStackTrace();
                            }
                            HttpHelper.sendHttpResponse(exchange, "json", payments, 200);
                            System.out.println("[+] GET " + requestUrl + ". Payments info received for AccNumber = [" + accNumber + "]");
                            break;
                        }
                    }
                    break;


                //[:POST] [/api/payments]. Add new payments to DB.
                case "POST":
//                    Payment payment = objectMapper.readValue(exchange.getRequestBody(), Payment.class);
//                    try {
//                        PaymentController paymentController = new PaymentController();
//                        String result = paymentController.addNewPaymentInfo(account);
//                        if(result.length()>0){
//                            HttpHelper.sendHttpResponse(exchange, "error", result,200);
//                        }
//                    } catch (SQLException ex) {
//                        ex.printStackTrace();
//                    }
//                    if(!Properties.debug){
//                        String jsonObjString = objectMapper.writeValueAsString(payment);
//                        HttpHelper.sendHttpResponse(exchange, "json", jsonObjString,200);
//                        System.out.println(jsonObjString);
//                    } else {
//                        HttpHelper.sendHttpResponse(exchange, "status","success",200);
//                    }
//                    System.out.println("[+] POST /api/payments. Added new payment data.");
                    break;

                default:
                    HttpHelper.sendHttpResponse(exchange, "error","Only GET and POST Requests are allowed", 405);
            }
            exchange.close();
        }));
    }
}
