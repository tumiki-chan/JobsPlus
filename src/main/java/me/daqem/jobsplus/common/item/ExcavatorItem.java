package me.daqem.jobsplus.common.item;

import me.daqem.jobsplus.Config;
import me.daqem.jobsplus.handlers.HotbarMessageHandler;
import me.daqem.jobsplus.handlers.SoundHandler;
import me.daqem.jobsplus.init.ModItems;
import me.daqem.jobsplus.utils.ChatColor;
import me.daqem.jobsplus.utils.JobGetters;
import me.daqem.jobsplus.utils.ToolFunctions;
import me.daqem.jobsplus.utils.TranslatableString;
import me.daqem.jobsplus.utils.enums.Jobs;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class ExcavatorItem extends ShovelItem {

    public ExcavatorItem(Tier tier, int attackDamage, float attackSpeed, Properties properties) {
        super(tier, attackDamage, attackSpeed, properties);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public boolean canAttackBlock(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player) {
        if (!level.isClientSide) {
            if (JobGetters.jobIsEnabled(player, Jobs.DIGGER)) {
                if (player.isCrouching()) {
                    return true;
                }
                boolean allowedToUseExcavator = false;
                Item item = player.getMainHandItem().getItem();
                int jobLevel = JobGetters.getJobLevel(player, Jobs.DIGGER);
                if (jobLevel >= Config.REQUIRED_LEVEL_DIGGERS_EXCAVATOR_LEVEL_1.get() && item == ModItems.DIGGERS_EXCAVATOR_LEVEL_1.get())
                    allowedToUseExcavator = true;
                if (jobLevel >= Config.REQUIRED_LEVEL_DIGGERS_EXCAVATOR_LEVEL_2.get() && item == ModItems.DIGGERS_EXCAVATOR_LEVEL_2.get())
                    allowedToUseExcavator = true;
                if (jobLevel >= Config.REQUIRED_LEVEL_DIGGERS_EXCAVATOR_LEVEL_3.get() && item == ModItems.DIGGERS_EXCAVATOR_LEVEL_3.get())
                    allowedToUseExcavator = true;
                if (jobLevel >= Config.REQUIRED_LEVEL_DIGGERS_EXCAVATOR_LEVEL_4.get() && item == ModItems.DIGGERS_EXCAVATOR_LEVEL_4.get())
                    allowedToUseExcavator = true;
                if (allowedToUseExcavator) {
                    if (player.getMainHandItem().getItem() instanceof ExcavatorItem) {
                        float originHardness = level.getBlockState(pos).getDestroySpeed(null, null);
                        if (player.getMainHandItem().getItem().isCorrectToolForDrops(level.getBlockState(pos))) {
                            int mode = 0;
                            if (player.getMainHandItem().getOrCreateTag().contains("mode")) {
                                mode = player.getMainHandItem().getOrCreateTag().getInt("mode");
                            }
                            ToolFunctions.breakInRadius(level, player, mode, (breakState) -> {
                                double hardness = breakState.getDestroySpeed(null, null);
                                boolean isEffective = player.getMainHandItem().isCorrectToolForDrops(breakState);
                                boolean verifyHardness = hardness < originHardness * 5 && hardness > 0;
                                return isEffective && verifyHardness;
                            }, true);
                            return true;
                        }
                    }
                } else {
                    HotbarMessageHandler.sendHotbarMessage((ServerPlayer) player, TranslatableString.get("error.magic"));
                    return true;
                }
            } else {
                HotbarMessageHandler.sendHotbarMessage((ServerPlayer) player, TranslatableString.get("error.magic"));
            }
        }
        return true;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        if (player.isShiftKeyDown() && !level.isClientSide) {
            ItemStack stack = player.getMainHandItem();
            if (stack.getItem() instanceof ExcavatorItem) {
                CompoundTag tag = stack.getOrCreateTag();
                if (tag.contains("mode")) {
                    int tagInt = tag.getInt("mode");
                    if (stack.getItem() == ModItems.DIGGERS_EXCAVATOR_LEVEL_1.get()) {
                        tag.putInt("mode", 0);
                    } else if (stack.getItem() == ModItems.DIGGERS_EXCAVATOR_LEVEL_2.get()) {
                        if (tagInt != 1) {
                            tag.putInt("mode", ++tagInt);
                        } else {
                            tag.putInt("mode", 0);
                        }
                    } else if (stack.getItem() == ModItems.DIGGERS_EXCAVATOR_LEVEL_3.get()) {
                        if (tagInt != 2) {
                            tag.putInt("mode", ++tagInt);
                        } else {
                            tag.putInt("mode", 0);
                        }
                    } else if (stack.getItem() == ModItems.DIGGERS_EXCAVATOR_LEVEL_4.get()) {
                        if (tagInt != 3) {
                            tag.putInt("mode", ++tagInt);
                        } else {
                            tag.putInt("mode", 0);
                        }
                    }
                } else {
                    tag.putInt("mode", 0);
                }
                HotbarMessageHandler.sendHotbarMessage((ServerPlayer) player, ChatColor.boldDarkGreen() + "Mode: " + ChatColor.green() + getModeString(stack));
                SoundHandler.playEXPOrbPickupSound(player, 0.7F, 1F);
            }
        }
        return super.use(level, player, hand);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level worldIn, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        if (Screen.hasShiftDown()) {
            int level = 0;
            String modes = "";
            Item item = stack.getItem();
            if (item == ModItems.DIGGERS_EXCAVATOR_LEVEL_1.get()) {
                level = Config.REQUIRED_LEVEL_DIGGERS_EXCAVATOR_LEVEL_1.get();
                modes = "3x3";
            }
            if (item == ModItems.DIGGERS_EXCAVATOR_LEVEL_2.get()) {
                level = Config.REQUIRED_LEVEL_DIGGERS_EXCAVATOR_LEVEL_2.get();
                modes = "3x3, 3x3x3";
            }
            if (item == ModItems.DIGGERS_EXCAVATOR_LEVEL_3.get()) {
                level = Config.REQUIRED_LEVEL_DIGGERS_EXCAVATOR_LEVEL_3.get();
                modes = "3x3, 3x3x3, 5x5";
            }
            if (item == ModItems.DIGGERS_EXCAVATOR_LEVEL_4.get()) {
                level = Config.REQUIRED_LEVEL_DIGGERS_EXCAVATOR_LEVEL_4.get();
                modes = "3x3, 3x3x3, 5x5, 5x5x5";
            }
            tooltip.add(Component.literal(ChatColor.boldDarkGreen() + "Requirements:"));
            tooltip.add(Component.literal(ChatColor.green() + "Job: " + ChatColor.reset() + "DIGGER"));
            tooltip.add(Component.literal(ChatColor.green() + "Job Level: " + ChatColor.reset() + level));
            tooltip.add(Component.literal(""));
            tooltip.add(Component.literal(ChatColor.boldDarkGreen() + "About:"));
            tooltip.add(Component.literal(ChatColor.green() + "Item Level: " + ChatColor.reset() + Objects.requireNonNull(stack.getItem().getDescriptionId()).replace("item.jobsplus.diggers_excavator_level_", "")));
            tooltip.add(Component.literal(ChatColor.green() + "Modes: " + ChatColor.reset() + modes));
            tooltip.add(Component.literal(""));
            tooltip.add(Component.literal(ChatColor.boldDarkGreen() + "Controls:"));
            tooltip.add(Component.literal(ChatColor.gray() + "Shift + right-click to change the mode."));
        } else {
            if (stack.getOrCreateTag().contains("mode")) {
                tooltip.add(Component.literal(ChatColor.boldDarkGreen() + "Mode: " + ChatColor.reset() + getModeString(stack)));
            }
            tooltip.add(Component.literal(ChatColor.gray() + "Hold [SHIFT] for more info."));
        }
        if (stack.isEnchanted()) {
            tooltip.add(Component.literal(""));
            tooltip.add(Component.literal(ChatColor.boldDarkGreen() + "Enchantments:"));
        }
    }

    public String getModeString(ItemStack stack) {
        if (stack.getOrCreateTag().getInt("mode") == 0) return "3x3";
        if (stack.getOrCreateTag().getInt("mode") == 1) return "3x3x3";
        if (stack.getOrCreateTag().getInt("mode") == 2) return "5x5";
        if (stack.getOrCreateTag().getInt("mode") == 3) return "5x5x5";
        return "";
    }
}
