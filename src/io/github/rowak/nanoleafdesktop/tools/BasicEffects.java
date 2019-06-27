package io.github.rowak.nanoleafdesktop.tools;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import io.github.rowak.Aurora;
import io.github.rowak.Color;
import io.github.rowak.Effect;
import io.github.rowak.Frame;
import io.github.rowak.StatusCodeException;
import io.github.rowak.StatusCodeException.UnauthorizedException;
import io.github.rowak.effectbuilder.StaticEffectBuilder;
import io.github.rowak.nanoleafdesktop.Main;

public class BasicEffects
{
	public static final Object[][] BUILTIN_EFFECTS =
		{
			new Object[]{"Warm White", 40, 80},
			new Object[]{"Reading Light", 48, 48},
			new Object[]{"Daylight", 50, 26}
		};
	
	public static void initializeBasicEffects()
	{
		PropertyManager manager = new PropertyManager(Main.PROPERTIES_FILEPATH);
		if (manager.getProperty("basicEffects") == null)
		{
			JSONArray arr = new JSONArray();
			for (int i = 0; i < BUILTIN_EFFECTS.length; i++)
			{
				JSONObject obj = new JSONObject();
				obj.put("name", BUILTIN_EFFECTS[i][0]);
				obj.put("hue", (int)BUILTIN_EFFECTS[i][1]);
				obj.put("sat", BUILTIN_EFFECTS[i][2]);
				arr.put(obj);
			}
			manager.setProperty("basicEffects", arr.toString());
		}
	}
	
	public static void addBasicEffect(String name, int hue, int sat)
	{
		initializeBasicEffects();
		
		PropertyManager manager = new PropertyManager(Main.PROPERTIES_FILEPATH);
		String saved = manager.getProperty("basicEffects");
		if (saved == null)
		{
			saved = "";
		}
		JSONArray arr = new JSONArray(saved);
		
		JSONObject obj = new JSONObject();
		obj.put("name", name);
		obj.put("hut", hue);
		obj.put("sat", sat);
		
		for (int i = 0; i < arr.length(); i++)
		{
			if (arr.getJSONObject(i).getString("name").equals(name))
			{
				// update effect with same name
				arr.put(i, obj);
				manager.setProperty("basicEffects", saved.toString());
				return;
			}
		}
		
		// add new effect
		arr.put(obj);
		manager.setProperty("basicEffects", saved.toString());
	}
	
	public static void removeBasicEffect(String name)
	{
		initializeBasicEffects();
		
		PropertyManager manager = new PropertyManager(Main.PROPERTIES_FILEPATH);
		String saved = manager.getProperty("basicEffects");
		if (saved == null)
		{
			saved = "";
		}
		JSONArray arr = new JSONArray(saved);
		
		for (int i = 0; i < arr.length(); i++)
		{
			if (arr.getJSONObject(i).getString("name").equals(name))
			{
				arr.remove(i);
				manager.setProperty("basicEffects", arr.toString());
				return;
			}
		}
	}
	
	public static void renameBasicEffect(String name, String newName)
	{
		initializeBasicEffects();
		
		PropertyManager manager = new PropertyManager(Main.PROPERTIES_FILEPATH);
		String saved = manager.getProperty("basicEffects");
		if (saved == null)
		{
			saved = "";
		}
		JSONArray arr = new JSONArray(saved);
		
		for (int i = 0; i < arr.length(); i++)
		{
			if (arr.getJSONObject(i).getString("name").equals(name))
			{
				arr.getJSONObject(i).put("name", newName);
				manager.setProperty("basicEffects", arr.toString());
			}
		}
	}
	
	public static List<List<Effect>> getBasicEffects(Aurora[] devices)
			throws UnauthorizedException, StatusCodeException
	{
		List<List<Effect>> basicEffects = new ArrayList<List<Effect>>();
		for (int i = 0; i < devices.length; i++)
		{
			basicEffects.add(new ArrayList<Effect>());
		}
		
		PropertyManager manager = new PropertyManager(Main.PROPERTIES_FILEPATH);
		String strEffects = manager.getProperty("basicEffects");
		if (strEffects != null)
		{
			JSONArray arr = new JSONArray(strEffects);
			for (int i = 0; i < arr.length(); i++)
			{
				for (int d = 0; d < devices.length; d++)
				{
					JSONObject obj = arr.getJSONObject(i);
					String name = obj.getString("name");
					int hue = obj.getInt("hue");
					int sat = obj.getInt("sat");
					basicEffects.get(d).add(getEffect(name,
							hue, sat, devices[d]));
				}
			}
		}
		return basicEffects;
	}
	
	private static Effect getEffect(String name, int hue, int sat, Aurora device)
			throws UnauthorizedException, StatusCodeException
	{
		Color color = Color.fromHSB(hue, sat,
				device.state().getBrightness());
		return new StaticEffectBuilder(device)
				.setAllPanels(new Frame(color, 2))
				.build(name);
	}
}