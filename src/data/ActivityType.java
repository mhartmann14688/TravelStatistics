package data;

/**
 * Created by Melanie Hartmann (msg systems ag) 20.11.2016.
 */
public enum ActivityType {
    IDLE, WALKING, BIKING, DRIVING;

    public static ActivityType getActivity(Float speed) {
        Config config = Config._getInstance();
        if (speed < config.getMaxSpeedIdle())
            return IDLE;
        else if (speed < config.getMaxSpeedWalking())
            return WALKING;
        else if (speed < config.getMaxSpeedBiking())
            return BIKING;
        else
            return DRIVING;
    }
}
