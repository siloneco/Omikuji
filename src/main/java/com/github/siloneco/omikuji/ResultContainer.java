package com.github.siloneco.omikuji;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Random;

@RequiredArgsConstructor
public class ResultContainer {

    @Getter
    private final HashMap<Double, OmikujiResult> results;
    private SecureRandom random;

    public OmikujiResult execute() throws NoSuchAlgorithmException {
        if (random == null) {
            random = SecureRandom.getInstance("SHA1PRNG");
        }
        double resultNum = random.nextDouble() * 100;

        double key = results.keySet().stream()
                .filter(num -> num <= resultNum)
                .sorted()
                .reduce((first, second) -> second)
                .orElse(-1d);

        if (key < 0) {
            Bukkit.getLogger().info("ResultNum: " + resultNum);
            for (Double mapKey : results.keySet()) {
                Bukkit.getLogger().info(mapKey + ": " + results.get(mapKey).getId());
            }
            return null;
        }

        return results.get(key);
    }

    public OmikujiResult getResult(String id) {
        return results.values().stream()
                .filter(result -> result.getId().equals(id))
                .findFirst().orElse(null);
    }
}
