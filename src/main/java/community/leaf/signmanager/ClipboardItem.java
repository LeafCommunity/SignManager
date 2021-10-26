/*
 * Copyright © 2016-2021, RezzedUp <https://github.com/LeafCommunity/SignManager>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package community.leaf.signmanager;

import community.leaf.signmanager.util.Components;
import community.leaf.signmanager.util.Keys;
import community.leaf.signmanager.util.MinecraftVersions;
import community.leaf.signmanager.util.persistence.JsonPersistentDataContainer;
import community.leaf.signmanager.util.persistence.Persistent;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import pl.tlinkowski.annotation.basic.NullOr;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ClipboardItem
{
	private static final NamespacedKey CLIPBOARD_KEY = Keys.signManager("clipboard");
	
	private static final NamespacedKey TEMPORARY_KEY = Keys.signManager("temporary");
	
	private static final NamespacedKey COPY_KEY = Keys.signManager("copy");
	
	private static final Map<CopyData.Version, CopyData> DATA_BY_VERSION = new EnumMap<>(CopyData.Version.class);
	
	static
	{
		DATA_BY_VERSION.put(CopyData.Version.JSON, new CopyData.JsonCopyData());
		DATA_BY_VERSION.put(CopyData.Version.NBT, new CopyData.NbtCopyData());
	}
	
	public static Optional<ClipboardItem> of(@NullOr ItemStack item)
	{
		if (item == null) { return Optional.empty(); }
		if (!Tag.SIGNS.isTagged(item.getType())) { return Optional.empty(); }
		
		@NullOr ItemMeta meta = item.getItemMeta();
		if (meta == null) { return Optional.empty(); }
		
		PersistentDataContainer data = meta.getPersistentDataContainer();
		
		CopyData.Version version = data.getOrDefault(CLIPBOARD_KEY, CopyData.Version.TYPE, CopyData.Version.supported());
		boolean isTemporary = data.getOrDefault(TEMPORARY_KEY, Persistent.Types.BOOLEAN, false);
		
		return Optional.of(new ClipboardItem(item, DATA_BY_VERSION.get(version), isTemporary));
	}
	
	public static Optional<ClipboardItem> of(Player player, EquipmentSlot hand)
	{
		if (hand == EquipmentSlot.HAND) { return of(player.getInventory().getItemInMainHand()); }
		if (hand == EquipmentSlot.OFF_HAND) { return of(player.getInventory().getItemInOffHand()); }
		return Optional.empty();
	}
	
	private final ItemStack item;
	private final CopyData storage;
	private final boolean isTemporary;
	
	public ClipboardItem(ItemStack item, CopyData storage, boolean isTemporary)
	{
		this.item = item;
		this.storage = storage;
		this.isTemporary = isTemporary;
	}
	
	public ItemStack item() { return item; }
	
	public CopyData storage() { return storage; }
	
	public boolean isTemporary() { return isTemporary; }
	
	private ItemMeta meta()
	{
		@NullOr ItemMeta meta = item.getItemMeta();
		if (meta != null) { return meta; }
		throw new NullPointerException("Item meta is null: " + item);
	}
	
	public Optional<CopiedSign> copiedSign()
	{
		return Optional.ofNullable(storage.getCopiedSign(meta().getPersistentDataContainer()));
	}
	
	public void storeCopyThenUpdateLore(CopiedSign copy)
	{
		ItemMeta meta = meta();
		PersistentDataContainer data = meta.getPersistentDataContainer();
		
		data.set(CLIPBOARD_KEY, CopyData.Version.TYPE, storage.version());
		if (isTemporary) { data.set(TEMPORARY_KEY, Persistent.Types.BOOLEAN, true); }
		storage.setCopiedSign(data, copy);
		
		meta.addEnchant(Enchantment.DURABILITY, 1, true);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		
		List<String> lore = new ArrayList<>();
		
		lore.add("Punch to copy or click to paste:");
		
		// Preview lines
		copy.lines().stream()
			.map(line ->
				Components.builder()
					.append("→ #" + (line.index() + 1) + ": ")
						.color(ChatColor.GRAY)
					.append(new TextComponent(line.toPreview()))
						.color(ChatColor.WHITE)
					.create()
			)
			.map(TextComponent::toLegacyText)
			.forEach(lore::add);
			
		meta.setDisplayName("Punch to copy, click to paste!");
		meta.setLore(lore);
		
		item.setItemMeta(meta);
	}
	
	public interface CopyData
	{
		enum Version
		{
			JSON,
			NBT;
			
			public static final PersistentDataType<Byte, Version> TYPE =
				Persistent.dataType(
					Byte.class,
					Version.class,
					(version) -> (byte) version.ordinal(),
					(primitive) -> Version.values()[primitive]
				);
			
			private static final Version SUPPORTED;
			
			static
			{
				SUPPORTED = (MinecraftVersions.SERVER.lessThan(MinecraftVersions.V1_16_0)) ? JSON : NBT;
			}
			
			public static Version supported() { return SUPPORTED; }
		}
		
		Version version();
		
		@NullOr
		CopiedSign getCopiedSign(PersistentDataContainer data);
		
		void setCopiedSign(PersistentDataContainer data, CopiedSign copy);
		
		class JsonCopyData implements CopyData
		{
			@Override
			public Version version() { return Version.JSON; }
			
			@Override
			public @NullOr CopiedSign getCopiedSign(PersistentDataContainer data)
			{
				@NullOr String json = data.get(COPY_KEY, Persistent.Types.STRING);
				return (json == null) ? null : JsonPersistentDataContainer.fromJsonString(CopiedSign.TYPE, json);
			}
			
			@Override
			public void setCopiedSign(PersistentDataContainer data, CopiedSign copy)
			{
				data.set(
					COPY_KEY,
					Persistent.Types.STRING,
					JsonPersistentDataContainer.of(CopiedSign.TYPE, copy).json().toString()
				);
			}
		}
		
		class NbtCopyData implements CopyData
		{
			@Override
			public Version version() { return Version.NBT; }
			
			@Override
			public @NullOr CopiedSign getCopiedSign(PersistentDataContainer data)
			{
				return data.get(COPY_KEY, CopiedSign.TYPE);
			}
			
			@Override
			public void setCopiedSign(PersistentDataContainer data, CopiedSign copy)
			{
				data.set(COPY_KEY, CopiedSign.TYPE, copy);
			}
		}
	}
}
