import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class TransactionSystem {
    private final List<String> transactionLog = new ArrayList<>();

    public boolean transfer(BankAccount from, BankAccount to, BigDecimal amount){
        BankAccount first = from.getAccountId() < to.getAccountId() ? from : to;
        BankAccount second = from.getAccountId() > to.getAccountId() ? from : to;
        first.getLock().lock();
        second.getLock().lock();
        try{
            if(from.getBalance().doubleValue() < amount.doubleValue()){
                transactionLog.add("Transfer from account:"+ from.getAccountId()+" to: "+to.getAccountId()+" failed due to insufficient funds");
                return  false;
            }
            from.withdraw(amount);
            to.deposit(amount);
            transactionLog.add("Transferred Rs."+amount+" from Account: "+from.getAccountId()+" to account: "+to.getAccountId());
            return true;
         }catch (Exception e){
            rollback(from, to, amount);
            return false;
        }finally {
            second.getLock().unlock();
            first.getLock().unlock();
        }

    }

    private void rollback(BankAccount from, BankAccount to, BigDecimal amount){
        transactionLog.add("Rollback initiated for transfer of Rs. "+amount+" between: "+from.getAccountId()+" and: "+to.getAccountId());
    to.withdraw(amount);
    from.deposit(amount);
    }

    public List<String> getTransactionLog(){
        return transactionLog;
    }

}
