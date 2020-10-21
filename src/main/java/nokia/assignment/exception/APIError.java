package nokia.assignment.exception;

import java.util.Calendar;

public class APIError {

    private String status;
    private Calendar timeStamp;
    private String message;

    public APIError() {
        this.timeStamp = Calendar.getInstance();
    }

    public APIError(String status) {
        this.status = status;
        this.timeStamp = Calendar.getInstance();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Calendar getTimeStamp() {
        return timeStamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
