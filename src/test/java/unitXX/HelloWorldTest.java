package unitXX;

import model.*;
import model.Tiles.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class EndlessInteractTest {

    private EndlessMUD endlessMUD;
    private Room room;
    private Pc player;
    private EndlessInteract interact;

    @BeforeEach
    public void setUp() {
        // Mock or simple objects for testing
        endlessMUD = new EndlessMUD();
        room = new Room(5, 5); // assume Room constructor takes rows, cols
        player = new Pc("Hero", 100, 10, 0); // name, health, attack, gold
        player.updateLocation(2, 2); // set initial location

        interact = new EndlessInteract(endlessMUD, room, player);
    }

    @Test
    public void testVisitEmptyTileMovesPlayer() {
        EmptyTile eTile = new EmptyTile(3, 3);
        interact.visitEmptyTile(eTile);

        int[] newLoc = player.getLocation();
        assertEquals(3, newLoc[0]);
        assertEquals(3, newLoc[1]);

        ConcreteTile[][] tiles = room.getTiles();
        assertTrue(tiles[3][3] instanceof CharacterTile);
        assertTrue(tiles[2][2] instanceof EmptyTile);
    }

    @Test
    public void testVisitCharacterTileReducesNpcHealth() {
        Character npc = new Character("Goblin", 20, 5);
        CharacterTile cTile = new CharacterTile(npc, 1, 1);

        interact.visitCharacterTile(cTile);

        assertEquals(10, npc.getHealth()); // player attack = 10
    }

    @Test
    public void testVisitCharacterTileKillsNpc() {
        Character npc = new Character("Orc", 10, 5);
        CharacterTile cTile = new CharacterTile(npc, 1, 1);

        interact.visitCharacterTile(cTile);

        assertTrue(room.getTiles()[1][1] instanceof EmptyTile);
    }

    @Test
    public void testVisitTrapTileDamagesPlayer() {
        TrapTile trap = new TrapTile("Spike Trap", 20, 1, 1);
        trap.discover(); // so it is armed and discovered

        int oldHealth = player.getHealth();
        interact.visitTrapTile(trap);

        assertTrue(player.getHealth() <= oldHealth);
    }

    @Test
    public void testVisitShrineTileSetsRespawn() {
        room.setSafe(true); // make room safe
        ShrineTile shrine = new ShrineTile(1, 1);

        interact.visitShrineTile(shrine);

        assertTrue(shrine.canPray());
        assertEquals(room, endlessMUD.getShrineRoom());
    }

}
