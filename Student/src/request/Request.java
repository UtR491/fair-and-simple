package request;

import entity.Main;

import java.io.Serializable;

public abstract class Request implements Serializable {
    private String sender = Main.userRegistrationNumber;
}
