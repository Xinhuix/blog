package com.visionvera.dao;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.visionvera.util.SerializerUtils;

import redis.clients.jedis.BinaryClient;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.SortingParams;

/**
 * Redis 单机版 使用单机版Redis，确保该类上的@Component，并且JRedisClusterDaoImpl没有存放在Spring
 * IoC容器中
 *
 */
@Component
public class JRedisSingleDaoImpl implements JRedisDao {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private JedisPool pool;

	/**
	 * 获取数据 字符串
	 * 
	 * @param key
	 * @return
	 */
	@Override
	public String get(String key) {
		String value = null;
		Jedis jedis = null;
		try {
			jedis = this.pool.getResource();
			value = jedis.get(key);
		} catch (Exception e) {
			this.logger.error("Jedis Single获取数据失败", e);
			throw new RuntimeException(e);
		} finally {
			// 返还到连接池
			this.returnResource(jedis);
		}

		return value;
	}

	/**
	 * 设置数据 字符串
	 * 
	 * @param key
	 * @return
	 */
	@Override
	public void set(String key, String value, int seconds) {
		Jedis jedis = null;
		try {
			jedis = this.pool.getResource();
			jedis.set(key, value);
			jedis.expire(key, seconds);
		} catch (Exception e) {
			this.logger.error("设置数据失败", e);
			throw new RuntimeException(e);
		} finally {
			// 返还到连接池
			this.returnResource(jedis);
		}
	}

	/**
	 * 当key不存在时，赋值
	 * 
	 * @param key
	 * @param value
	 * @param seconds
	 */
	@Override
	public void setnx(String key, String value, int seconds) {
		Jedis jedis = null;
		try {
			jedis = this.pool.getResource();
			// 第一个为key，第二个为value，第三个"NX"表示不存在时进行set，已存在则不做任何操作；第四个"EX"表示表示单位为秒；第五个为失效时间
			jedis.set(key, value, "NX", "EX", seconds);
		} catch (Exception e) {
			this.logger.error("当key不存在时赋值失败", e);
			throw new RuntimeException(e);
		} finally {
			// 返还到连接池
			this.returnResource(jedis);
		}
	}

	/**
	 * 将给定 key 的值设为 value ，并返回 key 的旧值(old value) 当 key 存在但不是字符串类型时，返回一个错误
	 * 
	 * @param key
	 * @param value
	 */
	@Override
	public String getSet(String key, String value) {
		String result = "";
		Jedis jedis = null;
		try {
			jedis = this.pool.getResource();
			result = jedis.getSet(key, value);
		} catch (Exception e) {
			this.logger.error("当key不存在时赋值失败", e);
			throw new RuntimeException(e);
		} finally {
			// 返还到连接池
			this.returnResource(jedis);
		}
		return result;
	}

	/**
	 * 自动递增，增值为1
	 * 
	 * @param key
	 */
	@Override
	public void incr(String key) {
		Jedis jedis = null;
		try {
			jedis = this.pool.getResource();
			jedis.incr(key);
		} catch (Exception e) {
			this.logger.error("自动加1失败", e);
			throw new RuntimeException(e);
		} finally {
			// 返还到连接池
			this.returnResource(jedis);
		}
	}

	/**
	 * 自定义增值
	 * 
	 * @param key
	 * @param scope
	 */
	@Override
	public void incrBy(String key, long scope) {
		Jedis jedis = null;
		try {
			jedis = this.pool.getResource();
			jedis.incrBy(key, scope);
		} catch (Exception e) {
			this.logger.error("自定义增值失败", e);
			throw new RuntimeException(e);
		} finally {
			// 返还到连接池
			returnResource(jedis);
		}
	}

	/**
	 * 自定义减值
	 * 
	 * @param key
	 * @param scope
	 */
	@Override
	public void descBy(String key, long scope) {
		Jedis jedis = null;
		try {
			jedis = this.pool.getResource();
			jedis.decrBy(key, scope);
		} catch (Exception e) {
			this.logger.error("自定义减值失败", e);
			throw new RuntimeException(e);
		} finally {
			returnResource(jedis);
		}
	}

	/**
	 * 一次性设置多个key-value
	 * 
	 * @param keysvalues
	 */
	@Override
	public void mset(String... keysvalues) {
		Jedis jedis = null;
		try {
			jedis = this.pool.getResource();
			jedis.mset(keysvalues);
		} catch (Exception e) {
			this.logger.error("一次性设置多个key-value失败", e);
			throw new RuntimeException(e);
		} finally {
			returnResource(jedis);
		}
	}

	/**
	 * 一次性获取多个值
	 * 
	 * @param keys
	 * @return
	 */
	@Override
	public List<String> mget(String... keys) {
		Jedis jedis = null;
		List<String> list = null;
		try {
			jedis = this.pool.getResource();
			list = jedis.mget(keys);
		} catch (Exception e) {
			this.logger.error("一次性获取多个值失败", e);
			throw new RuntimeException(e);
		} finally {
			returnResource(jedis);
		}
		return list;
	}

	/**
	 * 在原来基础上追加
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	@Override
	public long appendString(String key, String value) {
		Jedis jedis = null;
		long result = 0;
		try {
			jedis = this.pool.getResource();
			result = jedis.append(key, value);
		} catch (Exception e) {
			this.logger.error("在原来基础上追加值失败", e);
			throw new RuntimeException(e);
		} finally {
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 获取hash数据
	 * 
	 * @param key
	 * @param field
	 * @return
	 */
	@Override
	public String hget(String key, String field) {
		String value = null;
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			value = jedis.hget(key, field);

		} catch (Exception e) {
			logger.error("获取hash数据失败", e);
			throw new RuntimeException(e);
		} finally {
			// 返还到连接池
			returnResource(jedis);
		}

		return value;
	}

	/**
	 * 设置hash数据
	 * 
	 * @param key
	 * @param field
	 * @param value
	 * @return
	 */
	@Override
	public void hset(String key, String field, String value) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			jedis.hset(key, field, value);
		} catch (Exception e) {
			logger.error("设置hash数据失败", e);
			throw new RuntimeException(e);
		} finally {
			// 返还到连接池
			returnResource(jedis);
		}
	}

	/**
	 * 删除散列中的字段
	 * 
	 * @param key
	 * @param fields
	 */
	@Override
	public void hdel(String key, String... fields) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			jedis.hdel(key, fields);
		} catch (Exception e) {
			logger.error("删除散列中的字段失败", e);
			throw new RuntimeException(e);
		} finally {
			// 返还到连接池
			returnResource(jedis);
		}
	}

	/**
	 * 设置多个散列数据
	 * 
	 * @param key
	 * @return
	 */
	@Override
	public void hmset(String key, Map<String, String> value) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			jedis.hmset(key, value);
		} catch (Exception e) {
			logger.error("设置多个散列数据失败", e);
			throw new RuntimeException(e);
		} finally {
			// 返还到连接池
			returnResource(jedis);
		}
	}

	/**
	 * 获取多个散列数据
	 * 
	 * @param key
	 * @param fields
	 * @return
	 */
	@Override
	public List<String> hmget(String key, String... fields) {
		Jedis jedis = null;
		List<String> list = null;
		try {
			jedis = pool.getResource();
			list = jedis.hmget(key, fields);
		} catch (Exception e) {
			logger.error("获取多个散列数据失败", e);
			throw new RuntimeException(e);
		} finally {
			// 返还到连接池
			returnResource(jedis);
		}
		return list;
	}

	/**
	 * 当key中的field不存在时，给对应的key键中field字段赋值
	 * 
	 * @param key
	 * @param field
	 * @param value
	 * @return
	 */
	@Override
	public long hsetnx(String key, String field, String value) {
		Jedis jedis = null;
		long result = 0;
		try {
			jedis = pool.getResource();
			result = jedis.hsetnx(key, field, value);
		} catch (Exception e) {
			logger.error("设置散列值失败", e);
			throw new RuntimeException(e);
		} finally {
			// 返还到连接池
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 获取所有的散列信息
	 * 
	 * @param key
	 * @return Map<String, String>
	 */
	@Override
	public Map<String, String> hgetAll(String key) {
		Jedis jedis = null;
		Map<String, String> map = null;
		try {
			jedis = pool.getResource();
			map = jedis.hgetAll(key);
		} catch (Exception e) {
			logger.error("获取所有的散列信息失败", e);
			throw new RuntimeException(e);
		} finally {
			// 返还到连接池
			returnResource(jedis);
		}
		return map;
	}

	/**
	 * 判断key键中是否存在某字段
	 * 
	 * @param key
	 * @param field
	 * @return
	 */
	@Override
	public boolean hexists(String key, String field) {
		boolean result = false;
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			result = jedis.hexists(key, field);
		} catch (Exception e) {
			logger.error("判断key键中是否存在某字段失败", e);
			throw new RuntimeException(e);
		} finally {
			// 返还到连接池
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 字段值增加/减小
	 * 
	 * @param key
	 * @param field
	 * @param increment
	 */
	@Override
	public void hincyBy(String key, String field, long increment) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			jedis.hincrBy(key, field, increment);
		} catch (Exception e) {
			logger.error("字段值增加/减小失败", e);
			throw new RuntimeException(e);
		} finally {
			// 返还到连接池
			returnResource(jedis);
		}
	}

	/**
	 * 获取该key中所有的字段
	 * 
	 * @param key
	 * @return
	 */
	@Override
	public Set<String> hkeys(String key) {
		Jedis jedis = null;
		Set<String> keySet = null;
		try {
			jedis = pool.getResource();
			keySet = jedis.hkeys(key);
		} catch (Exception e) {
			logger.error("获取该key中所有的字段失败", e);
			throw new RuntimeException(e);
		} finally {
			// 返还到连接池
			returnResource(jedis);
		}
		return keySet;
	}

	/**
	 * 获取该key中字段的所有值
	 * 
	 * @param key
	 * @return
	 */
	@Override
	public List<String> hvals(String key) {
		Jedis jedis = null;
		List<String> fieldValues = null;
		try {
			jedis = pool.getResource();
			fieldValues = jedis.hvals(key);
		} catch (Exception e) {
			logger.error("获取该key中字段的所有值失败", e);
			throw new RuntimeException(e);
		} finally {
			// 返还到连接池
			returnResource(jedis);
		}
		return fieldValues;
	}

	/**
	 * 从左边添加列表信息lpush
	 * 
	 * @param key
	 * @param values
	 * @return
	 */
	@Override
	public void lpush(String key, String... values) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			jedis.lpush(key, values);
		} catch (Exception e) {
			logger.error("从左边添加列表信息lpush失败", e);
			throw new RuntimeException(e);
		} finally {
			// 返还到连接池
			returnResource(jedis);
		}
	}

	/**
	 * 从右边添加列表信息rpush
	 * 
	 * @param key
	 * @param value
	 */
	@Override
	public void rpush(String key, String... value) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			jedis.rpush(key, value);
		} catch (Exception e) {
			logger.error("从右边添加列表信息rpush失败", e);
			throw new RuntimeException(e);
		} finally {
			// 返还到连接池
			returnResource(jedis);
		}
	}

	/**
	 * 从左侧删除列表信息
	 * 
	 * @param key
	 * @return
	 */
	@Override
	public String lpop(String key) {
		Jedis jedis = null;
		String popValue = null;
		try {
			jedis = pool.getResource();
			popValue = jedis.lpop(key);
		} catch (Exception e) {
			logger.error("从左侧删除列表信息失败", e);
			throw new RuntimeException(e);
		} finally {
			// 返还到连接池
			returnResource(jedis);
		}
		return popValue;
	}

	/**
	 * 从右删除列表信息
	 * 
	 * @param key
	 * @return
	 */
	@Override
	public String rpop(String key) {
		Jedis jedis = null;
		String popValue = null;
		try {
			jedis = pool.getResource();
			popValue = jedis.rpop(key);
		} catch (Exception e) {
			logger.error("从右删除列表信息失败", e);
			throw new RuntimeException(e);
		} finally {
			// 返还到连接池
			returnResource(jedis);
		}
		return popValue;
	}

	/**
	 * 删除列表中指定的值
	 * 
	 * @param key
	 * @param count
	 *            1.当count > 0,从列表左边开始删除前count个值为value的元素 2.当count <
	 *            0,从列表右边开始删除前|count|个值为value的元素 3.当count = 0,删除所有值为value的元素。
	 * @param value
	 * @return
	 */
	@Override
	public long lrem(String key, long count, String value) {
		Jedis jedis = null;
		long remCount = 0;
		try {
			jedis = pool.getResource();
			remCount = jedis.lrem(key, count, value);
		} catch (Exception e) {
			logger.error("删除列表中指定的值失败", e);
			throw new RuntimeException(e);
		} finally {
			// 返还到连接池
			returnResource(jedis);
		}
		return remCount;
	}

	/**
	 * 获取列表长度
	 * 
	 * @param key
	 * @return
	 */
	@Override
	public long llen(String key) {
		Jedis jedis = null;
		long length = 0;
		try {
			jedis = pool.getResource();
			length = jedis.llen(key);
		} catch (Exception e) {
			logger.error("获取列表长度失败", e);
			throw new RuntimeException(e);
		} finally {
			// 返还到连接池
			returnResource(jedis);
		}
		return length;
	}

	/**
	 * 获取列表片段
	 * 
	 * @param key
	 * @param start
	 * @param end
	 * @return
	 */
	@Override
	public List<String> lrange(String key, long start, long end) {
		Jedis jedis = null;
		List<String> list = null;
		try {
			jedis = pool.getResource();
			list = jedis.lrange(key, start, end);
		} catch (Exception e) {
			logger.error("获取列表片段失败", e);
			throw new RuntimeException(e);
		} finally {
			// 返还到连接池
			returnResource(jedis);
		}
		return list;
	}

	/**
	 * 获取指定索引的元素
	 * 
	 * @param key
	 * @param index
	 * @return
	 */
	@Override
	public String lindex(String key, long index) {
		Jedis jedis = null;
		String value = null;
		try {
			jedis = pool.getResource();
			value = jedis.lindex(key, index);
		} catch (Exception e) {
			logger.error("获取指定索引的元素失败", e);
			throw new RuntimeException(e);
		} finally {
			// 返还到连接池
			returnResource(jedis);
		}
		return value;
	}

	/**
	 * 设置指定索引的元素
	 * 
	 * @param key
	 * @param index
	 * @param value
	 */
	@Override
	public void lset(String key, long index, String value) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			jedis.lset(key, index, value);
		} catch (Exception e) {
			logger.error("设置指定索引的元素失败", e);
			throw new RuntimeException(e);
		} finally {
			// 返还到连接池
			returnResource(jedis);
		}
	}

	/**
	 * 保留列表指定片段
	 * 
	 * @param key
	 * @param start
	 * @param end
	 */
	@Override
	public void ltrim(String key, long start, long end) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			jedis.ltrim(key, start, end);
		} catch (Exception e) {
			logger.error("保留列表指定片段失败", e);
			throw new RuntimeException(e);
		} finally {
			// 返还到连接池
			returnResource(jedis);
		}
	}

	/**
	 * 在指定值的前或后插入新元素
	 * 
	 * @param key
	 * @param where
	 *            1. before:插入值在pivot之前 2. after:插入值在pivot之后
	 * @param pivot
	 * @param value
	 */
	@Override
	public void linsert(String key, String where, String pivot, String value) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			if ("before".equals(where)) {
				jedis.linsert(key, BinaryClient.LIST_POSITION.BEFORE, pivot, value);
			} else if ("after".equals(where)) {
				jedis.linsert(key, BinaryClient.LIST_POSITION.AFTER, pivot, value);
			}
		} catch (Exception e) {
			logger.error("在指定值的前或后插入新元素失败", e);
			throw new RuntimeException(e);
		} finally {
			// 返还到连接池
			returnResource(jedis);
		}
	}

	/**
	 * 在此添加方法说明
	 * 
	 * @param key
	 * @param value
	 * @return void
	 */
	@Override
	public void rpushObject(String key, Object... value) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			jedis.rpush(key.getBytes(), SerializerUtils.serialize(value));
		} catch (Exception e) {
			logger.error("失败", e);
			throw new RuntimeException(e);
		} finally {
			// 返还到连接池
			returnResource(jedis);
		}
	}

	/**
	 * 在此添加方法说明
	 * 
	 * @param key
	 * @return Object
	 */
	@Override
	public Object lpopObject(String key) {
		Object object = null;
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			byte[] in = jedis.lpop(key.getBytes());
			object = SerializerUtils.deserialize(in);
		} catch (Exception e) {
			logger.error("失败", e);
			throw new RuntimeException(e);
		} finally {
			// 返还到连接池
			returnResource(jedis);
		}
		return object;
	}

	/**
	 * 为key续期 <br>
	 * <font color=red>默认用户没访问超过30分钟注销用户的登录状态，所以用户每次访问都要将用户的注销时间推迟30分钟</font>
	 * 
	 * @param key
	 * @param expiry
	 *            有效期，单位秒
	 * @return long
	 */
	@Override
	public long setKeyExpiry(String key, int expire) {
		long result = 0;
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			result = jedis.expire(key, expire);
		} catch (Exception e) {
			logger.error("删除数据失败", e);
			throw new RuntimeException(e);
		} finally {
			// 返还到连接池
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 删除数据
	 * 
	 * @param key
	 * @return
	 */
	@Override
	public void del(String key) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			jedis.del(key);
		} catch (Exception e) {
			logger.error("删除数据失败", e);
			throw new RuntimeException(e);
		} finally {
			// 返还到连接池
			returnResource(jedis);
		}
	}

	/**
	 * 将Set集合存入redis
	 * 
	 * @param key
	 * @param set
	 * @param seconds
	 * @return void
	 */
	@Override
	public void setSet(String key, Set<Object> set, int seconds) {

		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			jedis.set(key.getBytes(), SerializerUtils.serialize(set));
			if (seconds != 0)
				jedis.expire(key, seconds);
		} catch (Exception e) {
			logger.error("存储set失败", e);
			throw new RuntimeException(e);
		} finally {
			// 返还到连接池
			returnResource(jedis);
		}
	}

	/**
	 * 将Set集合存入redis
	 * 
	 * @param key
	 * @param set
	 * @param seconds
	 * @return void
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Set<Object> getSet(String key) {
		Set<Object> set = null;
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			byte[] bs = jedis.get(key.getBytes());
			set = (Set<Object>) SerializerUtils.deserialize(bs);
		} catch (Exception e) {
			logger.error("存储set失败", e);
			throw new RuntimeException(e);
		} finally {
			// 返还到连接池
			returnResource(jedis);
		}
		return set;
	}

	/**
	 * 删除redis中存储的set
	 * 
	 * @param key
	 */
	@Override
	public void delSet(String key) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			jedis.del(key.getBytes());
		} catch (Exception e) {
			logger.error("删除redis中存储的set失败", e);
			throw new RuntimeException(e);
		} finally {
			// 返还到连接池
			returnResource(jedis);
		}
	}

	/**
	 * 将List集合存入redis
	 * 
	 * @param key
	 * @param list
	 * @param seconds
	 * @return void
	 */
	@Override
	public <T> void setList(String key, List<T> list, int seconds) {

		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			jedis.set(key.getBytes(), SerializerUtils.serialize(list));
			if (seconds != 0)
				jedis.expire(key, seconds);
		} catch (Exception e) {
			logger.error("存储list失败", e);
			throw new RuntimeException(e);
		} finally {
			// 返还到连接池
			returnResource(jedis);
		}
	}

	/**
	 * 获取Redis中的List
	 * 
	 * @param key
	 * @return List<Object>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Object> getList(String key) {
		List<Object> list = null;
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			byte[] in = jedis.get(key.getBytes());
			list = (List<Object>) SerializerUtils.deserialize(in);
		} catch (Exception e) {
			logger.error("获取Redis中的List失败", e);
			throw new RuntimeException(e);
		} finally {
			// 返还到连接池
			returnResource(jedis);
		}
		return list;
	}

	/**
	 * 删除redis中存储的list
	 * 
	 * @param key
	 */
	@Override
	public void delList(String key) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			jedis.del(key.getBytes());
		} catch (Exception e) {
			logger.error("删除redis中存储的list失败", e);
			throw new RuntimeException(e);
		} finally {
			// 返还到连接池
			returnResource(jedis);
		}
	}

	/**
	 * 存储object对象
	 * 
	 * @param key
	 * @param object
	 * @param seconds
	 * @return void
	 */
	@Override
	public void setObject(String key, Object object, int seconds) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			jedis.set(key.getBytes(), SerializerUtils.serialize(object));
			if (seconds != 0)
				jedis.expire(key, seconds);
		} catch (Exception e) {
			logger.error("存储object对象失败", e);
			throw new RuntimeException(e);
		} finally {
			// 返还到连接池
			returnResource(jedis);
		}

	}

	/**
	 * 获取redis中的object对象
	 * 
	 * @param key
	 * @return Object
	 */
	@Override
	public Object getObject(String key) {
		Object object = null;
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			byte[] in = jedis.get(key.getBytes());
			object = SerializerUtils.deserialize(in);
		} catch (Exception e) {
			logger.error("获取redis中的object对象失败", e);
			throw new RuntimeException(e);
		} finally {
			// 返还到连接池
			returnResource(jedis);
		}
		return object;
	}

	/**
	 * 删除redis中的object
	 * 
	 * @param key
	 * @return void
	 */
	@Override
	public long delObject(String key) {
		long result = 0;
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			result = jedis.del(key.getBytes());
		} catch (Exception e) {
			logger.error("删除redis中的object失败", e);
			throw new RuntimeException(e);
		} finally {
			// 返还到连接池
			returnResource(jedis);
		}
		return result;
	}

	/**
	 * 正则匹配键
	 * 
	 * @param pattern
	 * @return
	 */
	@Override
	public Set<String> keys(String pattern) {
		Jedis jedis = null;
		Set<String> keySet = null;
		try {
			jedis = pool.getResource();
			keySet = jedis.keys(pattern);
		} catch (Exception e) {
			logger.error("获取key失败", e);
			throw new RuntimeException(e);
		} finally {
			returnResource(jedis);
		}
		return keySet;
	}

	/**
	 * 判断key键是否存在
	 * 
	 * @param key
	 * @return
	 */
	@Override
	public boolean exists(String key) {
		Jedis jedis = null;
		boolean exists = false;
		try {
			jedis = pool.getResource();
			exists = jedis.exists(key);
		} catch (Exception e) {
			logger.error("判断key键是否存在失败", e);
			throw new RuntimeException(e);
		} finally {
			returnResource(jedis);
		}
		return exists;
	}

	/**
	 * 根据key排序
	 * 
	 * @param key
	 * @return List<String>
	 */
	@Override
	public List<String> sort(String key) {
		Jedis jedis = null;
		List<String> list = null;
		try {
			jedis = pool.getResource();
			list = jedis.sort(key);
		} catch (Exception e) {
			logger.error("根据key排序失败", e);
			throw new RuntimeException(e);
		} finally {
			// 返还到连接池
			returnResource(jedis);
		}
		return list;
	}

	/**
	 * 自定义排序
	 * 
	 * @param key
	 * @param sortingParams
	 * @return
	 */
	@Override
	public List<String> sort(String key, SortingParams sortingParams) {
		Jedis jedis = null;
		List<String> list = null;
		try {
			jedis = pool.getResource();
			list = jedis.sort(key, sortingParams);
		} catch (Exception e) {
			logger.error("自定义排序失败", e);
			throw new RuntimeException(e);
		} finally {
			// 返还到连接池
			returnResource(jedis);
		}
		return list;
	}

	/**
	 * 单个添加元素到有序集合中
	 * 
	 * @param key
	 * @param score
	 * @param member
	 * @return
	 */
	@Override
	public long zadd(String key, double score, String member) {
		Jedis jedis = null;
		long count = 0;
		try {
			jedis = pool.getResource();
			count = jedis.zadd(key, score, member);
		} catch (Exception e) {
			logger.error("单个添加元素到有序集合中失败", e);
			throw new RuntimeException(e);
		} finally {
			// 返还到连接池
			returnResource(jedis);
		}
		return count;
	}

	private void returnResource(Jedis redis) {
		try {
			if (redis != null) {
				redis.close();
			}
		} catch (Exception e) {
			this.logger.error("释放redis资源失败", e);
			throw new RuntimeException(e);
		}
	}
}
