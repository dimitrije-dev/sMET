package networking.packages;

import java.io.Serializable;

public class ConnectRequest implements Serializable {
    private final String username,password;

    public ConnectRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    private boolean validRequest = false;
    /**
     * Check if the request is valid.
     *
     * @return         	true if the request is valid, false otherwise
     */
    public final boolean isValidRequest(){
            return this.validRequest;
    }
    /**
     * Set the validity of the request.
     *
     * @param validRequest   the validity status of the request
     */
    public void setValidRequest(boolean validRequest){
        this.validRequest = validRequest;
    }
    /**
     * Returns the username.
     */
    public String getUsername() {
        return username;
    }
    /**
     * Retrieves the password.
     *
     * @return         	the password
     */
    public String getPassword() {
        return password;
    }
}
