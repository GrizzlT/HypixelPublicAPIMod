package com.tvdp.hypixelapimod.commands;

import com.tvdp.hypixelapimod.HypixelPublicAPIMod;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.config.Configuration;

public class SaveApiKeyCommand extends CommandBase
{
    @Override
    public String getCommandName() {
        return "hpsaveapikey";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/hpsaveapikey";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length > 0) {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Too many arguments in command call!"));
            return;
        }

        HypixelPublicAPIMod stats = HypixelPublicAPIMod.instance;
        if (!stats.apiKeySet) {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "No API key has been set so far!"));
            return;
        }

        try {
            HypixelPublicAPIMod.config.get(Configuration.CATEGORY_GENERAL, "apiKey", "").set(stats.apiKey.toString());
            HypixelPublicAPIMod.config.save();
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "Successfully stored API key in config file!"));
        } catch (Exception e) {
            sender.addChatMessage(new ChatComponentText("Unexpected error: APIkey was not set!"));
        }
    }
}
