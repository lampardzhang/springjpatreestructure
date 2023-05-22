package com.damon.springboot.treestructure.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

public class SnowFlakeIdWorker {
    private static final long TWEPOCH = 1577836800000L;
    private static final long WORKER_ID_BITS = 3L;
    private static final long DATA_CENTER_ID_BITS = 3L;
    public static final long MAX_WORKER_ID = 7L;
    public static final long MAX_DATA_CENTER_ID = 7L;
    private static final long SEQUENCE_BITS = 14L;
    private static final long WORKER_ID_SHIFT = 14L;
    private static final long DATA_CENTER_ID_SHIFT = 17L;
    private static final long TIMESTAMP_LEFT_SHIFT = 20L;
    private static final long SEQUENCE_MASK = 16383L;
    private long workerId;
    private long dataCenterId;
    private long sequence = 0L;
    private long lastTimestamp = -1L;
    private static final SnowFlakeIdWorker idWorker = new SnowFlakeIdWorker(getWorkId(), getDataCenterId());

    public SnowFlakeIdWorker(long workerId, long dataCenterId) {
        if (workerId <= 7L && workerId >= 0L) {
            if (dataCenterId <= 7L && dataCenterId >= 0L) {
                this.workerId = workerId;
                this.dataCenterId = dataCenterId;
            } else {
                throw new IllegalArgumentException(String.format("data center id can't be greater than %d or less than 0", 7L));
            }
        } else {
            throw new IllegalArgumentException(String.format("worker id can't be greater than %d or less than 0", 7L));
        }
    }

    public SnowFlakeIdWorker() {
    }

    public synchronized long nextId() {
        long timestamp = this.timeGen();
        if (timestamp < this.lastTimestamp) {
            throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", this.lastTimestamp - timestamp));
        } else {
            if (this.lastTimestamp == timestamp) {
                this.sequence = this.sequence + 1L & 16383L;
                if (this.sequence == 0L) {
                    timestamp = this.tilNextMillis(this.lastTimestamp);
                }
            } else {
                this.sequence = 0L;
            }

            this.lastTimestamp = timestamp;
            return timestamp - 1577836800000L << 20 | this.dataCenterId << 17 | this.workerId << 14 | this.sequence;
        }
    }

    private static Long getWorkId() {
        try {
            String hostAddress = InetAddress.getLocalHost().getHostAddress();
            int[] ints = StringUtils.toCodePoints(hostAddress);
            int sums = 0;
            int[] var3 = ints;
            int var4 = ints.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                int b = var3[var5];
                sums += b;
            }

            return (long)(sums % 8);
        } catch (UnknownHostException var7) {
            return RandomUtils.nextLong(0L, 7L);
        }
    }

    private static Long getDataCenterId() {
        String hostName = SystemUtils.getHostName();
        if (StringUtils.isEmpty(hostName)) {
            try {
                hostName = InetAddress.getLocalHost().getHostName();
            } catch (UnknownHostException var7) {
                var7.printStackTrace();
            }
        }

        int[] ints = StringUtils.toCodePoints(hostName == null ? "" : hostName);
        int sums = 0;
        int[] var3 = ints;
        int var4 = ints.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            int i = var3[var5];
            sums += i;
        }

        return (long)(sums % 8);
    }

    protected long tilNextMillis(long lastTimestamp) {
        long timestamp;
        for(timestamp = this.timeGen(); timestamp <= lastTimestamp; timestamp = this.timeGen()) {
        }

        return timestamp;
    }

    protected long timeGen() {
        return System.currentTimeMillis();
    }

    public static synchronized Long generateId() {
        return idWorker.nextId();
    }
}