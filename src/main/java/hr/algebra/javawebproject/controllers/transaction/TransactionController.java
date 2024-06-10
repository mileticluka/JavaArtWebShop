package hr.algebra.javawebproject.controllers.transaction;

import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import hr.algebra.javawebproject.model.authModel.ApplicationUser;
import hr.algebra.javawebproject.model.dataModel.Transaction;
import hr.algebra.javawebproject.model.dataModel.TransactionItem;
import hr.algebra.javawebproject.repository.ITransactionItemRepository;
import hr.algebra.javawebproject.repository.ITransactionRepository;
import hr.algebra.javawebproject.repository.IUserRepository;
import hr.algebra.javawebproject.service.PaypalService;
import hr.algebra.javawebproject.service.TransactionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/transaction")
public class TransactionController {

    private final ITransactionRepository transactionRepository;
    private final ITransactionItemRepository transactionItemRepository;

    private final TransactionService transactionService;
    private final IUserRepository userRepository;

    private final PaypalService paypalService;

    public TransactionController(ITransactionItemRepository transactionItemRepository,
                                 ITransactionRepository transactionRepository,
                                 TransactionService transactionService, IUserRepository userRepository, PaypalService paypalService) {
        this.transactionRepository = transactionRepository;
        this.transactionItemRepository = transactionItemRepository;
        this.transactionService = transactionService;
        this.userRepository = userRepository;
        this.paypalService = paypalService;
    }

    @GetMapping("/get")
    public String getFilteredTransactions(
            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) LocalDateTime startTime,
            @RequestParam(required = false) LocalDateTime endTime,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            Model model) {

        Page<Transaction> transactionPage = transactionService.getFilteredTransactions(customerName,startTime,endTime,page,size);

        model.addAttribute("transactions", transactionPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", transactionPage.getTotalPages());
        model.addAttribute("totalItems", transactionPage.getTotalElements());
        return "transaction/index";
    }

    @GetMapping("/add-transaction-cash")
    public String addTransactionCash(HttpServletRequest request, Model model) {
        // Get the cart from session
        HttpSession session = request.getSession();
        List<TransactionItem> cartItems = (List<TransactionItem>) session.getAttribute("cart");

        // Get the currently authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        ApplicationUser user = userRepository.findByUsername(username);

        // If cart is empty or user is null, return an error view
        if (user == null || cartItems == null || cartItems.isEmpty()) {
            model.addAttribute("statusCode", 400);
            model.addAttribute("errorMessage", "Cart is empty or user not found, cannot create transaction");
            return "error/error";
        }

        // Create a new transaction for cash transactions
        Transaction transaction = new Transaction();
        transaction.setTransactionType("CASH");
        for (TransactionItem item : cartItems) {
            item.setTransaction(transaction);
        }
        transaction.setTransactionItems(cartItems);
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setUser(user);

        // Calculate total amount
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (TransactionItem ti : transaction.getTransactionItems()) {
            totalAmount = totalAmount.add(ti.getArtPiece().getPrice().multiply(BigDecimal.valueOf(ti.getQuantity())));
        }
        transaction.setTotalAmount(totalAmount);

        // Add the transaction to the session for the success page
        session.setAttribute("transaction", transaction);

        // Redirect to the success page
        return "redirect:/transaction/success";
    }

    // Helper method to get base URL dynamically
    private String getBaseUrl(HttpServletRequest request) {
        String baseUrl = request.getScheme() + "://" + request.getServerName();
        if (request.getServerPort() != 80 && request.getServerPort() != 443) {
            baseUrl += ":" + request.getServerPort();
        }
        return baseUrl;
    }

    @GetMapping("/add-transaction-paypal")
    public String addTransactionPaypal(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        List<TransactionItem> cartItems = (List<TransactionItem>) session.getAttribute("cart");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        ApplicationUser user = userRepository.findByUsername(username);

        if (user != null && cartItems != null && !cartItems.isEmpty()) {

            Transaction transaction = new Transaction();
            transaction.setTransactionType("PAYPAL");
            for (TransactionItem item : cartItems) {
                item.setTransaction(transaction);
            }
            transaction.setTransactionItems(cartItems);
            transaction.setTransactionDate(LocalDateTime.now());
            transaction.setUser(user);

            BigDecimal totalAmount = BigDecimal.ZERO;
            for (TransactionItem ti : transaction.getTransactionItems()) {
                totalAmount = totalAmount.add(ti.getArtPiece().getPrice().multiply(BigDecimal.valueOf(ti.getQuantity())));
            }
            transaction.setTotalAmount(totalAmount);

            try {
                String cancelUrl = getBaseUrl(request) + "/cart/view-cart";
                String successUrl = getBaseUrl(request) + "/transaction/execute-payment";
                Payment payment = paypalService.createPayment(
                        totalAmount,
                        "USD",
                        "paypal",
                        "sale",
                        transaction.getTransactionItems(),
                        cancelUrl,
                        successUrl);

                for (Links link : payment.getLinks()) {
                    if (link.getRel().equals("approval_url")) {
                        session.setAttribute("transaction", transaction);
                        return "redirect:" + link.getHref();
                    }
                }

            } catch (com.paypal.base.rest.PayPalRESTException e) {
                throw new RuntimeException(e);
            }
        }

        model.addAttribute("statusCode", 400);
        model.addAttribute("errorMessage", "Cart is empty or user not found, cannot create transaction");
        return "error/error";
    }

    @GetMapping("/execute-payment")
    public String executePayment(@RequestParam("paymentId") String paymentId,
                                 @RequestParam("PayerID") String payerId,
                                 HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        Transaction transaction = (Transaction) session.getAttribute("transaction");

        if (transaction == null) {
            model.addAttribute("statusCode", 400);
            model.addAttribute("errorMessage", "Transaction not found in session!");
            return "error/error";
        }

        try {
            Payment payment = paypalService.executePayment(paymentId, payerId);

            if (payment.getState().equals("approved")) {
                transaction = transactionRepository.save(transaction);

                session.removeAttribute("cart");
                session.removeAttribute("transaction");

                model.addAttribute("transaction", transaction);
                return "transaction/details";
            } else {
                model.addAttribute("statusCode", 400);
                model.addAttribute("errorMessage", "Payment not approved");
                return "error/error";
            }

        } catch (com.paypal.base.rest.PayPalRESTException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/success")
    public String success(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        Transaction transaction = (Transaction) session.getAttribute("transaction");

        if (transaction != null) {
            // Save the transaction to the repository
            transaction = transactionRepository.save(transaction);

            // Clear the cart in session after the transaction is added
            session.removeAttribute("cart");

            // Clear the transaction attribute from the session
            session.removeAttribute("transaction");

            model.addAttribute("transaction", transaction);

            return "transaction/details";
        } else {
            model.addAttribute("statusCode", 400);
            model.addAttribute("errorMessage", "Transaction not found in session!");
            return "error/error";
        }
    }

    @GetMapping("/details")
    public String getDetails(@RequestParam(name = "id") Long id, Model model) {

        Optional<Transaction> transactionOptional = transactionRepository.findById(id);

        if (transactionOptional.isPresent()) {
            Transaction transaction = transactionOptional.get();
            model.addAttribute("transaction", transaction);
            return "transaction/details";
        } else {
            model.addAttribute("statusCode", 400);
            model.addAttribute("errorMessage", "Requested resource does not exist!");
            return "error/error";
        }
    }
}
