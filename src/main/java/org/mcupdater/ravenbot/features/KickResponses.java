package org.mcupdater.ravenbot.features;

public enum KickResponses {
    a("Meh...  I didn't like them anyway. :P"),
    b("That'll teach them!"),
    c("... and don't come back!"),
    d("It's about time!"),
    e("Somebody's in trouble!");

    private String message;

    KickResponses(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
