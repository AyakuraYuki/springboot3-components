package cc.ayakurayuki.spring.components.utility.strings;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * @author Ayakura Yuki
 */
public record Cut(
    @Nullable String before,
    @NonNull String after,
    boolean found
) {}
