package com.aayushatharva.sourcecenginequerycacher.server;

import java.util.SplittableRandom;

import static com.aayushatharva.sourcecenginequerycacher.constants.Packets.A2S_CHALLENGE_LENGTH;

public class ChallengeGenerator {
    private static final SplittableRandom RANDOM = new SplittableRandom();

    public static byte[] generateRandomChallenge() {
        // Generate Random Data of 4 Bytes
        byte[] challenge = new byte[A2S_CHALLENGE_LENGTH];
        RANDOM.nextBytes(challenge);
        return challenge;
    }
}
