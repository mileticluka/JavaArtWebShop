package hr.algebra.javawebproject.controllers.transaction;

import hr.algebra.javawebproject.model.dataModel.ArtPiece;
import hr.algebra.javawebproject.model.dataModel.TransactionItem;
import hr.algebra.javawebproject.service.ArtPieceService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/cart")
public class CartController {

    private final ArtPieceService artPieceService;

    public CartController(ArtPieceService artPieceService) {
        this.artPieceService = artPieceService;
    }

    @GetMapping("/view-cart")
    public String viewCart(HttpServletRequest request, Model model) throws IOException {
        // Get the cart from session
        HttpSession session = request.getSession();
        List<TransactionItem> cartItems = (List<TransactionItem>) session.getAttribute("cart");

        model.addAttribute("cartItems",cartItems);

        return "cart/index";
    }

    @GetMapping("/remove-from-cart")
    public void removeFromCart(@RequestParam Long id, HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Get the cart from session
        HttpSession session = request.getSession();
        List<TransactionItem> cart = (List<TransactionItem>) session.getAttribute("cart");

        if (cart != null) {
            // Find and remove the item from the cart based on the provided id
            cart.removeIf(item -> item.getArtPiece().getId().equals(id));
            // Update the cart in session
            session.setAttribute("cart", cart);
            // Send a response indicating success
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("Item removed from cart successfully");
        } else {
            // Send a response indicating failure
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("Cart is empty");
        }
    }

    @PostMapping("/update-cart")
    public void updateCart(@RequestParam Long id, @RequestParam int quantity, HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Get the cart from session
        HttpSession session = request.getSession();
        List<TransactionItem> cart = (List<TransactionItem>) session.getAttribute("cart");

        if (cart != null) {
            // Find the item in the cart based on the provided id
            for (TransactionItem item : cart) {
                if (item.getArtPiece().getId().equals(id)) {
                    // Update the quantity of the item
                    item.setQuantity(quantity);
                    // Update the cart in session
                    session.setAttribute("cart", cart);
                    // Send a response indicating success
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getWriter().write("Cart updated successfully");
                    return;
                }
            }
            // Send a response indicating failure if item not found
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("Item not found in cart");
        } else {
            // Send a response indicating failure if cart is empty
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("Cart is empty");
        }
    }

    @GetMapping("/add-to-cart")
    public void addToCart(@RequestParam Long id, @RequestParam int quantity, HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Get the ArtPiece based on the provided id
        Optional<ArtPiece> artPieceOptional = artPieceService.getById(id);

        if (artPieceOptional.isPresent()) {
            ArtPiece artPiece = artPieceOptional.get();

            // Create or get the cart from session
            HttpSession session = request.getSession();
            List<TransactionItem> cart = (List<TransactionItem>) session.getAttribute("cart");
            if (cart == null) {
                cart = new ArrayList<>();
            }

            // Add the item to the cart
            TransactionItem transactionItem = new TransactionItem();
            transactionItem.setArtPiece(artPiece);
            transactionItem.setQuantity(quantity);
            cart.add(transactionItem);

            // Update the cart in session
            session.setAttribute("cart", cart);

            // Send a response indicating success
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("Item added to cart successfully");
        } else {
            // Send a response indicating failure
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("Art piece not found");
        }
    }
}
