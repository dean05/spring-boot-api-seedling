package com.zoctan.seedling.util;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Redis工具
 * hash 以 h 开头的方法
 * Set 以 s 开头的方法（不含通用方法）
 * List 以 l 开头的方法
 *
 * @author Zoctan
 * @date 2018/05/27
 */
@Component
public class RedisUtils {
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    //=============================common============================

    /**
     * 指定缓存失效时间
     *
     * @param key  键
     * @param time 时间(秒)
     * @return 是否设置成功
     */
    public Boolean setExpire(final String key, final Long time) {
        try {
            if (time > 0) {
                this.redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
            return true;
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 根据key 获取过期时间
     *
     * @param key 键
     * @return 时间(秒) 返回0代表为永久有效
     */
    public Long getExpire(final String key) {
        try {
            return this.redisTemplate.getExpire(key, TimeUnit.SECONDS);
        } catch (final Exception e) {
            return -1L;
        }
    }

    /**
     * 判断key是否存在
     *
     * @param key 键
     * @return true 存在 false不存在
     */
    public Boolean hasKey(final String key) {
        try {
            return this.redisTemplate.hasKey(key);
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 删除缓存
     *
     * @param key 可以传一个值 或多个
     */
    public void delete(final String... key) {
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                this.redisTemplate.delete(key[0]);
            } else {
                this.redisTemplate.delete(Arrays.asList(key));
            }
        }
    }

    //============================String=============================

    /**
     * 普通缓存获取
     *
     * @param key 键
     * @return 值
     */
    public Object get(final String key) {
        return key == null ? null : this.redisTemplate.opsForValue().get(key);
    }

    /**
     * 普通缓存放入
     *
     * @param key   键
     * @param value 值
     * @return {Boolean}
     */
    public Boolean set(final String key, final Object value) {
        try {
            this.redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 普通缓存放入并设置时间
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     */
    public Boolean set(final String key, final Object value, final Long time) {
        try {
            if (time > 0) {
                this.redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            } else {
                this.set(key, value);
            }
            return true;
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 递增
     *
     * @param key   键
     * @param delta 要增加几(大于0)
     * @return 加上指定值之后 key 的值
     */
    public Long increment(final String key, final Long delta) {
        if (delta < 0) {
            throw new RuntimeException("递增因子必须大于0");
        }
        return this.redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 递减
     *
     * @param key   键
     * @param delta 要减少几(小于0)
     * @return 减少指定值之后 key 的值
     */
    public Long decrement(final String key, final Long delta) {
        if (delta < 0) {
            throw new RuntimeException("递减因子必须大于0");
        }
        return this.redisTemplate.opsForValue().increment(key, -delta);
    }

    //================================Map=================================

    /**
     * HashGet
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return 值
     */
    public Object hGet(final String key, final String item) {
        return this.redisTemplate.opsForHash().get(key, item);
    }

    /**
     * 获取hashKey对应的所有键值
     *
     * @param key 键
     * @return 对应的多个键值
     */
    public Map<Object, Object> hmGet(final String key) {
        return this.redisTemplate.opsForHash().entries(key);
    }

    /**
     * HashSet
     *
     * @param key 键
     * @param map 对应多个键值
     * @return {Boolean}
     */
    public Boolean hmSet(final String key, final Map<String, Object> map) {
        try {
            this.redisTemplate.opsForHash().putAll(key, map);
            return true;
        } catch (final Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * HashSet 并设置时间
     *
     * @param key  键
     * @param map  对应多个键值
     * @param time 时间(秒)
     * @return {Boolean}
     */
    public Boolean hmSet(final String key, final Map<String, Object> map, final Long time) {
        try {
            this.redisTemplate.opsForHash().putAll(key, map);
            if (time > 0) {
                this.setExpire(key, time);
            }
            return true;
        } catch (final Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @return {Boolean}
     */
    public Boolean hSet(final String key, final String item, final Object value) {
        try {
            this.redisTemplate.opsForHash().put(key, item, value);
            return true;
        } catch (final Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @param time  时间(秒)  注意:如果已存在的hash表有时间,这里将会替换原有的时间
     * @return {Boolean}
     */
    public Boolean hSet(final String key, final String item, final Object value, final Long time) {
        try {
            this.redisTemplate.opsForHash().put(key, item, value);
            if (time > 0) {
                this.setExpire(key, time);
            }
            return true;
        } catch (final Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除hash表中的值
     *
     * @param key  键 不能为null
     * @param item 项 可以使多个 不能为null
     */
    public void hDelete(final String key, final Object... item) {
        this.redisTemplate.opsForHash().delete(key, item);
    }

    /**
     * 判断hash表中是否有该项的值
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return true 存在 false不存在
     */
    public Boolean hHasKey(final String key, final String item) {
        return this.redisTemplate.opsForHash().hasKey(key, item);
    }

    /**
     * hash递增 如果不存在,就会创建一个 并把新增后的值返回
     *
     * @param key  键
     * @param item 项
     * @param by   要增加几(大于0)
     * @return 加上指定值之后 key 的值
     */
    public double hIncrement(final String key, final String item, final double by) {
        return this.redisTemplate.opsForHash().increment(key, item, by);
    }

    /**
     * hash递减
     *
     * @param key  键
     * @param item 项
     * @param by   要减少记(小于0)
     * @return 减少指定值之后 key 的值
     */
    public double hDecrement(final String key, final String item, final double by) {
        return this.redisTemplate.opsForHash().increment(key, item, -by);
    }

    //============================set=============================

    /**
     * 根据key获取Set中的所有值
     *
     * @param key 键
     * @return Set<Object>
     */
    public Set<Object> sGet(final String key) {
        try {
            return this.redisTemplate.opsForSet().members(key);
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据value从一个set中查询,是否存在
     *
     * @param key   键
     * @param value 值
     * @return true 存在 false不存在
     */
    public Boolean sHasKey(final String key, final Object value) {
        try {
            return this.redisTemplate.opsForSet().isMember(key, value);
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 将数据放入set缓存
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public Long sSet(final String key, final Object... values) {
        try {
            return this.redisTemplate.opsForSet().add(key, values);
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return 0L;
    }

    /**
     * 将set数据放入缓存
     *
     * @param key    键
     * @param time   时间(秒)
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public Long sSetAndTime(final String key, final Long time, final Object... values) {
        try {
            final Long count = this.redisTemplate.opsForSet().add(key, values);
            if (time > 0) {
                this.setExpire(key, time);
            }
            return count;
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return 0L;
    }

    /**
     * 获取set缓存的长度
     *
     * @param key 键
     * @return 缓存的长度
     */
    public Long sGetSetSize(final String key) {
        try {
            return this.redisTemplate.opsForSet().size(key);
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return 0L;
    }

    /**
     * 移除值为value的
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 移除的个数
     */
    public Long setRemove(final String key, final Object... values) {
        try {
            return this.redisTemplate.opsForSet().remove(key, values);
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return 0L;
    }
    //===============================list=================================

    /**
     * 获取list缓存的内容
     *
     * @param key   键
     * @param start 开始
     * @param end   结束  0 到 -1代表所有值
     * @return list缓存的内容
     */
    public List<Object> lGet(final String key, final Long start, final Long end) {
        try {
            return this.redisTemplate.opsForList().range(key, start, end);
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取list缓存的长度
     *
     * @param key 键
     * @return list缓存的长度
     */
    public Long lGetListSize(final String key) {
        try {
            return this.redisTemplate.opsForList().size(key);
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return 0L;
    }

    /**
     * 通过索引 获取list中的值
     *
     * @param key   键
     * @param index 索引  index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
     * @return list中的值
     */
    public Object lGetIndex(final String key, final Long index) {
        try {
            return this.redisTemplate.opsForList().index(key, index);
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @return 是否放入成功
     */
    public Boolean lSet(final String key, final Object value) {
        try {
            this.redisTemplate.opsForList().rightPush(key, value);
            return true;
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return 是否放入成功
     */
    public Boolean lSet(final String key, final Object value, final Long time) {
        try {
            this.redisTemplate.opsForList().rightPush(key, value);
            if (time > 0) {
                this.setExpire(key, time);
            }
            return true;
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @return 是否放入成功
     */
    public Boolean lSet(final String key, final List<Object> value) {
        try {
            this.redisTemplate.opsForList().rightPushAll(key, value);
            return true;
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return 是否放入成功
     */
    public Boolean lSet(final String key, final List<Object> value, final Long time) {
        try {
            this.redisTemplate.opsForList().rightPushAll(key, value);
            if (time > 0) {
                this.setExpire(key, time);
            }
            return true;
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 根据索引修改list中的某条数据
     *
     * @param key   键
     * @param index 索引
     * @param value 值
     * @return 是否修改成功
     */
    public Boolean lUpdateIndex(final String key, final Long index, final Object value) {
        try {
            this.redisTemplate.opsForList().set(key, index, value);
            return true;
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 移除N个值为value
     *
     * @param key   键
     * @param count 移除多少个
     * @param value 值
     * @return 移除的个数
     */
    public Long lRemove(final String key, final Long count, final Object value) {
        try {
            return this.redisTemplate.opsForList().remove(key, count, value);
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return 0L;
    }

}