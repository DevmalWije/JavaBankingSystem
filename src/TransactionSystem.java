import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;

public class TransactionSystem {
    private final List<String> transactionLog = new ArrayList<>();

    public boolean transfer(BankAccount from, BankAccount to, BigDecimal amount) {
        ReadWriteLock firstLock = from.getAccountId() < to.getAccountId() ? from.getRwLock() : to.getRwLock();
        ReadWriteLock secondLock = from.getAccountId() > to.getAccountId() ? from.getRwLock() : to.getRwLock();

        firstLock.writeLock().lock();
        secondLock.writeLock().lock();
        try {
            if (from.getBalance().doubleValue() < amount.doubleValue()) {
                transactionLog.add("Transfer from account:" + from.getAccountId() + " to: " + to.getAccountId() + " failed due to insufficient funds \n------------------------------------------------");
                return false;
            }
            from.withdraw(amount);
            to.deposit(amount);
            transactionLog.add("Transferred Rs." + amount + " from Account: " + from.getAccountId() + " to account: " + to.getAccountId() + "\n------------------------------------------------");
            return true;
        } catch (Exception e) {
            rollback(from, to, amount);
            return false;
        } finally {
            secondLock.writeLock().unlock();
            firstLock.writeLock().unlock();
        }
    }

    private void rollback(BankAccount from, BankAccount to, BigDecimal amount) {
        transactionLog.add("Rollback initiated for transfer of Rs. " + amount + " between: " + from.getAccountId() + " and: " + to.getAccountId() + "\n------------------------------------------------");
        to.withdraw(amount);
        from.deposit(amount);
    }

    public List<String> getTransactionLog() {
        return transactionLog;
    }
}