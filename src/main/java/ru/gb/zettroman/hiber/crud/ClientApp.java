package ru.gb.zettroman.hiber.crud;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import ru.gb.zettroman.hiber.entities.Customer;
import ru.gb.zettroman.hiber.entities.Purchase;
import ru.gb.zettroman.hiber.entities.Product;

import java.util.List;
import java.util.Random;

public class ClientApp {
    public static void main(String[] args) {

        try (SessionFactory factory = new Configuration()
                .configure("configs/hibernate.cfg.xml")
                .buildSessionFactory()) {
            init(factory);  // initialize database. Database should be empty!
            Random random = new Random();
            // making some purchases
            for (int i = 0; i < 20; i++) {
                Long customerId = (long) (random.nextInt(10) + 1);
                Long productId = (long) (random.nextInt(10) + 1);
                int quantity = random.nextInt(5) + 1;
                makePurchase(factory, customerId, productId, quantity);
            }
            // listing current purchases in database for every customer
            for (long i = 1L; i <= 10L; i++) {
                viewCustomerPurchases(factory, i);
            }

            // let's remove some products form DB
            for (long i = 1L; i < 10L; i++) {
                removeProduct(factory, i);
            }

            // let's remove some customers
            removeCustomer(factory, 3L);
            removeCustomer(factory, 5L);
            removeCustomer(factory, 8L);

            // List purchases in DB again. All purchases of deleted customers
            // should be eliminated
            for (long i = 1L; i <= 10L; i++) {
                viewCustomerPurchases(factory, i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void removeCustomer(SessionFactory factory, Long customerId) {
        try (Session session = factory.getCurrentSession()) {
            session.beginTransaction();
            session.delete(session.get(Customer.class, customerId));
            session.getTransaction().commit();
        } catch (Exception e) {
            System.out.println("Error occurred while removing customer with Id = " + customerId);
        }
    }

    private static void removeProduct(SessionFactory factory, Long productId) {
        try (Session session = factory.getCurrentSession()) {
            session.beginTransaction();
            session.delete(session.get(Product.class, productId));
            session.getTransaction().commit();
        } catch (Exception e) {
            System.out.println("Error occurred while removing product with Id = " + productId);
        }
    }

    private static void viewCustomerPurchases(SessionFactory factory, Long customerId) {
        System.out.println("---------------------------------------");
        try (Session session = factory.getCurrentSession()) {
            session.beginTransaction();
            Customer customer = session.get(Customer.class, customerId);
            List<Purchase> purchases = customer.getPurchases();
            if (purchases.isEmpty()) {
                System.out.println("Customer " + customer.getName() + " hasn't made any purchase yet.");
            } else {
                System.out.println("Products bought by " + customer.getName() + " are:");
                for (Purchase purchase : purchases) {
                    System.out.println(purchase);
                }
            }
            session.getTransaction().commit();
        } catch (Exception e) {
            System.out.println("Customer with Id = " + customerId + " doesn't exist or error occurred while retrieving customer info");
        }
    }

    private static void makePurchase(SessionFactory factory, Long customerId, Long productId, int quantity) {
        try (Session session = factory.getCurrentSession()) {
            session.beginTransaction();
            Customer customer = session.get(Customer.class, customerId);
            Product product = session.get(Product.class, productId);
            Purchase purchase = new Purchase(customer, product, quantity);
            session.saveOrUpdate(purchase);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void init(SessionFactory factory) {
        try (Session session = factory.getCurrentSession()) {
            session.beginTransaction();
            session.saveOrUpdate(new Customer("Bruce Willis"));
            session.saveOrUpdate(new Customer("Tom Cruise"));
            session.saveOrUpdate(new Customer("Benedict Cumberbatch"));
            session.saveOrUpdate(new Customer("Daniel Radcliffe"));
            session.saveOrUpdate(new Customer("Christian Bale"));
            session.saveOrUpdate(new Customer("Tim Roth"));
            session.saveOrUpdate(new Customer("Tom Hardy"));
            session.saveOrUpdate(new Customer("Sean Penn"));
            session.saveOrUpdate(new Customer("Edward Norton"));
            session.saveOrUpdate(new Customer("Elijah Wood"));
            session.saveOrUpdate(new Product("Milk", 45));
            session.saveOrUpdate(new Product("Bread", 30));
            session.saveOrUpdate(new Product("Butter", 90));
            session.saveOrUpdate(new Product("Ham", 145));
            session.saveOrUpdate(new Product("Cheese", 160));
            session.saveOrUpdate(new Product("Juice", 50));
            session.saveOrUpdate(new Product("Salt", 15));
            session.saveOrUpdate(new Product("Sugar", 60));
            session.saveOrUpdate(new Product("Potatoes", 75));
            session.saveOrUpdate(new Product("Onion", 35));
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
