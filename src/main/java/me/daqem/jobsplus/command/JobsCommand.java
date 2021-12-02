package me.daqem.jobsplus.command;

import com.mojang.brigadier.CommandDispatcher;
import me.daqem.jobsplus.capability.ModCapabilityImpl;
import me.daqem.jobsplus.handlers.ChatHandler;
import me.daqem.jobsplus.handlers.LevelHandler;
import me.daqem.jobsplus.utils.JobGetters;
import me.daqem.jobsplus.utils.enums.CapType;
import me.daqem.jobsplus.utils.enums.Jobs;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

public class JobsCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("jobs").executes(context -> jobs(context.getSource())));
    }

    public static int jobs(CommandSourceStack source) {
        try {
            if (source.getEntity() instanceof Player player) {
                player.getCapability(ModCapabilityImpl.MOD_CAPABILITY).ifPresent(handler -> {
                    List<Jobs> enabledJobs = new ArrayList<>();
                    ChatHandler.sendMessage(player, (ChatHandler.header("JOBS")));
                    for (Jobs job : Jobs.values()) {
                        if (JobGetters.jobIsEnabled(player, job)) { enabledJobs.add(job); }
                    }
                    if (!enabledJobs.isEmpty()) {
                        ChatHandler.sendMessage(player, (ChatFormatting.GREEN + "Currently Performing Jobs:"));

                        for (Jobs job : enabledJobs) {
                            switch (job) {
                                case ALCHEMIST -> ChatHandler.sendMessage(player, enabledJobString("Alchemist      ", handler.getAlchemist(), JobGetters.getJobLevel(player, job)));
                                case BUILDER -> ChatHandler.sendMessage(player, enabledJobString("Builder        ", handler.getBuilder(), JobGetters.getJobLevel(player, job)));
                                case BUTCHER -> ChatHandler.sendMessage(player, enabledJobString("Butcher       ", handler.getButcher(), JobGetters.getJobLevel(player, job)));
                                case CRAFTSMAN -> ChatHandler.sendMessage(player, enabledJobString("Craftsman    ", handler.getCraftsman(), JobGetters.getJobLevel(player, job)));
                                case DIGGER -> ChatHandler.sendMessage(player, enabledJobString("Digger         ", handler.getDigger(), JobGetters.getJobLevel(player, job)));
                                case ENCHANTER -> ChatHandler.sendMessage(player, enabledJobString("Enchanter    ", handler.getEnchanter(), JobGetters.getJobLevel(player, job)));
                                case FARMER -> ChatHandler.sendMessage(player, enabledJobString("Farmer        ", handler.getFarmer(), JobGetters.getJobLevel(player, job)));
                                case FISHERMAN -> ChatHandler.sendMessage(player, enabledJobString("Fisherman    ", handler.getFisherman(), JobGetters.getJobLevel(player, job)));
                                case HUNTER -> ChatHandler.sendMessage(player, enabledJobString("Hunter        ", handler.getHunter(), JobGetters.getJobLevel(player, job)));
                                case LUMBERJACK -> ChatHandler.sendMessage(player, enabledJobString("Lumberjack  ", handler.getLumberjack(), JobGetters.getJobLevel(player, job)));
                                case MINER -> ChatHandler.sendMessage(player, enabledJobString("Miner          ", handler.getMiner(), JobGetters.getJobLevel(player, job)));
                                case SMITH -> ChatHandler.sendMessage(player, enabledJobString("Smith           ", handler.getSmith(), JobGetters.getJobLevel(player, job)));
                            }
                        }
                    }
                    if (enabledJobs.size() != 12) {
                        ChatHandler.sendMessage(player, ChatFormatting.GREEN + "\nAvailable Jobs:");
                        StringBuilder availableJobs = new StringBuilder();
                        for (Jobs job : Jobs.values()) {
                            if (!enabledJobs.contains(job)) {
                                if (availableJobs.isEmpty()) {
                                    availableJobs.append(" ").append(ChatHandler.capitalizeWord(job.toString().toLowerCase()));
                                } else {
                                    availableJobs.append(", ").append(ChatHandler.capitalizeWord(job.toString().toLowerCase()));
                                }
                            }
                        }
                        ChatHandler.sendMessage(player, ChatFormatting.GRAY + availableJobs.toString());
                    }
                });
                ChatHandler.sendMessage(player, ChatHandler.footer(4));
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return 1;
    }

    private static String enabledJobString(String job, int[] jobInfo, int level) {
        if (level == 100) {
            return ChatFormatting.DARK_GRAY + " - " + ChatFormatting.DARK_GREEN + job +
                    ChatFormatting.DARK_GRAY + "[" + ChatFormatting.GREEN + "LVL " + jobInfo[CapType.LEVEL.get()] +
                    ChatFormatting.DARK_GRAY + "] [" + ChatFormatting.GREEN + "MAX LEVEL" +
                    ChatFormatting.DARK_GRAY + "]";
        }
        return ChatFormatting.DARK_GRAY + " - " + ChatFormatting.DARK_GREEN + job +
                ChatFormatting.DARK_GRAY + "[" + ChatFormatting.GREEN + "LVL " + jobInfo[CapType.LEVEL.get()] +
                ChatFormatting.DARK_GRAY + "] [" + ChatFormatting.GREEN + "EXP " + jobInfo[CapType.EXP.get()] +
                ChatFormatting.DARK_GRAY + "/" + ChatFormatting.DARK_GREEN + LevelHandler.calcExp(jobInfo[CapType.LEVEL.get()]) +
                ChatFormatting.DARK_GRAY + "]";
    }
}
