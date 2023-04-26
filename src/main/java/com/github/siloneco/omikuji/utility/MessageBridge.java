package com.github.siloneco.omikuji.utility;

import com.google.common.base.Strings;
import java.util.LinkedList;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import me.rayzr522.jsonmessage.JSONMessage;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MessageBridge {

  private JSONMessage jsonMessage;
  private LinkedList<TextComponent> textComponents;

  public static MessageBridge create() {
    if (VersionUtils.getVersion() <= 18) {
      return new MessageBridge(JSONMessage.create(), null);
    } else {
      return new MessageBridge(null, new LinkedList<>());
    }
  }

  public MessageBridge then(String text) {
    if (jsonMessage != null) {
      jsonMessage = jsonMessage.then(text);
    } else {
      TextComponent message = new TextComponent(text);
      textComponents.add(message);
    }
    return this;
  }

  public MessageBridge newline() {
    if (jsonMessage != null) {
      jsonMessage = jsonMessage.newline();
    } else {
      TextComponent message = new TextComponent("\n");
      textComponents.add(message);
    }
    return this;
  }

  public MessageBridge bar() {
    if (jsonMessage != null) {
      jsonMessage = jsonMessage.bar();
    } else {
      then(Strings.repeat("-", 53)).color(ChatColor.DARK_GRAY).strikeThought();
    }
    return this;
  }

  public MessageBridge color(ChatColor color) {
    if (jsonMessage != null) {
      jsonMessage = jsonMessage.color(color);
    } else {
      textComponents.getLast().setColor(net.md_5.bungee.api.ChatColor.of(color.name()));
    }
    return this;
  }

  public MessageBridge strikeThought() {
    if (jsonMessage != null) {
      jsonMessage = jsonMessage.style(ChatColor.STRIKETHROUGH);
    } else {
      textComponents.getLast().setStrikethrough(true);
    }
    return this;
  }

  public MessageBridge runCommand(String command) {
    if (jsonMessage != null) {
      jsonMessage = jsonMessage.runCommand(command);
    } else {
      textComponents.getLast().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
    }
    return this;
  }

  public MessageBridge suggestCommand(String command) {
    if (jsonMessage != null) {
      jsonMessage = jsonMessage.suggestCommand(command);
    } else {
      textComponents.getLast().setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command));
    }
    return this;
  }

  public void send(Player... players) {
    if (jsonMessage != null) {
      jsonMessage.send(players);
    } else {
      TextComponent[] array = textComponents.toArray(new TextComponent[textComponents.size()]);
      for (Player p : players) {
        p.spigot().sendMessage(array);
      }
    }
  }
}