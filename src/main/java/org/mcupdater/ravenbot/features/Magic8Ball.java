package org.mcupdater.ravenbot.features;

import org.pircbotx.Colors;

public enum Magic8Ball {
    YES1(Colors.GREEN + " Signs point to yes."),
    YES2(Colors.GREEN + " Yes."),
    MAYBE1(Colors.YELLOW + " Reply hazy, try again."),
    YES3(Colors.GREEN + " Without a doubt."),
    NO1(Colors.RED + " My sources say no."),
    YES4(Colors.GREEN + " As I see it, yes."),
    YES5(Colors.GREEN + " You may rely on it."),
    MAYBE2(Colors.YELLOW + " Concentrate and ask again."),
    NO2(Colors.RED + " Outlook not so good."),
    YES6(Colors.GREEN + " It is decidedly so."),
    MAYBE3(Colors.YELLOW + " Better not tell you now."),
    NO3(Colors.RED + " Very doubtful."),
    YES7(Colors.GREEN + " Yes - definitely."),
    YES8(Colors.GREEN + " It is certain."),
    MAYBE4(Colors.YELLOW + " Cannot predict now."),
    YES9(Colors.GREEN + " Most likely."),
    MAYBE5(Colors.YELLOW + " Ask again later."),
    NO4(Colors.RED + " My reply is no."),
    YES10(Colors.GREEN + " Outlook good."),
    NO5(Colors.RED + " Do not count on it.");

    private final String message;

    Magic8Ball(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
