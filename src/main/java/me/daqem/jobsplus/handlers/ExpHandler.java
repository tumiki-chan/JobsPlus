package me.daqem.jobsplus.handlers;

import me.daqem.jobsplus.utils.JobGetters;
import me.daqem.jobsplus.utils.JobSetters;
import me.daqem.jobsplus.utils.enums.ChatColor;
import me.daqem.jobsplus.utils.enums.Jobs;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.KeybindComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.Random;

public class ExpHandler {

    public static void addEXPLowest(Player player, Jobs job) {
        addRandomJobEXP(player, job, 0, 2);
    }

    public static void addEXPLow(Player player, Jobs job) {
        addRandomJobEXP(player, job, 1, 3);
    }

    public static void addEXPMid(Player player, Jobs job) {
        addRandomJobEXP(player, job, 2, 6);
    }

    public static void addEXPHigh(Player player, Jobs job) {
        addRandomJobEXP(player, job, 3, 9);
    }

    public static void addEXPHighest(Player player, Jobs job) {
        addRandomJobEXP(player, job, 4, 11);
    }

    public static void addEXPFishing(Player player, Jobs job) {
        addRandomJobEXP(player, job, 8, 14);
    }

    public static int getEXPLowest() {
        return getRandomJobEXP(0, 2);
    }

    public static int getEXPLow() {
        return getRandomJobEXP(1, 3);
    }

    public static int getEXPMid() {
        return getRandomJobEXP(2, 6);
    }

    public static int getEXPHigh() {
        return getRandomJobEXP(3, 9);
    }

    public static int getEXPHighest() {
        return getRandomJobEXP(4, 11);
    }

    public static int getEXPFishing() {
        return getRandomJobEXP(8, 14);
    }

    public static void addRandomJobEXP(Player player, Jobs job, int lowerBound, int upperBound) {
        Random random = new Random();
        int randomNumber = random.nextInt(upperBound - lowerBound) + lowerBound;
        addJobEXP(player, job, randomNumber);
    }

    public static int getRandomJobEXP(int lowerBound, int upperBound) {
        Random random = new Random();
        return random.nextInt(upperBound - lowerBound) + lowerBound;
    }

    public static void addJobEXP(Player player, Jobs job, int exp) {
        JobSetters.addEXP(job, player, exp);
        int maxEXP = LevelHandler.calcExp(JobGetters.getJobLevel(player, job));
        int newEXP = JobGetters.getJobEXP(player, job);
        if (newEXP >= maxEXP) {
            JobSetters.setLevel(job, player, -2);
            JobSetters.setEXP(job, player, newEXP - maxEXP);
            LevelUpHandler.handler(player, job, JobGetters.getJobLevel(player, job));
        }
        if (player instanceof ServerPlayer serverPlayer) {
            if (exp != 0) {
                serverPlayer.sendMessage(new KeybindComponent(ChatHandler.ColorizedJobName(job) + ChatColor.gray() +
                        " +" + exp + " EXP"), ChatType.GAME_INFO, player.getUUID());
            }
        }
    }
}
