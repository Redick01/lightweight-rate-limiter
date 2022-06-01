package io.redick01.ratelimiter.common.enums;

import java.util.Arrays;

/**
 * @author Redick01
 */
public enum RedisModeEnum {

    /**
     * Cluster redis mode enum.
     */
    CLUSTER("cluster"),

    /**
     * Sentinel redis mode enum.
     */
    SENTINEL("sentinel"),

    /**
     * Standalone redis mode enum.
     */
    STANDALONE("standalone");

    /**
     * Redis Mode Name.
     */
    private final String name;

    /**
     * all args constructor.
     *
     * @param name name
     */
    RedisModeEnum(final String name) {
        this.name = name;
    }

    /**
     * get name.
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Acquire by name data sync enum.
     *
     * @param name redisModeName
     * @return RedisModeEnum
     */
    public static RedisModeEnum acquireByName(final String name) {
        return Arrays.stream(RedisModeEnum.values())
                .filter(e -> e.getName().equals(name)).findFirst()
                .orElse(RedisModeEnum.STANDALONE);
    }
}
