package exception;

import java.io.IOException;

public class ManagerSaveException extends RuntimeException {
    public ManagerSaveException(String cause) {
        super(cause);
    }



}
/*public class ManagerSaveException extends RuntimeException {
    public ManagerSaveException(String message, Throwable cause) {
        super(message, cause);
    }

    public ManagerSaveException(String u, void l) {
    }

    /*public String getDetailMessage() {
        return "Ошибка ввода: " + getMessage();
    }
}*/