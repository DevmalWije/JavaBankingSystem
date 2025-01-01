import javax.swing.*;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        TransactionSystem system = new TransactionSystem();
        List<Thread> threads = new ArrayList<>();

        //Define bank accounts.
        BankAccount account1 = new BankAccount(1,new BigDecimal(1000));
        BankAccount account2 = new BankAccount(2,new BigDecimal(5000));
        BankAccount account3 = new BankAccount(3,new BigDecimal(50000));

        //Simulating transactions between accounts
        Thread thread1 = new Thread(() -> system.transfer(account1, account2, new BigDecimal(200)));
        Thread thread2 = new Thread(() -> system.transfer(account2,account3,new BigDecimal(500)));
        Thread thread3 = new Thread(() -> system.transfer(account3,account1, new BigDecimal(20000)));
        Thread thread4 = new Thread(() -> {
            try {
                Thread.sleep(100);
                System.out.println("Account 1 Balance: " + account1.getBalance());
                System.out.println("Account 2 Balance: " + account2.getBalance());
                System.out.println("Account 3 Balance: " + account3.getBalance());
                System.out.println("------------------------");
            }catch (InterruptedException e){
                System.err.println("Interrupted: " + e.getMessage());
            }
        });
        //testing rollbacks
        Thread thread5 = new Thread(() -> system.transfer(account1,account2, new BigDecimal(20000)));
        Thread thread6 = new Thread(() -> system.transfer(account3,account2, new BigDecimal(200000)));
        //testing deadlock and rollback handling
        Thread thread7 = new Thread(() -> system.transfer(account1,account2, new BigDecimal(200)));
        Thread thread8 = new Thread(() -> system.transfer(account2,account1, new BigDecimal(2000)));

        threads.add(thread1);
        threads.add(thread2);
        threads.add(thread3);
        threads.add(thread4);
        threads.add(thread5);
        threads.add(thread6);
        threads.add(thread7);
        threads.add(thread8);

        //start threads
        for (Thread thread: threads){
            thread.start();
            System.out.println("Thread: "+thread.getName()+" started");
            System.out.println("------------------------");
        }

        //waiting for threads to finish
        try{
            for(Thread thread: threads){
                thread.join();
            }
        }catch (InterruptedException e){
            System.out.println("Thread interrupted: "+e.getMessage());
        }

        //printing transaction log
        System.out.println("Transaction Log: ");
        system.getTransactionLog().forEach(System.out::println);
    }
}