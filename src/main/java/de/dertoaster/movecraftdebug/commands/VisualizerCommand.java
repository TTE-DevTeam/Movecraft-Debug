package de.dertoaster.movecraftdebug.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import de.dertoaster.movecraftdebug.CraftUtil;
import de.dertoaster.movecraftdebug.features.trackedLocationVisualizer.TrackedLocationVisualizerJob;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes
import net.countercraft.movecraft.craft.CraftManager;
import net.countercraft.movecraft.craft.PlayerCraft;
import net.kyori.adventure.text.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.arguments.ColorArgument;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class VisualizerCommand {

    static enum EOperation {
        ADD,
        REMOVE,
        COLOR
    }

    public static void register(final Commands commands) {
        commands.register(
            Commands.literal("trackedlocationvisualizer")
                    .requires(source -> source.getSender() instanceof Player)
                    .requires(source -> source.getSender().hasPermission("movecraft.debug.trackedlocations"))
                    // Toggle off / Reset
                    .executes(commandSourceStack -> {
                        CommandSender sender = commandSourceStack.getSource().getSender();
                        Component message;
                        if (sender instanceof Player player) {
                            PlayerCraft craft = CraftManager.getInstance().getCraftByPlayer(player);
                            if (craft == null) {
                                message = Component.text("You must be piloting a craft for this to work!");
                            } else {
                                message = Component.text("Visualization settings have been reset for you!");
                            }

                            TrackedLocationVisualizerJob.remove(player);
                        } else {
                            message = Component.text("Must be run as a player!");
                        }
                        sender.sendMessage(message);
                        return Command.SINGLE_SUCCESS;
                    })
                    // Operation (add / remove)
                    .then(Commands.argument("operation", StringArgumentType.word())
                            .suggests((ctx, builder) -> {
                                for (EOperation operation : EOperation.values()) {
                                    builder.suggest(operation.name().toLowerCase());
                                }
                                return builder.buildFuture();
                            })
                            .executes(context -> {
                                String argumentProvided = context.getArgument("operation", String.class);
                                EOperation operation;
                                try {
                                    operation = EOperation.valueOf(argumentProvided.toUpperCase());
                                } catch(IllegalArgumentException iae) {
                                    context.getSource().getSender().sendMessage(Component.text("Invalid option! Use add, remove or color!"));
                                }
                                return Command.SINGLE_SUCCESS;
                            })
<<<<<<< HEAD
                            .then(Commands.argument("namespacedkey", StringArgumentType.greedyString())
=======
                            .then(Commands.argument("namespacedkey", ArgumentTypes.namespacedKey())
>>>>>>> 8f92ce0eb24c1d5937cd1e9f7d9a67d421c76356
                                    .suggests((ctx, builder) -> {
                                        ctx.getSource().getSender();
                                        Player source = (Player) ctx.getSource().getSender();
                                        Set<NamespacedKey> keys = new HashSet<>();
                                        CraftUtil.getRelevantCrafts(source).forEach(craft -> {
                                            if (craft.getTrackedLocations().size() > 0 && craft.getTrackedLocations().keySet().size() > 0) {
                                                keys.addAll(craft.getTrackedLocations().keySet());
                                            }
                                        });
                                        if (keys.size() > 0) {
                                            keys.forEach(k -> {
                                                builder.suggest(k.toString());
                                            });
                                        } else {
                                            builder.suggest("No tracked locations yet!");
                                        }
                                        return builder.buildFuture();
                                    })
                                    .executes(context -> {
                                        Player source = (Player) context.getSource().getSender();

                                        EOperation operation = EOperation.valueOf(context.getArgument("operation", String.class).toUpperCase());
                                        NamespacedKey key = NamespacedKey.fromString(context.getArgument("namespacedkey", String.class));

                                        process(source, operation, key);

                                        return Command.SINGLE_SUCCESS;
                                    })
                                    .then(Commands.argument("color", ColorArgument.color())
                                            .suggests((ctx, builder) -> {
                                                for (ChatColor chatColor : ChatColor.values()) {
                                                    builder.suggest(chatColor.name().toLowerCase());
                                                }
                                                return builder.buildFuture();
                                            })
                                            .executes(context -> {
                                                Player source = (Player) context.getSource().getSender();

                                                EOperation operation = EOperation.valueOf(context.getArgument("operation", String.class).toUpperCase());
                                                NamespacedKey key = NamespacedKey.fromString(context.getArgument("namespacedkey", String.class));
                                                ChatFormatting color = context.getArgument("color", ChatFormatting.class);
                                                ChatColor convertedColor = ChatColor.getByChar(color.code);

                                                process(source, operation, key, convertedColor);

                                                return Command.SINGLE_SUCCESS;
                                            })
                                    )
                            )
                    )
                    // Key
                    // Optional color
                    .build(),
         "command to change visualizer settings for tracked locations",
            List.of("tlv")
        );
    }

    private static void process(Player source, EOperation operation, NamespacedKey key) {
        process(source, operation, key, null);
    }

    private static void process(Player source, EOperation operation, NamespacedKey key, ChatColor color) {
        switch(operation) {
            case REMOVE -> TrackedLocationVisualizerJob.getSettingsFor(source).enable(key, false);
            case ADD -> TrackedLocationVisualizerJob.getSettingsFor(source).enable(key, true);
            case COLOR -> TrackedLocationVisualizerJob.getSettingsFor(source).setHighlightColor(key, color);
        }
    }

}
