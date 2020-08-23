package com.github.ThomasVDP.hypixelapimod.commands;

import com.github.ThomasVDP.hypixelapimod.HypixelPublicAPIMod;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.util.UUID;

public class SetApiKeyCommand extends CommandBase
{
    @Override
    public String getCommandName() {
        return "hpsetapikey";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/hpsetapikey <apiKey>";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length != 1)
        {
            throw new WrongUsageException("Usage: /hpsetapikey apiKey");
        } else {
            HypixelPublicAPIMod stats = HypixelPublicAPIMod.instance;
            if (stats != null) {
                if (stats.apiKeySet) {
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "You already set an API key!"));
                    return;
                }

                try {
                    stats.setApiKey(UUID.fromString(args[0]));
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "Successfully set your API key!"));
                } catch (IllegalArgumentException e) {
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Please provide a valid key!"));
                }
            }
        }
    }
}
