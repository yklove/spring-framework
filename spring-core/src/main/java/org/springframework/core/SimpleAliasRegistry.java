/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.util.StringValueResolver;

/**
 * Simple implementation of the {@link AliasRegistry} interface.
 * AliasRegistry接口简单的实现
 * Serves as base class for
 * {@link org.springframework.beans.factory.support.BeanDefinitionRegistry}
 * implementations.
 * 该实现类是BeanDefinitionRegistry类的基类
 *
 * @author Juergen Hoeller
 * @since 2.5.2
 */
public class SimpleAliasRegistry implements AliasRegistry {

	/**
     * Map from alias to canonical name
     * 从别名映射到实际名称(别名做key,实际名称做value)
     * 这里使用了一个ConcurrentHashMap来存储别名和实际名称的关系
     */
	private final Map<String, String> aliasMap = new ConcurrentHashMap<>(16);


    /**
     * 实现了AliasRegistry接口中的注册别名方法
     * @param name the canonical name 一个规范的名称
     * @param alias the alias to be registered 需要注册的别名
     */
	@Override
	public void registerAlias(String name, String alias) {
	    //断言 如果name为空,抛出AssertionError
		Assert.hasText(name, "'name' must not be empty");
		//断言 如果alias为空,抛出AssertionError
		Assert.hasText(alias, "'alias' must not be empty");
		//加锁，原因是ConcurrentHashMap的迭代器并不是线程安全的。
        //在checkForAliasCircle时用到了ConcurrentHashMap的迭代器
		synchronized (this.aliasMap) {
		    //如果别名和名称相同,则从aliasMap删除该别名。
            //原因：别名等于原名,认为别名没有意义。
			if (alias.equals(name)) {
				this.aliasMap.remove(alias);
			}
			//如果别名和名称不相同
			else {
			    //根据别名取名称(registeredName)
				String registeredName = this.aliasMap.get(alias);
				//如果名称(registeredName)不为空,说明该别名已被占用。
				if (registeredName != null) {
				    //判断该别名对应的名称是否等于传入的名称。
					if (registeredName.equals(name)) {
						// An existing alias - no need to re-register
                        // 得出别名已存在的结论,无需操作,直接return
						return;
					}
					//根据别名得到的名称与传入的名称不相等
                    //如果别名不允许重写,抛出IllegalStateException异常
					if (!allowAliasOverriding()) {
                        throw new IllegalStateException("Cannot register alias '" + alias + "' for name '" +
								name + "': It is already registered for name '" + registeredName + "'.");
					}
				}
				//检查别名与名称的关系,防止出现名称与别名的关系循环(循环引用)。
				checkForAliasCircle(name, alias);
				//增加别名与名称的关系,存储到aliasMap中
				this.aliasMap.put(alias, name);
			}
		}
	}

	/**
	 * Return whether alias overriding is allowed.
     * 是否允许别名重写
	 * Default is {@code true}.
     * 默认返回允许
	 */
	protected boolean allowAliasOverriding() {
		return true;
	}

	/**
	 * Determine whether the given name has the given alias registered.
     * 确定给定名称是否已注册给定的别名。
	 * @param name the name to check 需要检查的名称
	 * @param alias the alias to look for 要查找的别名
	 * @since 4.2.1
	 */
	public boolean hasAlias(String name, String alias) {
	    //遍历aliasMap,根据名称查找对应的别名。
		for (Map.Entry<String, String> entry : this.aliasMap.entrySet()) {
		    //取出名称
			String registeredName = entry.getValue();
			//如果取出的名称与传入的名称相同
			if (registeredName.equals(name)) {
			    //取出别名
				String registeredAlias = entry.getKey();
				//如果别名与传入的别名相同 则返回true
                //或者 别名与传入的别名不相同 但是别名的别名与传入的别名相同 返回true 注意这里是递归进行判断,会遍历所有的别名。
				return (registeredAlias.equals(alias) || hasAlias(registeredAlias, alias));
			}
		}
		return false;
	}

    /**
     * 从注册表中删除指定的别名
     * @param alias the alias to remove 要删除的别名
     */
	@Override
	public void removeAlias(String alias) {
	    //加锁
        //原因：？？
		synchronized (this.aliasMap) {
		    //移除别名,并且获取到原名称
			String name = this.aliasMap.remove(alias);
			//如果原名称为空,抛出IllegalStateException表示没有该别名
			if (name == null) {
				throw new IllegalStateException("No alias '" + alias + "' registered");
			}
		}
	}

    /**
     * 确定给定的名称是否为别名。
     * @param name the name to check 要检查的名称
     * @return
     */
	@Override
	public boolean isAlias(String name) {
	    //判断给定的别名是否存在
		return this.aliasMap.containsKey(name);
	}

    /**
     * 获取别名
     * @param name the name to check for aliases 要检查别名的名称
     * @return
     */
	@Override
	public String[] getAliases(String name) {
	    //创建一个List用来存放别名
		List<String> result = new ArrayList<>();
		//加锁
		synchronized (this.aliasMap) {
			retrieveAliases(name, result);
		}
		//将list转成String[]返回
		return StringUtils.toStringArray(result);
	}

	/**
	 * Transitively retrieve all aliases for the given name.
	 * 检索给定名称的全部别名
	 * @param name the target name to find aliases for 需要查找别名的目标名称
	 * @param result the resulting aliases list 目标名称的别名列表
	 */
	private void retrieveAliases(String name, List<String> result) {
		// lambda表达式,遍历aliasMap
		this.aliasMap.forEach((alias, registeredName) -> {
			//如果别名对应的名称与目标名称相同
			if (registeredName.equals(name)) {
				//将别名添加到结果列表中
				result.add(alias);
				//递归查找
				retrieveAliases(alias, result);
			}
		});
	}

	/**
	 * Resolve all alias target names and aliases registered in this
	 * factory, applying the given StringValueResolver to them.
	 * <p>The value resolver may for example resolve placeholders
	 * in target bean names and even in alias names.
	 * 解析在该工厂中注册的所有别名目标名称和别名，
	 * 并将给定的用于解析字符串的接口(StringValueResolver)应用于它们。
	 * 值解析器可以例如解析目标bean名称中的占位符，甚至可以解析别名。
	 * @param valueResolver the StringValueResolver to apply
	 */
	public void resolveAliases(StringValueResolver valueResolver) {
		// Java断言 StringValueResolver 不能为null
		Assert.notNull(valueResolver, "StringValueResolver must not be null");
		//加锁
		synchronized (this.aliasMap) {
			//复制了一份aliasMap
			Map<String, String> aliasCopy = new HashMap<>(this.aliasMap);
			// lambda表达式 遍历别名
			aliasCopy.forEach((alias, registeredName) -> {
				//使用解析器解析别名
				String resolvedAlias = valueResolver.resolveStringValue(alias);
				//使用解析器解析名称
				String resolvedName = valueResolver.resolveStringValue(registeredName);
				//如果解析后别名为空 或者 名称为空 或者两者相等
				if (resolvedAlias == null || resolvedName == null || resolvedAlias.equals(resolvedName)) {
					//从aliasMap删除改别名
					this.aliasMap.remove(alias);
				}
				//否则的话 判断解析后的别名与原始别名是否相同 如果不相同
				else if (!resolvedAlias.equals(alias)) {
					//定义一个现有名称为解析后别名的名称
					String existingName = this.aliasMap.get(resolvedAlias);
					//如果现有名称不为空
					if (existingName != null) {
						//判断现有名称与解析后别名对应的名称是否相同 如果相同
						if (existingName.equals(resolvedName)) {
							// Pointing to existing alias - just remove placeholder
							// 指向现有的别名(解析后的别名) - 只需删除原先的别名
							//删除该别名
							this.aliasMap.remove(alias);
							//方法结束
							return;
						}
						//如果不相同,抛出IllegalStateException异常
						throw new IllegalStateException(
								"Cannot register resolved alias '" + resolvedAlias + "' (original: '" + alias +
								"') for name '" + resolvedName + "': It is already registered for name '" +
								registeredName + "'.");
					}
					//检查解析后的别名和名称是否存在循环引用
					checkForAliasCircle(resolvedName, resolvedAlias);
					//删除掉原有别名映射
					this.aliasMap.remove(alias);
					//增加解析后的别名与名称映射
					this.aliasMap.put(resolvedAlias, resolvedName);
				}
				//如果解析前后别名相同 但是解析前后名称不同
				else if (!registeredName.equals(resolvedName)) {
					//修改原有别名映射,指向解析后的名称。
					this.aliasMap.put(alias, resolvedName);
				}
			});
		}
	}

	/**
	 * Check whether the given name points back to the given alias as an alias
	 * in the other direction already, catching a circular reference upfront
	 * and throwing a corresponding IllegalStateException.
     * 检查名称是否以及指向了别名,或者别名的别名。
     * 防止出现循环引用，如果会出现,抛出IllegalStateException。
	 * @param name the candidate name 待判断的名称
	 * @param alias the candidate alias 待判断的别名
	 * @see #registerAlias
	 * @see #hasAlias
	 */
	protected void checkForAliasCircle(String name, String alias) {
	    //如果名称已经执行了别名，或者别名的别名，抛出IllegalStateException异常。
		if (hasAlias(alias, name)) {
			throw new IllegalStateException("Cannot register alias '" + alias +
					"' for name '" + name + "': Circular reference - '" +
					name + "' is a direct or indirect alias for '" + alias + "' already");
		}
	}

	/**
	 * Determine the raw name, resolving aliases to canonical names.
	 * 确定原始名称，将别名解析为真实名称。
	 * @param name the user-specified name 用户指定的名称
	 * @return the transformed name 转换后的名字
	 */
	public String canonicalName(String name) {
		//定义一个真实的名称 默认等于用户指定的名称
		String canonicalName = name;
		// Handle aliasing...
		// 处理别名
		// 定义一个别名
		String resolvedName;
		do {
			// 获取别名的真实名称 并 赋值给别名
			resolvedName = this.aliasMap.get(canonicalName);
			// 如果此时别名不为null
			// 说明可能获取到的并不是真实名称
			if (resolvedName != null) {
				// 将resolvedName赋值给真实名称
				canonicalName = resolvedName;
			}
			//如果别名不为null，继续获取真实名称，直到找到真正的名称
			//例如 A->B->C->D ,name = A ,最后返回 D
		}
		while (resolvedName != null);
		//返回真实名称
		return canonicalName;
	}

}
