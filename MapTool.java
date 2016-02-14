package com.netease.module.util;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sourceforge.jeval.EvaluationException;

import org.apache.commons.lang3.StringUtils;

import com.sun.corba.se.impl.io.OptionalDataException;

/**
 * Map工具
 * 
 * @author Administrator
 * 
 */
public class MapTool
{
	/**
	 * 从data中检索满足条件的数据。
	 * 
	 * @param input
	 *            检索条件
	 * @param output
	 *            检索结果需要的字段
	 * @param a
	 *            数据
	 * @return
	 */
	public static List<Map> queryFromMap(Map<?, ?> input, Map<?, Map> data)
	{
		return queryFromMap(input, data, null, false, new ArrayList());
	}

	public static List<Map> queryFromMap(Map<?, ?> input, Map<?, Map> data, List listRoutes)
	{
		return queryFromMap(input, data, null, false, listRoutes);
	}

	/**
	 * 
	 * @param input
	 * @param data
	 * @param location
	 * @param ifAddCon2Ret
	 *            是否把条件加到结果里
	 * @return
	 */
	public static List<Map> queryFromMap(Map<?, ?> input, Map<?, Map> data, String location, boolean ifAddCon2Ret,
			List listRoutes)
	{
		if (null == data || null == data.entrySet())
		{
			return null;
		}
		List<Map> output = new ArrayList<Map>();

		Iterator<?> itr = data.entrySet().iterator();
		while (itr.hasNext())
		{
			Entry entry = (Entry) itr.next();
			Map beingSearched = null;
			if (null == location)
			{
				beingSearched = (Map) entry.getValue();
			}
			else
			{
				beingSearched = (Map) ((Map) entry.getValue()).get(location);
			}
			List listRoute = null;
			if (null != listRoutes)
			{
				listRoute = new ArrayList();
			}
			boolean isSatisfied = ifEntrySatisfyInput(beingSearched, input, listRoute);
			if (true == isSatisfied)
			{
				Map value = (Map) entry.getValue();
				Map ret = null;
				try
				{
					ret = (Map) CommonUtil.deepClone(value);
				}
				catch (OptionalDataException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch (ClassNotFoundException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (null != listRoutes)
				{
					listRoute.add(0, entry.getKey());
					listRoutes.add(listRoute);
				}
				if (null != input && true == ifAddCon2Ret)
				{
					ret.putAll(input);
				}
				output.add(ret);
			}
		}
		return output;
	}

	/**
	 * 参数退化版。 output只有一个的情况
	 * 
	 * @param input
	 * @param output
	 * @param data
	 * @return
	 */
	public static List<Map> queryFromMap(Map<Object, ?> input, String output, Map<Object, Map> data)
	{
		Map map = new HashMap();
		map.put("output", true);
		return queryFromMap(input, data);
	}

	/**
	 * 参数退化版。 output用String[]表示
	 * 
	 * @param input
	 * @param output
	 * @param data
	 * @return
	 */
	public static List<Map> queryFromMap(Map<Object, ?> input, String[] output, Map<Object, Map> data)
	{
		Map map = new HashMap();
		for (int i = 0; i < output.length; i++)
		{
			map.put(output[i], true);
		}

		return queryFromMap(input, data);
	}

	/**
	 * 参数退化版。 output用List<String>表示
	 * 
	 * @param input
	 * @param output
	 * @param data
	 * @return
	 */
	public static List<Map> queryFromMap(Map<Object, ?> input, List<String> output, Map<Object, Map> data)
	{
		Map map = new HashMap();
		for (int i = 0; i < output.size(); i++)
		{
			map.put(output.get(i), true);
		}

		return queryFromMap(input, data);
	}

	/**
	 * 检查一个Map.Entry（暂时只支持一个entry是一个Map,不支持Object）是否满足input条件
	 * 
	 * @param entry
	 * @param input
	 * @return
	 */
	private static boolean ifEntrySatisfyInput(final Map map, Map<?, ?> input, List listRoute)
	{
		if (null == input)
		{
			return true;
		}
		if (null == map)
		{
			return true;
		}
		Map subMap = map;

		Iterator itr = input.entrySet().iterator();
		while (itr.hasNext())
		{
			Entry condition = (Entry) itr.next();
			Object key = condition.getKey();
			Object value = condition.getValue();

			//**_List
			if (key instanceof String && ((String) key).endsWith("_List"))
			{
				key = ((String) key).substring(0, ((String) key).length() - 5);
			}
			// 多层结构1： 用.或_表示
			while (key instanceof String && ((String) key).indexOf(".") >= 0)
			{
				String[] keys = ((String) key).split("\\.");
				subMap = (Map) map.get(keys[0]);
				if (null == subMap)
				{// 不存在该属性时认为满足条件
					return true;
				}
				key = keys[1];
			}
			// 多层结构2：多层用map表示,并且data的对应数据是List.匹配List中的任何一个即认为整体匹配
			if (value instanceof Map && map.get(key) instanceof List)
			{
				List list = (List) map.get(key);
				int i = 0;
				for (; i < list.size(); i++)
				{
					listRoute.add(key + "[" + i + "]");
					subMap = (Map) list.get(i);
					if (ifEntrySatisfyInput(subMap, (Map) value, listRoute))
					{
						break;
					}
				}
				if (i < list.size())
				{
					continue;
				}// 有匹配的item
				listRoute.remove(listRoute.size() - 1);
				return false;// 一个都不匹配
			}
			// 多层结构3：多层用map表示,并且data的对应数据是从List转换来的Map（为了索引!）.匹配Map中的任何一个即认为整体匹配
			if (value instanceof Map && key instanceof String && "*".equalsIgnoreCase((String) key))
			{
				Iterator mapItr = map.entrySet().iterator();
				boolean isSatisfy = false;
				while (mapItr.hasNext())
				{
					Entry entry = (Entry) mapItr.next();
					listRoute.add(entry.getKey());
					if (!(entry.getValue() instanceof Map))
					{
						listRoute.remove(listRoute.size() - 1);
						return false;// 类型不匹配
					}
					subMap = (Map) entry.getValue();
					if (ifEntrySatisfyInput(subMap, (Map) value, listRoute))
					{
						isSatisfy = true;
						break;
					}
					listRoute.remove(listRoute.size() - 1);
				}
				if (true == isSatisfy)
				{
					continue;
				}// 有匹配的item
					//	listRoute.remove(listRoute.size() - 1);
				return false;// 一个都不匹配
			}
			// 多层结构4：多层用map表示,并且data的对应数据是Map
			if (null == map.get(key) || (value instanceof Map && map.get(key) instanceof Map))
			{
				listRoute.add(key);
				subMap = (Map) map.get(key);
				if (null == subMap)
				{// 不存在该属性时认为满足条件
					//listRoute.remove(listRoute.size() - 1);
					return true;
				}
				if (!ifEntrySatisfyInput(subMap, (Map) value, listRoute))
				{
					listRoute.remove(listRoute.size() - 1);
					return false;
				}
				continue;
			}

			// data没有指定属性时认为满足条件
			Object mapVal = map.get(key);
			if (null == map.get(key))
			{// 不存在该属性时认为满足条件
				listRoute.add(key);
				continue;
			}
			listRoute.add(key);
			if (!CommonUtil.compareObject(subMap.get(key), value))
			{
				//listRoute.add(key);
				listRoute.remove(listRoute.size() - 1);
				return false;
			}
		}

		//listRoute.remove(listRoute.size() - 1);
		return true;
	}

	public static Map dotMap2StandardMap(Map args)
	{
		Map ret = new LinkedHashMap();
		Iterator<Entry> itr = args.entrySet().iterator();
		while (itr.hasNext())
		{
			Entry entry = (Entry) itr.next();
			String key = (String) entry.getKey();
			Object value = entry.getValue();
			put(key, value, ret);
		}
		return ret;
	}

	public static Map put(String dotKey, Object value, Map map)
	{
		String[] keys = (String[]) dotKey.split("\\.", 2);
		// 如果没有.了...
		if (1 == keys.length)
		{
			map.put(dotKey, value);
		}
		else
		{// 如果还有.
			Map subMap = (Map) map.get(keys[0]);
			if (null == subMap)
			{
				subMap = new HashMap();
				map.put(keys[0], subMap);
			}
			put(keys[1], value, subMap);
		}
		return map;
	}

	/**
	 * 把来自页面的单层次的参数列表转换为多层次的map结构 例：request.get("policy.applicant.name")="鱼人"
	 * 转换为 map.get("policy").get("applicant").get("name")="鱼人" 简单实现以后再改
	 */
	public static Map pageParam2NativeCalFee(Map args)
	{
		if (null == args)
		{
			return null;
		}

		Iterator<Entry> itr = args.entrySet().iterator();
		Map feeMap = new TreeMap();
		while (itr.hasNext())
		{
			Entry entry = itr.next();
			String key = (String) entry.getKey();
			if (!key.startsWith("fee.") && !key.startsWith("fee_"))
			{
				continue;
			}

			String[] labels = key.split("\\.|_");
			if (labels.length == 2)
			{// 非保障利益，key结构为fee.xml标签名
				feeMap.put(labels[1], entry.getValue());
				// 如果是s,ssdda,sdas这种格式...
				if (labels[1].endsWith("List"))
				{
					String[] values = ((String) entry.getValue()).split("\\,");
					feeMap.put(labels[1], Arrays.asList(values));
				}
			}
			else if (labels.length == 3)
			{
				Map subMap = (Map) feeMap.get(labels[1]);
				// fee.clauseList[i].*** 特殊处理
				List clauseList = (List) feeMap.get("clauseList");
				if (null == clauseList)
				{
					clauseList = new ArrayList();
					feeMap.put("clauseList", clauseList);
				}
				BigDecimal insuredAmount = BigDecimal.ZERO;
				if (entry.getValue() != null && StringUtils.isNotBlank((String) entry.getValue()))
				{
					insuredAmount = new BigDecimal((String) entry.getValue());
				}
				if (insuredAmount.compareTo(BigDecimal.ZERO) > 0)
				{
					// 添加
					Map clauseMap = new TreeMap();
					clauseMap.put("productNumber", labels[2]);
					clauseMap.put("insuredAmount", (String) entry.getValue());
					clauseList.add(clauseMap);
				}
			}
			else
			{
			}//if...else if...else
		}//while

		return feeMap;
	}

	@SuppressWarnings("unchecked")
	public static Map pageParam2NativePolicy(Map args)
	{
		Iterator<Entry> itr = args.entrySet().iterator();
		Map policyMap = new TreeMap();
		while (itr.hasNext())
		{
			Entry entry = itr.next();
			String key = (String) entry.getKey();
			if (!key.startsWith("policy.") && !key.startsWith("policy_"))
			{
				continue;
			}

			String[] labels = key.split("\\.|_");
			if (labels.length == 2)
			{// 非List型节点，key结构为policy.xxx.xxxx
				policyMap.put(labels[1], entry.getValue());
			}
			else if (labels.length == 3)
			{// 保障利益，key结构为policy./insurant/benefit/question.保障利益名
				Map subMap = (Map) policyMap.get(labels[1]);
				if (!"insurant".equalsIgnoreCase(labels[1]) && !"benefit".equalsIgnoreCase(labels[1])
						&& !"question".equalsIgnoreCase(labels[1]))
				{
					if (null == subMap)
					{
						subMap = new TreeMap();
						policyMap.put(labels[1], subMap);
					}
					subMap.put(labels[2], entry.getValue());
					continue;
				}

				if ("question".equalsIgnoreCase(labels[1]))
				{
					// policy.questionList[i].*** 特殊处理
					List questionList = (List) policyMap.get("questionList");
					if (null == questionList)
					{
						questionList = new ArrayList();
						policyMap.put("questionList", questionList);
					}

					// 添加
					Map questionMap = new TreeMap();

					questionMap.put("id", labels[2]);
					questionMap.put("answer", (String) entry.getValue());
					//questionMap.put("serialNum", value)
					questionList.add(questionMap);
					continue;
				}
				// policy.insurantList/benefitList[i].*** 特殊处理
				List itemList = (List) policyMap.get(labels[1] + "List");
				if (null == itemList)
				{
					itemList = new ArrayList();
					policyMap.put(labels[1] + "List", itemList);
				}

				// 如果policy.insurant/benefit/question.X只有1个元素，也转换为队列
				String[] elements = null;
				if (entry.getValue() instanceof String)
				{
					elements = new String[1];
					elements[0] = (String) entry.getValue();
				}
				else
				{
					elements = (String[]) entry.getValue();
				}

				// 添加
				for (int i = 0; i < elements.length; i++)
				{
					Map itemMap = null;
					if (i >= itemList.size())
					{
						itemMap = new TreeMap();
						itemList.add(itemMap);
					}
					else
					{
						itemMap = (Map) itemList.get(i);
					}
					itemMap.put(labels[2], elements[i]);
				}
			}
			else
			{
			}//if...else if...else
		}//while
		/**
		 * 如果没有被保人则将一个空的list放入map，防止页面报错
		 */
		List insurantList = (List) policyMap.get("insurantList");
		if (null == insurantList && policyMap.get("applicant") != null)
		{
			insurantList = new ArrayList();
			Map insurant = new TreeMap((Map) policyMap.get("applicant"));
			insurant.put("relation", "1");// 被保人和投保人是同一人，关系设置为本人
			insurantList.add(insurant);
			policyMap.put("insurantList", insurantList);
		}
		return policyMap;
	}

	/**
	 * 把List<Map>转换为Map<Map>, 以key字段为key
	 * 
	 * @param list
	 * @param key
	 */
	public static Map listToMap(List list, String key)
	{
		Map result = new LinkedHashMap();

		for (int i = 0; i < list.size(); i++)
		{
			Map dataLine = (Map) list.get(i);
			Object keyObject = dataLine.get(key);
			// dataLine.remove(key);
			if (null != key)
			{
				result.put(keyObject, dataLine);
			}
			else
			{
				result.put(i, dataLine);
			}
		}

		return result;
	}

	// list元素为
	public static List mapToList(Map map)
	{
		List result = new ArrayList();
		Iterator itr = map.entrySet().iterator();
		while (itr.hasNext())
		{
			Entry entry = (Entry) itr.next();
			Map subMap = new HashMap();
			subMap.put(entry.getKey(), entry.getValue());
			result.add(subMap);
		}
		return result;
	}

	// 略去索引
	public static List mapToList1(Map map)
	{
		List result = new ArrayList();
		Iterator itr = map.entrySet().iterator();
		while (itr.hasNext())
		{
			Entry entry = (Entry) itr.next();
			result.add(entry.getValue());
		}
		return result;
	}

	/**
	 * 把List<多属性对象>转换为Map<多属性对象>, 以key字段为key
	 * 
	 * @param list
	 * @param key
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	public static Map ObjectToMap(List list, String key) throws SecurityException, NoSuchMethodException,
			IllegalArgumentException, IllegalAccessException, InvocationTargetException
	{
		Map result = new HashMap();

		for (int i = 0; i < list.size(); i++)
		{
			Object object = list.get(i);

			// 获取key属性的get方法
			String methodName = key.substring(0, 1).toUpperCase() + key.substring(1);
			methodName = "get" + methodName;
			Method method = object.getClass().getDeclaredMethod(methodName);

			// 获取key属性的取值
			Object value = method.invoke(object);

			// dataLine.remove(key);
			result.put(value, object);
		}

		return result;
	}

	/**
	 * 把List<Map>转换为Map<List<Map>>, 以key字段为key
	 * 
	 * @param list
	 * @param key
	 */
	public static Map listToMapOfList(List list, String key)
	{
		Map result = new LinkedHashMap();

		for (int i = 0; i < list.size(); i++)
		{
			Map dataLine = (Map) list.get(i);
			Object keyObject = dataLine.get(key);

			List subList = (List) result.get(keyObject);
			if (null == subList)
			{
				subList = new ArrayList();
				result.put(keyObject, subList);
			}
			dataLine.remove(key);
			subList.add(dataLine);
		}
		return result;
	}

	public static Map valueListMaptoMap(Map m)
	{
		HashMap<String, String> sm = new HashMap<String, String>();
		Set<String> set = m.keySet();
		Iterator<String> iterator = set.iterator();
		while (iterator.hasNext())
		{
			String keyString = iterator.next();
			if (m.get(keyString) != null)
			{
				String[] value = (String[]) m.get(keyString);
				sm.put(keyString, value[0]);
			}
		}
		return sm;
	}

	/**
	 * 从List<Map>中提取出指定的列
	 * 
	 * @param listGoods3
	 * @param string
	 * @return
	 */
	public static List getPropList(List<Map> listMap, String key)
	{
		List ret = new ArrayList();
		for (int i = 0; i < listMap.size(); i++)
		{
			Map map = listMap.get(i);
			Object prop = map.get(key);
			if (null == prop)
			{
				continue;
			}
			ret.add(prop);
		}
		return ret;
	}

	/**
	 * 从List<Map>中提取出指定的列.去掉重复数据
	 * 
	 * @param listGoods3
	 * @param string
	 * @return
	 */
	public static List getPropListDistinct(List<Map> listMap, String key)
	{
		List ret = new ArrayList();
		for (int i = 0; i < listMap.size(); i++)
		{
			Map map = listMap.get(i);
			Object prop = map.get(key);
			if (null == prop)
			{
				continue;
			}
			if (!ret.contains(prop))
			{
				ret.add(prop);
			}
		}
		return ret;
	}

	/**
	 * 从List<Map>中提取出指定的列。改成所有类型通用
	 * 
	 * @param listGoods3
	 * @param string
	 * @param ignore
	 *            忽略value末尾的ingore个字符
	 * @return
	 */
	public static List getPropListBigDecimal(List<Map> listMap, String key, int ignore)
	{
		List ret = new ArrayList();
		for (int i = 0; i < listMap.size(); i++)
		{
			Map map = listMap.get(i);
			String propStr = map.get(key).toString();
			String prop = map.get(key).toString().substring(0, propStr.length() - ignore);
			if (null == prop)
			{
				continue;
			}
			ret.add(new BigDecimal(prop));
		}
		return ret;
	}

	public static void mapKVCopyInside(Map m, String keyFrom, String keyTo)
	{
		if (!m.containsKey(keyFrom))
		{
			return;
		}
		m.put(keyTo, m.get(keyFrom));
	}

	public static String toString(Map m)
	{
		StringBuilder result = new StringBuilder();
		Set<String> set = m.keySet();
		Iterator<String> iterator = set.iterator();
		while (iterator.hasNext())
		{
			String keyString = iterator.next();
			result.append("[").append(keyString).append("=").append(m.get(keyString)).append("]; ");
		}
		return result.toString();
	}

	/**
	 * 把形如key1=exp1;key2=exp2的表达式转为map;字符串中用单引号表示string, 转为map后去掉单引号
	 * 
	 * @param object
	 * @param args
	 * @return
	 * @throws EvaluationException
	 */
	public static Map String2Map(String content, Map args) throws EvaluationException
	{
		Map ret = new HashMap();
		if (null == content)
		{
			return ret;
		}

		String[] exps = content.split("\\;");
		for (int i = 0; i < exps.length; i++)
		{
			String[] keyVal = exps[i].split("=");
			if (keyVal.length == 1)
			{
				ret.put(keyVal[0], null);
				continue;
			}
			LoggerUtil.debug(keyVal[1]);

			// 因单引号问题，暂不用Evaluator
			/*			Evaluator evaluator = new Evaluator();
						evaluator.setVariables(args);
						String result = evaluator.evaluate(keyVal[1]);
						if(result.trim().startsWith("'") && result.trim().startsWith("'", result.length() - 1)){
							result = result.substring(1, result.length() - 1);
						}*/
			if (keyVal[1].startsWith("#{") && keyVal[1].endsWith("}"))
			{
				String key = keyVal[1].substring(2, keyVal[1].length() - 1);
				if (!"val".equals(keyVal[0]))
				{
					ret.put(keyVal[0], args.get(key));
				}
				else
				{
					ret.put(ret.get("CODE"), args.get(key));// CONTENT字段必须先写CODE,
															// 再写val
				}
			}
			else
			{
				ret.put(keyVal[0], keyVal[1]);
			}
		}

		return ret;
	}

	/*
	 * 按http要求把key = Object[] 拆成 key = object & key = object
	 */
	public static Map splitHttpArray(Map args)
	{
		Iterator itr = args.entrySet().iterator();
		while (itr.hasNext())
		{
			Entry entry = (Entry) itr.next();
			Object key = entry.getKey();
			Object value = entry.getValue();
			if (!(value instanceof Object[]))
			{
				continue;
			}

			Object[] valueArray = (Object[]) value;
			String valueStr = (String) valueArray[0];
			for (int i = 1; i < valueArray.length; i++)
			{
				valueStr += "&" + key + "=" + valueArray[i];
			}

			args.put(key, valueStr);
		}

		return args;
	}

	/**
	 * 所有value都放到Array里
	 * 
	 * @param args
	 * @return
	 */
	public static Map allValueToArray(Map args)
	{
		List listKeyToRemove = new ArrayList();
		Iterator itr = args.entrySet().iterator();
		while (itr.hasNext())
		{
			Entry entry = (Entry) itr.next();
			Object key = entry.getKey();
			Object value = entry.getValue();
			if (value instanceof Object[])
			{
				continue;
			}
			if (null == value || "".equalsIgnoreCase((String) value))
			{
				listKeyToRemove.add(key);
				continue;
			}

			Object[] valueArray = new Object[]
			{ value };
			args.put(key, valueArray);
		}

		for (int i = 0; i < listKeyToRemove.size(); i++)
		{
			args.remove(listKeyToRemove.get(i));
		}

		return args;
	}

	/**
	 * 所有value都放到Array里，有多个值时只留第一个
	 * 
	 * @param args
	 * @return
	 */
	public static Map allValueToArray1(Map args)
	{
		List listKeyToRemove = new ArrayList();
		Iterator itr = args.entrySet().iterator();
		while (itr.hasNext())
		{
			Entry entry = (Entry) itr.next();
			Object key = entry.getKey();
			Object value = entry.getValue();
			if (value instanceof Object[])
			{
				// 找第一个非空的
				Object[] tempValue = (Object[]) value;
				int i = 0;
				for (i = 0; i < tempValue.length - 1; i++)
				{
					if (null != tempValue[i] && !"".equalsIgnoreCase(tempValue[i].toString()))
					{
						break;
					}
				}

				Object[] valueArray = new Object[]
				{ ((Object[]) value)[i] };
				args.put(key, valueArray);
				continue;
			}
			if (null == value || "".equalsIgnoreCase((String) value))
			{
				listKeyToRemove.add(key);
				continue;
			}

			Object[] valueArray = new Object[]
			{ value };
			args.put(key, valueArray);
		}

		for (int i = 0; i < listKeyToRemove.size(); i++)
		{
			args.remove(listKeyToRemove.get(i));
		}

		return args;
	}

	/**
	 * 按cols指定的列把list排序
	 * 
	 * @param lis
	 * @param cols
	 */
	public static void order(List<Map> list, String[] cols)
	{
		if (null == cols)
		{
			return;
		}
		for (int i = 0; i < cols.length; i++)
		{
			if (null == cols[i] || "".equalsIgnoreCase(cols[i]))
			{
				continue;
			}
			MapComparator mapComparator = MapComparator.getInstance(cols[i], -1);
			Collections.sort(list, mapComparator);
		}
	}

	public static Map<String, Object> jsonToMap(JSONObject json)
	{
		if (json == null)
		{
			return null;
		}
		Map<String, Object> map = new HashMap<String, Object>();
		for (Object k : json.keySet())
		{
			Object v = json.get(k);
			// 如果内层还是数组的话，继续解析
			if (v instanceof JSONArray)
			{
				List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
				Iterator<JSONObject> it = ((JSONArray) v).iterator();
				while (it.hasNext())
				{
					JSONObject json2 = it.next();
					list.add(jsonToMap(json2));
				}
				map.put(k.toString(), list);
			}
			else
			{
				map.put(k.toString(), v);
			}
		}
		return map;
	}

	static public void main(String[] args)
	{
		Map param = new HashMap();
		//param.put("fee.sssList","ddfs,fdsfsdf,sdf,dsf,sfdd,sdf,dsf");
		param.put("a.b.c", 2);
		param.put("a.c", 2);
		param.put("a.b.d.a", 2);
		param.put("a.b.d.b.f", 2);
		param.put("a.b.d.b.g", 2);

		//Map result = pageParam2NativeCalFee(param);
		Map result = MapTool.dotMap2StandardMap(param);
		System.out.println(result);
		//	System.out.println(XmlTool.mapToXml(result,"dd"));
		/*		
				Map param1 = new HashMap();
				param1.put("policy.sss.sfd",1);
				param1.put("policy_sss4_sfds",2);
				param1.put("policy.sss4_sfds",2);
				
				//Map result2 = pageParam2NativePolicy(param1);
				Map result2 = MapTool.dotMap2StandardMap(param1);
				System.out.println(result2);*/
	}
}
