package com.github.ThomasVDP.hypixelapimod.commands;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

public class QuickStartCommand extends CommandBase
{
    @Override
    public String getCommandName ()
    {
        return "hpapiquickstart";
    }

    @Override
    public int getRequiredPermissionLevel ()
    {
        return 0;
    }

    @Override
    public String getCommandUsage (ICommandSender sender)
    {
        return "/hpapiquickstart";
    }

    @Override
    public void processCommand (ICommandSender sender, String[] args) throws CommandException
    {
        sender.addChatMessage(new ChatComponentText("We will now automatically ask Hypixel for an API key!"));
        Minecraft.getMinecraft().thePlayer.sendChatMessage("/api new");
    }
}
