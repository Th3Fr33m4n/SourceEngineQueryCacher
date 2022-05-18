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
    private boolean keepRunning = true;

    public static final AtomicLong BPS = new AtomicLong();
    public static final AtomicLong PPS = new AtomicLong();

    @SuppressWarnings("BusyWait")
    @Override
    public void run() {

        logger.info("Starting Stats, PPS Enabled: " + Config.stats_PPS + ", bPS Enabled: " + Config.stats_bPS);

        while (keepRunning) {

            if (Config.stats_PPS && Config.stats_bPS) {
                System.out.print("[" + getTimestamp() + "] [STATS] p/s: " + PPS.getAndSet(0L));
                System.out.println(" | b/s: " + calculateBps());
            } else {
                if (Config.stats_PPS) {
                    System.out.println("[" + getTimestamp() + "] [STATS] p/s: " + PPS.getAndSet(0L) + " | b/s: 0");
                }

                if (Config.stats_bPS) {
                    System.out.println("[" + getTimestamp() + "] [STATS] p/s: 0 | b/s: " + calculateBps());
                }
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
        var sdf = new SimpleDateFormat("HH:mm:ss.SSS");
        return sdf.format(new Date());
    }

    public void shutdown() {
        interrupt();
        keepRunning = false;
    }
}
