package com.tvdp.hypixelapimod.commands;

import com.tvdp.hypixelapimod.HypixelPublicAPIMod;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class UnSetApiKeyCommand extends CommandBase
{
    @Override
    public String getCommandName() {
        return "hpunsetapikey";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/hpunsetapikey";
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
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "No key present to unbind!"));
            return;
        }

        stats.setApiKey(null);
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "Successfully unset API key!"));
    }
}
