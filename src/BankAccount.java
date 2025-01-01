import java.math.BigDecimal;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class BankAccount {
    private final int accountId;
    private BigDecimal balance;
    private final ReentrantLock lock = new ReentrantLock(true);
    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();

    public BankAccount(int accountId, BigDecimal initialBalance){
        this.accountId = accountId;
        this.balance = initialBalance;
    }

    public int getAccountId(){
        return accountId;
    }

    public ReentrantLock getLock(){
        return lock;
    }

    public BigDecimal getBalance(){
        rwLock.readLock().lock();
        try{
            return balance;
        }finally {
            rwLock.readLock().unlock();
        }
    }

    public void deposit(BigDecimal amount){
        rwLock.writeLock().lock();
        try {
           this.balance = this.balance.add(amount);
        }finally {
            rwLock.writeLock().unlock();
        }
    }

    public void withdraw(BigDecimal amount){
        rwLock.writeLock().lock();
        try{
            this.balance = this.balance.subtract(amount);
        }finally {
            rwLock.writeLock().unlock();
        }
    }
}
