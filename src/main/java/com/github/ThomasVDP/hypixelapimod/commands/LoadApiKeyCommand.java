package com.github.ThomasVDP.hypixelapimod.commands;

import com.github.ThomasVDP.hypixelapimod.HypixelPublicAPIMod;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.config.Configuration;

import java.util.UUID;

public class LoadApiKeyCommand extends CommandBase
{
    @Override
    public String getCommandName() {
        return "hploadapikey";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/hploadapikey";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length > 0) {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Too many arguments in command call!"));
            return;
        }

        HypixelPublicAPIMod hypixelPublicAPIMod = HypixelPublicAPIMod.instance;
        if (hypixelPublicAPIMod.apiKeySet) {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "You already set an API key before! (you first need to unbind it)"));
            return;
        }

        if (HypixelPublicAPIMod.config.hasKey(Configuration.CATEGORY_GENERAL, "apiKey")) {
            try {
                hypixelPublicAPIMod.setApiKey(UUID.fromString(HypixelPublicAPIMod.config.get(Configuration.CATEGORY_GENERAL, "apiKey", "").getString()));
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "Successfully loaded your API key from the config file!"));
            } catch (IllegalArgumentException e) {
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "An invalid key was found in your config file! (Might have been empty?)"));
            }
        } else {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Couldn't find an API key in your config file! (pls save one first!)"));
        }
    }
}
