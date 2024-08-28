package kgg.translator.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public interface CommandConfigurable {
    void register(LiteralArgumentBuilder<FabricClientCommandSource> node);
}
