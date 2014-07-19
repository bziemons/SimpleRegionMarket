package com.thezorro266.bukkit.srm.exceptions;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NotEnoughPermissionsException extends Exception {
	private CommandSender sender;
	private String permNode;

	public CommandSender getCommandSender() {
		return sender;
	}

	public Player getPlayer() {
		if (sender instanceof Player) {
			return (Player) sender;
		} else {
			return null;
		}
	}

	public String getPermNode() {
		return permNode;
	}

	public NotEnoughPermissionsException(CommandSender sender, String permNode) {
		super(String.format("Sender %s does not have the permission %s", sender, permNode));
		this.sender = sender;
		this.permNode = permNode;
	}
}
