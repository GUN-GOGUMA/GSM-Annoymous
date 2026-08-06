package dev.gungoguma.annoymous;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class AnonymousNameGeneratorTest {
    private static final UUID PLAYER_UUID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    @Test
    void generatesSameNameForSameUuidAndSalt() {
        AnonymousNameGenerator generator = new AnonymousNameGenerator("gsm_", "salt", 6);

        String first = generator.generate(PLAYER_UUID, Set.of());
        String second = generator.generate(PLAYER_UUID, Set.of());

        assertEquals(first, second);
    }

    @Test
    void changesNameWhenSaltChanges() {
        String first = new AnonymousNameGenerator("gsm_", "salt-a", 6).generate(PLAYER_UUID, Set.of());
        String second = new AnonymousNameGenerator("gsm_", "salt-b", 6).generate(PLAYER_UUID, Set.of());

        assertNotEquals(first, second);
    }

    @Test
    void keepsNameInsideMinecraftLimit() {
        AnonymousNameGenerator generator = new AnonymousNameGenerator("very_long_prefix", "salt", 10);

        String name = generator.generate(PLAYER_UUID, Set.of());

        assertTrue(name.length() <= 16);
    }

    @Test
    void resolvesCollisionWithSuffix() {
        AnonymousNameGenerator generator = new AnonymousNameGenerator("gsm_", "salt", 6);
        String original = generator.generate(PLAYER_UUID, Set.of());

        String resolved = generator.generate(PLAYER_UUID, Set.of(original));

        assertNotEquals(original, resolved);
        assertTrue(resolved.length() <= 16);
    }
}
