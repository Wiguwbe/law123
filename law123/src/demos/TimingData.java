package demos;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

/**
 * Represents all the information that the demo might need about the
 * timing of the game: current time, fps, frame number, and so on.
 */
class TimingData {

    private static final TimingData timingData = new TimingData();

    private TimingData() {
    }

    // Hold internal timing data for the performance counter.
    private static boolean qpcFlag;
    private static long qpcFrequency = System.nanoTime();

    /** The current render frame. This simply increments. */
    private int frameNumber;

    /**
     * The timestamp when the last frame ended. Times are
     * given in milliseconds since some undefined time.
     */
    private long lastFrameTimestamp;

    /**
     * The duration of the last frame in milliseconds.
     */
    private long lastFrameDuration;

    /**
     * The clockstamp of the end of the last frame.
     */
    private long lastFrameClockstamp;

    /**
     * The duration of the last frame in clock ticks.
     */
    private long lastFrameClockTicks;

    /**
     * Keeps track of whether the rendering is paused.
     */
    private boolean isPaused;

    // Calculated data

    /**
     * This is a recency weighted average of the frame time, calculated
     * from frame durations.
     */
    private double averageFrameDuration;

    /**
     * The reciprocal of the average frame duration giving the mean
     * fps over a recency weighted average.
     */
    private float fps;

    /**
     * Gets the global timing data object.
     */
    public static TimingData get() {
        return timingData;
    }

    public long getLastFrameDuration() {
        return lastFrameDuration;
    }

    /**
     * Updates the timing system, should be called once per frame.
     */
    public static void update()// Updates the global frame information. Should be called once per frame.
    {
        // Advance the frame number.
        if (!timingData.isPaused) {
            timingData.frameNumber++;
        }

        // Update the timing information.
        long thisTime = getTime();
        timingData.lastFrameDuration = thisTime - timingData.lastFrameTimestamp;
        timingData.lastFrameTimestamp = thisTime;

        // Update the tick information.
        long thisClock = getClock();
        timingData.lastFrameClockTicks = thisClock - timingData.lastFrameClockstamp;
        timingData.lastFrameClockstamp = thisClock;

        // Update the RWA frame rate if we are able to.
        if (timingData.frameNumber > 1) {
            if (timingData.averageFrameDuration <= 0) {
                timingData.averageFrameDuration = timingData.lastFrameDuration;
            } else {
                // RWA over 100 frames.
                timingData.averageFrameDuration *= 0.99;
                timingData.averageFrameDuration += 0.01 * timingData.lastFrameDuration;

                // Invert to get FPS
                timingData.fps = (float) (1000.0 / timingData.averageFrameDuration);
            }
        }
    }

    /**
     * Initialises the frame information system. Use the overall
     * init function to set up all modules.
     */
    public static void init() {
        // Set up the timing system.
        initTime();

        // Set up the frame info structure.
        timingData.frameNumber = 0;

        timingData.lastFrameTimestamp = getTime();
        timingData.lastFrameDuration = 0;

        timingData.lastFrameClockstamp = getClock();
        timingData.lastFrameClockTicks = 0;

        timingData.isPaused = false;

        timingData.averageFrameDuration = 0;
        timingData.fps = 0;
    }

    // Sets up the timing system and registers the performance timer.
    private static void initTime() {

        qpcFlag = qpcFrequency > 0;

        // Check if we have access to the performance counter at this
        // resolution.
        if (qpcFlag) qpcFrequency = (long) (1000.0 / getTime());
    }

    /**
     * Gets the global system time, in the best resolution possible.
     * Timing is in milliseconds.
     */
    public static long getTime() {
        return System.currentTimeMillis();
    }

    // Internal time and clock access functions
    private static long getClock() {
        ThreadMXBean bean = ManagementFactory.getThreadMXBean();
        return bean.isCurrentThreadCpuTimeSupported() ? (bean.getCurrentThreadCpuTime() - bean.getCurrentThreadUserTime()) : 0L;
    }

}
