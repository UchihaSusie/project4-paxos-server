package server;


/**
 * The Response class encapsulates the details of a server's response.
 * It contains information about the operation performed, the status of the operation,
 * and a description providing additional details.
 */
public class Response {
    private String operation;
    private String status;
    private String description;

    public Response(String operation, String status, String description) {
        this.operation = operation;
        this.status = status;
        this.description = description;
    }

    @Override
    public String toString() {
        return operation +" "+status +" "+description;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


}
