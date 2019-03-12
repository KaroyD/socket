package indi.dpl.server;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
*
*Description:
*@author Karoy
*Start time:2018/8/1
*Version:v0.1 2018/8/1
*
*/

public class UserMap<K,V> {

	public synchronized void removeFromMap(Object value)
	{
		for(Object mem:map.keySet())
		{
			if(map.get(mem)==value)
			{
				map.remove(mem);
				break;
			}
		}
	}
	

	public synchronized Set<V> getValueSet()
	{
		Set<V> result= new HashSet<>();
		map.forEach((key,value)->result.add(value));
		return result;
	}
	

	public synchronized K getKeyByValue(V value)
	{
		for(K key:map.keySet())
		{

			if(map.get(key).equals(value)||map.get(key).hashCode()==value.hashCode())
			{
				return key;
			}
		}
		return null;
	}
	
	public synchronized Set<K> getUserSet()
	{
		return map.keySet();
	}
	
	public synchronized V put(K key, V value)
	{
		for(V val:getValueSet())
		{
			if(val.equals(value) && val.hashCode()==value.hashCode())
			{
				throw new RuntimeException("the value: "+value+" already in the map!");
			}
		}
		return map.put(key, value);
	}
	

	public Map<K, V>map=Collections.synchronizedMap(new HashMap<>());
}
