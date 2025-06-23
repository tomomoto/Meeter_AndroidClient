package com.tom.meeter.context.profile.subscriber;

import com.tom.meeter.context.network.dto.UserDTO;

public class Subscriber {

    private UserDTO user;
    private boolean amISubscribedTo;

    public Subscriber(UserDTO user, boolean amISubscribedTo) {
        this.user = user;
        this.amISubscribedTo = amISubscribedTo;
    }

    public UserDTO getUser() {
        return user;
    }

    public boolean isAmISubscribedTo() {
        return amISubscribedTo;
    }

    public void setAmISubscribedTo(boolean amISubscribedTo) {
        this.amISubscribedTo = amISubscribedTo;
    }
}
