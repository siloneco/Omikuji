package com.github.siloneco.omikuji.utility;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;

/**
 * Cited: https://github.com/AzisabaNetwork/LeonGunWar/blob/master/src/main/java/net/azisaba/lgw/core/utils/Args.java
 * コマンドの引数チェックを超簡単に！ タブ補完にも対応！
 *
 * @author YukiLeafX
 */
@UtilityClass
public class Args {

    public boolean isEmpty(String[] args) {
        return args.length == 0;
    }

    public String get(String[] args, int index) {
        return args.length > index ? args[index] : null;
    }

    public boolean check(String[] args, int index, String... checks) {
        String input = get(args, index);
        return input != null && (checks.length == 0 || Arrays.stream(checks)
                .anyMatch(input::equalsIgnoreCase));
    }

    public List<String> complete(String[] args, int index, String... suggestions) {
        String input = get(args, index);
        return input != null ? Arrays.stream(suggestions)
                .filter(suggestion -> suggestion.startsWith(input))
                .collect(Collectors.toList()) : null;
    }
}