package dev.gungoguma.annoymous;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public final class AnonymousNameGenerator {
    private static final int MINECRAFT_NAME_LIMIT = 16;

    private final String prefix;
    private final String salt;
    private final int hashLength;

    public AnonymousNameGenerator(String prefix, String salt, int hashLength) {
        this.prefix = normalizePrefix(prefix);
        this.salt = salt == null ? "" : salt;
        this.hashLength = normalizeHashLength(this.prefix, hashLength);
    }

    public String generate(UUID uuid, Set<String> existingNames) {
        Set<String> names = existingNames == null ? Set.of() : new HashSet<>(existingNames);
        String baseHash = sha1(uuid + salt);
        String baseName = prefix + baseHash.substring(0, hashLength);

        if (!names.contains(baseName)) {
            return baseName;
        }

        int suffix = 1;
        while (suffix < 1000) {
            String suffixText = Integer.toString(suffix);
            int availableHashLength = MINECRAFT_NAME_LIMIT - prefix.length() - suffixText.length();
            if (availableHashLength <= 0) {
                throw new IllegalStateException("Anonymous name prefix is too long.");
            }

            String candidate = prefix + baseHash.substring(0, availableHashLength) + suffixText;
            if (!names.contains(candidate)) {
                return candidate;
            }
            suffix++;
        }

        throw new IllegalStateException("Failed to resolve anonymous name collision.");
    }

    private String sha1(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] hashed = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder(hashed.length * 2);
            for (byte current : hashed) {
                builder.append(String.format(Locale.ROOT, "%02x", current));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-1 is not available.", exception);
        }
    }

    private String normalizePrefix(String value) {
        String normalized = value == null || value.isBlank() ? "gsm_" : value;
        if (normalized.length() >= MINECRAFT_NAME_LIMIT) {
            return normalized.substring(0, MINECRAFT_NAME_LIMIT - 1);
        }
        return normalized;
    }

    private int normalizeHashLength(String normalizedPrefix, int value) {
        int maximum = MINECRAFT_NAME_LIMIT - normalizedPrefix.length();
        if (value <= 0) {
            return Math.min(6, maximum);
        }
        return Math.min(value, maximum);
    }
}
