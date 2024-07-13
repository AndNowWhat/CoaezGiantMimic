package net.botwithus;

import net.botwithus.rs3.imgui.ImGui;
import net.botwithus.rs3.imgui.ImGuiWindowFlag;
import net.botwithus.rs3.script.ScriptConsole;
import net.botwithus.rs3.script.ScriptGraphicsContext;
import net.botwithus.rs3.script.config.ScriptConfig;

public class CoaezGiantMimicGraphicsContext extends ScriptGraphicsContext {

    private CoaezGiantMimic script;
    private ScriptConfig config;

    private boolean beginner;
    private boolean medium;
    private boolean hard;
    private boolean elite;

    private static final String KEY_BEGINNER = "beginner";
    private static final String KEY_MEDIUM = "medium";
    private static final String KEY_HARD = "hard";
    private static final String KEY_ELITE = "elite";
    private static final String KEY_ACTIVATE_HAND_OF_DOOM = "activateHandOfDoom";
    private static final String KEY_ACTIVATE_ACCELERATED_DECAY = "activateAcceleratedDecay";
    private static final String KEY_ACTIVATE_PIETY = "activatePiety";
    private static final String KEY_ACTIVATE_CHIVALRY = "activateChivalry";
    private static final String KEY_ACTIVATE_OVERCHARGE = "activateOvercharge";
    private static final String KEY_ACTIVATE_MYSTIC_MIGHT = "activateMysticMight";
    private static final String KEY_ACTIVATE_AUGURY = "activateAugury";
    private static final String KEY_ACTIVATE_RIGOUR = "activateRigour";
    private static final String KEY_ACTIVATE_EAGLE_EYE = "activateEagleEye";
    private static final String KEY_ACTIVATE_OVERPOWERING_FORCE = "activateOverpoweringForce";
    private static final String KEY_ACTIVATE_ANGUISH = "activateAnguish";
    private static final String KEY_ACTIVATE_TURMOIL = "activateTurmoil";
    private static final String KEY_ACTIVATE_TORMENT = "activateTorment";
    private static final String KEY_ACTIVATE_SORROW_CURSE = "activateSorrowCurse";
    private static final String KEY_ACTIVATE_MALEVOLENCE = "activateMalevolence";
    private static final String KEY_ACTIVATE_DESOLATION = "activateDesolation";
    private static final String KEY_ACTIVATE_AFFLICTION = "activateAffliction";
    private static final String KEY_ACTIVATE_RUINATION = "activateRuination";
    private static final String KEY_USE_SURGE = "useSurge";
    private static final String KEY_ACTIVATE_SOUL_SPLIT = "activateSoulSplit";
    private static final String KEY_ACTIVATE_OVERLOADS = "activateOverloads";
    private static final String KEY_USE_FOOD = "useFood";
    private static final String KEY_USE_EXCALIBUR = "useExcalibur";
    private static final String KEY_USE_ELVENRITUALSHARD = "useElvenRitualShard";
    //private static final String KEY_USE_SUPER_POTIONS = "activateSuperPotions";
    private static final String KEY_USE_ALTAR = "useAltar";
    private static final String KEY_USE_INVOKEDEATH = "useInvokeDeath";


    public CoaezGiantMimicGraphicsContext(ScriptConsole scriptConsole, CoaezGiantMimic script, ScriptConfig config) {
        super(scriptConsole);
        this.script = script;
        this.config = config != null ? config : new ScriptConfig("defaultConfig", true);
        loadConfig();

        this.beginner = false;
        this.medium = false;
        this.hard = false;
        this.elite = false;

        loadConfig();
    }

    public void setBeginner(boolean beginner) {
        this.beginner = beginner;
    }

    public void setMedium(boolean medium) {
        this.medium = medium;
    }

    public void setHard(boolean hard) {
        this.hard = hard;
    }

    public void setElite(boolean elite) {
        this.elite = elite;
    }

    public void saveConfig() {
        ScriptConfig config = script.getConfig();
        if (config != null) {
            config.addProperty(KEY_BEGINNER, Boolean.toString(beginner));
            config.addProperty(KEY_MEDIUM, Boolean.toString(medium));
            config.addProperty(KEY_HARD, Boolean.toString(hard));
            config.addProperty(KEY_ELITE, Boolean.toString(elite));
            config.addProperty(KEY_ACTIVATE_HAND_OF_DOOM, Boolean.toString(script.activateHandOfDoom));
            config.addProperty(KEY_ACTIVATE_ACCELERATED_DECAY, Boolean.toString(script.activateAcceleratedDecay));
            config.addProperty(KEY_ACTIVATE_PIETY, Boolean.toString(script.activatePiety));
            config.addProperty(KEY_ACTIVATE_CHIVALRY, Boolean.toString(script.activateChivalry));
            config.addProperty(KEY_ACTIVATE_OVERCHARGE, Boolean.toString(script.activateOvercharge));
            config.addProperty(KEY_ACTIVATE_MYSTIC_MIGHT, Boolean.toString(script.activateMysticMight));
            config.addProperty(KEY_ACTIVATE_AUGURY, Boolean.toString(script.activateAugury));
            config.addProperty(KEY_ACTIVATE_RIGOUR, Boolean.toString(script.activateRigour));
            config.addProperty(KEY_ACTIVATE_EAGLE_EYE, Boolean.toString(script.activateEagleEye));
            config.addProperty(KEY_ACTIVATE_OVERPOWERING_FORCE, Boolean.toString(script.activateOverpoweringForce));
            config.addProperty(KEY_ACTIVATE_ANGUISH, Boolean.toString(script.activateAnguish));
            config.addProperty(KEY_ACTIVATE_TURMOIL, Boolean.toString(script.activateTurmoil));
            config.addProperty(KEY_ACTIVATE_TORMENT, Boolean.toString(script.activateTorment));
            config.addProperty(KEY_ACTIVATE_SORROW_CURSE, Boolean.toString(script.activateSorrowCurse));
            config.addProperty(KEY_ACTIVATE_MALEVOLENCE, Boolean.toString(script.activateMalevolence));
            config.addProperty(KEY_ACTIVATE_DESOLATION, Boolean.toString(script.activateDesolation));
            config.addProperty(KEY_ACTIVATE_AFFLICTION, Boolean.toString(script.activateAffliction));
            config.addProperty(KEY_ACTIVATE_RUINATION, Boolean.toString(script.activateRuination));
            config.addProperty(KEY_USE_SURGE, Boolean.toString(script.useSurge));
            config.addProperty(KEY_ACTIVATE_SOUL_SPLIT, Boolean.toString(script.activateSoulSplit));
            config.addProperty(KEY_ACTIVATE_OVERLOADS, Boolean.toString(script.activateOverloads));
            config.addProperty(KEY_USE_FOOD, Boolean.toString(script.useFood));
            config.addProperty(KEY_USE_EXCALIBUR, Boolean.toString(script.useExcalibur));
            config.addProperty(KEY_USE_ELVENRITUALSHARD, Boolean.toString(script.useElvenRitualShard));
            //config.addProperty(KEY_USE_SUPER_POTIONS, Boolean.toString(script.activateSuperPotions));
            config.addProperty(KEY_USE_ALTAR, Boolean.toString(script.useAltar));
            config.addProperty(KEY_USE_INVOKEDEATH, Boolean.toString(script.useInvokeDeath));

            config.save();
        }

    }

    public void loadConfig() {
        ScriptConfig config = script.getConfig();
        if (config != null) {
            config.load();
            if (config.containsKey(KEY_BEGINNER)) beginner = Boolean.parseBoolean(config.getProperty(KEY_BEGINNER));
            if (config.containsKey(KEY_MEDIUM)) medium = Boolean.parseBoolean(config.getProperty(KEY_MEDIUM));
            if (config.containsKey(KEY_HARD)) hard = Boolean.parseBoolean(config.getProperty(KEY_HARD));
            if (config.containsKey(KEY_ELITE)) elite = Boolean.parseBoolean(config.getProperty(KEY_ELITE));

            if (beginner) script.setDifficulty(CoaezGiantMimic.Difficulty.BEGINNER);
            if (medium) script.setDifficulty(CoaezGiantMimic.Difficulty.MEDIUM);
            if (hard) script.setDifficulty(CoaezGiantMimic.Difficulty.HARD);
            if (elite) script.setDifficulty(CoaezGiantMimic.Difficulty.ELITE);

            if (config.containsKey(KEY_ACTIVATE_HAND_OF_DOOM)) script.activateHandOfDoom = Boolean.parseBoolean(config.getProperty(KEY_ACTIVATE_HAND_OF_DOOM));
            if (config.containsKey(KEY_ACTIVATE_ACCELERATED_DECAY)) script.activateAcceleratedDecay = Boolean.parseBoolean(config.getProperty(KEY_ACTIVATE_ACCELERATED_DECAY));
            if (config.containsKey(KEY_ACTIVATE_PIETY)) script.activatePiety = Boolean.parseBoolean(config.getProperty(KEY_ACTIVATE_PIETY));
            if (config.containsKey(KEY_ACTIVATE_CHIVALRY)) script.activateChivalry = Boolean.parseBoolean(config.getProperty(KEY_ACTIVATE_CHIVALRY));
            if (config.containsKey(KEY_ACTIVATE_OVERCHARGE)) script.activateOvercharge = Boolean.parseBoolean(config.getProperty(KEY_ACTIVATE_OVERCHARGE));
            if (config.containsKey(KEY_ACTIVATE_MYSTIC_MIGHT)) script.activateMysticMight = Boolean.parseBoolean(config.getProperty(KEY_ACTIVATE_MYSTIC_MIGHT));
            if (config.containsKey(KEY_ACTIVATE_AUGURY)) script.activateAugury = Boolean.parseBoolean(config.getProperty(KEY_ACTIVATE_AUGURY));
            if (config.containsKey(KEY_ACTIVATE_RIGOUR)) script.activateRigour = Boolean.parseBoolean(config.getProperty(KEY_ACTIVATE_RIGOUR));
            if (config.containsKey(KEY_ACTIVATE_EAGLE_EYE)) script.activateEagleEye = Boolean.parseBoolean(config.getProperty(KEY_ACTIVATE_EAGLE_EYE));
            if (config.containsKey(KEY_ACTIVATE_OVERPOWERING_FORCE)) script.activateOverpoweringForce = Boolean.parseBoolean(config.getProperty(KEY_ACTIVATE_OVERPOWERING_FORCE));
            if (config.containsKey(KEY_ACTIVATE_ANGUISH)) script.activateAnguish = Boolean.parseBoolean(config.getProperty(KEY_ACTIVATE_ANGUISH));
            if (config.containsKey(KEY_ACTIVATE_TURMOIL)) script.activateTurmoil = Boolean.parseBoolean(config.getProperty(KEY_ACTIVATE_TURMOIL));
            if (config.containsKey(KEY_ACTIVATE_TORMENT)) script.activateTorment = Boolean.parseBoolean(config.getProperty(KEY_ACTIVATE_TORMENT));
            if (config.containsKey(KEY_ACTIVATE_SORROW_CURSE)) script.activateSorrowCurse = Boolean.parseBoolean(config.getProperty(KEY_ACTIVATE_SORROW_CURSE));
            if (config.containsKey(KEY_ACTIVATE_MALEVOLENCE)) script.activateMalevolence = Boolean.parseBoolean(config.getProperty(KEY_ACTIVATE_MALEVOLENCE));
            if (config.containsKey(KEY_ACTIVATE_DESOLATION)) script.activateDesolation = Boolean.parseBoolean(config.getProperty(KEY_ACTIVATE_DESOLATION));
            if (config.containsKey(KEY_ACTIVATE_AFFLICTION)) script.activateAffliction = Boolean.parseBoolean(config.getProperty(KEY_ACTIVATE_AFFLICTION));
            if (config.containsKey(KEY_ACTIVATE_RUINATION)) script.activateRuination = Boolean.parseBoolean(config.getProperty(KEY_ACTIVATE_RUINATION));
            if (config.containsKey(KEY_USE_SURGE)) script.useSurge = Boolean.parseBoolean(config.getProperty(KEY_USE_SURGE));
            if (config.containsKey(KEY_ACTIVATE_SOUL_SPLIT)) script.activateSoulSplit = Boolean.parseBoolean(config.getProperty(KEY_ACTIVATE_SOUL_SPLIT));
            if (config.containsKey(KEY_ACTIVATE_OVERLOADS)) script.activateOverloads = Boolean.parseBoolean(config.getProperty(KEY_ACTIVATE_OVERLOADS));
            if (config.containsKey(KEY_USE_FOOD)) script.useFood = Boolean.parseBoolean(config.getProperty(KEY_USE_FOOD));
            if (config.containsKey(KEY_USE_EXCALIBUR)) script.useExcalibur = Boolean.parseBoolean(config.getProperty(KEY_USE_EXCALIBUR));
            if (config.containsKey(KEY_USE_ELVENRITUALSHARD)) script.useElvenRitualShard = Boolean.parseBoolean(config.getProperty(KEY_USE_ELVENRITUALSHARD));
            //if (config.containsKey(KEY_USE_SUPER_POTIONS)) script.activateSuperPotions = Boolean.parseBoolean(config.getProperty(KEY_USE_SUPER_POTIONS));
            if (config.containsKey(KEY_USE_ALTAR)) script.useAltar = Boolean.parseBoolean(config.getProperty(KEY_USE_ALTAR));
            if (config.containsKey(KEY_USE_INVOKEDEATH)) script.useInvokeDeath = Boolean.parseBoolean(config.getProperty(KEY_USE_INVOKEDEATH));

        }

    }

    @Override
    public void drawSettings() {
        // Set style variables for a professional look
        ImGui.PushStyleVar(ImGuiStyleVar.WindowRounding, 5.0f);
        ImGui.PushStyleVar(ImGuiStyleVar.FrameRounding, 5.0f);
        ImGui.PushStyleVar(ImGuiStyleVar.GrabRounding, 5.0f);
        ImGui.PushStyleVar(ImGuiStyleVar.ItemSpacing, 10.0f, 10.0f);

        // Set colors for a dark background and modern theme
        ImGui.PushStyleColor(ImGuiCol.WindowBg, 0.1f, 0.1f, 0.1f, 1.0f);  // Dark background
        ImGui.PushStyleColor(ImGuiCol.FrameBg, 0.2f, 0.2f, 0.2f, 1.0f);  // Dark for frame background
        ImGui.PushStyleColor(ImGuiCol.FrameBgHovered, 0.3f, 0.3f, 0.3f, 1.0f);  // Lighter for hovered frame
        ImGui.PushStyleColor(ImGuiCol.FrameBgActive, 0.4f, 0.4f, 0.4f, 1.0f);  // Even lighter for active frame
        ImGui.PushStyleColor(ImGuiCol.TitleBg, 0.2f, 0.2f, 0.2f, 1.0f);  // Dark for title background
        ImGui.PushStyleColor(ImGuiCol.TitleBgActive, 0.3f, 0.3f, 0.3f, 1.0f);  // Dark for active title background
        ImGui.PushStyleColor(ImGuiCol.TitleBgCollapsed, 0.2f, 0.2f, 0.2f, 1.0f);  // Dark for collapsed title background
        ImGui.PushStyleColor(ImGuiCol.Button, 0.4f, 0.4f, 0.4f, 1.0f);  // Dark for buttons
        ImGui.PushStyleColor(ImGuiCol.ButtonHovered, 0.5f, 0.5f, 0.5f, 1.0f);  // Lighter for hovered buttons
        ImGui.PushStyleColor(ImGuiCol.ButtonActive, 0.6f, 0.6f, 0.6f, 1.0f);  // Even lighter for active buttons
        ImGui.PushStyleColor(ImGuiCol.CheckMark, 1.0f, 1.0f, 1.0f, 1.0f);  // White check mark
        ImGui.PushStyleColor(ImGuiCol.Text, 0.9f, 0.9f, 0.9f, 1.0f);  // Light gray text
        ImGui.PushStyleColor(ImGuiCol.TextDisabled, 0.6f, 0.6f, 0.6f, 1.0f);  // Dark gray for disabled text

        if (ImGui.Begin("Coaez Giant Mimic", ImGuiWindowFlag.None.getValue())) {
            if (ImGui.Button("Start")) {
                script.startScript();
            }
            ImGui.SameLine();
            if (ImGui.Button("Stop")) {
                script.stopScript();
            }
            ImGui.SameLine();
            if (ImGui.Button("Save Config")) {
                saveConfig();
            }
            ImGui.SameLine();
            if (ImGui.Button("Load Config")) {
                loadConfig();
            }

            ImGui.Text(String.format("Script state: %s", script.getBotState().toString()));
            long elapsedTime = script.getElapsedTime();
            ImGui.Text(String.format("Script running time: %02d:%02d:%02d",
                    elapsedTime / 3600000, (elapsedTime / 60000) % 60, (elapsedTime / 1000) % 60));
            int mimicKills = script.getMimicKillCount();
            ImGui.Text(String.format("Mimic Kills: %d", mimicKills));

            if (mimicKills > 0) {
                long averageKillTime = elapsedTime / mimicKills;
                ImGui.Text(String.format("Average Kill Time: %s", formatTime(averageKillTime)));
            } else {
                ImGui.Text("Average Kill Time: N/A");
            }

            if (ImGui.BeginTabBar("SettingsTabBar", ImGuiWindowFlag.None.getValue())) {
                // Description Tab
                if (ImGui.BeginTabItem("Description", ImGuiWindowFlag.None.getValue())) {
                    drawDescriptionWindow();
                    ImGui.EndTabItem();
                }

                // Difficulty Settings Tab
                if (ImGui.BeginTabItem("Difficulty Settings", ImGuiWindowFlag.None.getValue())) {
                    ImGui.BeginChild("DifficultySettingsChild", 0.0f, 350.0f, true, 0);

                    ImGui.Text("Choose Difficulty:");
                    boolean prevBeginner = beginner;
                    boolean prevMedium = medium;
                    boolean prevHard = hard;
                    boolean prevElite = elite;

                    beginner = ImGui.Checkbox("Beginner", beginner);
                    if (ImGui.IsItemHovered()) ImGui.SetTooltip("Set difficulty to Beginner.");
                    if (beginner && !prevBeginner) {
                        setAllFalse();
                        setBeginner(true);
                        script.setDifficulty(CoaezGiantMimic.Difficulty.BEGINNER);
                    }

                    medium = ImGui.Checkbox("Medium", medium);
                    if (ImGui.IsItemHovered()) ImGui.SetTooltip("Set difficulty to Medium.");
                    if (medium && !prevMedium) {
                        setAllFalse();
                        setMedium(true);
                        script.setDifficulty(CoaezGiantMimic.Difficulty.MEDIUM);
                    }

                    hard = ImGui.Checkbox("Hard", hard);
                    if (ImGui.IsItemHovered()) ImGui.SetTooltip("Set difficulty to Hard.");
                    if (hard && !prevHard) {
                        setAllFalse();
                        setHard(true);
                        script.setDifficulty(CoaezGiantMimic.Difficulty.HARD);
                    }

                    elite = ImGui.Checkbox("Elite", elite);
                    if (ImGui.IsItemHovered()) ImGui.SetTooltip("Set difficulty to Elite.");
                    if (elite && !prevElite) {
                        setAllFalse();
                        setElite(true);
                        script.setDifficulty(CoaezGiantMimic.Difficulty.ELITE);
                    }

                    ImGui.EndChild();
                    ImGui.EndTabItem();
                }

                // Prayer Settings Tab
                if (ImGui.BeginTabItem("Prayer Settings", ImGuiWindowFlag.None.getValue())) {
                    ImGui.BeginChild("PrayerSettingsChild", 0.0f, 350.0f, true, 0);

                    drawPrayerSettings();

                    ImGui.EndChild();
                    ImGui.EndTabItem();
                }

                // Utility Settings Tab
                if (ImGui.BeginTabItem("Utility Settings", ImGuiWindowFlag.None.getValue())) {
                    ImGui.BeginChild("UtilitySettingsChild", 0.0f, 350.0f, true, 0);

                    drawUtilitySettings();

                    ImGui.EndChild();
                    ImGui.EndTabItem();
                }

                ImGui.EndTabBar();
            }

            ImGui.End();
        }

        // Pop style variables and colors
        ImGui.PopStyleVar(4);
        ImGui.PopStyleColor(12);
    }

    private String formatTime(long timeInMillis) {
        long hours = timeInMillis / 3600000;
        long minutes = (timeInMillis / 60000) % 60;
        long seconds = (timeInMillis / 1000) % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    private void drawPrayerSettings() {
        ImGui.Columns(2, "prayerColumns", false);

        ImGui.Text("Necro Prayers");
        script.activateHandOfDoom = ImGui.Checkbox("Hand of Doom", script.activateHandOfDoom);
        script.activateAcceleratedDecay = ImGui.Checkbox("Accelerated Decay", script.activateAcceleratedDecay);

        ImGui.Text("Melee Prayers");
        script.activatePiety = ImGui.Checkbox("Piety", script.activatePiety);
        script.activateChivalry = ImGui.Checkbox("Chivalry", script.activateChivalry);

        ImGui.Text("Magic Prayers");
        script.activateOvercharge = ImGui.Checkbox("Overcharge", script.activateOvercharge);
        script.activateMysticMight = ImGui.Checkbox("Mystic Might", script.activateMysticMight);
        script.activateAugury = ImGui.Checkbox("Augury", script.activateAugury);

        ImGui.NextColumn();

        ImGui.Text("Range Prayers");
        script.activateRigour = ImGui.Checkbox("Rigour", script.activateRigour);
        script.activateEagleEye = ImGui.Checkbox("Eagle Eye", script.activateEagleEye);
        script.activateOverpoweringForce = ImGui.Checkbox("Overpowering Force", script.activateOverpoweringForce);

        ImGui.Text("Curses Prayers");
        script.activateAnguish = ImGui.Checkbox("Anguish", script.activateAnguish);
        script.activateTurmoil = ImGui.Checkbox("Turmoil", script.activateTurmoil);
        script.activateTorment = ImGui.Checkbox("Torment", script.activateTorment);
        script.activateSorrowCurse = ImGui.Checkbox("Sorrow", script.activateSorrowCurse);
        script.activateMalevolence = ImGui.Checkbox("Malevolence", script.activateMalevolence);
        script.activateDesolation = ImGui.Checkbox("Desolation", script.activateDesolation);
        script.activateAffliction = ImGui.Checkbox("Affliction", script.activateAffliction);
        script.activateRuination = ImGui.Checkbox("Ruination", script.activateRuination);

        ImGui.Columns(1, "prayerColumns", false);
    }

    private void drawUtilitySettings() {
        ImGui.Text("Utility settings");
        script.useSurge = ImGui.Checkbox("Surge", script.useSurge);
        script.activateSoulSplit = ImGui.Checkbox("Soul Split", script.activateSoulSplit);
        script.activateOverloads = ImGui.Checkbox("Overloads", script.activateOverloads);
        script.useFood = ImGui.Checkbox("Use food", script.useFood);
        script.useExcalibur = ImGui.Checkbox("Use Excalibur", script.useExcalibur);
        script.useElvenRitualShard = ImGui.Checkbox("Use Ritual Shard", script.useElvenRitualShard);
        script.useAltar = ImGui.Checkbox("Use Altar of War", script.useAltar);
        script.useInvokeDeath = ImGui.Checkbox("Use Invoke Death", script.useInvokeDeath);

        //script.activateSuperPotions = ImGui.Checkbox("Use Super Potions", script.activateSuperPotions);

    }

    private void drawDescriptionWindow() {
        ImGui.Text("Kills Giant Mimic on all difficulties.");

        ImGui.Text("Usage:");
        ImGui.Text("- Turn OFF area loot.");
        ImGui.Text("- Select Difficulty: Choose the desired difficulty setting.");
        ImGui.Text("- Configure Prayers: Set up prayer we will use.");
        ImGui.Text("- Utility Settings: Select utility options like surge, excalibur, shard etc...");
        ImGui.Text("- Save/Load Configurations: Save your settings for future use.");
        ImGui.Text("- Start the Script: Press start to begin.");

        ImGui.Text("Requirements:");
        ImGui.Text("- Combat Mode: Revolution combat.");
        ImGui.Text("- Starting Point: Begin in War's Retreat.");
        ImGui.Text("- Preset: Load a preset with necessary items (tokens, food, overloads).");
        ImGui.Text("- Action Bar Visibility: Ensure selected skills/prayers are visible on the action bar (excluding food/overloads).");
        ImGui.Text("- Food Option: Ensure food has the 'eat' option.");

        ImGui.Text("Recommended Setup for Hard/Elite Difficulty:");
        ImGui.Text("- DPS: Sufficient DPM with Revolution to defeat the Mimic within 2:30 (using t95/t90 weapons).");
        ImGui.Text("- Movement: Double surge.");
        ImGui.Text("- Utility Items: Elven ritual shard and enhanced Excalibur, unless fully maxed as it will slow us down");
    }

    private void setAllFalse() {
        setBeginner(false);
        setMedium(false);
        setHard(false);
        setElite(false);
    }

    @Override
    public void drawOverlay() {
        super.drawOverlay();
    }
    
    public class ImGuiCol {
        public static final int Text = 0;
        public static final int TextDisabled = 1;
        public static final int WindowBg = 2;
        public static final int ChildBg = 3;
        public static final int PopupBg = 4;
        public static final int Border = 5;
        public static final int BorderShadow = 6;
        public static final int FrameBg = 7;
        public static final int FrameBgHovered = 8;
        public static final int FrameBgActive = 9;
        public static final int TitleBg = 10;
        public static final int TitleBgActive = 11;
        public static final int TitleBgCollapsed = 12;
        public static final int MenuBarBg = 13;
        public static final int ScrollbarBg = 14;
        public static final int ScrollbarGrab = 15;
        public static final int ScrollbarGrabHovered = 16;
        public static final int ScrollbarGrabActive = 17;
        public static final int CheckMark = 18;
        public static final int SliderGrab = 19;
        public static final int SliderGrabActive = 20;
        public static final int Button = 21;
        public static final int ButtonHovered = 22;
        public static final int ButtonActive = 23;
        public static final int Header = 24;
        public static final int HeaderHovered = 25;
        public static final int HeaderActive = 26;
        public static final int Separator = 27;
        public static final int SeparatorHovered = 28;
        public static final int SeparatorActive = 29;
        public static final int ResizeGrip = 30;
        public static final int ResizeGripHovered = 31;
        public static final int ResizeGripActive = 32;
        public static final int Tab = 33;
        public static final int TabHovered = 34;
        public static final int TabActive = 35;
        public static final int TabUnfocused = 36;
        public static final int TabUnfocusedActive = 37;
        public static final int DockingPreview = 38;
        public static final int DockingEmptyBg = 39;
        public static final int PlotLines = 40;
        public static final int PlotLinesHovered = 41;
        public static final int PlotHistogram = 42;
        public static final int PlotHistogramHovered = 43;
        public static final int TableHeaderBg = 44;
        public static final int TableBorderStrong = 45;
        public static final int TableBorderLight = 46;
        public static final int TableRowBg = 47;
        public static final int TableRowBgAlt = 48;
        public static final int TextSelectedBg = 49;
        public static final int DragDropTarget = 50;
        public static final int NavHighlight = 51;
        public static final int NavWindowingHighlight = 52;
        public static final int NavWindowingDimBg = 53;
        public static final int ModalWindowDimBg = 54;
    }

    public class ImGuiStyleVar {
        public static final int Alpha = 0;
        public static final int DisabledAlpha = 1;
        public static final int WindowPadding = 2;
        public static final int WindowRounding = 3;
        public static final int WindowBorderSize = 4;
        public static final int WindowMinSize = 5;
        public static final int WindowTitleAlign = 6;
        public static final int ChildRounding = 7;
        public static final int ChildBorderSize = 8;
        public static final int PopupRounding = 9;
        public static final int PopupBorderSize = 10;
        public static final int FramePadding = 11;
        public static final int FrameRounding = 12;
        public static final int FrameBorderSize = 13;
        public static final int ItemSpacing = 14;
        public static final int ItemInnerSpacing = 15;
        public static final int IndentSpacing = 16;
        public static final int CellPadding = 17;
        public static final int ScrollbarSize = 18;
        public static final int ScrollbarRounding = 19;
        public static final int GrabMinSize = 20;
        public static final int GrabRounding = 21;
        public static final int TabRounding = 22;
        public static final int ButtonTextAlign = 23;
        public static final int SelectableTextAlign = 24;
        public static final int COUNT = 25;
    }
}
