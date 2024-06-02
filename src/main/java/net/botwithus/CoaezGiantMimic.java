package net.botwithus;

import net.botwithus.internal.scripts.ScriptDefinition;
import net.botwithus.rs3.game.Client;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.script.LoopingScript;
import net.botwithus.rs3.script.config.ScriptConfig;
import net.botwithus.rs3.script.Execution;
import net.botwithus.rs3.game.movement.NavPath;
import net.botwithus.rs3.game.movement.Movement;
import net.botwithus.rs3.game.movement.TraverseEvent;
import net.botwithus.rs3.game.Coordinate;
import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.api.game.hud.inventories.Bank;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.queries.builders.items.InventoryItemQuery;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.game.actionbar.ActionBar;
import net.botwithus.rs3.game.queries.builders.components.ComponentQuery;
import net.botwithus.rs3.game.queries.Queries;
import net.botwithus.rs3.game.hud.interfaces.Component;
import net.botwithus.rs3.game.Item;
import net.botwithus.api.game.hud.Dialog;
import net.botwithus.rs3.game.hud.interfaces.Interfaces;
import net.botwithus.rs3.input.KeyboardInput;
import net.botwithus.rs3.util.Regex;
import net.botwithus.rs3.game.queries.builders.characters.NpcQuery;
import net.botwithus.rs3.game.scene.entities.characters.npc.Npc;
import net.botwithus.rs3.game.vars.VarManager;
import net.botwithus.rs3.game.scene.entities.item.GroundItem;
import net.botwithus.rs3.game.queries.builders.items.GroundItemQuery;
import net.botwithus.rs3.util.RandomGenerator;
import net.botwithus.rs3.game.Area;
import net.botwithus.rs3.game.queries.builders.animations.ProjectileQuery;
import net.botwithus.rs3.game.scene.entities.animation.Projectile;
import net.botwithus.rs3.game.queries.results.ResultSet;
import net.botwithus.rs3.game.queries.results.EntityResultSet;
import net.botwithus.rs3.game.queries.builders.components.ComponentQuery;
import net.botwithus.rs3.game.vars.VarManager;
import net.botwithus.rs3.script.config.ScriptConfig;
import net.botwithus.rs3.game.queries.builders.QueryBuilder;
import net.botwithus.rs3.game.hud.interfaces.Interfaces;
import net.botwithus.rs3.events.impl.ChatMessageEvent;
import net.botwithus.rs3.game.scene.entities.animation.SpotAnimation;
import net.botwithus.rs3.game.queries.builders.animations.SpotAnimationQuery;
import net.botwithus.rs3.game.hud.interfaces.Component.Type;

import java.awt.event.KeyEvent;
import java.util.Random;
import java.util.regex.Pattern;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Arrays;
import java.util.ArrayList;

public class CoaezGiantMimic extends LoopingScript {

    private long scriptStartTime;
    private boolean isRunning;
    private CoaezGiantMimicGraphicsContext sgc;
    private ScriptConfig config;

    private BotState botState = BotState.NOT_LOGGED_IN;
    private Difficulty difficulty = Difficulty.NONE;
    private DialogState dialogState = DialogState.START;
    
    private Random random = new Random();
    private boolean difficultyMessageDisplayed = false;
    
    private boolean presetLoaded = false;
    private boolean waitingForPreset;

    private boolean mimicKilled = false;
    private boolean mimicSpawned = false;
    public int mimicKillCounter = 0;
    
    private long surgeDelayStartTime = 0;
    private long lastSurgeTime = 0;
    private static final long SURGE_COOLDOWN = 2500;
    
    private static final int CHARGE_ANIMATION_ID = 28071;
    private static final int LEAP_ANIMATION_ID = 28074;
    private static final int COIN_ATTACK_ANIMATION_ID = 28080;
    
    private static final int TILE_THRESHOLD = 1;
    
    private static final int MAX_RETRIES = 5;
    private static final int TIMEOUT = 5000;
    private static final int MAX_RETRIES_SPAWN = 100;
    private int retryCountSpawn = 0;
    
    //Utility
    public boolean activateSoulSplit;
    public boolean useSurge;
    public boolean activateOverloads;
    public boolean useFood;
    public boolean useExcalibur;
    public boolean useElvenRitualShard;
    public boolean useAltar;
    //public boolean activateSuperPotions;

    // Necro prayers
    public boolean activateSanctity;
    public boolean activateHandOfDoom;
    public boolean activateAcceleratedDecay;

    // Range prayers
    public boolean activateRigour;
    public boolean activateEagleEye;
    public boolean activateOverpoweringForce;

    // Melee prayers
    public boolean activatePiety;
    public boolean activateChivalry;

    // Magic prayers
    public boolean activateOvercharge;
    public boolean activateMysticMight;
    public boolean activateAugury;

    // Curses
    public boolean activateAnguish;
    public boolean activateTurmoil;
    public boolean activateTorment;
    public boolean activateSorrowCurse;
    public boolean activateMalevolence;
    public boolean activateDesolation;
    public boolean activateAffliction;
    public boolean activateRuination;
    public boolean activateSorrow;

    

    public enum BotState {
        NOT_LOGGED_IN,
        CHECK_DIFFICULTY,
        LOAD_PRESET,
        REFILL_PRAYER,
        USE_TOKEN,
        WAIT_FOR_SPAWN,
        COMBAT,
        PICKUP_LOOT,
        NAVIGATING_TO_WARS_RETREAT,
        STOPPED
    }

    public enum DialogState {
        DIFFICULTY_BEGINNER,
        DIFFICULTY_MEDIUM,
        DIFFICULTY_HARD,
        DIFFICULTY_ELITE,
        CONFIRM_YES,
        SELECT_DIFFICULTY,
        START
    }

    public enum Difficulty {
        BEGINNER,
        MEDIUM,
        HARD,
        ELITE,
        NONE
    }

    public CoaezGiantMimic(String s, ScriptConfig scriptConfig, ScriptDefinition scriptDefinition) {
        super(s, scriptConfig, scriptDefinition);
        this.sgc = new CoaezGiantMimicGraphicsContext(getConsole(), this, new ScriptConfig("defaultConfig", true));
        this.scriptStartTime = System.currentTimeMillis();
        this.config = scriptConfig;
        subscribe(ChatMessageEvent.class, this::onChatMessage);

        this.activateSoulSplit = false;
        this.useSurge = false;
        this.activateOverloads = false;
        this.useFood = false;
        this.useExcalibur = false;
        this.useElvenRitualShard = false;
        this.useAltar = false;

        // Necro prayers
        this.activateSanctity = false;
        this.activateHandOfDoom = false;
        this.activateAcceleratedDecay = false;

        // Range prayers
        this.activateRigour = false;
        this.activateEagleEye = false;
        this.activateOverpoweringForce = false;

        // Melee prayers
        this.activatePiety = false;
        this.activateChivalry = false;

        // Magic prayers
        this.activateOvercharge = false;
        this.activateMysticMight = false;
        this.activateAugury = false;

        // Curses
        this.activateAnguish = false;
        this.activateTurmoil = false;
        this.activateTorment = false;
        this.activateSorrowCurse = false;
        this.activateMalevolence = false;
        this.activateDesolation = false;
        this.activateAffliction = false;
        this.activateRuination = false;
    }


    public long getElapsedTime() {
        return isRunning ? System.currentTimeMillis() - scriptStartTime : 0;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public DialogState getDialogState() {
        return dialogState;
    }

    public void setDifficulty(Difficulty difficulty) {
        if (this.difficulty != difficulty) {
            this.difficulty = difficulty;
            difficultyMessageDisplayed = false;
        }
        if (!difficultyMessageDisplayed) {
            getConsole().println("Difficulty set to " + difficulty);
            difficultyMessageDisplayed = true;
        }
        dialogState = getDialogStateForDifficulty(difficulty);
    }

    public boolean inventoryInteract(String option, String... items) {
        Pattern pattern = Regex.getPatternForContainingOneOf(items);
        Item item = InventoryItemQuery.newQuery().name(pattern).results().first();
        if (item != null) {
            String itemName = item.getName();
            Component itemComponent = ComponentQuery.newQuery(1473).componentIndex(5).itemName(itemName).results().first();
            if (itemComponent != null) {
                itemComponent.interact(option);
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private DialogState getDialogStateForDifficulty(Difficulty difficulty) {
        switch (difficulty) {
            case BEGINNER:
                return DialogState.DIFFICULTY_BEGINNER;
            case MEDIUM:
                return DialogState.DIFFICULTY_MEDIUM;
            case HARD:
                return DialogState.DIFFICULTY_HARD;
            case ELITE:
                return DialogState.DIFFICULTY_ELITE;
            default:
                return DialogState.START;
        }
    }

    @Override
    public void onLoop() {
        if (!isRunning) {
            return;
        }

        if (botState == BotState.STOPPED) {
            getConsole().println("Bot is stopped.");
            stopScript();
            return;
        }

        updatePlayerState();
        saveConfiguration();

        switch (botState) {
            case NOT_LOGGED_IN:
                handleNotLoggedIn();
                break;
            case CHECK_DIFFICULTY:
                checkDifficulty();
                if (!isRunning) return;
                break;
            case NAVIGATING_TO_WARS_RETREAT:
                handleNavigatingToWarsRetreat();
                if (!isRunning) return;
                break;
            case LOAD_PRESET:
            	loadPreset();
                if (!isRunning) return;
                break;
            case REFILL_PRAYER:
                handleRefillPrayer();
                if (!isRunning) return;
                break;
            case USE_TOKEN:
                handleUseToken();
                if (!isRunning) return;
                botState = BotState.WAIT_FOR_SPAWN;
                break;
            case WAIT_FOR_SPAWN:
                handleWaitForSpawn();
                if (!isRunning) return;
                break;
            case COMBAT:
                handleCombat();
                if (!isRunning) return;
                break;
            case PICKUP_LOOT:
                handlePickupLoot();
                botState = BotState.NAVIGATING_TO_WARS_RETREAT;
                break;
            case STOPPED:
                getConsole().println("Bot is stopped.");
                stopScript();
                return;
        }
    }



    public void startScript() {
        if (!isRunning) {
            scriptStartTime = System.currentTimeMillis();
            isRunning = true;
            botState = BotState.CHECK_DIFFICULTY;
        }
    }

    public void stopScript() {
        isRunning = false;
    }

    public boolean isRunning() {
        return isRunning;
    }

    private void updatePlayerState() {
        LocalPlayer player = Client.getLocalPlayer();
        if (player == null) {
            botState = BotState.NOT_LOGGED_IN;
        } else if (botState == BotState.NOT_LOGGED_IN) {
            botState = BotState.CHECK_DIFFICULTY;
        }
    }
    
    private void checkDifficulty() {
        if (difficulty == Difficulty.NONE) {
            getConsole().println("Please select a difficulty level.");
            botState = BotState.STOPPED;
        } else {
            botState = BotState.NAVIGATING_TO_WARS_RETREAT;
        }
    }
    private void handleNotLoggedIn() {
        Execution.delay(random.nextLong(3000, 7000));
    }

    private void handleNavigatingToWarsRetreat() {
        handleOpeningLoot();

        SceneObject bankChest = SceneObjectQuery.newQuery().name("Bank chest").results().nearest();
        LocalPlayer player = Client.getLocalPlayer();

        if (bankChest != null && bankChest.getCoordinate().distanceTo(player.getCoordinate()) < 10) {
            loadPreset();
        } else {
            getConsole().println("Navigating to War's Retreat.");
            boolean success = ActionBar.useTeleport("War's Retreat Teleport");
            if (success) {
                Execution.delayUntil(random.nextLong(6000, 8000), () -> {
                    SceneObject updatedBankChest = SceneObjectQuery.newQuery().name("Bank chest").results().nearest();
                    return updatedBankChest != null && updatedBankChest.isReachable();
                });
                loadPreset();
            } else {
                getConsole().println("Failed to teleport to War's Retreat.");
                botState = BotState.STOPPED;
            }
        }
    }

    private void loadPreset() {
        SceneObject bankChest = SceneObjectQuery.newQuery().name("Bank chest").results().nearest();
        LocalPlayer player = Client.getLocalPlayer();

        if (bankChest != null && bankChest.getCoordinate().distanceTo(player.getCoordinate()) < 10) {
            bankChest.interact("Use");

            boolean interacted = Execution.delayUntil(random.nextLong(6000, 8000), () -> {
                SceneObject updatedBankChest = SceneObjectQuery.newQuery().name("Bank chest").results().nearest();
                return !player.isMoving() && updatedBankChest != null;
            });

            if (interacted) {
                getConsole().println("Loading last preset.");
                presetLoaded = false;
                waitingForPreset = true;
                Bank.loadLastPreset();

                boolean success = Execution.delayUntil(random.nextLong(5000, 10000), () -> presetLoaded);

                waitingForPreset = false;

                if (success) {
                    botState = BotState.REFILL_PRAYER;
                } else {
                    getConsole().println("Failed to load preset in time.");
                    botState = BotState.NAVIGATING_TO_WARS_RETREAT;
                }
            } else {
                getConsole().println("Failed to interact with bank chest.");
                botState = BotState.NAVIGATING_TO_WARS_RETREAT;
            }
        } else {
            getConsole().println("Failed to find bank chest or it is too far.");
            botState = BotState.NAVIGATING_TO_WARS_RETREAT;
        }
    }
    
    private void onChatMessage(ChatMessageEvent event) {
        if (waitingForPreset && event.getMessage().contains("Your preset is being withdrawn")) {
            presetLoaded = true;
            setBotState(BotState.REFILL_PRAYER);
        }
    }


    private void handleUseToken() {
        if (inventoryInteract("Teleport", "Mimic kill token")) {
            getConsole().println("Teleport action performed on Mimic kill token.");
            Execution.delay(random.nextInt(1200, 1800));
            dialogState = DialogState.CONFIRM_YES;
            handleMimicDialog();
            if (!isRunning) return; 

            navigateToArenaMiddle();
        } else {
            getConsole().println("No tokens found. Stopping.");
            botState = BotState.STOPPED;
            stopScript();
        }
    }

    private void handleRefillPrayer() {
        if (!useAltar) {
            botState = BotState.USE_TOKEN;
            return;
        }

        if (botState != BotState.REFILL_PRAYER) return;

        SceneObject altar = SceneObjectQuery.newQuery()
            .name("Altar of War")
            .option("Pray")
            .results()
            .nearest();

        if (altar != null) {
            if (altar.getOptions().contains("Pray")) {
                getConsole().println("Praying at Altar of War.");
                altar.interact("Pray");
                Execution.delay(random.nextLong(3000, 5000));
                botState = BotState.USE_TOKEN;
            } else {
                getConsole().println("Altar interaction option not found.");
                botState = BotState.NAVIGATING_TO_WARS_RETREAT;
            }
        } else {
            getConsole().println("Altar not found. Teleporting...");
            botState = BotState.NAVIGATING_TO_WARS_RETREAT;
        }
    }


    private void handleWaitForSpawn() {
        SceneObject altar = SceneObjectQuery.newQuery().name("Altar of War").results().nearest();
        
        if (altar != null) {
            botState = BotState.USE_TOKEN; 
            return;
        }

        if (retryCountSpawn < MAX_RETRIES_SPAWN) {
            if (checkMimicSpawn()) {
                botState = BotState.COMBAT;
                retryCountSpawn = 0; 
            } else {
                Execution.delay(100);
                retryCountSpawn++;
            }
        } else {
            getConsole().println("Max retries reached. Changing state.");
            botState = BotState.NAVIGATING_TO_WARS_RETREAT; 
        }
    }

    
    private void handleCombat() {
        LocalPlayer player = Client.getLocalPlayer();
        if (player == null) {
            getConsole().println("Player not found.");
            return;
        }

        if (!mimicSpawned) {
            mimicSpawned = checkMimicSpawn();
            if (!mimicSpawned) {
                botState = BotState.WAIT_FOR_SPAWN;
                return;
            }
        }

        Npc minimic = NpcQuery.newQuery().name("Minimic").results().nearest();
        Npc mimic = NpcQuery.newQuery().name("Giant Mimic").results().nearest();

        if (mimic != null) {
            int animationId = mimic.getAnimationId();
            boolean surgeReady = System.currentTimeMillis() - lastSurgeTime >= SURGE_COOLDOWN;

            switch (animationId) {
                case CHARGE_ANIMATION_ID:
                    handleChargeAttack(mimic, surgeReady);
                    break;
                case LEAP_ANIMATION_ID:
                    handleLeapAttack(mimic, surgeReady);
                    break;
                case COIN_ATTACK_ANIMATION_ID:
                    handleCoinAttack(mimic);
                    break;
                default:
                    if (minimic != null) {
                        minimic.interact("Attack");
                        boolean successMinimic = Execution.delayUntil(random.nextLong(100, 600), () -> minimic.getCurrentHealth() >= 0);
                    } else {
                        mimic.interact("Attack");
                        Execution.delay(random.nextLong(100, 200));
                    }
                    break;
            }

            if (healIfNecessary()) {
                return;
            }
            if (restorePrayerIfNecessary()) {
                return;
            }

            if (activateOverloads && !isOverloadPotActive()) {
                handleOverloadPotion();
            }
        } else {
            mimicSpawned = false;
            if (mimicKilled()) {
                mimicKillCounter++;
                getConsole().println("Mimic killed. Total kills: " + mimicKillCounter);
                botState = BotState.PICKUP_LOOT;
            } else {
                botState = BotState.WAIT_FOR_SPAWN;
            }
        }

        if (!activateOverloads) {
            getConsole().println("Not using Overloads");
        }
        activateSelectedPrayersAndCurses();
    }
    
    private void handleLeapAttack(Npc mimic, boolean surgeReady) {
        LocalPlayer player = Client.getLocalPlayer();
        if (useSurge && surgeReady) {
            useSurge();
            lastSurgeTime = System.currentTimeMillis();
        } else if (System.currentTimeMillis() - lastSurgeTime >= SURGE_COOLDOWN) {
            Coordinate mimicCoordinate = mimic.getCoordinate();
            int direction1 = (int) player.getDirection1();
            int direction2 = (int) player.getDirection2();
            Coordinate targetCoordinate = getTargetCoordinate(mimicCoordinate, direction1, direction2, 5);

            moveTo(targetCoordinate);
        }
        Execution.delay(100);
        mimic.interact("Attack");
    }

    private Coordinate getTargetCoordinate(Coordinate start, int direction1, int direction2, int distance) {
        int x = start.getX();
        int y = start.getY();
        
        // Adjust the target coordinate based on the direction1 and direction2
        if (direction1 == 0 || direction2 == 0) { // North
            y += distance;
        }
        if (direction1 == 4 || direction2 == 4) { // South
            y -= distance;
        }
        if (direction1 == 2 || direction2 == 2) { // East
            x += distance;
        }
        if (direction1 == 6 || direction2 == 6) { // West
            x -= distance;
        }
        if ((direction1 == 1 || direction2 == 1) || (direction1 == 3 || direction2 == 3)) { // North-East / South-East
            x += distance;
            y += (direction1 == 1 || direction2 == 1) ? distance : -distance;
        }
        if ((direction1 == 5 || direction2 == 5) || (direction1 == 7 || direction2 == 7)) { // South-West / North-West
            x -= distance;
            y += (direction1 == 7 || direction2 == 7) ? distance : -distance;
        }

        return new Coordinate(x, y, start.getZ());
    }
	    
    private void handleChargeAttack(Npc mimic, boolean surgeReady) {
        LocalPlayer player = Client.getLocalPlayer();
        if (useSurge && surgeReady) {
            useSurge();
            lastSurgeTime = System.currentTimeMillis();
        } else if (System.currentTimeMillis() - lastSurgeTime >= SURGE_COOLDOWN) {
            Coordinate playerCoordinate = player.getCoordinate();
            int direction1 = (int) player.getDirection1();
            int direction2 = (int) player.getDirection2();
            Coordinate targetCoordinate = getAvoidanceCoordinate(playerCoordinate, direction1, direction2, 6);

            moveTo(targetCoordinate);
        }
        Execution.delay(100);
        mimic.interact("Attack");
    }

    private Coordinate getAvoidanceCoordinate(Coordinate start, int direction1, int direction2, int distance) {
        List<Coordinate> possibleCoordinates = new ArrayList<>();

        switch (direction1) {
            case 0: // North
                possibleCoordinates.add(new Coordinate(start.getX() + distance, start.getY(), start.getZ())); // East
                possibleCoordinates.add(new Coordinate(start.getX() - distance, start.getY(), start.getZ())); // West
                possibleCoordinates.add(new Coordinate(start.getX(), start.getY() - distance, start.getZ())); // South
                break;
            case 1: // North-East
                possibleCoordinates.add(new Coordinate(start.getX() + distance, start.getY(), start.getZ())); // East
                possibleCoordinates.add(new Coordinate(start.getX(), start.getY() - distance, start.getZ())); // South
                possibleCoordinates.add(new Coordinate(start.getX() - distance, start.getY() - distance, start.getZ())); // South-West
                break;
            case 2: // East
                possibleCoordinates.add(new Coordinate(start.getX(), start.getY() + distance, start.getZ())); // North
                possibleCoordinates.add(new Coordinate(start.getX(), start.getY() - distance, start.getZ())); // South
                possibleCoordinates.add(new Coordinate(start.getX() - distance, start.getY(), start.getZ())); // West
                break;
            case 3: // South-East
                possibleCoordinates.add(new Coordinate(start.getX(), start.getY() + distance, start.getZ())); // North
                possibleCoordinates.add(new Coordinate(start.getX() - distance, start.getY(), start.getZ())); // West
                possibleCoordinates.add(new Coordinate(start.getX() - distance, start.getY() + distance, start.getZ())); // North-West
                break;
            case 4: // South
                possibleCoordinates.add(new Coordinate(start.getX() + distance, start.getY(), start.getZ())); // East
                possibleCoordinates.add(new Coordinate(start.getX() - distance, start.getY(), start.getZ())); // West
                possibleCoordinates.add(new Coordinate(start.getX(), start.getY() + distance, start.getZ())); // North
                break;
            case 5: // South-West
                possibleCoordinates.add(new Coordinate(start.getX(), start.getY() + distance, start.getZ())); // North
                possibleCoordinates.add(new Coordinate(start.getX() + distance, start.getY(), start.getZ())); // East
                possibleCoordinates.add(new Coordinate(start.getX() + distance, start.getY() + distance, start.getZ())); // North-East
                break;
            case 6: // West
                possibleCoordinates.add(new Coordinate(start.getX(), start.getY() + distance, start.getZ())); // North
                possibleCoordinates.add(new Coordinate(start.getX(), start.getY() - distance, start.getZ())); // South
                possibleCoordinates.add(new Coordinate(start.getX() + distance, start.getY(), start.getZ())); // East
                break;
            case 7: // North-West
                possibleCoordinates.add(new Coordinate(start.getX(), start.getY() - distance, start.getZ())); // South
                possibleCoordinates.add(new Coordinate(start.getX() + distance, start.getY(), start.getZ())); // East
                possibleCoordinates.add(new Coordinate(start.getX() + distance, start.getY() - distance, start.getZ())); // South-East
                break;
        }

        java.util.Collections.shuffle(possibleCoordinates);

        return possibleCoordinates.get(0);
    }
	
    private void handleCoinAttack(Npc mimic) {
        movePlayerAvoidingProjectiles(mimic, 2);
        lastSurgeTime = System.currentTimeMillis();
        Execution.delay(100);
        mimic.interact("Attack");
    }

    private void handleOverloadPotion() {
        if (inventoryInteract("Drink", "overload")) {
            boolean success = Execution.delayUntil(random.nextInt(1200, 1800), () -> isOverloadPotActive());
            if (success) {
                getConsole().println("Overload potion activated.");
            } else {
                getConsole().println("Failed to activate Overload potion in time.");
            }
        } else {
            getConsole().println("Overload potion not found in inventory.");
        }
    }

    
    private boolean checkMimicSpawn() {
        Npc mimic = NpcQuery.newQuery().name("Giant Mimic").results().nearest();
        if (mimic != null) {
            return true;
        } else {
            Execution.delay(100);
            return false;
        }
    }

    private void useSurge() {
        ActionBar.useAbility("Surge");
        Execution.delay(1200);

    }

    private void activateSelectedPrayersAndCurses() {
        if (activateSorrow && VarManager.getVarbitValue(53279) == 0) {
            enablePrayer("Sorrow");
        }
        if (activateSoulSplit && VarManager.getVarbitValue(16779) == 0) {
            enablePrayer("Soul Split");
        }
        if (activateSanctity && VarManager.getVarbitValue(53273) == 0) {
            enablePrayer("Sanctity");
        }
        if (activateHandOfDoom && VarManager.getVarbitValue(53271) == 0) {
            enablePrayer("Hand of Doom");
        }
        if (activateAcceleratedDecay && VarManager.getVarbitValue(53272) == 0) {
            enablePrayer("Accelerated Decay");
        }
        if (activateRigour && VarManager.getVarbitValue(16760) == 0) {
            enablePrayer("Rigour");
        }
        if (activateEagleEye && VarManager.getVarbitValue(16751) == 0) {
            enablePrayer("Eagle Eye");
        }
        if (activateOverpoweringForce && VarManager.getVarbitValue(16753) == 0) {
            enablePrayer("Overpowering Force");
        }
        if (activatePiety && VarManager.getVarbitValue(16757) == 0) {
            enablePrayer("Piety");
        }
        if (activateChivalry && VarManager.getVarbitValue(16756) == 0) {
            enablePrayer("Chivalry");
        }
        if (activateOvercharge && VarManager.getVarbitValue(16754) == 0) {
            enablePrayer("Overcharge");
        }
        if (activateMysticMight && VarManager.getVarbitValue(16752) == 0) {
            enablePrayer("Mystic Might");
        }
        if (activateAugury && VarManager.getVarbitValue(16759) == 0) {
            enablePrayer("Augury");
        }
        if (activateAnguish && VarManager.getVarbitValue(16783) == 0) {
            enablePrayer("Anguish");
        }
        if (activateTurmoil && VarManager.getVarbitValue(16780) == 0) {
            enablePrayer("Turmoil");
        }
        if (activateTorment && VarManager.getVarbitValue(16784) == 0) {
            enablePrayer("Torment");
        }
        if (activateSorrowCurse && VarManager.getVarbitValue(53279) == 0) {
            enablePrayer("Sorrow");
        }
        if (activateMalevolence && VarManager.getVarbitValue(34866) == 0) {
            enablePrayer("Malevolence");
        }
        if (activateDesolation && VarManager.getVarbitValue(34867) == 0) {
            enablePrayer("Desolation");
        }
        if (activateAffliction && VarManager.getVarbitValue(34868) == 0) {
            enablePrayer("Affliction");
        }
        if (activateRuination && VarManager.getVarbitValue(53280) == 0) {
            enablePrayer("Ruination");
        }
    }

    public boolean isExcaliburOnCooldown() {
        int value = VarManager.getVarbitValue(22838);
        return value != 0; 
    }

    public boolean isElvenRitualShardOnCooldown() {
        int value = VarManager.getVarbitValue(40606);
        return value != 0;
    }
    
    public boolean isOverloadPotActive() {
    	Component overloadTimer = ComponentQuery.newQuery(284).item(49039).results().first();
    	return overloadTimer != null;
        }
    
    private void enablePrayer(String prayerName) {
        boolean success = ActionBar.usePrayer(prayerName);
        Execution.delay(random.nextLong(1550, 2050));
        if (success) {
            getConsole().println("Enabled " + prayerName + "!");
        }
    }
    
    private boolean enableExcalibur() {
        if (isExcaliburOnCooldown()) {
            return false;
        }

        ResultSet<Component> results = ComponentQuery.newQuery(1473)
                                                     .componentIndex(5)
                                                     .itemName("Augmented enhanced Excalibur")
                                                     .option("Activate")
                                                     .results();
        if (!results.isEmpty()) {
            Component excaliburComponent = results.first();
            boolean success = excaliburComponent.interact("Activate");
            if (success) {
                getConsole().println("Used Excalibur");
                Execution.delay(800);
                return true;
            } else {
                getConsole().println("Failed to activate Excalibur.");
            }
        } else {
            getConsole().println("Excalibur not found in the inventory.");
        }

        return false;
    }

    private boolean enableElvenShard() {
        if (isElvenRitualShardOnCooldown()) {
            return false;
        }

        ResultSet<Component> results = ComponentQuery.newQuery(1473)
                                                     .componentIndex(5)
                                                     .itemName("Ancient elven ritual shard")
                                                     .option("Activate")
                                                     .results();
        if (!results.isEmpty()) {
            Component shardComponent = results.first();
            boolean success = shardComponent.interact("Activate");
            if (success) {
                getConsole().println("Used Elven Shard");
                Execution.delay(800);
                return true;
            } else {
                getConsole().println("Failed to activate Elven Shard.");
            }
        } else {
            getConsole().println("Elven Shard not found in the inventory.");
        }

        return false;
    }

    private boolean mimicKilled() {
        SpotAnimationQuery query = SpotAnimationQuery.newQuery().animations(4183);
        return query.results().isEmpty();
    }


    private void handlePickupLoot() {
        String[] lootChestNames = {
            "Small loot chest",
            "Medium loot chest",
            "Large loot chest",
            "Huge loot chest",
            "Mimic plushie",
            "Mimic tongue cape"
        };

        boolean foundLoot = true;
        while (foundLoot) {
            foundLoot = false;
            for (String chestName : lootChestNames) {
                GroundItem lootChest = GroundItemQuery.newQuery()
                    .name(chestName)
                    .results()
                    .nearest();

                if (lootChest != null) {
                    foundLoot = true;
                    lootChest.interact("Take");

                    boolean success = Execution.delayUntil(random.nextLong(6000, 8000), () -> {
                        return GroundItemQuery.newQuery().name(chestName).results().nearest() == null;
                    });

                    if (success) {
                        handleOpeningLoot();
                    } else {
                        getConsole().println("Failed to pick up loot chest in time.");
                    }
                    break;
                }
            }
        }

        if (!foundLoot) {
            getConsole().println("No loot chests found.");
        }
    }
  
    private void handleOpeningLoot() {
        String[] lootChestNames = {
            "Small loot chest",
            "Medium loot chest",
            "Large loot chest",
            "Huge loot chest"
        };

        boolean interactionSuccessful = false;

        for (String chestName : lootChestNames) {
            if (inventoryInteract("Open", chestName)) {
                long timeout = random.nextInt(600) + 1200;
                boolean opened = Execution.delayUntil(timeout, () -> !Backpack.contains(chestName));
                
                if (opened) {
                    botState = BotState.NAVIGATING_TO_WARS_RETREAT;
                    interactionSuccessful = true;
                    break;
                } else {
                    getConsole().println(chestName + " failed to open, retrying.");
                }
            }
        }

        if (!interactionSuccessful) {
            botState = BotState.NAVIGATING_TO_WARS_RETREAT;
        }else 
            botState = BotState.NAVIGATING_TO_WARS_RETREAT;
    }
    
    private void handleMimicDialog() {
        int retryCount = 0;
        
        while (retryCount < MAX_RETRIES) {
            dialogState = DialogState.START;
            long startTime = System.currentTimeMillis();

            while (Dialog.isOpen() && (System.currentTimeMillis() - startTime < TIMEOUT)) {
                switch (dialogState) {
                    case START:
                        dialogState = DialogState.CONFIRM_YES;
                        break;
                    case CONFIRM_YES:
                        if (Interfaces.isOpen(1188)) {
                            pressKeyAndAdvance(KeyEvent.VK_1, DialogState.SELECT_DIFFICULTY);
                        }
                        break;
                    case SELECT_DIFFICULTY:
                        handleDifficultySelection();
                        break;
                    default:
                        getConsole().println("Unknown Dialog State: " + dialogState);
                        break;
                }
                Execution.delay(100);
            }

            if (!Dialog.isOpen()) {
                return;
            }

            retryCount++;
            getConsole().println("Retrying... Attempt: " + retryCount);
            Execution.delay(200); 
        }

        getConsole().println("Max retries reached. Exiting...");
    }

    private void handleDifficultySelection() {
        switch (difficulty) {
            case BEGINNER:
                pressKeyAndAdvance(KeyEvent.VK_1, DialogState.START);
                getConsole().println("Starting DIFFICULTY_BEGINNER instance");
                break;
            case MEDIUM:
                pressKeyAndAdvance(KeyEvent.VK_2, DialogState.START);
                getConsole().println("Starting DIFFICULTY_MEDIUM instance");
                break;
            case HARD:
                pressKeyAndAdvance(KeyEvent.VK_3, DialogState.START);
                getConsole().println("Starting DIFFICULTY_HARD instance");
                break;
            case ELITE:
                pressKeyAndAdvance(KeyEvent.VK_4, DialogState.START);
                getConsole().println("Starting DIFFICULTY_ELITE instance");
                break;
        }
        Execution.delay(random.nextInt(700, 1000));
    }

    private void pressKeyAndAdvance(int key, DialogState nextState) {
        KeyboardInput.pressKey(key);
        Execution.delay(random.nextInt(700, 1000));
        this.dialogState = nextState;
    }
    
    private void logProjectileDetails() {
        EntityResultSet<Projectile> projectiles = ProjectileQuery.newQuery().results();
        getConsole().println("Number of projectiles found: " + projectiles.size());

        for (Projectile projectile : projectiles) {
            getConsole().println("Projectile ID: " + projectile.getId());
            if (projectile.getSource() != null) {
                getConsole().println("Projectile Source: " + projectile.getSource().getName() + " (ID: " + projectile.getSource().getId() + ")");
            } else {
                getConsole().println("Projectile Source: null");
            }
            Coordinate destination = projectile.getDestination();
            getConsole().println("Projectile End Position: X=" + destination.getX() + ", Y=" + destination.getY() + ", Z=" + destination.getZ());
        }
    }
    private void logSpotAnimationDetails() {
        EntityResultSet<SpotAnimation> spotAnimations = SpotAnimationQuery.newQuery().results();
        getConsole().println("Number of spot animations found: " + spotAnimations.size());

        for (SpotAnimation spotAnimation : spotAnimations) {
            getConsole().println("Spot Animation ID: " + spotAnimation.getId());
            Coordinate position = spotAnimation.getCoordinate();
            getConsole().println("Spot Animation Position: X=" + position.getX() + ", Y=" + position.getY() + ", Z=" + position.getZ());
            if (spotAnimation.getScene() != null) {
                getConsole().println("Spot Animation Scene: " + spotAnimation.getScene().toString());
            } else {
                getConsole().println("Spot Animation Scene: null");
            }
        }
    }

    private boolean moveToWithPrayers(Coordinate coordinate) {
        if (coordinate == null) {
            getConsole().println("Target coordinate is null.");
            return false;
        }

        Movement.walkTo(coordinate.getX(), coordinate.getY(), false);

        activateSelectedPrayersAndCurses();

        boolean reached = Execution.delayUntil(random.nextInt(4000, 5000), () -> {
            LocalPlayer player = Client.getLocalPlayer();
            return player != null && player.getCoordinate().equals(coordinate);
        });

        if (reached) {
            return true;
        } else {
            return false;
        }
    }

    private boolean moveTo(Coordinate location) {
        LocalPlayer player = Client.getLocalPlayer();


        Movement.walkTo(location.getX(), location.getY(), false);

        boolean reached = Execution.delayUntil(random.nextInt(2400, 3000), () -> {
            return player != null && player.getCoordinate().equals(location);
        });

        if (reached) {
            return true;
        } else {
            return false;
        }
    }

    private List<Coordinate> getProjectileLandingPositions() {
        List<Coordinate> landingPositions = new ArrayList<>();
        EntityResultSet<Projectile> projectiles = ProjectileQuery.newQuery().results();
        for (Projectile projectile : projectiles) {
            if (projectile.getId() == 6042) {
                landingPositions.add(projectile.getDestination());
            }
        }
        return landingPositions;
    }


    private void movePlayerToSide(int distance) {
        LocalPlayer player = Client.getLocalPlayer();
        Coordinate currentCoord = player.getCoordinate();

        List<Coordinate> possibleMoves = new ArrayList<>();
        possibleMoves.add(new Coordinate(currentCoord.getX() + distance, currentCoord.getY(), currentCoord.getZ()));
        possibleMoves.add(new Coordinate(currentCoord.getX() - distance, currentCoord.getY(), currentCoord.getZ()));
        possibleMoves.add(new Coordinate(currentCoord.getX(), currentCoord.getY() + distance, currentCoord.getZ()));
        possibleMoves.add(new Coordinate(currentCoord.getX(), currentCoord.getY() - distance, currentCoord.getZ()));

        java.util.Collections.shuffle(possibleMoves, RandomGenerator.getSecureThreadLocalRandom());

        for (Coordinate move : possibleMoves) {
            if (moveToWithPrayers(move)) {
                return;
            } else {
            }
        }
    }

    public void movePlayerAvoidingProjectiles(Npc mimic, int moveCount) {
        LocalPlayer player = Client.getLocalPlayer();
        Coordinate playerCoordinate = player.getCoordinate();
        Coordinate mimicCoordinate = mimic.getCoordinate();
        List<Coordinate> projectileLandingPositions = getProjectileLandingPositions();
        List<Coordinate> visitedPositions = new ArrayList<>();

        for (int i = 0; i < moveCount; i++) {
            Coordinate safeCoordinate = findSafeCoordinate(playerCoordinate, projectileLandingPositions, 6, mimicCoordinate, 4, 10, visitedPositions);
            if (safeCoordinate != null) {
                moveTo(safeCoordinate);
                visitedPositions.add(safeCoordinate);
                playerCoordinate = safeCoordinate;
            } else {
                break;
            }
        }

        botState = BotState.COMBAT;
    }

    private Coordinate findSafeCoordinate(Coordinate playerCoordinate, List<Coordinate> landingPositions, int minDistance, Coordinate mimicCoordinate, int minMimicDistance, int maxMimicDistance, List<Coordinate> visitedPositions) {
        List<Coordinate> possibleCoordinates = new ArrayList<>();

        possibleCoordinates.add(new Coordinate(playerCoordinate.getX() + minDistance, playerCoordinate.getY(), playerCoordinate.getZ()));
        possibleCoordinates.add(new Coordinate(playerCoordinate.getX() - minDistance, playerCoordinate.getY(), playerCoordinate.getZ()));
        possibleCoordinates.add(new Coordinate(playerCoordinate.getX(), playerCoordinate.getY() + minDistance, playerCoordinate.getZ()));
        possibleCoordinates.add(new Coordinate(playerCoordinate.getX(), playerCoordinate.getY() - minDistance, playerCoordinate.getZ()));
        possibleCoordinates.add(new Coordinate(playerCoordinate.getX() + minDistance, playerCoordinate.getY() + minDistance, playerCoordinate.getZ()));
        possibleCoordinates.add(new Coordinate(playerCoordinate.getX() - minDistance, playerCoordinate.getY() - minDistance, playerCoordinate.getZ()));
        possibleCoordinates.add(new Coordinate(playerCoordinate.getX() + minDistance, playerCoordinate.getY() - minDistance, playerCoordinate.getZ()));
        possibleCoordinates.add(new Coordinate(playerCoordinate.getX() - minDistance, playerCoordinate.getY() + minDistance, playerCoordinate.getZ()));

        java.util.Collections.shuffle(possibleCoordinates, RandomGenerator.getSecureThreadLocalRandom());

        for (Coordinate coord : possibleCoordinates) {
            if (isSafeCoordinate(coord, landingPositions) 
                && isWithinMimicDistance(coord, mimicCoordinate, minMimicDistance, maxMimicDistance)
                && !visitedPositions.contains(coord)) {
                return coord;
            }
        }
        return null;
    }


    private boolean isSafeCoordinate(Coordinate coordinate, List<Coordinate> landingPositions) {
        for (Coordinate landingPosition : landingPositions) {
            if (landingPosition.distanceTo(coordinate) < 3) {
                return false;
            }
        }
        return true;
    }

    private boolean isWithinMimicDistance(Coordinate coordinate, Coordinate mimicCoordinate, int minMimicDistance, int maxMimicDistance) {
    	int distanceToMimic = (int) coordinate.distanceTo(mimicCoordinate);
        return distanceToMimic >= minMimicDistance && distanceToMimic <= maxMimicDistance;
    }

    public void movePlayerAwayFromMimic(Npc mimic, int minDistance) {
        LocalPlayer player = Client.getLocalPlayer();
        Coordinate playerCoordinate = player.getCoordinate();
        List<Coordinate> safeCoordinates = getSafeCoordinatesAwayFromMimic(playerCoordinate, mimic.getArea(), minDistance);

        if (!safeCoordinates.isEmpty()) {
            Coordinate safeCoordinate = safeCoordinates.get(RandomGenerator.nextInt(0, safeCoordinates.size()));
            moveTo(safeCoordinate);
            playerCoordinate = safeCoordinate;
        } else {
            getConsole().println("No safe coordinate found, stopping.");
        }

        botState = BotState.COMBAT;
    }

    private List<Coordinate> getSafeCoordinatesAwayFromMimic(Coordinate playerCoordinate, Area mimicArea, int minDistance) {
        List<Coordinate> possibleCoordinates = new ArrayList<>();

        possibleCoordinates.add(new Coordinate(playerCoordinate.getX() + minDistance, playerCoordinate.getY(), playerCoordinate.getZ()));
        possibleCoordinates.add(new Coordinate(playerCoordinate.getX() - minDistance, playerCoordinate.getY(), playerCoordinate.getZ()));
        possibleCoordinates.add(new Coordinate(playerCoordinate.getX(), playerCoordinate.getY() + minDistance, playerCoordinate.getZ()));
        possibleCoordinates.add(new Coordinate(playerCoordinate.getX(), playerCoordinate.getY() - minDistance, playerCoordinate.getZ()));
        possibleCoordinates.add(new Coordinate(playerCoordinate.getX() + minDistance, playerCoordinate.getY() + minDistance, playerCoordinate.getZ()));
        possibleCoordinates.add(new Coordinate(playerCoordinate.getX() - minDistance, playerCoordinate.getY() - minDistance, playerCoordinate.getZ()));
        possibleCoordinates.add(new Coordinate(playerCoordinate.getX() + minDistance, playerCoordinate.getY() - minDistance, playerCoordinate.getZ()));
        possibleCoordinates.add(new Coordinate(playerCoordinate.getX() - minDistance, playerCoordinate.getY() + minDistance, playerCoordinate.getZ()));

        return possibleCoordinates.stream()
                .filter(coord -> !mimicArea.contains(coord))
                .collect(Collectors.toList());
    }

    private List<Coordinate> getSafeCoordinatesAwayFromMimic(Coordinate playerCoordinate, Area mimicArea) {
        List<Coordinate> possibleCoordinates = new ArrayList<>();

        possibleCoordinates.add(new Coordinate(playerCoordinate.getX() + 3, playerCoordinate.getY(), playerCoordinate.getZ()));
        possibleCoordinates.add(new Coordinate(playerCoordinate.getX() - 3, playerCoordinate.getY(), playerCoordinate.getZ()));
        possibleCoordinates.add(new Coordinate(playerCoordinate.getX(), playerCoordinate.getY() + 3, playerCoordinate.getZ()));
        possibleCoordinates.add(new Coordinate(playerCoordinate.getX(), playerCoordinate.getY() - 3, playerCoordinate.getZ()));
        possibleCoordinates.add(new Coordinate(playerCoordinate.getX() + 3, playerCoordinate.getY() + 3, playerCoordinate.getZ()));
        possibleCoordinates.add(new Coordinate(playerCoordinate.getX() - 3, playerCoordinate.getY() - 3, playerCoordinate.getZ()));
        possibleCoordinates.add(new Coordinate(playerCoordinate.getX() + 3, playerCoordinate.getY() - 3, playerCoordinate.getZ()));
        possibleCoordinates.add(new Coordinate(playerCoordinate.getX() - 3, playerCoordinate.getY() + 3, playerCoordinate.getZ()));

        return possibleCoordinates.stream()
                .filter(coord -> !mimicArea.contains(coord))
                .collect(Collectors.toList());
    }

    private Coordinate getCoordinateNorthOfPlayer(int tilesNorth) {
        LocalPlayer player = Client.getLocalPlayer();
        if (player != null) {
            Coordinate playerCoordinate = player.getCoordinate();
            return new Coordinate(playerCoordinate.getX(), playerCoordinate.getY() + tilesNorth, playerCoordinate.getZ());
        } else {
            getConsole().println("Player not found.");
            return null;
        }
    }

    public void navigateToArenaMiddle() {
        SceneObject altar = SceneObjectQuery.newQuery()
                .name("Altar of War")
                .option("Pray")
                .results()
                .nearest();
        if (altar != null) {
            botState = BotState.USE_TOKEN;
            return;
        }

        Coordinate targetCoordinate = getCoordinateNorthOfPlayer(9);
        if (targetCoordinate != null) {
            boolean moved = moveToWithPrayers(targetCoordinate);
            if (moved) {
                botState = BotState.COMBAT;
            } else {
                getConsole().println("Failed to move closer to Arena center.");
            }
        } else {
            getConsole().println("Failed to determine target coordinate.");
        }
    }
    
    public static int getCurrentPlayerHealth() {
        LocalPlayer player = LocalPlayer.LOCAL_PLAYER;
        if (player != null) {
            return player.getCurrentHealth();
        }
        return -1;
    }
    private int getHealthPercentage() {
        LocalPlayer player = LocalPlayer.LOCAL_PLAYER;
        if (player != null) {
            int currentHealth = getCurrentPlayerHealth();
            int maxHealth = player.getMaximumHealth();
            if (currentHealth >= 0 && maxHealth > 0) {
                return (int) ((currentHealth / (double) maxHealth) * 100);
            }
        }
        return -1; 
    }
    public static int getCurrentPlayerPrayer() {
        LocalPlayer player = Client.getLocalPlayer();
        if (player != null) {
            return player.getPrayerPoints();
        }
        return -1;
    }
    
    private int getPrayerPercentage() {
        LocalPlayer player = Client.getLocalPlayer();
        if (player != null) {
            int currentPrayer = getCurrentPlayerPrayer();
            int maxPrayer = 990;  
            if (currentPrayer >= 0 && maxPrayer > 0) {
                return (int) ((currentPrayer / (double) maxPrayer) * 100);
            }
        }
        return -1;
    }
    
    public boolean healIfNecessary() {
        boolean healed = false;
        int healthPercentage = getHealthPercentage();

        if (useExcalibur && healthPercentage < 90 && !isExcaliburOnCooldown()) {
            if (enableExcalibur()) {
                healed = true;
            } else {
                getConsole().println("Failed to activate Excalibur.");
            }
        }

        if (useFood && healthPercentage < 70) {
            List<Item> foodItems = Backpack.getItemsWithOption("Eat");
            if (!foodItems.isEmpty()) {
                for (Item foodItem : foodItems) {
                    if (inventoryInteract("Eat", foodItem.getName())) {
                        Execution.delay(random.nextInt(1600, 2000));
                        healed = true;
                        break;
                    }
                }
                if (!healed) {
                    getConsole().println("Failed to eat any food item.");
                }
            } else {
                getConsole().println("No food item found in inventory with 'Eat' option.");
            }
        }

        return healed;
    }

    public boolean restorePrayerIfNecessary() {
        boolean restored = false;
        int prayerPercentage = getPrayerPercentage() / 10;

        if (useElvenRitualShard && prayerPercentage < 90 && !isElvenRitualShardOnCooldown()) {
            if (enableElvenShard()) {
                restored = true;
            } else {
                getConsole().println("Failed to activate Elven Ritual Shard.");
            }
        }

        return restored;
    }

    @Override
    public CoaezGiantMimicGraphicsContext getGraphicsContext() {
        return sgc;
    }

    public BotState getBotState() {
        return botState;
    }

    public void setBotState(BotState botState) {
        this.botState = botState;
    }

    public int getMimicKillCount() {
        return mimicKillCounter;
    }
    
    public ScriptConfig getConfig() {
        return config;
    }

    public void saveConfiguration() {
        configuration.save();
    }

    public void loadConfiguration() {
        configuration.load();
    }
}