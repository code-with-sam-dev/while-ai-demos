import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The same retry, with the one thing the request never mentioned.
 *
 * The difference is not more code. It is a key that makes the SECOND call the
 * same call as the first, so the provider can recognise it and return the
 * original result instead of performing the work again.
 */
public class RetryWithKey {

    static class Provider {
        private final Map<String, String> receiptsByKey = new LinkedHashMap<>();
        private final Map<String, String> rows = new LinkedHashMap<>();
        private int calls = 0;

        String charge(String idempotencyKey, String orderId, long amountCents)
                throws TimeoutException {
            calls++;
            String existing = receiptsByKey.get(idempotencyKey);
            if (existing != null) {
                System.out.println("  provider: key already seen, returning the original");
                return existing;
            }
            String receipt = "ch_" + calls;
            receiptsByKey.put(idempotencyKey, receipt);
            rows.put(receipt, orderId + " " + amountCents);
            if (calls == 1) throw new TimeoutException("read timed out after 2000ms");
            return receipt;
        }

        Map<String, String> charges() { return rows; }
    }

    static class TimeoutException extends Exception {
        TimeoutException(String m) { super(m); }
    }

    public static void main(String[] args) {
        Provider provider = new Provider();
        String orderId = "ord_8812";
        long amount = 4999;
        // Derived from the order, not from the attempt. That is the point.
        String key = "idem_" + orderId;

        String receipt = null;
        for (int attempt = 1; attempt <= 3 && receipt == null; attempt++) {
            try {
                receipt = provider.charge(key, orderId, amount);
                System.out.println("attempt " + attempt + ": ok, receipt " + receipt);
            } catch (TimeoutException e) {
                System.out.println("attempt " + attempt + ": " + e.getMessage() + ", retrying");
            }
        }

        System.out.println();
        System.out.println("charges recorded by the provider:");
        for (String c : provider.charges().values()) System.out.println("  " + c);
        System.out.println();
        System.out.println("rows: " + provider.charges().size());
        System.out.println("customer was charged: " + (provider.charges().size() * amount) + " cents");
    }
}
