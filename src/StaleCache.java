import java.util.HashMap;
import java.util.Map;

/**
 * The cache the agent added, exactly as asked for, with no invalidation story.
 *
 * "Add a cache" is one of the most reasonable sounding instructions in
 * software, and it is one of the few that can change what a customer BELIEVES
 * is true about their own money. The read path gets faster. The refund becomes
 * invisible.
 */
public class StaleCache {

    static class Database {
        private final Map<String, String> orders = new HashMap<>();
        Database() { orders.put("ord_8812", "PAID"); }
        String status(String id) { return orders.get(id); }
        void refund(String id) { orders.put(id, "REFUNDED"); }
    }

    static class OrderService {
        private final Database db;
        private final Map<String, String> cache = new HashMap<>();
        private int dbReads = 0;

        OrderService(Database db) { this.db = db; }

        String status(String id) {
            String hit = cache.get(id);
            if (hit != null) return hit;
            dbReads++;
            String fresh = db.status(id);
            cache.put(id, fresh);
            return fresh;
        }

        int dbReads() { return dbReads; }
    }

    public static void main(String[] args) {
        Database db = new Database();
        OrderService service = new OrderService(db);

        System.out.println("customer opens the order : " + service.status("ord_8812"));
        System.out.println("support issues a refund");
        db.refund("ord_8812");

        System.out.println();
        System.out.println("what the database says    : " + db.status("ord_8812"));
        System.out.println("what the customer sees    : " + service.status("ord_8812"));
        System.out.println();
        System.out.println("database reads            : " + service.dbReads());
        System.out.println("the cache did its job. that is the problem.");
    }
}
