package networking.packages;

import java.io.Serializable;

public class TestPacket implements Serializable {
    public int someValue;

    public TestPacket() {
    }

    public TestPacket(int someValue) {
        this.someValue = someValue;
    }
}
