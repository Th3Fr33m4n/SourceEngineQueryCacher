package com.aayushatharva.sourcecenginequerycacher.server;

import com.aayushatharva.sourcecenginequerycacher.config.Config;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;


public final class Stats extends Thread {

    private static final Logger logger = LogManager.getLogger(Stats.class);
    private static final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
    private boolean keepRunning = true;

    private static final AtomicLong BPS = new AtomicLong();
    private static final AtomicLong PPS = new AtomicLong();

    public static void incrementPPS() {
        Stats.PPS.incrementAndGet();
    }

    public static void incrementBPS(int byteCount) {
        Stats.BPS.addAndGet(byteCount);
    }

    @SuppressWarnings("BusyWait")
    @Override
    public void run() {

        logger.info("Starting Stats, PPS Enabled: " + Config.ppaStats + ", bPS Enabled: " + Config.bpsStats);

        while (keepRunning) {

            if (Config.ppaStats && Config.bpsStats) {
                System.out.print("[" + getTimestamp() + "] [STATS] p/s: " + PPS.getAndSet(0L));
                System.out.println(" | b/s: " + calculateBps());
            } else if (Config.ppaStats) {
                System.out.println("[" + getTimestamp() + "] [STATS] p/s: " + PPS.getAndSet(0L) + " | b/s: 0");
            } else if (Config.bpsStats) {
                System.out.println("[" + getTimestamp() + "] [STATS] p/s: 0 | b/s: " + calculateBps());
            }

            try {
                sleep(1000L);
            } catch (InterruptedException e) {
                logger.error("Error at Stats During Interval Sleep", e);
                return;
            }

            // If false then we're requested to shutdown.
            if (!keepRunning) {
                return;
            }
        }
    }

    @SuppressWarnings("BigDecimalMethodWithoutRoundingCalled")
    private String calculateBps() {
        var bits = new BigDecimal(BPS.getAndSet(0L));
        bits = bits.divide(new BigDecimal("8"));
        return bits.toString();
    }

    private String getTimestamp() {
        return sdf.format(new Date());
    }

    public void shutdown() {
        interrupt();
        keepRunning = false;
    }
}
