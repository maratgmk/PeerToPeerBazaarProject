package org.gafiev.peertopeerbazaar.entity.user;

import jakarta.persistence.*;
import lombok.*;
import org.gafiev.peertopeerbazaar.entity.order.Basket;
import org.gafiev.peertopeerbazaar.entity.order.BuyerOrder;
import org.gafiev.peertopeerbazaar.entity.order.SellerOffer;
import org.gafiev.peertopeerbazaar.entity.payment.PaymentAccount;
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
@EqualsAndHashCode(exclude = {"sellerOfferSet","buyerOrderSet","paymentAccountSet","productSet"})
@ToString(exclude = {"sellerOfferSet","buyerOrderSet","paymentAccountSet","productSet"})
@Entity
@Table(name="users")
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
    @Column(name="first_name")
    private String firstName;

    /**
     * lastName есть фамилия пользователя
     */
    @Column(name="last_name")
    private String lastName;

    /**
     * email электронная почта пользователя
     */
    @Column(name = "email",unique = true)
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
     * role указывает как приложение отождествляет пользователя
     */
    @Enumerated(EnumType.STRING)
    private Role role;

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
     * Это поле является коллекцией дочерних сущностей, которая содержит внешний ключ (id) продавца (автора).
     * Параметр mappedBy = "author" указывает, что в классе Product есть поле author, которое является родительской сущностью.
     * Это создает связь "один ко многим" (OneToMany) между User и Product, где User создаёт множество продуктов.
     * User является управляющей стороной, управляет жизненным циклом связанных сущностей Product в контексте каскадирования (персистентности).
     * Это означает, что при выполнении операций (например, PERSIST, MERGE, REMOVE) на User
     * все связанные Product также будут затронуты каскадно.
     * Параметр orphanRemoval = true позволяет автоматически управлять удалением дочерних сущностей Product,
     * которые больше не связаны с родительской сущностью User. Это означает, что если продукт будет удален из коллекции,
     * он также будет удален из базы данных.
     */
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Product> productSet = new HashSet<>();

    /**
     * Поле sellerSellerOffers представляет собой множество заказов, созданных продавцом.
     * Это поле является дочерней сущностью и владеющей стороной, которая содержит внешний ключ (id) продавца.
     * Параметр mappedBy = "seller" указывает, что в классе SellerOffer есть поле seller, которое является родительской сущностью.
     * Это создает связь "один ко многим" (OneToMany) между User и SellerOffer, где User порождает множество заказов.
     * Каскадирование указано на стороне User, что делает User управляющей стороной,
     * которая управляет жизненным циклом связанных сущностей sellerSellerOffers в контексте каскадирования (персистентности).
     * Это означает, что при выполнении операций (например, PERSIST, MERGE, REMOVE) на User
     * все связанные SellerOffer также будут затронуты каскадно.
     * Параметр orphanRemoval = true позволяет автоматически управлять удалением дочерних сущностей SellerOffer,
     * которые больше не связаны с родительской сущностью User. Это означает, что если заказ будет удален из коллекции,
     * он также будет удален из базы данных.
     */
    @OneToMany(mappedBy = "buyer", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<BuyerOrder> buyerOrderSet = new HashSet<>();

    /**
     * Поле buyerSellerOffers представляет собой множество заказов, созданных покупателем.
     * Это поле является дочерней сущностью и владеющей стороной, которая содержит внешний ключ (id) покупателя.
     * Параметр mappedBy = "buyer" указывает, что в классе SellerOffer есть поле buyer, которое является родительской сущностью.
     * Это создает связь "один ко многим" (OneToMany) между User и SellerOffer, где User порождает множество заказов.
     * Каскадирование указано на стороне User, что делает User управляющей стороной,
     * которая управляет жизненным циклом связанных сущностей buyerSellerOffers в контексте каскадирования (персистентности).
     * Это означает, что при выполнении операций (например, PERSIST, MERGE, REMOVE) на User
     * все связанные SellerOffer также будут затронуты каскадно.
     * Параметр orphanRemoval = true позволяет автоматически управлять удалением дочерних сущностей SellerOffer,
     * которые больше не связаны с родительской сущностью User. Это означает, что если заказ будет удален из коллекции,
     * он также будет удален из базы данных.
     */
    @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SellerOffer> sellerOfferSet = new HashSet<>();

    /**
     * Поле paymentAccounts является множеством платежных счетов пользователя
     * и является владеющей стороной, владеет ключом пользователя,
     * атрибут mappedBy = "user" означает, что в классе PaymentAccount есть поле "user",
     * атрибут CascadeType.ALL относится к paymentAccounts и означает, что
     * при всех изменениях в сущности пользователя, каскадом изменятся пользователи в
     * таблице "payment_account", атрибут orphanRemoval = true означает,
     * что при удалении пользователя на обратной стороне,
     * из БД автоматически удалится владеющая сторона, т.е. платежные аккаунты данного пользователя
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PaymentAccount> paymentAccountSet = new HashSet<>();


    /**
     * Добавляет продукт в коллекцию продуктов автора.
     * <p>
     * Этот метод добавляет указанный продукт в коллекцию продуктов
     * и устанавливает автора для данного продукта.
     * </p>
     * @param product продукт, который нужно добавить
     */
    public void addProduct(@NonNull Product product){
        this.productSet.add(product);
        product.setAuthor(this);
    }

    /**
     * Удаляет продукт из коллекции продуктов автора.
     * <p>
     * Этот метод удаляет указанный продукт из коллекции продуктов
     * и сбрасывает автора для данного продукта.
     * </p>
     * @param product продукт, который нужно удалить
     */
    public void removeProduct(@NonNull Product product){
        this.productSet.remove(product);
        product.setAuthor(null);
    }

    /**
     * Добавляет платежный аккаунт в коллекцию аккаунтов пользователя.
     *
     * @param paymentAccount платежный аккаунт, который нужно добавить
     */
    public void addPaymentAccount(@NonNull PaymentAccount paymentAccount){
        this.paymentAccountSet.add(paymentAccount);
        paymentAccount.setUser(this);
    }

    /**
     * Удаляет платежный аккаунт из коллекции аккаунтов пользователя.
     *
     * @param paymentAccount платежный аккаунт, который нужно удалить
     */
    public void removePaymentAccount(@NonNull PaymentAccount paymentAccount){
        this.paymentAccountSet.remove(paymentAccount);
        paymentAccount.setUser(null);
    }

    /**
     * метод добавления предложение продавца в коллекцию предложений продавца
     *
     * @param sellerOffer предложение продавца, которое нужно добавить
     */
    public void addSellerOffer(@NonNull SellerOffer sellerOffer){
        sellerOfferSet.add(sellerOffer);
        sellerOffer.setSeller(this);
    }

    /**
     * метод удаления предложения продавца из коллекции предложений продавца
     *
     * @param sellerOffer заказ, который нужно удалить; не должен быть <code>null</code>
     */
    public void removeSellerOrder(@NonNull SellerOffer sellerOffer){
        sellerOfferSet.remove(sellerOffer);
        sellerOffer.setSeller(null);
    }

    /**
     * Добавляет заказ в коллекцию заказов покупателя.
     *
     * <p>
     * Этот метод добавляет указанный заказ в коллекцию
     * и устанавливает покупателя для данного заказа.
     * </p>
     *
     * @param buyerOrder заказ, который нужно добавить; не должен быть <code>null</code>
     */
    public void addBuyerOrder(@NonNull BuyerOrder buyerOrder){
        buyerOrderSet.add(buyerOrder);
        buyerOrder.setBuyer(this);
    }

    /**
     * Удаляет заказ из коллекции заказов покупателя.
     *
     * <p>
     * Этот метод удаляет указанный заказ из коллекции
     * и сбрасывает покупателя для данного заказа на <code>null</code>.
     * </p>
     *
     * @param buyerOrder заказ, который нужно удалить; не должен быть <code>null</code>
     */
    public void removeBuyerOrder(@NonNull BuyerOrder buyerOrder){
        buyerOrderSet.remove(buyerOrder);
        buyerOrder.setBuyer(null);
    }

}
