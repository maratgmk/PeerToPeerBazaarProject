package org.gafiev.peertopeerbazaar.entity.user;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import org.gafiev.peertopeerbazaar.entity.order.Basket;
import org.gafiev.peertopeerbazaar.entity.order.BuyerOrder;
import org.gafiev.peertopeerbazaar.entity.order.SellerOffer;
import org.gafiev.peertopeerbazaar.entity.product.Product;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents the core User entity within the Peer-to-Peer Bazaar application.
 * A user can operate in multiple roles, such as a Seller (product creator) or a Buyer.
 *
 * This entity implements UserDetails for seamless integration with
 * Spring Security's authentication and authorization mechanisms.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"sellerOfferSet", "buyerOrderSet", "productSet", "basket"})
@ToString(exclude = {"sellerOfferSet", "buyerOrderSet", "productSet", "basket", "password"})
@Entity
@Table(name = "users")
public class User implements UserDetails {
    /**
     * Unique identifier for the user.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * The first name of the user
     */
    @Column(name = "first_name")
    private String firstName;

    /**
     * The last name of the user.
     */
    @Column(name = "last_name")
    private String lastName;

    /**
     * The user's email address, which serves as a unique identifier
     * for login purposes across the entire system.
     */
    @Column(name = "email", unique = true)
    private String email;

    /**
     * The hashed password used for authentication.
     * Excluded from toString() for security compliance.
     */
    @Column(name = "password")
    private String password;

    /**
     * Contact phone number of the user.
     */
    @Column(name = "phone")
    private String phone;

    /**
     * A collection of roles assigned to the user, defining their permissions.
     * Stored in the 'user_roles' join table and fetched eagerly for security checks.
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id")) // FK: user_id -> users.id
    @Column(name = "roles")
    @Enumerated(EnumType.STRING)
    private Set<Role> roles = new HashSet<>();

    /**
     * Represents the seller's performance rating, ranging from 0 to 100.
     * Higher values indicate better reputation and reliability as a vendor.
     */
    @Column(name = "rating_seller")
    private Integer ratingSeller;

    /**
     * Represents the buyer's reliability rating, ranging from 0 to 100.
     * Used to assess the user's trustworthiness in transactions and payments.
     */
    @Column(name = "rating_buyer")
    private Integer ratingBuyer;

    /**
     * The shopping basket associated with the user for pending purchases.
     */
    @OneToOne(mappedBy = "buyer", cascade = CascadeType.ALL, orphanRemoval = true)
    private Basket basket;

    /**
     * Automatic timestamp indicating the moment of user registration.
     */
    @CreationTimestamp
    @Column(name = "created_at")
    private Instant createdAt;

    /**
     * A set of products cataloged or owned by the user.
     */
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Product> productSet = new HashSet<>();


    /**
     * A set of orders placed by the user acting as a Buyer..
     */
    @OneToMany(mappedBy = "buyer", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<BuyerOrder> buyerOrderSet = new HashSet<>();

    /**
     * A set of offers initiated by the user acting as a Seller
     */
    @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SellerOffer> sellerOfferSet = new HashSet<>();

    /**
     * Adds a product to the user's collection and establishes a bidirectional link
     * by setting this user as the product's author.
     *
     * @param product the product to be added.
     */
    public void addProduct(@NonNull Product product) {
        productSet.add(product);
        product.setAuthor(this);
    }

    /**
     * Removes a product from the user's collection and breaks the bidirectional link
     * by clearing the product's author.
     *
     * @param product the product to be removed.
     */
    public void removeProduct(@NonNull Product product) {
        productSet.remove(product);
        product.setAuthor(null);
    }

    /**
     * Assigns a new security role to the user.
     *
     * @param role the role to be granted.
     */
    public void addRole(@NonNull Role role) {
        roles.add(role);
    }

    /**
     * Revokes a specific security role from the user.
     *
     * @param role the role to be removed.
     */
    public void removeRole(@NonNull Role role) {
        roles.remove(role);
    }


    /**
     * Adds a seller's offer to the user's offer set and links this user
     * as the seller of the offer.
     *
     * @param sellerOffer the seller offer to be associated.
     */
    public void addSellerOffer(@NonNull SellerOffer sellerOffer) {
        sellerOfferSet.add(sellerOffer);
        sellerOffer.setSeller(this);
    }

    /**
     * Removes a seller's offer from the user's collection and dissociates
     * the user from the offer.
     *
     * @param sellerOffer the seller offer to be detached.
     */
    public void removeSellerOrder(@NonNull SellerOffer sellerOffer) {
        sellerOfferSet.remove(sellerOffer);
        sellerOffer.setSeller(null);
    }

    /**
     * Adds a buyer's order to the user's order collection and sets this user
     * as the buyer for the specified order.
     *
     * @param buyerOrder the order to be added.
     */
    public void addBuyerOrder(@NonNull BuyerOrder buyerOrder) {
        buyerOrderSet.add(buyerOrder);
        buyerOrder.setBuyer(this);
    }

    /**
     * Removes an order from the buyer's collection and clears the buyer
     * association for that order.
     *
     * @param buyerOrder the order to be removed.
     */
    public void removeBuyerOrder(@NonNull BuyerOrder buyerOrder) {
        buyerOrderSet.remove(buyerOrder);
        buyerOrder.setBuyer(null);
    }

    /**
     * Converts the user's roles into Spring Security granted authorities.
     * Each role is prefixed with "ROLE_".
     *
     * @return a collection of link GrantedAuthority
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(r -> new SimpleGrantedAuthority(r.name()))
                .toList();
    }

    /**
     * Checks whether the user account is enabled or disabled.
     * In this implementation, the account is considered disabled if the user
     * has been assigned the  Role BLOCKED status.
     *
     * @return true if the user is not blocked; false otherwise.
     */
    @Override
    public boolean isEnabled() {
        return !this.roles.contains(Role.BLOCKED);
    }

    /**
     * Retrieves the hashed password for the authentication process.
     *
     * @return the encoded password string.
     */
    @Override
    public String getPassword() {
        return password;
    }

    /**
     * Returns the identification string for authentication.
     *
     * @return the user's email address.
     */
    @Override
    public String getUsername() {
        return this.email;
    }
}
