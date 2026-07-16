package com.example.demo.controller;

import com.example.demo.model.Subscription;
import com.example.demo.service.SubscriptionService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/subscription")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    // Initialize Paystack payment → returns payment URL
    @PostMapping("/initialize/{userId}")
    public Map<String, Object> initializePayment(@PathVariable Long userId) {
        return subscriptionService.initializePayment(userId);
    }

    // Paystack webhook — called automatically after payment
    @PostMapping("/webhook")
    public void handleWebhook(@RequestBody Map<String, Object> payload) {
        String event = (String) payload.get("event");
        if ("charge.success".equals(event)) {
            Map<String, Object> data = (Map<String, Object>) payload.get("data");
            String reference = (String) data.get("reference");
            subscriptionService.handlePaymentSuccess(reference);
        }
    }

    // Serve HTML mock checkout page
    @GetMapping(value = "/mock-checkout", produces = MediaType.TEXT_HTML_VALUE)
    public String showMockCheckout(@RequestParam Long userId, @RequestParam String reference) {
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <title>Paystack Mock Payment Gateway</title>\n" +
                "    <meta name='viewport' content='width=device-width, initial-scale=1.0'>\n" +
                "    <link href='https://fonts.googleapis.com/css2?family=Outfit:wght@400;600;800&display=swap' rel='stylesheet'>\n" +
                "    <style>\n" +
                "        body {\n" +
                "            background: radial-gradient(circle at top right, #1f1235, #0f0a1c);\n" +
                "            color: #ffffff;\n" +
                "            font-family: 'Outfit', sans-serif;\n" +
                "            margin: 0;\n" +
                "            display: flex;\n" +
                "            align-items: center;\n" +
                "            justify-content: center;\n" +
                "            min-height: 100vh;\n" +
                "            padding: 20px;\n" +
                "            box-sizing: border-box;\n" +
                "        }\n" +
                "        .card {\n" +
                "            background: rgba(255, 255, 255, 0.03);\n" +
                "            backdrop-filter: blur(20px);\n" +
                "            -webkit-backdrop-filter: blur(20px);\n" +
                "            border: 1px solid rgba(255, 255, 255, 0.08);\n" +
                "            border-radius: 28px;\n" +
                "            padding: 40px;\n" +
                "            width: 100%;\n" +
                "            max-width: 440px;\n" +
                "            text-align: center;\n" +
                "            box-shadow: 0 20px 50px rgba(0, 0, 0, 0.3);\n" +
                "        }\n" +
                "        .logo {\n" +
                "            font-size: 50px;\n" +
                "            margin-bottom: 24px;\n" +
                "            animation: float 3s ease-in-out infinite;\n" +
                "        }\n" +
                "        @keyframes float {\n" +
                "            0% { transform: translateY(0px); }\n" +
                "            50% { transform: translateY(-10px); }\n" +
                "            100% { transform: translateY(0px); }\n" +
                "        }\n" +
                "        .title {\n" +
                "            font-size: 24px;\n" +
                "            font-weight: 800;\n" +
                "            margin: 0 0 8px 0;\n" +
                "            letter-spacing: -0.5px;\n" +
                "            color: #ffffff;\n" +
                "        }\n" +
                "        .subtitle {\n" +
                "            font-size: 14px;\n" +
                "            color: #a09cb0;\n" +
                "            margin: 0 0 32px 0;\n" +
                "        }\n" +
                "        .details-box {\n" +
                "            background: rgba(255, 255, 255, 0.02);\n" +
                "            border: 1px solid rgba(255, 255, 255, 0.04);\n" +
                "            border-radius: 20px;\n" +
                "            padding: 24px;\n" +
                "            margin-bottom: 32px;\n" +
                "            text-align: left;\n" +
                "        }\n" +
                "        .detail-row {\n" +
                "            display: flex;\n" +
                "            justify-content: space-between;\n" +
                "            margin-bottom: 12px;\n" +
                "            font-size: 14px;\n" +
                "        }\n" +
                "        .detail-row:last-child {\n" +
                "            margin-bottom: 0;\n" +
                "            border-top: 1px solid rgba(255, 255, 255, 0.08);\n" +
                "            padding-top: 12px;\n" +
                "            font-weight: 600;\n" +
                "        }\n" +
                "        .detail-label {\n" +
                "            color: #8c88a0;\n" +
                "        }\n" +
                "        .detail-val {\n" +
                "            color: #ffffff;\n" +
                "        }\n" +
                "        .amount-val {\n" +
                "            color: #00e676;\n" +
                "            font-size: 18px;\n" +
                "            font-weight: 800;\n" +
                "        }\n" +
                "        .btn {\n" +
                "            display: block;\n" +
                "            width: 100%;\n" +
                "            padding: 16px;\n" +
                "            border-radius: 16px;\n" +
                "            font-size: 16px;\n" +
                "            font-weight: 700;\n" +
                "            border: none;\n" +
                "            cursor: pointer;\n" +
                "            transition: all 0.3s ease;\n" +
                "            box-sizing: border-box;\n" +
                "            text-decoration: none;\n" +
                "        }\n" +
                "        .btn-pay {\n" +
                "            background: linear-gradient(135deg, #00e676, #00b0ff);\n" +
                "            color: #ffffff;\n" +
                "            box-shadow: 0 8px 25px rgba(0, 230, 118, 0.25);\n" +
                "            margin-bottom: 16px;\n" +
                "        }\n" +
                "        .btn-pay:hover {\n" +
                "            transform: translateY(-2px);\n" +
                "            box-shadow: 0 12px 30px rgba(0, 230, 118, 0.4);\n" +
                "        }\n" +
                "        .btn-cancel {\n" +
                "            background: transparent;\n" +
                "            color: #a09cb0;\n" +
                "            border: 1.5px solid rgba(255, 255, 255, 0.1);\n" +
                "        }\n" +
                "        .btn-cancel:hover {\n" +
                "            background: rgba(255, 255, 255, 0.05);\n" +
                "            color: #ffffff;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class='card'>\n" +
                "        <div class='logo'>🪙</div>\n" +
                "        <h1 class='title'>Mock Paystack Checkout</h1>\n" +
                "        <p class='subtitle'>FinLit Premium Subscription Upgrade</p>\n" +
                "        <div class='details-box'>\n" +
                "            <div class='detail-row'>\n" +
                "                <span class='detail-label'>User Account ID</span>\n" +
                "                <span class='detail-val'>#" + userId + "</span>\n" +
                "            </div>\n" +
                "            <div class='detail-row'>\n" +
                "                <span class='detail-label'>Transaction Ref</span>\n" +
                "                <span class='detail-val' style='font-family: monospace; font-size:12px;'>" + reference + "</span>\n" +
                "            </div>\n" +
                "            <div class='detail-row'>\n" +
                "                <span class='detail-label'>Price per Month</span>\n" +
                "                <span class='detail-val amount-val'>GHS 20.00</span>\n" +
                "            </div>\n" +
                "        </div>\n" +
                "        <form action='/api/subscription/mock-checkout/complete' method='post'>\n" +
                "            <input type='hidden' name='userId' value='" + userId + "'>\n" +
                "            <input type='hidden' name='reference' value='" + reference + "'>\n" +
                "            <button type='submit' class='btn btn-pay'>Simulate Successful Payment</button>\n" +
                "        </form>\n" +
                "        <a href='/api/subscription/mock-checkout/cancel' class='btn btn-cancel'>Cancel Payment</a>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>";
    }

    // Complete mock payment directly and display success HTML
    @PostMapping(value = "/mock-checkout/complete", produces = MediaType.TEXT_HTML_VALUE)
    public String completeMockCheckout(@RequestParam Long userId, @RequestParam String reference) {
        subscriptionService.activateSubscriptionDirectly(userId, reference);
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <title>Payment Successful</title>\n" +
                "    <meta name='viewport' content='width=device-width, initial-scale=1.0'>\n" +
                "    <link href='https://fonts.googleapis.com/css2?family=Outfit:wght@400;600;800&display=swap' rel='stylesheet'>\n" +
                "    <style>\n" +
                "        body {\n" +
                "            background: radial-gradient(circle at top right, #09201a, #050f0c);\n" +
                "            color: #ffffff;\n" +
                "            font-family: 'Outfit', sans-serif;\n" +
                "            margin: 0;\n" +
                "            display: flex;\n" +
                "            align-items: center;\n" +
                "            justify-content: center;\n" +
                "            min-height: 100vh;\n" +
                "            padding: 20px;\n" +
                "            box-sizing: border-box;\n" +
                "        }\n" +
                "        .card {\n" +
                "            background: rgba(255, 255, 255, 0.03);\n" +
                "            backdrop-filter: blur(20px);\n" +
                "            -webkit-backdrop-filter: blur(20px);\n" +
                "            border: 1px solid rgba(255, 255, 255, 0.08);\n" +
                "            border-radius: 28px;\n" +
                "            padding: 40px;\n" +
                "            width: 100%;\n" +
                "            max-width: 440px;\n" +
                "            text-align: center;\n" +
                "            box-shadow: 0 20px 50px rgba(0, 0, 0, 0.3);\n" +
                "        }\n" +
                "        .success-icon {\n" +
                "            font-size: 64px;\n" +
                "            color: #00e676;\n" +
                "            margin-bottom: 24px;\n" +
                "            animation: scaleUp 0.5s cubic-bezier(0.175, 0.885, 0.32, 1.275);\n" +
                "        }\n" +
                "        @keyframes scaleUp {\n" +
                "            0% { transform: scale(0.5); opacity: 0; }\n" +
                "            100% { transform: scale(1); opacity: 1; }\n" +
                "        }\n" +
                "        .title {\n" +
                "            font-size: 24px;\n" +
                "            font-weight: 800;\n" +
                "            margin: 0 0 12px 0;\n" +
                "            color: #ffffff;\n" +
                "        }\n" +
                "        .msg {\n" +
                "            font-size: 15px;\n" +
                "            color: #a09cb0;\n" +
                "            line-height: 22px;\n" +
                "            margin: 0 0 32px 0;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class='card'>\n" +
                "        <div class='success-icon'>🎉</div>\n" +
                "        <h1 class='title'>Payment Successful!</h1>\n" +
                "        <p class='msg'>Thank you! Your account #<strong>" + userId + "</strong> has been upgraded to <strong>Premium</strong>. You now have full access to all roadmap learning modules and features.</p>\n" +
                "        <span style='color: #00e676; font-weight:600; display:block;'>You can now close this tab and return to the FinLit app.</span>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>";
    }

    // Cancel mock payment and display cancel HTML
    @GetMapping(value = "/mock-checkout/cancel", produces = MediaType.TEXT_HTML_VALUE)
    public String cancelMockCheckout() {
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <title>Payment Canceled</title>\n" +
                "    <meta name='viewport' content='width=device-width, initial-scale=1.0'>\n" +
                "    <link href='https://fonts.googleapis.com/css2?family=Outfit:wght@400;600;800&display=swap' rel='stylesheet'>\n" +
                "    <style>\n" +
                "        body {\n" +
                "            background: radial-gradient(circle at top right, #251212, #100606);\n" +
                "            color: #ffffff;\n" +
                "            font-family: 'Outfit', sans-serif;\n" +
                "            margin: 0;\n" +
                "            display: flex;\n" +
                "            align-items: center;\n" +
                "            justify-content: center;\n" +
                "            min-height: 100vh;\n" +
                "            padding: 20px;\n" +
                "            box-sizing: border-box;\n" +
                "        }\n" +
                "        .card {\n" +
                "            background: rgba(255, 255, 255, 0.03);\n" +
                "            backdrop-filter: blur(20px);\n" +
                "            -webkit-backdrop-filter: blur(20px);\n" +
                "            border: 1px solid rgba(255, 255, 255, 0.08);\n" +
                "            border-radius: 28px;\n" +
                "            padding: 40px;\n" +
                "            width: 100%;\n" +
                "            max-width: 440px;\n" +
                "            text-align: center;\n" +
                "            box-shadow: 0 20px 50px rgba(0, 0, 0, 0.3);\n" +
                "        }\n" +
                "        .cancel-icon {\n" +
                "            font-size: 64px;\n" +
                "            color: #ff1744;\n" +
                "            margin-bottom: 24px;\n" +
                "        }\n" +
                "        .title {\n" +
                "            font-size: 24px;\n" +
                "            font-weight: 800;\n" +
                "            margin: 0 0 12px 0;\n" +
                "            color: #ffffff;\n" +
                "        }\n" +
                "        .msg {\n" +
                "            font-size: 15px;\n" +
                "            color: #a09cb0;\n" +
                "            line-height: 22px;\n" +
                "            margin: 0 0 32px 0;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class='card'>\n" +
                "        <div class='cancel-icon'>❌</div>\n" +
                "        <h1 class='title'>Payment Canceled</h1>\n" +
                "        <p class='msg'>The subscription transaction was canceled and no charges were made.</p>\n" +
                "        <span style='color: #ff1744; font-weight:600; display:block;'>You can now close this tab and return to the FinLit app.</span>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>";
    }

    // Check module access
    @GetMapping("/access/{userId}/module/{moduleId}")
    public Map<String, Object> checkAccess(
            @PathVariable Long userId,
            @PathVariable Long moduleId) {
        return subscriptionService.checkModuleAccess(userId, moduleId);
    }

    // Use a life when attempting a module
    @PostMapping("/use-life/{userId}/module/{moduleId}")
    public Map<String, Object> useLife(
            @PathVariable Long userId,
            @PathVariable Long moduleId) {
        return subscriptionService.useLife(userId, moduleId);
    }

    // Buy a life with points
    @PostMapping("/buy-life/{userId}/module/{moduleId}")
    public Map<String, Object> buyLife(
            @PathVariable Long userId,
            @PathVariable Long moduleId) {
        return subscriptionService.buyLife(userId, moduleId);
    }

    // Buy a quiz hint with points
    @PostMapping("/buy-hint/{userId}")
    public Map<String, Object> buyHint(@PathVariable Long userId) {
        return subscriptionService.buyHint(userId);
    }

    // Get subscription status
    @GetMapping("/status/{userId}")
    public Optional<Subscription> getStatus(@PathVariable Long userId) {
        return subscriptionService.getSubscription(userId);
    }
}