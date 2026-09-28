import java.util.ArrayList;
import java.util.List;

/**
 * The retry the agent added, exactly as asked for, with no idempotency key.
 *
 * This is not a strawman. "Add retries" is a completely reasonable instruction,
 * the code below is what a competent implementation of that instruction looks
 * like, and every line of it does what was asked. The defect is not in the
 * code. It is in the request, and nobody checked the request.
 */
public class RetryNoKey {

    /** Stands in for the payment provider: times out once, then succeeds. */
    static class Provider {
        private final List<String> charges = new ArrayList<>();
        private int calls = 0;

        String charge(String orderId, long amountCents) throws TimeoutException {
            calls++;
            // The charge is RECORDED before the response is sent. This is the
            // whole problem: the timeout is on the way back, not on the way in.
            charges.add(orderId + " " + amountCents);
            if (calls == 1) throw new TimeoutException("read timed out after 2000ms");
            return "ch_" + calls;
        }

        List<String> charges() { return charges; }
    }

    static class TimeoutException extends Exception {
        TimeoutException(String m) { super(m); }
    }

    public static void main(String[] args) {
        Provider provider = new Provider();
        String orderId = "ord_8812";
        long amount = 4999;

        String receipt = null;
        for (int attempt = 1; attempt <= 3 && receipt == null; attempt++) {
            try {
                receipt = provider.charge(orderId, amount);
                System.out.println("attempt " + attempt + ": ok, receipt " + receipt);
            } catch (TimeoutException e) {
                System.out.println("attempt " + attempt + ": " + e.getMessage() + ", retrying");
            }
        }

        System.out.println();
        System.out.println("charges recorded by the provider:");
        for (String c : provider.charges()) System.out.println("  " + c);
        System.out.println();
        System.out.println("rows: " + provider.charges().size());
        System.out.println("customer was charged: " + (provider.charges().size() * amount) + " cents");
    }
}
