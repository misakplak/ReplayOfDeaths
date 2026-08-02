package misakplak.deathLogging.recordables;


import misakplak.deathLogging.misc.SwingHand;

public record SwingArmRecord(
        long tick,
        SwingHand hand
) implements Recordable {

}
