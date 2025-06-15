package server;

import java.util.HashMap;
import java.util.Map;

/**
 * The KeyValue class provides a simple in-memory key-value store
 * for managing key-value pairs with PUT, GET, and DELETE operations.
 */
public class KeyValue {
    private Map<String, String> store;

    /**
     * Constructor to initialize the key-value store.
     * The store is pre-populated with keys "1" to "5" and their values set to "1".
     */
    public KeyValue() {
        store = new HashMap<>();
        store.put("1", "1");
        store.put("2", "1");
        store.put("3", "1");
        store.put("4", "1");
        store.put("5", "1");
    }

    /**
     * Handles the PUT operation to add or update a key-value pair in the store.
     *
     * @param key The key to add or update.
     * @param value The value to associate with the key.
     * @return A Response object indicating the success of the operation,
     *         and whether it was an update or a new insertion.
     */
    public Response put(String key, String value) {
        if (store.containsKey(key)) {
            store.put(key, value);
            return new Response("PUT","SUCCESS",
                    "key "+ key +" is exist, update value to " + value);
        }else{
            store.put(key, value);
            return new Response("PUT","SUCCESS",
                    "PUT operation successful for key " + key +" with value " + value);
        }
    }

    /**
     * Handles the GET operation to retrieve the value associated with a key.
     *
     * @param key The key to retrieve.
     * @return A Response object with the value if the key exists, or an error if not.
     */
    public Response get(String key) {
        if (store.containsKey(key)) {
            return new Response("GET","SUCCESS", store.get(key));
        } else {
            return new Response("GET","FAIL",
                    "GET operation failed. Key " + key + " not found.");
        }
    }

    /**
     * Handles the DELETE operation to remove a key-value pair from the store.
     *
     * @param key The key to delete.
     * @return A Response object indicating the success of the deletion
     *         or an error if the key does not exist.
     */
    public Response delete(String key) {
        if (store.containsKey(key)) {
            store.remove(key);
            return new Response("DELETE","SUCCESS",
                    "DELETE operation successful for key: " + key);
        } else {
            return new Response("DELETE","FAIL",
                    "DELETE operation failed. Key " + key + " not found.");
        }
    }

}
