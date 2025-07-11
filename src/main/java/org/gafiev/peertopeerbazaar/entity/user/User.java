package org.gafiev.peertopeerbazaar.entity.user;

import jakarta.persistence.*;
import lombok.*;
import org.gafiev.peertopeerbazaar.entity.order.Basket;
import org.gafiev.peertopeerbazaar.entity.order.BuyerOrder;
import org.gafiev.peertopeerbazaar.entity.order.SellerOffer;
import org.gafiev.peertopeerbazaar.entity.product.Product;

import java.util.HashSet;
import java.util.Set;

/**
 * Сущность User является пользователем приложения,
 * пользователь может быть продавцом(он же создатель продукта), покупателем
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"sellerOfferSet", "buyerOrderSet", "productSet", "basket"})
@ToString(exclude = {"sellerOfferSet", "buyerOrderSet",  "productSet", "basket","password"})
@Entity
@Table(name = "users")
public class User {
    /**
     * id является уникальным идентификатором пользователя
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * firstName есть имя пользователя
     */
    @Column(name = "first_name")
    private String firstName;

    /**
     * lastName есть фамилия пользователя
     */
    @Column(name = "last_name")
    private String lastName;

    /**
     * email электронная почта пользователя
     */
    @Column(name = "email", unique = true)
    private String email;

    /**
     * password есть пароль пользователя
     */
    @Column(name = "password")
    private String password;

    /**
     * phone номер телефона пользователя
     */
    @Column(name = "phone")
    private String phone;

    /**
     * roles указывает, что у пользователя может быть множество ролей.
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    private Set<Role> roles = new HashSet<>();

    /**
     * ratingSeller показывает рейтинг продавца от 0 до 100;
     */
    @Column(name = "rating_seller")
    private Integer ratingSeller;

    /**
     * ratingBuyer показывает рейтинг покупателя от 0 до 100;
     */
    @Column(name = "rating_buyer")
    private Integer ratingBuyer;

    /**
     * basket устанавливает связь с покупателем, который заполняет корзину
     */
    @OneToOne(mappedBy = "buyer", cascade = CascadeType.ALL, orphanRemoval = true)

    private Basket basket;

    /**
     * productSet является коллекцией дочерних сущностей, которая содержит внешний ключ (id) продавца (автора).
     * mappedBy = "author" указывает, что в классе Product есть поле author, которое является родительской сущностью.
     * User является управляющей (родительской) стороной, управляет жизненным циклом связанных сущностей Product в контексте каскадирования (персистентности).
     * Это означает, что при выполнении операций (например, PERSIST, MERGE, REMOVE) на User
     * все связанные Product также будут затронуты каскадно.
     * orphanRemoval = true позволяет автоматически управлять удалением дочерних сущностей Product из БД,
     * что означает, если продукт будет удален из коллекции productSet методом productSet.remove(product),
     * product также будет удален из базы данных. Это заслуга Hibernate.
     */
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Product> productSet = new HashSet<>();



    /**
     * buyerOrderSet представляет собой множество заказов, созданных покупателем
     * является дочерней сущностью и владеющей стороной, которая содержит внешний ключ (id) покупателя.
     * mappedBy = "buyer" указывает, что в классе BuyerOrder есть поле buyer, которое является родительской сущностью.
     * Это создает связь "один ко многим" (OneToMany) между User и BuyerOrder, где User порождает множество заказов.
     * Каскадирование указано на стороне User, что делает User управляющей (родительской) стороной,
     * которая управляет жизненным циклом связанных сущностей buyerOrderDrone в контексте каскадирования (персистентности).
     * Это означает, что при выполнении операций (например, PERSIST, MERGE, REMOVE) на User
     * все связанные buyerOrder также будут затронуты каскадно.
     * orphanRemoval = true позволяет автоматически управлять удалением дочерних сущностей BuyerOrder из БД,
     * что означает, если buyerOrder будет удален из коллекции buyerOrderSet методом buyerOrderSet.remove(buyerOrderDrone),
     * buyerOrderDrone также будет удален из базы данных. Это заслуга Hibernate.
     */
    @OneToMany(mappedBy = "buyer", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<BuyerOrder> buyerOrderSet = new HashSet<>();

    /**
     * sellerOfferSet представляет собой множество предложений, созданных продавцом.
     * SellerOffer является владеющей стороной, которая содержит внешний ключ (id) покупателя.
     * mappedBy = "seller" указывает, что в классе SellerOffer есть поле buyer, которое является родительской сущностью.
     * Это создает связь "один ко многим" (OneToMany) между User и SellerOffer, где User порождает множество заказов.
     * Каскадирование указано на стороне User, что делает User управляющей стороной,
     * которая управляет жизненным циклом связанных сущностей SellerOffer в контексте каскадирования (персистентности).
     * Это означает, что при выполнении операций (например, PERSIST, MERGE, REMOVE) на User
     * все связанные SellerOffer также будут затронуты каскадно.
     * orphanRemoval = true позволяет автоматически управлять удалением дочерних сущностей BuyerOrder из БД,
     * что означает, если sellerOffer будет удален из коллекции sellerOffer методом sellerOfferSet.remove(sellerOffer),
     * sellerOffer также будет удален из базы данных. Это заслуга Hibernate.
     */
    @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SellerOffer> sellerOfferSet = new HashSet<>();



    /**
     * метод добавляет указанный продукт в коллекцию продуктов
     * и устанавливает автора для данного продукта.
     *
     * @param product продукт, который нужно добавить
     */
    public void addProduct(@NonNull Product product) {
        productSet.add(product);
        product.setAuthor(this);
    }



    /**
     * Этот метод удаляет указанный продукт из коллекции продуктов
     * и сбрасывает автора для данного продукта.
     *
     * @param product продукт, который нужно удалить
     */
    public void removeProduct(@NonNull Product product) {
        productSet.remove(product);
        product.setAuthor(null);
    }

    public void addRole(@NonNull Role role){
        roles.add(role);
    }

    public void removeRole(@NonNull Role role){
        roles.remove(role);
    }


    /**
     * метод добавления предложение продавца в коллекцию sellerOfferSet
     *
     * @param sellerOffer предложение продавца, которое нужно добавить
     */
    public void addSellerOffer(@NonNull SellerOffer sellerOffer) {
        sellerOfferSet.add(sellerOffer);
        sellerOffer.setSeller(this);
    }

    /**
     * метод удаления предложения продавца из коллекции sellerOfferSet
     *
     * @param sellerOffer предложение, который нужно удалить
     */
    public void removeSellerOrder(@NonNull SellerOffer sellerOffer) {
        sellerOfferSet.remove(sellerOffer);
        sellerOffer.setSeller(null);
    }

    /**
     * метод добавляет указанный заказ в коллекцию заказов покупателя
     * и устанавливает покупателя для данного заказа.
     *
     * @param buyerOrder заказ, который нужно добавить
     */
    public void addBuyerOrder(@NonNull BuyerOrder buyerOrder) {
        buyerOrderSet.add(buyerOrder);
        buyerOrder.setBuyer(this);
    }

    /**
     * метод удаляет указанный заказ из коллекции заказов покупателя
     * и сбрасывает покупателя для данного заказа
     *
     * @param buyerOrder заказ, который нужно удалить
     */
    public void removeBuyerOrder(@NonNull BuyerOrder buyerOrder) {
        buyerOrderSet.remove(buyerOrder);
        buyerOrder.setBuyer(null);
    }

}
